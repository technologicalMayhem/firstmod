package technologicalmayhem.firstmod.proxy;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import technologicalmayhem.firstmod.FirstMod;
import net.minecraftforge.fml.relauncher.Side;
import technologicalmayhem.firstmod.ModBlocks;
import technologicalmayhem.firstmod.ModItems;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        FirstMod.logger.info("Loading Models");
        ModBlocks.initModels();
        ModItems.initModels();
    }
}