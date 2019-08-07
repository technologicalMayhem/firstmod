package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.FirstMod;
import technologicalmayhem.firstmod.ModBlocks;

public class BlockLaserBase extends Block {

    public BlockLaserBase() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".laserbase");
        setRegistryName("laserbase");
        setHardness(2.0f);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(getDefaultState());
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(1), state);
        worldIn.setBlockState(pos.up(2), ModBlocks.laserTurret.getDefaultState());
    }
}
