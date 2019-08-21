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
import net.minecraft.world.World;
import technologicalmayhem.firstmod.world.GenerationAnchor;
import technologicalmayhem.firstmod.world.PartPlacer;

import java.util.ArrayList;
import java.util.Arrays;

public class WorldGenBuilding {

    public static void generate(World worldIn, BlockPos position) {
        ArrayList<GenerationAnchor> anchors = new ArrayList<>();
        anchors.add(new GenerationAnchor(position, EnumFacing.SOUTH));

        //For debugging purposes
        EnumBuildingPart[] parts = new EnumBuildingPart[]{
                EnumBuildingPart.HALLWAY,
                EnumBuildingPart.HALLWAY,
                EnumBuildingPart.HALLWAY,
                EnumBuildingPart.HALLWAY,
                EnumBuildingPart.ROOM_3WAY,
                EnumBuildingPart.HALLWAY,
        };
        ArrayList<EnumBuildingPart> debugParts = new ArrayList<>(Arrays.asList(parts));
        int steps = 25;

        while (!debugParts.isEmpty()) {
            GenerationAnchor anchor = anchors.remove(0);
            EnumBuildingPart part = debugParts.remove(0); //getNextPiece();

            anchors.addAll(Arrays.asList(new PartPlacer(part).generate(worldIn, anchor)));
        }
    }
}
