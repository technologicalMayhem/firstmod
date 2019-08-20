/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world.building;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import technologicalmayhem.firstmod.util.WorldUtil;

import java.util.ArrayList;

public enum EnumBuildingPiece {
    HALLWAY("brick_hallway", new int[]{0, 0}, new ExtensionPoint[]{
            new ExtensionPoint(4, 0, -1, EnumFacing.NORTH),
            new ExtensionPoint(0, 0, 5, EnumFacing.SOUTH)
    }, 20),
    CORNER("brick_corner", new int[]{0, 0}, new ExtensionPoint[]{
            new ExtensionPoint(4, 0, -1, EnumFacing.NORTH),
            new ExtensionPoint(5, 0, 4, EnumFacing.EAST)
    }, 10),
    ROOM_3WAY("brick_room_3way", new int[]{2, 5, 2}, new ExtensionPoint[]{
            new ExtensionPoint(6, 0, -1, EnumFacing.NORTH),
            new ExtensionPoint(15, 0, 9, EnumFacing.EAST),
            new ExtensionPoint(8, 0, 15, EnumFacing.SOUTH)
    }, 5);

    private String templateName;
    private int[] offsets;
    private ExtensionPoint[] extensionPoints;
    private int weight;

    EnumBuildingPiece(String templateName, int[] offsets, ExtensionPoint[] extensionPoints, int weight) {
        this.templateName = templateName;
        this.offsets = offsets;
        this.extensionPoints = extensionPoints;
        this.weight = weight;
    }

    public String getTemplateName() {
        return templateName;
    }

    public ExtensionPoint[] getExtensionPoints() {
        return extensionPoints;
    }

    public ExtensionPoint[] getExtensionPoints(Rotation rotation) {
        ArrayList<ExtensionPoint> rotatedPoints = new ArrayList<>();
        for (ExtensionPoint p : extensionPoints) {
            rotatedPoints.add(p.rotate(rotation));
        }
        return rotatedPoints.toArray(new ExtensionPoint[rotatedPoints.size() - 1]);
    }

    public BlockPos getCenter(World worldIn) {
        BlockPos size = WorldUtil.getStructureDimensions(worldIn, templateName).add(-1, -1, -1);
        return new BlockPos(size.getX() / 2, size.getY() / 2, size.getZ() / 2);
    }

    public BlockPos getWallOffset(World worldIn, ExtensionPoint extensionPoint) {
        BlockPos size = WorldUtil.getStructureDimensions(worldIn, templateName);
        BlockPos pos;
        switch (extensionPoint.getFacing()) {
            case SOUTH:
                pos = new BlockPos(size.getX(), 0, size.getY());
                break;
            case WEST:
                pos = new BlockPos(0, 0, size.getZ());
            case EAST:
                pos = new BlockPos(size.getX(), 0, 0);
            default:
                pos = new BlockPos(0, 0, 0);
        }
        return new BlockPos(extensionPoint.getOffset().add(WorldUtil.negateBlockPos(pos)));
    }

    public int getWeight() {
        return weight;
    }

    public int[] getOffsets() {
        return offsets;
    }
}
