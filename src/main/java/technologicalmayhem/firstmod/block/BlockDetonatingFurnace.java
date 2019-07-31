package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.FirstMod;
import technologicalmayhem.firstmod.block.tile.TileDetonatingFurnace;
import technologicalmayhem.firstmod.util.EnumFurnaceIgnitionResult;
import technologicalmayhem.firstmod.util.EnumFurnacePhase;

import javax.annotation.Nullable;

public class BlockDetonatingFurnace extends Block {

    public static final PropertyEnum<EnumFurnacePhase> FURNACE_STATE = PropertyEnum.create("furnacestate", EnumFurnacePhase.class);

    public BlockDetonatingFurnace() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".detonatingfurnace");
        setRegistryName("detonatingfurnace");
        setCreativeTab(CreativeTabs.DECORATIONS);
        this.setDefaultState(getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH).withProperty(FURNACE_STATE, EnumFurnacePhase.INACTIVE));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileDetonatingFurnace te = ((TileDetonatingFurnace) worldIn.getTileEntity(pos));
        if (!worldIn.isRemote && te.phase == EnumFurnacePhase.INACTIVE) {
            if (playerIn.getHeldItemMainhand().getItem().getClass().equals(ItemFlintAndSteel.class)) {
                EnumFurnaceIgnitionResult result = te.ignite();
                if (result == EnumFurnaceIgnitionResult.SUCCESS) {
                    playerIn.getHeldItemMainhand().damageItem(1, playerIn);
                } else {
                    playerIn.sendMessage(new TextComponentString(result.message));
                }
            } else {
                ItemStack stack = playerIn.getHeldItemMainhand();
                int size = stack.getCount();
                if (facing == EnumFacing.UP) {
                    stack = te.insertFuel(stack);
                } else {
                    stack = te.insertSmeltableItem(stack);
                }
                if (size != stack.getCount()) {
                    playerIn.setHeldItem(EnumHand.MAIN_HAND, stack);
                }
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileDetonatingFurnace te = ((TileDetonatingFurnace) worldIn.getTileEntity(pos));
            NonNullList<ItemStack> items = NonNullList.create();
            if (te.isDone) {
                items = te.getSmeltingResults();
            } else {
                for (int i = 0; i < te.items.getSlots(); i++) {
                    items.add(te.items.extractItem(i, 64, true));
                }
            }
            for (int i = 0; i < items.size(); i++) {
                EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, items.get(i));

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
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileDetonatingFurnace te = ((TileDetonatingFurnace) world.getTileEntity(pos));
        if (te != null) {
            return te.phase == EnumFurnacePhase.INACTIVE ? 0 : 13;
        } else return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockHorizontal.FACING, FURNACE_STATE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileDetonatingFurnace te = ((TileDetonatingFurnace) worldIn.getTileEntity(pos));
        return state.withProperty(FURNACE_STATE, te.phase);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDetonatingFurnace();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
