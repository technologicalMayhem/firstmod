package technologicalmayhem.firstmod.block.tile;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import technologicalmayhem.firstmod.util.enums.EnumFurnaceIgnitionResult;
import technologicalmayhem.firstmod.util.enums.EnumFurnacePhase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileDetonatingFurnace extends TileEntity implements ITickable {

    private int totalTime = 0;
    private int remainingTime = 0;
    private int nextPhase = 0;
    private int warningCooldown = 0;
    public boolean isDone = false;
    public EnumFurnacePhase phase = EnumFurnacePhase.INACTIVE;
    public ItemStackHandler items = new ItemStackHandler(9) {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (phase != EnumFurnacePhase.INACTIVE) return stack;
                //Test if item can actually be inserted into any of the slots
            else if ((slot < 8 && canSmelt(stack)) || (slot == 8 && isFuel(stack))) {
                return super.insertItem(slot, stack, simulate);
            } else return stack;
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    @Override
    public void update() {
        if (phase != EnumFurnacePhase.INACTIVE) remainingTime--;
        if (remainingTime < 0 && phase == EnumFurnacePhase.PHASE_3) {
            detonate();
            return;
        }
        if (world.isRemote) {
            burningParticles();
        } else {
            if (warningCooldown > 0) warningCooldown--;
            if (phase != EnumFurnacePhase.INACTIVE) {
                markDirty();
                if (remainingTime < nextPhase) advancePhase();
            }
        }
    }

    //All the Rendering stuff goes here

    private void burningParticles() {
        EnumFurnacePhase phaseI = EnumFurnacePhase.INACTIVE;
        EnumFurnacePhase phaseA = EnumFurnacePhase.ACTIVE;
        EnumFurnacePhase phase1 = EnumFurnacePhase.PHASE_1;
        EnumFurnacePhase phase2 = EnumFurnacePhase.PHASE_2;
        EnumFurnacePhase phase3 = EnumFurnacePhase.PHASE_3;

        smallSmoke(phaseA, phase1, phase2, phase3);
        frontBurn(phaseI, phase3);
        fullBurn(phase3);
    }

    private void smallSmoke(EnumFurnacePhase phaseA, EnumFurnacePhase phase1, EnumFurnacePhase phase2, EnumFurnacePhase phase3) {
        if (phase.equals(phaseA) || phase.equals(phase1)) {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0.05, 0);
        }
        //Big smoke
        if (phase.equals(phase2) || phase.equals(phase3)) {
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0.1, 0);
        }
        //Lava particles
        if ((phase.equals(phase1) && (remainingTime % 10) == 0) ||
                (phase.equals(phase2) && (remainingTime % 5) == 0) ||
                (phase.equals(phase3))) {
            world.spawnParticle(EnumParticleTypes.LAVA, pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, 0, 0.1, 0);
        }
    }

    private void frontBurn(EnumFurnacePhase phaseI, EnumFurnacePhase phase3) {
        if (!phase.equals(phaseI) && !phase.equals(phase3) && (remainingTime % 5) == 0) {
            EnumFacing enumfacing = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
            double x = (double) pos.getX() + 0.5D;
            double y = (double) pos.getY() + world.rand.nextDouble() * 6.0D / 16.0D + (2.0D / 16.0D);
            double z = (double) pos.getZ() + 0.5D;
            double d3 = world.rand.nextDouble() * 0.6D - 0.3D;

            switch (enumfacing) {
                case WEST:
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - 0.52D, y, z + d3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, x - 0.52D, y, z + d3, 0.0D, 0.0D, 0.0D);
                    break;
                case EAST:
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.52D, y, z + d3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, x + 0.52D, y, z + d3, 0.0D, 0.0D, 0.0D);
                    break;
                case NORTH:
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + d3, y, z - 0.52D, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, x + d3, y, z - 0.52D, 0.0D, 0.0D, 0.0D);
                    break;
                case SOUTH:
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + d3, y, z + 0.52D, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, x + d3, y, z + 0.52D, 0.0D, 0.0D, 0.0D);
                    break;
                default:
                    break;
            }
        }
    }

    private void fullBurn(EnumFurnacePhase phase3) {
        if (phase.equals(phase3)) {
            for (EnumFacing side : EnumFacing.values()) {
                if (!world.getBlockState(pos.offset(side)).isFullBlock()) {
                    double x = (double) pos.getX();
                    double y = (double) pos.getY();
                    double z = (double) pos.getZ();
                    double r1 = world.rand.nextDouble();
                    double r2 = world.rand.nextDouble();
                    double offset = 0.05D;


                    switch (side) {
                        case WEST:
                            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + offset + 1.0D, y + r1, z + r2, 0.0D, 0.0D, 0.0D);
                            world.spawnParticle(EnumParticleTypes.FLAME, x + offset + 1.0D, y + r1, z + r2, 0.0D, 0.0D, 0.0D);
                            break;
                        case EAST:
                            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - offset, y + r1, z + r2, 0.0D, 0.0D, 0.0D);
                            world.spawnParticle(EnumParticleTypes.FLAME, x - offset, y + r1, z + r2, 0.0D, 0.0D, 0.0D);
                            break;
                        case NORTH:
                            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + r1, y + r2, z + offset + 1.0D, 0.0D, 0.0D, 0.0D);
                            world.spawnParticle(EnumParticleTypes.FLAME, x + r1, y + r2, z + offset + 1.0D, 0.0D, 0.0D, 0.0D);
                            break;
                        case SOUTH:
                            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + r1, y + r2, z - offset, 0.0D, 0.0D, 0.0D);
                            world.spawnParticle(EnumParticleTypes.FLAME, x + r1, y + r2, z - offset, 0.0D, 0.0D, 0.0D);
                            break;
                        case UP:
                            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + r1, y - offset + 1.0D, z + r2, 0.0D, 0.0D, 0.0D);
                            world.spawnParticle(EnumParticleTypes.FLAME, x + r1, y - offset + 1.0D, z + r2, 0.0D, 0.0D, 0.0D);
                            break;
                        case DOWN:
                            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + r1, y + offset, z + r2, 0.0D, 0.0D, 0.0D);
                            world.spawnParticle(EnumParticleTypes.FLAME, x + r1, y + offset, z + r2, 0.0D, 0.0D, 0.0D);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    //Rendering stuff ends here

    public ItemStack insertSmeltableItem(ItemStack item) {
        ItemStack itemIn = item;
        int size = itemIn.getCount();
        for (int i = 0; i < 8; i++) {
            itemIn = items.insertItem(i, itemIn, false);
            if (itemIn.getCount() != size) return itemIn;
        }
        return itemIn;
    }

    public ItemStack insertFuel(ItemStack item) {
        return items.insertItem(8, item, false);
    }

    public EnumFurnaceIgnitionResult ignite() {
        double required = calculateFuelRequirement();
        int fuel = calculateFuelAmount();

        if (required == fuel || warningCooldown > 0) {
            remainingTime = (int) Math.round(required * (Math.pow(((required / 200) / -607), 3) + 1));
            totalTime = remainingTime;

            advancePhase();
            markDirty();
            return EnumFurnaceIgnitionResult.SUCCESS;
        } else if (required < fuel) {
            warningCooldown = 1000;
            return EnumFurnaceIgnitionResult.FUELWARNIGN;
        } else {
            return EnumFurnaceIgnitionResult.FUELMISSING;
        }
    }

    private int calculateFuelRequirement() {
        int fuelRequirement = 0;
        for (int i = 0; i < 8; i++) {
            fuelRequirement += items.getStackInSlot(i).getCount() * 200;
        }
        return fuelRequirement;
    }

    private int calculateFuelAmount() {
        return getBurnTime(items.getStackInSlot(8)) * items.getStackInSlot(8).getCount();
    }

    public void detonate() {
        if (world.isRemote) {
            world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
            SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation("minecraft", "entity.generic.explode"));
            world.playSound(pos.getX(), pos.getY(), pos.getZ(), soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        } else {
            isDone = true;
            world.destroyBlock(pos, false);
        }
    }

    public void advancePhase() {
        phase = phase.getNextPhase();
        nextPhase = Math.round(totalTime * phase.percentage);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        markDirty();
    }

    public NonNullList<ItemStack> getSmeltingResults() {
        NonNullList<ItemStack> result = NonNullList.create();
        for (int i = 0; i < 8; i++) {
            result.add(getSmeltingResult(items.getStackInSlot(i)));
        }
        return result;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("totalTime", totalTime);
        compound.setInteger("remainingTime", remainingTime);
        compound.setString("phase", phase.name());
        compound.setTag("items", items.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        totalTime = compound.getInteger("totalTime");
        remainingTime = compound.getInteger("remainingTime");
        phase = EnumFurnacePhase.valueOf(compound.getString("phase"));
        items.deserializeNBT(compound.getCompoundTag("items"));
        nextPhase = Math.round(totalTime * phase.percentage);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        nbtTag.setString("phase", phase.name());
        nbtTag.setInteger("remainingTime", remainingTime);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        phase = EnumFurnacePhase.valueOf(tag.getString("phase"));
        remainingTime = tag.getInteger("remainingTime");
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setString("phase", phase.name());
        tag.setInteger("remainingTime", remainingTime);
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        phase = EnumFurnacePhase.valueOf(tag.getString("phase"));
        remainingTime = tag.getInteger("remainingTime");
        super.handleUpdateTag(tag);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    private boolean isFuel(ItemStack stack) {
        return getBurnTime(stack) > 0;
    }

    private boolean canSmelt(ItemStack stack) {
        return !getSmeltingResult(stack).isEmpty();
    }

    private int getBurnTime(ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime(stack);
    }

    private ItemStack getSmeltingResult(ItemStack stack) {
        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
        result.setCount(stack.getCount());
        return result;
    }

    public int getFillLevel() {
        float totalItems = 0;
        for (int i = 0; i < 8; i++) {
            totalItems += items.getStackInSlot(i).getCount();
        }
        double powerOut = Math.round(totalItems / 512 * 15);
        if (totalItems < 512 && powerOut == 15) powerOut--;
        if (totalItems > 0 && powerOut == 0) powerOut++;
        return (int) powerOut;
    }
}
