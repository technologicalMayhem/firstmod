package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.FirstMod;
import technologicalmayhem.firstmod.ModBlocks;
import technologicalmayhem.firstmod.block.tile.TileLaserTurret;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockLaserTurret extends Block {

    protected static final AxisAlignedBB LASER_TURRET_AABB = new AxisAlignedBB(0.25D, 0D, 0.25D, 0.75D, 0.75, 0.75D);

    public BlockLaserTurret() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".laserturret");
        setRegistryName("laserturret");
        setHardness(2.0f);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(getDefaultState());
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.getBlockState(pos.down(1)).getBlock() instanceof BlockLaserBase) worldIn.setBlockToAir(pos.down(1));
        if (worldIn.getBlockState(pos.down(2)).getBlock() instanceof BlockLaserBase) worldIn.setBlockToAir(pos.down(2));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemBlock.getItemFromBlock(ModBlocks.laserBase);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileLaserTurret();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return LASER_TURRET_AABB;
    }
}
