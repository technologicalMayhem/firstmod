/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world.building;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public enum EnumBuildingPiece {
    HALLWAY("building_hallway", Rotation.NONE, new BlockPos[]{new BlockPos(0, 0, 5)}, 30, new BlockPos(0, 0, 0)),
    CORNER("building_corner", Rotation.CLOCKWISE_90, new BlockPos[]{new BlockPos(-3, 0, 2)}, 5, new BlockPos(-2, 0, 0)),
    CORNER_MIRRORED("building_corner", Rotation.CLOCKWISE_90, new BlockPos[]{new BlockPos(-3, 0, 2)}, 5, new BlockPos(-2, 0, 0));

    private String templateName;
    private BlockPos offset;
    private Rotation rotation;
    private BlockPos[] extensionPoints;
    private int weight;

    EnumBuildingPiece(String templateName, Rotation rotation, BlockPos[] extensionPoints, int weight, BlockPos offset) {
        this.templateName = templateName;
        this.offset = offset;
        this.rotation = rotation;
        this.extensionPoints = extensionPoints;
        this.weight = weight;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public BlockPos[] getExtensionPoints() {
        return extensionPoints;
    }

    public int getWeight() {
        return weight;
    }

    public BlockPos getOffset() {
        return offset;
    }
}
