package technologicalmayhem.firstmod.block.tile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TileLaserDefense extends TileEntity implements ITickable {

    static final int range = 10;
    int charge = 0;
    int cooldown = 20;
    int connectedSensors = 0;
    EntityLivingBase target = null;

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
                List<EntityMob> entities = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(pos.add(range, range, range), pos.add(-range, -range, -range)));
                if (!entities.isEmpty()) {
                    EntityMob newMob = null;
                    double distance = 20D;
                    for (EntityMob mob : entities) {
                        if (mob.getDistanceSq(pos) < distance) {
                            newMob = mob;
                            distance = mob.getDistanceSq(pos);
                        }
                    }
                    target = newMob;
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                }
            }
        } else {
            if (target != null) {
                BlockPos position = target.getPosition().add(0, 2, 0);
                world.spawnParticle(EnumParticleTypes.REDSTONE, position.getX(), position.getY(), position.getZ(), 0, 0, 0);
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
        tag.setInteger("charge", charge);
        tag.setInteger("cooldown", cooldown);
        if (target != null) tag.setString("target", target.getPersistentID().toString());
        return new SPacketUpdateTileEntity(getPos(), 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        charge = tag.getInteger("charge");
        cooldown = tag.getInteger("cooldown");
        if (!tag.getString("target").isEmpty()) {
            target = findByUUID(UUID.fromString(tag.getString("target")));
        } else {
            target = null;
        }
    }
}
