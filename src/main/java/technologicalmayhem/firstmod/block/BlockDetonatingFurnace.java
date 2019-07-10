package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import technologicalmayhem.firstmod.FirstMod;

public class BlockDetonatingFurnace extends Block  {

    public BlockDetonatingFurnace() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".detonatingfurnace");
        setRegistryName("detonatingfurnace");
		this.setCreativeTab(CreativeTabs.MISC);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityDetonatingFurnace();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		TileEntityDetonatingFurnace furnace = (TileEntityDetonatingFurnace)worldIn.getTileEntity(pos);

		if (furnace.isBurning) {
			return false;
		}

		if (!playerIn.isSneaking()) {
			if (playerIn.getHeldItemMainhand().getItem().getClass().equals(ItemFlintAndSteel.class)) {
				furnace.ignite(playerIn);
			}
			IItemHandler handler = furnace.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
			ItemStack heldItem = playerIn.getHeldItemMainhand();
			for (int i = 0; i < handler.getSlots(); i++) {	
				if (heldItem.isEmpty()) {
					break;
				}
				heldItem = handler.insertItem(i, heldItem, false);
			}
			playerIn.setHeldItem(EnumHand.MAIN_HAND, heldItem);
		}
		else
		{
			IItemHandler handler = furnace.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
			ItemStack fuel = handler.getStackInSlot(0);
			playerIn.sendMessage(new TextComponentString("Fuel [" + fuel.getDisplayName() + "x" + fuel.getCount() + "]"));
			handler = furnace.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
			String message = "Smeltables [";
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack item = handler.getStackInSlot(i);
				message += item.getDisplayName() + "x" + item.getCount() + ", ";
			}
			message = message.substring(0, message.length() - 2) + "]";
			playerIn.sendMessage(new TextComponentString(message));
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityDetonatingFurnace furnace = (TileEntityDetonatingFurnace)worldIn.getTileEntity(pos);
		IItemHandler fuel = furnace.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		IItemHandler smeltables = furnace.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		for (int i = 0; i < fuel.getSlots(); i++) {
			EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, fuel.extractItem(i, 64, true));

			// Apply some random motion to the item
			float multiplier = 0.1f;
			float motionX = worldIn.rand.nextFloat() - 0.5f;
			float motionY = worldIn.rand.nextFloat() - 0.5f;
			float motionZ = worldIn.rand.nextFloat() - 0.5f;

			item.motionX = motionX * multiplier;
			item.motionY = motionY * multiplier;
			item.motionZ = motionZ * multiplier;

			// Spawn the item in the world
			worldIn.spawnEntity(item);
		}
		for (int i = 0; i < smeltables.getSlots(); i++) {
			EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, smeltables.extractItem(i, 64, true));

			// Apply some random motion to the item
			float multiplier = 0.1f;
			float motionX = worldIn.rand.nextFloat() - 0.5f;
			float motionY = worldIn.rand.nextFloat() - 0.5f;
			float motionZ = worldIn.rand.nextFloat() - 0.5f;

			item.motionX = motionX * multiplier;
			item.motionY = motionY * multiplier;
			item.motionZ = motionZ * multiplier;

			// Spawn the item in the world
			worldIn.spawnEntity(item);
		}
	}

    @Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}