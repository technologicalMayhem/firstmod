package technologicalmayhem.firstmod.block.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import technologicalmayhem.firstmod.FirstMod;
import technologicalmayhem.firstmod.util.EnumFurnaceIgnitionResult;
import technologicalmayhem.firstmod.util.EnumFurnacePhase;

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
        if (world.isRemote) {
            //TODO: Add rendering stuff
        } else {
            if (warningCooldown > 0) warningCooldown--;
            if (phase != EnumFurnacePhase.INACTIVE) {
                remainingTime--;
                if (remainingTime < nextPhase) advancePhase();
            }
        }
    }

    private void createParticles() {
        if (phase == EnumFurnacePhase.ACTIVE) {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() - 0.5f, pos.getY(), pos.getZ() - 0.5f, 0, 1, 0);
        }
    }

    public ItemStack insertSmeltableItem(ItemStack item) {
        int size = item.getCount();
        for (int i = 0; i < 8; i++) {
            item = items.insertItem(i, item, false);
            if (item.getCount() != size) return item;
        }
        return item;
    }

    public ItemStack insertFuel(ItemStack item) {
        return items.insertItem(8, item, false);
    }

    public EnumFurnaceIgnitionResult ignite() {
        int required = calculateFuelRequirement();
        int fuel = calculateFuelAmount();

        if (required == fuel || warningCooldown > 0) {
            advancePhase();
            FirstMod.logger.info(fuel);
            remainingTime = (int) Math.round(fuel * (1 - (1.1 * fuel / (fuel + 200 * 20))));
            FirstMod.logger.info("Cooktime: " + remainingTime);
            totalTime = remainingTime;
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
            createParticles();
        } else {
            isDone = true;
            world.destroyBlock(pos, false);
        }
    }

    public void advancePhase() {
        if (phase == EnumFurnacePhase.PHASE_3) {
            FirstMod.logger.info("Furnace is done");
            detonate();
        }
        phase = phase.getNextPhase();
        FirstMod.logger.info("New phase: " + phase);
        nextPhase = Math.round(totalTime * phase.percentage);
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        world.scheduleBlockUpdate(pos, this.getBlockType(), 0, 0);
        markDirty();
    }

    //TODO: Fix bug returning wrong amount
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
        compound.setInteger("remainingTime", remainingTime);
        compound.setString("phase", phase.name());
        compound.setTag("items", items.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
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
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        phase = EnumFurnacePhase.valueOf(tag.getString("phase"));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setString("phase", phase.name());
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        phase = EnumFurnacePhase.valueOf(tag.getString("phase"));
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
        return FurnaceRecipes.instance().getSmeltingResult(stack);
    }
}
