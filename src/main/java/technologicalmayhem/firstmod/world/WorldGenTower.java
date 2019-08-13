/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import technologicalmayhem.firstmod.util.WorldUtil;

public class WorldGenTower {

    public static void generate(World worldIn, BlockPos position) {
        WorldUtil.generateStructure(worldIn, position, "test", Mirror.NONE, Rotation.NONE);
        BlockPos size = WorldUtil.getStructureDimensions(worldIn, "test").add(-1, -1, -1);
        for (BlockPos pos : WorldUtil.findAllBlocksInArea(worldIn, Blocks.COBBLESTONE, position, position.add(size))) {
            if (worldIn.rand.nextInt(4) == 0) worldIn.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3);
        }
    }
}
