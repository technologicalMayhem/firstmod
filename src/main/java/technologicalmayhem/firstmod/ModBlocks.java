package technologicalmayhem.firstmod;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import technologicalmayhem.firstmod.block.BlockDetonatingFurnace;
import technologicalmayhem.firstmod.block.BlockFirstBlock;

public class ModBlocks
{
    @GameRegistry.ObjectHolder("firstmod:firstblock")
    public static BlockFirstBlock firstBlock;

    @GameRegistry.ObjectHolder("firstmod:detonatingfurnace")
    public static BlockDetonatingFurnace detonatingFurnace;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        firstBlock.initModel();
        detonatingFurnace.initModel();
    }
}