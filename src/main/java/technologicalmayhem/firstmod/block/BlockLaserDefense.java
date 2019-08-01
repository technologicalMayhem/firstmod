package technologicalmayhem.firstmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.FirstMod;

public class BlockLaserDefense extends Block {

    public BlockLaserDefense() {
        super(Material.ROCK);
        setUnlocalizedName(FirstMod.MODID + ".laserDefense");
        setRegistryName("laserDefense");
        setHardness(2.0f);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
