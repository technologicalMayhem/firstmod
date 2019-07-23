package technologicalmayhem.firstmod.block.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import technologicalmayhem.firstmod.util.EnumFurnacePhase;

import javax.annotation.Nonnull;

public class TileDetonatingFurnace extends TileEntity implements ITickable {

    private Integer totalTime = 0;
    private Integer remainingTime = 0;
    private EnumFurnacePhase phase = EnumFurnacePhase.INACTIVE;
    private ItemStackHandler items = new ItemStackHandler(9) {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (phase != EnumFurnacePhase.INACTIVE) return stack;
                //Test if item can actually be inserted into any of the slots
                //
            else if ((slot < 9 && canSmelt(stack)) || (slot == 9 && isFuel(stack))) {
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
        if (phase != EnumFurnacePhase.INACTIVE) {

        }
    }

    //TODO: Implement Method that returns smelting results
    //TODO: Implement ignition logic

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("remainingTime", remainingTime);
        compound.setString("phase", phase.getName());
        compound.setTag("items", items.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        remainingTime = compound.getInteger("remainingTime");
        phase = EnumFurnacePhase.valueOf(compound.getString("phase"));
        items.deserializeNBT(compound.getCompoundTag("items"));
    }

    private Boolean isFuel(ItemStack stack) {
        return getBurnTime(stack) > 0;
    }

    private Boolean canSmelt(ItemStack stack) {
        return !getSmeltingResult(stack).isEmpty();
    }

    private Integer getBurnTime(ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime(stack);
    }

    private ItemStack getSmeltingResult(ItemStack stack) {
        return FurnaceRecipes.instance().getSmeltingResult(stack);
    }
}
