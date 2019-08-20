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
import technologicalmayhem.firstmod.util.WorldUtil;
import technologicalmayhem.firstmod.world.GenerationAnchor;

import java.util.ArrayList;
import java.util.Arrays;

public class WorldGenBuilding {

    public static void generate(World worldIn, BlockPos position) {
        ArrayList<GenerationAnchor> anchors = new ArrayList<>();
        anchors.add(new GenerationAnchor(position, EnumFacing.SOUTH));

        //For debugging purposes
        EnumBuildingPiece[] pieces = new EnumBuildingPiece[]{
                EnumBuildingPiece.HALLWAY,
                EnumBuildingPiece.HALLWAY,
                EnumBuildingPiece.HALLWAY,
                EnumBuildingPiece.HALLWAY,
                EnumBuildingPiece.ROOM_3WAY,
                EnumBuildingPiece.HALLWAY,
        };
        ArrayList<EnumBuildingPiece> debugPieces = new ArrayList<>(Arrays.asList(pieces));
        int steps = 25;

        while (!debugPieces.isEmpty()) {
            GenerationAnchor anchor = anchors.remove(0);
            EnumBuildingPiece piece = debugPieces.remove(0); //getNextPiece();
//            int random = 1;
            int random = (int) Math.round(worldIn.rand.nextDouble() * (piece.getExtensionPoints().length - 1));
            ExtensionPoint point = piece.getExtensionPoints()[random];
            BlockPos offset = new BlockPos(-piece.getOffsets()[random], 0, 0);
            BlockPos curPos = anchor.getPos();
            BlockPos size = WorldUtil.getStructureDimensions(worldIn, piece.getTemplateName()).add(-1, -1, -1);
            BlockPos center = new BlockPos(size.getX() / 2, size.getY() / 2, size.getZ() / 2);

            WorldUtil.generateStructure(worldIn, curPos.add(offset), piece.getTemplateName(), Mirror.NONE, point.getRotation());

            for (ExtensionPoint e : piece.getExtensionPoints()) {
                if (!point.equals(e)) {
                    BlockPos offsetRot = WorldUtil.rotateAroundCenter(e.getOffset(), center, point.getRotation().add(Rotation.CLOCKWISE_180));
                    BlockPos pos = curPos.add(offsetRot).add(offset);
//                    worldIn.setBlockState(pos, Blocks.WOOL.getDefaultState(), 2);
                    anchors.add(anchor.createChild(pos, anchor.getFacing()));
                }
            }
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
