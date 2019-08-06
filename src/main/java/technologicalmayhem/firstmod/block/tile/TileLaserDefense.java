package technologicalmayhem.firstmod.block.tile;

import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import java.util.List;
import java.util.UUID;

public class TileLaserDefense extends TileEntity implements ITickable {

    //TODO: Test if counting of sensors now works properly

    private static final int range = 10;
    private int charges = 0;
    private int cooldown = 20;
    public int connectedSensors = 0;
    private int collectedEnergy = 0;
    private Entity target = null;

    public void setConnectedSensors(int connectedSensors) {
        this.connectedSensors = connectedSensors;
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            charge();
            if (charges > 0) {
                if (hasValidTarget()) {
                    attackTarget();
                } else {
                    searchForNewTarget();
                }
            }
        } else {
            if (charges > 0 && hasValidTarget()) {
                doParticles();
            }
        }
    }

    private void charge() {
        collectedEnergy += connectedSensors;
        if (collectedEnergy >= 800) {
            collectedEnergy = 0;
            if (charges < 100) {
                charges++;
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            }
        }
    }

    private boolean hasValidTarget() {
        if (target != null && target.getDistanceSq(pos) < range && !target.isDead) {
            return true;
        }
        target = null;
        if (!world.isRemote) markDirty();
        return false;
    }

    private void doParticles() {
        //Get the difference between block and target
        Vector3d blockPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vector3d diff = new Vector3d(target.posX - blockPos.x, target.posY - blockPos.y + target.height / 2, target.posZ - blockPos.z);
        //Find the greatest difference
        double greatest = 0;
        for (Double n : new Double[]{diff.x, diff.y, diff.z}) {
            if (Math.abs(n) > greatest) greatest = Math.abs(n);
        }
        //Find out how many we need to take and how much we need to move each step along the way
        double posSteps = greatest / 0.2;
        Vector3d step = new Vector3d(diff.x / posSteps, diff.y / posSteps, diff.z / posSteps);

        Vector3d curDiff = new Vector3d();
        for (int i = 0; i < Math.floor(posSteps); i++) {
            world.spawnParticle(EnumParticleTypes.REDSTONE,
                    blockPos.x + curDiff.x, blockPos.y + curDiff.y, blockPos.z + curDiff.z,
                    0, 0, 0);
            curDiff.add(step);
        }
    }

    private void searchForNewTarget() {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getY();
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(range, range, range), pos.add(-range, -range, -range)));
        if (!entities.isEmpty()) {
            Entity newMob = null;
            double distance = 20D;
            for (Entity mob : entities) {
                if (mob.getDistanceSq(pos) < distance && mob instanceof IMob) {
                    newMob = mob;
                    distance = mob.getDistanceSq(pos);
                }
            }
            if (target != newMob) {
                target = newMob;
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                markDirty();
            }
        }
    }

    private void attackTarget() {
        cooldown--;
        if (cooldown == 0) {
            target.attackEntityFrom(DamageSource.GENERIC, 3f);
            charges--;
            cooldown = 20;
            if (target.isDead) {
                target = null;
            }
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            markDirty();
        }
    }

    public void countSensors() {
        int sensors = 0;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(pos.offset(side)).getBlock() instanceof BlockDaylightDetector) {
                sensors++;
            }
        }
        connectedSensors = sensors;
        markDirty();
    }

    private EntityMob findByUUID(UUID uuid) {
        List<EntityMob> entities = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(pos.add(range, range, range), pos.add(-range, -range, -range)));
        if (!entities.isEmpty()) {
            for (EntityMob ent : entities) {
                if (ent.getPersistentID().equals(uuid)) return ent;
            }
        }
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("charges", charges);
        compound.setInteger("cooldown", cooldown);
        compound.setInteger("connectedSensory", connectedSensors);
        if (target != null) compound.setString("target", target.getPersistentID().toString());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        charges = compound.getInteger("charges");
        cooldown = compound.getInteger("cooldown");
        connectedSensors = compound.getInteger("connectedSensors");
        if (!compound.getString("target").isEmpty()) target = findByUUID(UUID.fromString(compound.getString("target")));
        super.readFromNBT(compound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("charges", charges);
        if (target != null) tag.setString("target", target.getPersistentID().toString());
        return new SPacketUpdateTileEntity(getPos(), 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        charges = tag.getInteger("charges");
        if (!tag.getString("target").isEmpty()) {
            target = findByUUID(UUID.fromString(tag.getString("target")));
        } else {
            target = null;
        }
    }
}
