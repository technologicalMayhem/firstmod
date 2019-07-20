package technologicalmayhem.firstmod;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.item.ItemWeirdShard;

public class ModItems
{
    @GameRegistry.ObjectHolder("firstmod:weirdshard")
    public static ItemWeirdShard weirdShard;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        weirdShard.initModel();
    }
}