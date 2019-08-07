package technologicalmayhem.firstmod.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import technologicalmayhem.firstmod.ModBlocks;
import technologicalmayhem.firstmod.block.BlockDetonatingFurnace;
import technologicalmayhem.firstmod.block.BlockLaserDefense;
import technologicalmayhem.firstmod.block.BlockLaserEnergyCollector;
import technologicalmayhem.firstmod.block.BlockPotentiometer;
import technologicalmayhem.firstmod.item.ItemWeirdShard;

@Mod.EventBusSubscriber
public class CommonProxy {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockDetonatingFurnace(),
                new BlockPotentiometer(),
                new BlockLaserDefense(),
                new BlockLaserEnergyCollector()
        );
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemWeirdShard());

        event.getRegistry().register(new ItemBlock(ModBlocks.detonatingFurnace).setRegistryName(ModBlocks.detonatingFurnace.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.potentiometer).setRegistryName(ModBlocks.potentiometer.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.laserDefense).setRegistryName(ModBlocks.laserDefense.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.laserEnergyCollector).setRegistryName(ModBlocks.laserEnergyCollector.getRegistryName()));
    }
}