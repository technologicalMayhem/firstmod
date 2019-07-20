package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.FirstMod;

import javax.annotation.Nullable;

public class BlockPotentiometer extends Block {

    private static final IProperty<Integer> power = PropertyInteger.create("power", 0, 15);

    public BlockPotentiometer() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".potentiometer");
        setRegistryName("potentiometer");
        setHardness(1.0f);
        this.setCreativeTab(CreativeTabs.REDSTONE);
        setLightLevel(0);
        setDefaultState(this.blockState.getBaseState().withProperty(power, 0));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, power);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(power);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return blockState.getBaseState().withProperty(power, meta);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(power);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking() && state.getValue(power) > 0) {
            worldIn.setBlockState(pos, blockState.getBaseState().withProperty(power, state.getValue(power) - 1));
        }
        if (!playerIn.isSneaking() && state.getValue(power) < 15) {
            worldIn.setBlockState(pos, blockState.getBaseState().withProperty(power, state.getValue(power) + 1));
        }
        return true;
    }
}
