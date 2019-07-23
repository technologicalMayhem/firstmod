package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import technologicalmayhem.firstmod.FirstMod;
import technologicalmayhem.firstmod.block.tile.TileDetonatingFurnace;
import technologicalmayhem.firstmod.util.EnumFurnacePhase;

import javax.annotation.Nullable;

public class BlockDetonatingFurnace extends Block {

    private static final PropertyEnum<EnumFurnacePhase> FURNACESTATE = PropertyEnum.create("furnaceState", EnumFurnacePhase.class);

    public BlockDetonatingFurnace() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".detonatingfurnace");
        setRegistryName("detonatingfurnace");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
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

}
