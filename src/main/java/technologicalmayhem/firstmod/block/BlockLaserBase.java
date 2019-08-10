package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.FirstMod;
import technologicalmayhem.firstmod.ModBlocks;

public class BlockLaserBase extends Block {

    public static final PropertyBool MIDDLE = PropertyBool.create("middle");
    protected static final AxisAlignedBB LASER_BASE_MIDDLE_AABB = new AxisAlignedBB(0.0625D, 0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

    public BlockLaserBase() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".laserbase");
        setRegistryName("laserbase");
        setHardness(2.0f);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(getDefaultState().withProperty(MIDDLE, false));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(1), state.withProperty(MIDDLE, true));
        worldIn.setBlockState(pos.up(2), ModBlocks.laserTurret.getDefaultState());
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(MIDDLE)) {
            if (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockLaserBase)
                worldIn.setBlockToAir(pos.down());
            if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockLaserTurret) worldIn.setBlockToAir(pos.up());
        } else {
            if (worldIn.getBlockState(pos.up(1)).getBlock() instanceof BlockLaserBase) worldIn.setBlockToAir(pos.up(1));
            if (worldIn.getBlockState(pos.up(2)).getBlock() instanceof BlockLaserTurret)
                worldIn.setBlockToAir(pos.up(2));
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(MIDDLE) ? 1 : 0;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta == 1 ? getDefaultState().withProperty(MIDDLE, true) : getDefaultState();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MIDDLE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return !state.getValue(MIDDLE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return !state.getValue(MIDDLE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(MIDDLE) ? LASER_BASE_MIDDLE_AABB : FULL_BLOCK_AABB;
    }
}
