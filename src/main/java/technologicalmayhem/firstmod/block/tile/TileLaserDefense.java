package technologicalmayhem.firstmod.block.tile;

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
import technologicalmayhem.firstmod.block.BlockLaserEnergyCollector;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import java.util.List;
import java.util.UUID;

public class TileLaserDefense extends TileEntity implements ITickable {

    private static final int range = 10;
    private int charges = 0;
    private int cooldown = 20;
    private int collectedEnergy = 0;
    private Entity target = null;

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
        if (world.isDaytime()) {
            collectedEnergy += getValidSensors();
            if (collectedEnergy >= 600) {
                collectedEnergy = 0;
                if (charges < 100) {
                    charges++;
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                }
            }
        }
    }

    private int getValidSensors() {
        int count = 0;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(pos.offset(side)).getBlock() instanceof BlockLaserEnergyCollector) {
                if (world.canBlockSeeSky(pos.offset(side).up())) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean hasValidTarget() {
        if (target != null && target.getDistance(pos.getX(), pos.getY(), pos.getZ()) < range && target.isEntityAlive()) {
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
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(range, range, range), pos.add(-range, -range, -range)));
        if (!entities.isEmpty()) {
            Entity newMob = null;
            double distance = Double.MAX_VALUE;
            for (Entity mob : entities) {
                if (mob.getDistance(pos.getX(), pos.getY(), pos.getZ()) < distance && mob instanceof IMob) {
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
            if (!target.attackEntityFrom(DamageSource.GENERIC, 3f)) {
                cooldown++;
                return;
            }
            charges--;
            cooldown = 20;
            if (target.isDead) {
                target = null;
            }
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            markDirty();
        }
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
        compound.setInteger("collectedEnergy", collectedEnergy);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        charges = compound.getInteger("charges");
        cooldown = compound.getInteger("cooldown");
        collectedEnergy = compound.getInteger("collectedEnergy");
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
