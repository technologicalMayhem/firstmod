package technologicalmayhem.firstmod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import technologicalmayhem.firstmod.block.tile.TileDetonatingFurnace;
import technologicalmayhem.firstmod.block.tile.TileLaserDefense;

@Mod(modid = FirstMod.MODID, name = FirstMod.NAME, version = FirstMod.VERSION)
public class FirstMod {
    public static final String MODID = "firstmod";
    public static final String NAME = "First Mod";
    public static final String VERSION = "1.0";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        GameRegistry.registerTileEntity(TileDetonatingFurnace.class, new ResourceLocation(MODID, "tileDetonatingFurnace"));
        GameRegistry.registerTileEntity(TileLaserDefense.class, new ResourceLocation(MODID, "tileLaserDefense"));
    }
}
