package technologicalmayhem.firstmod;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.block.BlockDetonatingFurnace;
import technologicalmayhem.firstmod.block.BlockLaserDefense;
import technologicalmayhem.firstmod.block.BlockLaserEnergyCollector;
import technologicalmayhem.firstmod.block.BlockPotentiometer;

public class ModBlocks {

    @GameRegistry.ObjectHolder("firstmod:detonatingfurnace")
    public static BlockDetonatingFurnace detonatingFurnace;
    @GameRegistry.ObjectHolder("firstmod:potentiometer")
    public static BlockPotentiometer potentiometer;
    @GameRegistry.ObjectHolder("firstmod:laserDefense")
    public static BlockLaserDefense laserDefense;
    @GameRegistry.ObjectHolder("firstmod:laserenergycollector")
    public static BlockLaserEnergyCollector laserEnergyCollector;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        detonatingFurnace.initModel();
        potentiometer.initModel();
        laserDefense.initModel();
        laserEnergyCollector.initModel();
    }
}