/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world.building;

import net.minecraft.util.Rotation;
import net.minecraft.world.World;
import technologicalmayhem.firstmod.util.RandomUtil;
import technologicalmayhem.firstmod.util.enums.IWeightable;
import technologicalmayhem.firstmod.world.ExtensionPoint;
import technologicalmayhem.firstmod.world.IGenerationInfo;

import java.util.function.Predicate;

public enum EnumBuildingPart implements IGenerationInfo, IWeightable {
    HALLWAY(5, 5, 5, "brick_hallway", new ExtensionPoint[]{
            new ExtensionPoint(2, 0, -1, Rotation.NONE),
            new ExtensionPoint(2, 0, 5, Rotation.CLOCKWISE_180)
    }, 20),
    CORNER(5, 5, 5, "brick_corner", new ExtensionPoint[]{
            new ExtensionPoint(2, 0, -1, Rotation.NONE),
            new ExtensionPoint(2, 0, 4, Rotation.COUNTERCLOCKWISE_90)
    }, 10),
    ROOM_3WAY(15, 5, 15, "brick_room_3way", new ExtensionPoint[]{
            new ExtensionPoint(4, 0, -1, Rotation.NONE),
            new ExtensionPoint(15, 0, 7, Rotation.COUNTERCLOCKWISE_90),
            new ExtensionPoint(10, 0, 15, Rotation.CLOCKWISE_180)
    }, 5);

    public int sizeX;
    public int sizeY;
    public int sizeZ;
    public String structureName;
    public ExtensionPoint[] extensionPoints;
    public Predicate<World> canGenerate = world -> true;

    int weight;

    EnumBuildingPart(int sizeX, int sizeY, int sizeZ, String structureName, ExtensionPoint[] extensionPoints, int weight) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.structureName = structureName;
        this.extensionPoints = extensionPoints;

        this.weight = weight;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public String getStructureName() {
        return structureName;
    }

    public ExtensionPoint[] getExtensionPoints() {
        return extensionPoints;
    }

    public int getWeight() {
        return weight;
    }

    public EnumBuildingPart getRandom() {
        return (EnumBuildingPart) RandomUtil.weightedRandomness(EnumBuildingPart.values());
    }
}
