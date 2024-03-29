package technologicalmayhem.firstmod.block.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import technologicalmayhem.firstmod.block.BlockLaserEnergyCollector;
import technologicalmayhem.firstmod.util.EntityDistanceComparator;
import technologicalmayhem.firstmod.util.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TileLaserTurret extends TileEntity implements ITickable {

    private int charges = 0;
    private int collectedEnergy = 0;
    private int connectedSensors = 0;
    private int checkInterval = 0;
    private List<Pair<Entity, Integer>> targets = new ArrayList<>();

    private int additionalTargets = 0;
    private float damageMod = 1.0F;
    private float speedMod = 1.0F;
    private float rangeMod = 1.0F;

    @Override
    public void update() {
        if (!world.isRemote) {
            charge();
            if (charges > 0) {
                validateTargets();
                attackTarget();
            }
        } else {
            if (charges > 0) {
                validateTargets();
                doParticles();
            }
        }
    }

    private void charge() {
        if (world.isDaytime()) {
            collectedEnergy += connectedSensors;
            if (collectedEnergy >= 1600) {
                collectedEnergy = 0;
                if (charges < 100) {
                    charges++;
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                }
            }
        }
    }

    public void checkSensors() {
        int count = 0;
        int[][] offsets = new int[][]{{1, 0}, {1, 1}, {1, -1}, {-1, 0}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}};
        for (int[] offset : offsets) {
            if (world.getBlockState(pos.add(offset[0], -2, offset[1])).getBlock() instanceof BlockLaserEnergyCollector
                    && world.canBlockSeeSky(pos.add(offset[0], -2, offset[1]).up())) {
                count++;
            }
        }
        checkInterval = 100;
        connectedSensors = count;
    }

    private void validateTargets() {
        boolean areAllValid = true;

        Iterator<Pair<Entity, Integer>> iterator = targets.iterator();

        while (iterator.hasNext()) {
            Pair<Entity, Integer> target = iterator.next();
            if (!isTargetValid(target.A)) {
                areAllValid = false;
                iterator.remove();
                if (!world.isRemote) markDirty();
            }
        }
        if (!world.isRemote && (targets.size() < getBaseTargets() || !areAllValid)) {
            searchForNewTargets();
        }
    }

    private boolean isTargetValid(Entity target) {
        return target != null && target.getDistance(pos.getX(), pos.getY(), pos.getZ()) < getRange() && target.isEntityAlive();
    }

    private void doParticles() {
        for (Pair<Entity, Integer> target : targets) {
            //Get the difference between block and target
            Vector3d blockPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Vector3d diff = new Vector3d(target.A.posX - blockPos.x, target.A.posY - blockPos.y + target.A.height / 2, target.A.posZ - blockPos.z);
            //Find the greatest difference
            double greatest = 0;
            for (Double n : new Double[]{diff.x, diff.y, diff.z}) {
                if (Math.abs(n) > greatest) greatest = Math.abs(n);
            }
            //Find out how many we need to take and how much we need to move each step along the way
            double posSteps = greatest / 0.1;
            Vector3d step = new Vector3d(diff.x / posSteps, diff.y / posSteps, diff.z / posSteps);

            Vector3d curDiff = new Vector3d();
            for (int i = 0; i < Math.floor(posSteps); i++) {
                if (world.rand.nextInt(200) == 0) {
                    world.spawnParticle(EnumParticleTypes.REDSTONE,
                            blockPos.x + curDiff.x, blockPos.y + curDiff.y, blockPos.z + curDiff.z,
                            0, 0, 0);
                }
                curDiff.add(step);
            }
        }
    }

    private void searchForNewTargets() {
        int range = getRange();
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(range, 3, range), pos.add(-range, -3, -range)));
        entities.removeIf(entity -> !(entity instanceof IMob));
        for (Pair<Entity, Integer> target : targets) {
            entities.remove(target.A);
        }

        if (!entities.isEmpty()) {
            entities.sort(new EntityDistanceComparator(pos));
            int missingTargets = Math.abs(getBaseTargets() - targets.size());
            List<Entity> available = entities.subList(0, Math.min(missingTargets, entities.size()));
            for (Entity entity : available) {
                targets.add(new Pair<>(entity, getAttackSpeed()));
            }
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            markDirty();
        }
    }

    private void attackTarget() {
        for (Pair<Entity, Integer> target : targets) {
            target.B--;
            if (target.B == 0) {
                if (!target.A.attackEntityFrom(DamageSource.GENERIC, getDamage())) {
                    target.B++;
                    return;
                }
                charges--;
                target.B = getAttackSpeed();
                if (target.A.isDead) {
                    targets.remove(target);
                }
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                markDirty();
            }
        }
    }

    private Entity findByUUID(UUID uuid) {
        int range = getRange();
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(range, range, range), pos.add(-range, -range, -range)));
        if (!entities.isEmpty()) {
            for (Entity ent : entities) {
                if (ent.getPersistentID().equals(uuid)) return ent;
            }
        }
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("charges", charges);
        compound.setInteger("collectedEnergy", collectedEnergy);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        charges = compound.getInteger("charges");
        collectedEnergy = compound.getInteger("collectedEnergy");
        super.readFromNBT(compound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("charges", charges);
        StringBuilder idString = new StringBuilder();
        for (Pair<Entity, Integer> target : targets) {
            idString.append(target.A.getPersistentID().toString()).append(',');
        }
        if (idString.length() > 0) tag.setString("target", idString.toString());
        return new SPacketUpdateTileEntity(getPos(), 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        charges = tag.getInteger("charges");
        if (!tag.getString("target").isEmpty()) {
            targets.clear();
            for (String target : tag.getString("target").split(",")) {
                targets.add(new Pair<>(findByUUID(UUID.fromString(target)), 0));
            }
        }
    }

    private int getBaseTargets() {
        int baseTargets = 1;
        return baseTargets + additionalTargets;
    }

    private float getDamage() {
        float damage = 3.0F;
        return damage * damageMod;
    }

    private int getAttackSpeed() {
        int attackSpeed = 20;
        return (int) (attackSpeed * speedMod);
    }

    private int getRange() {
        int range = 10;
        return (int) (range * rangeMod);
    }
}
