/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world.building;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import technologicalmayhem.firstmod.util.Pair;
import technologicalmayhem.firstmod.util.WorldUtil;
import technologicalmayhem.firstmod.world.GenerationAnchor;

import java.util.ArrayList;
import java.util.Arrays;

public class WorldGenBuilding {

    public static void generate(World worldIn, BlockPos position) {
        ArrayList<GenerationAnchor> anchors = new ArrayList<>();
        anchors.add(new GenerationAnchor(position, EnumFacing.NORTH));

        //For debugging purposes
        EnumBuildingPiece[] pieces = new EnumBuildingPiece[]{
                EnumBuildingPiece.HALLWAY,
                EnumBuildingPiece.HALLWAY,
                EnumBuildingPiece.HALLWAY,
                EnumBuildingPiece.CORNER,
                EnumBuildingPiece.HALLWAY,
        };
        ArrayList<EnumBuildingPiece> debugPieces = new ArrayList<>(Arrays.asList(pieces));
        int steps = 25;

        while (!debugPieces.isEmpty()) {
            GenerationAnchor anchor = anchors.remove(0);
            EnumBuildingPiece piece = debugPieces.remove(0); //getNextPiece();

            ExtensionPoint point = piece.getExtensionPoints()[(int) Math.round(worldIn.rand.nextDouble() * (piece.getExtensionPoints().length - 1))];
            BlockPos curPos = anchor.getPos();
            EnumFacing curFace = anchor.getFacing();
            Rotation requiredRotation = facingToRotation(point.getFacing());
            ArrayList<Pair<BlockPos, EnumFacing>> extensionPoints = new ArrayList<>();

            WorldUtil.generateStructure(worldIn, curPos.add(piece.getWallOffset(worldIn, point)), piece.getTemplateName(), Mirror.NONE, requiredRotation);

            for (ExtensionPoint e : piece.getExtensionPoints(requiredRotation)) {
                if (!point.equals(e)) {
                    anchors.add(anchor.createChild(curPos.add(e.getOffset().rotate(requiredRotation)), requiredRotation.rotate(curFace)));
                }
            }
        }
    }

    static Rotation facingToRotation(EnumFacing facing) {
        switch (facing) {
            case SOUTH:
                return Rotation.NONE;
            case WEST:
                return Rotation.CLOCKWISE_90;
            case NORTH:
                return Rotation.CLOCKWISE_180;
            case EAST:
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                throw new IllegalArgumentException("We shouldn't get this far");
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
