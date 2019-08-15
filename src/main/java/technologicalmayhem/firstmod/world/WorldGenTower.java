/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import technologicalmayhem.firstmod.util.WorldUtil;
import technologicalmayhem.firstmod.world.building.EnumBuildingPiece;

public class WorldGenTower {

    public static void generate(World worldIn, BlockPos position) {
        Rotation currentRotation = Rotation.NONE;
        BlockPos currentPosition = position;


        for (int i = 0; i < 10; i++) {
            EnumBuildingPiece piece = getNextPiece();
            WorldUtil.generateStructure(worldIn, currentPosition.add(piece.getOffset().rotate(currentRotation)), piece.getTemplateName(), Mirror.NONE, currentRotation);

            currentPosition = currentPosition.add(piece.getExtensionPoints()[0].rotate(currentRotation));
            currentRotation = currentRotation.add(piece.getRotation());
        }
    }

    static EnumBuildingPiece getNextPiece() {
        EnumBuildingPiece[] items = EnumBuildingPiece.values();
        double totalWeight = 0.0d;
        for (EnumBuildingPiece i : items) {
            totalWeight += i.getWeight();
        }

        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < items.length; ++i) {
            random -= items[i].getWeight();
            if (random <= 0.0d) {
                randomIndex = i;
                break;
            }
        }
        return items[randomIndex];
    }
}
