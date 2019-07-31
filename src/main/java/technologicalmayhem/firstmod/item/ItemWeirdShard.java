package technologicalmayhem.firstmod.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.FirstMod;

public class ItemWeirdShard extends Item {

    public ItemWeirdShard() {
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.MISC);
        this.setUnlocalizedName(FirstMod.MODID + ".weirdshard");
        this.setRegistryName("weirdshard");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}