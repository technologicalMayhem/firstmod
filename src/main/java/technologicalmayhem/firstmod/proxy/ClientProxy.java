package technologicalmayhem.firstmod.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import technologicalmayhem.firstmod.ModBlocks;
import technologicalmayhem.firstmod.ModItems;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.initModels();
        ModItems.initModels();

        ModelLoader.setCustomModelResourceLocation(new ItemBlock(ModBlocks.laserBase), 0, new ModelResourceLocation(ModBlocks.laserBase.getRegistryName(), "inventory"));
    }
}