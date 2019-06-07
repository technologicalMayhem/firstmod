package technologicalmayhem.firstmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.FirstMod;

public class FirstBlock extends Block
{
    public FirstBlock()
    {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".firstblock");
        setRegistryName("firstblock");
        setHardness(1.0f);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}