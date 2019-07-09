package technologicalmayhem.firstmod.block;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityDetonatingFurnace extends TileEntity implements ITickable, ICapabilityProvider {

	Boolean isBurning = false;
	Integer remainingTime = 0;
	Integer totalTime = 0;

	IItemHandler smeltables = new ItemStackHandler(8){
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if(!FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty()) {
				return super.insertItem(slot, stack, simulate);
			}
			return stack;
		}
	};
	IItemHandler fuelHandler = new ItemStackHandler(1)
	{
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (TileEntityFurnace.getItemBurnTime(stack) > 0) {
				return super.insertItem(slot, stack, simulate);
			}
			return stack;
		}
	};
	ArrayList<ItemStack> result = new ArrayList<ItemStack>();

	public TileEntityDetonatingFurnace()
	{

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		
	}

	public void ignite(EntityPlayer playerIn)
	{
		int totalCookTime = 0;
		int totalFuel = 0;
		for (int i = 0; i < smeltables.getSlots(); i++) {
			totalCookTime += smeltables.getStackInSlot(i).getCount() * 200;
		}
		for (int i = 0; i < fuelHandler.getSlots(); i++) {
			ItemStack fuel = fuelHandler.getStackInSlot(i);
			totalFuel += TileEntityFurnace.getItemBurnTime(fuel) * fuel.getCount();
		}
		if (totalCookTime <= totalFuel) {
			for (int i = 0; i < smeltables.getSlots(); i++) {
				ItemStack item = smeltables.getStackInSlot(i);
				ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(item);
				smeltResult.setCount(item.getCount());
				result.add(smeltResult);
			}
			totalTime = totalCookTime;
			remainingTime = totalCookTime / 4;
			isBurning = true;
		}
		else
		{
			playerIn.sendMessage(new TextComponentString("There is not enough fuel."));
		}
	}

	@Override
	public void update() {
		if (isBurning)
		{
			world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, true, pos.getX() + 0.5 + (0.25 * (world.rand.nextDouble() - 0.5)), pos.getY() + 1, pos.getZ() + 0.5 + (0.25 * (world.rand.nextDouble() - 0.5)), 0, 0.1, 0);
			remainingTime--;
			if (remainingTime.equals(0)) {
				for (int i = 0; i < fuelHandler.getSlots(); i++) {
					fuelHandler.extractItem(0, 64, false);
				}
				smeltables = new ItemStackHandler(8);
				for (int i = 0; i < result.size(); i++) {
					smeltables.insertItem(i, result.get(i), false);
				}
				world.destroyBlock(pos, false);
			}
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side) {
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == EnumFacing.UP)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(fuelHandler);
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(smeltables);
		return super.getCapability(cap, side);
	}
}