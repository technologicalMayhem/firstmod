package technologicalmayhem.firstmod;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.item.ItemTurretHead;
import technologicalmayhem.firstmod.item.ItemWeirdShard;

public class ModItems {
    @GameRegistry.ObjectHolder("firstmod:weirdshard")
    public static ItemWeirdShard weirdShard;
    @GameRegistry.ObjectHolder("firstmod:turrethead")
    public static ItemTurretHead turretHead;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        weirdShard.initModel();
        turretHead.initModel();
    }
}