package technologicalmayhem.firstmod.block.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import java.util.List;
import java.util.UUID;

public class TileLaserDefense extends TileEntity implements ITickable {

    static final int range = 10;
    int charge = 0;
    int cooldown = 20;
    int connectedSensors = 0;
    Entity target = null;

    @Override
    public void update() {
        if (!world.isRemote) {
            if (target != null) {
                cooldown--;
                if (cooldown == 0) {
                    target.attackEntityFrom(DamageSource.GENERIC, 3f);
                    cooldown = 20;
                    if (target.isDead) {
                        target = null;
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                    }
                }
            } else {
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
                    target = newMob;
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                }
            }
        } else {
            if (target != null && target.isEntityAlive()) {
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
        compound.setInteger("charge", charge);
        compound.setInteger("cooldown", cooldown);
        if (target != null) compound.setString("target", target.getPersistentID().toString());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        charge = compound.getInteger("charge");
        cooldown = compound.getInteger("cooldown");
        if (!compound.getString("target").isEmpty()) target = findByUUID(UUID.fromString(compound.getString("target")));
        super.readFromNBT(compound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        if (target != null) tag.setString("target", target.getPersistentID().toString());
        return new SPacketUpdateTileEntity(getPos(), 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        if (!tag.getString("target").isEmpty()) {
            target = findByUUID(UUID.fromString(tag.getString("target")));
        } else {
            target = null;
        }
    }
}
