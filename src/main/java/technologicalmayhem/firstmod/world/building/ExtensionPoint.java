/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world.building;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ExtensionPoint {
    private final BlockPos offset;
    private final EnumFacing facing;

    public ExtensionPoint(int x, int y, int z, EnumFacing facing) {
        this.offset = new BlockPos(x, y, z);
        this.facing = facing;
    }

    public BlockPos getOffset() {
        return offset;
    }

    public EnumFacing getFacing() {
        return facing;
    }
}
