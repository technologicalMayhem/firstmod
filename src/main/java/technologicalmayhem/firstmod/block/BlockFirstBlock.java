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

public class BlockFirstBlock extends Block {
    public BlockFirstBlock() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".firstblock");
        setRegistryName("firstblock");
        setHardness(1.0f);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (placer.isSneaking()) {
            if (pos.getY() != 256) {
                for (int i = 0; i < 256; i++) {
                    worldIn.setBlockState(pos.up(i), ModBlocks.firstBlock.getDefaultState());
                }
            }
        }
    }
}