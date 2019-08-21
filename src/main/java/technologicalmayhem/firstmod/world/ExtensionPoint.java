/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExtensionPoint) {
            ExtensionPoint e = ((ExtensionPoint) o);
            if (this.offset == e.getOffset() && this.facing == e.getFacing()) {
                return true;
            }
        }
        return false;
    }

    public Rotation getRotation() {
        switch (facing) {
            case WEST:
                return Rotation.CLOCKWISE_90;
            case NORTH:
                return Rotation.CLOCKWISE_180;
            case EAST:
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                return Rotation.NONE;
        }
    }

    public ExtensionPoint rotate(Rotation rotation) {
        BlockPos rotatedPos = offset.rotate(rotation);
        return new ExtensionPoint(rotatedPos.getX(), rotatedPos.getY(), rotatedPos.getZ(), rotation.rotate(facing));
    }
}
