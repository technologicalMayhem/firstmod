/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import technologicalmayhem.firstmod.FirstMod;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class WorldUtil {

    public static void generateStructure(World worldIn, BlockPos pos, String structureName, Mirror mirrorIn, Rotation rotationIn) {
        MinecraftServer minecraftServer = worldIn.getMinecraftServer();
        TemplateManager templateManager = ((WorldServer) worldIn).getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(FirstMod.MODID, structureName);
        Template template = templateManager.get(minecraftServer, resourceLocation);
        BlockPos dimensions = getStructureDimensions(worldIn, structureName).add(-1, -1, -1);
        BlockPos offset = new BlockPos(0, 0, 0);

        switch (rotationIn) {
            case CLOCKWISE_90:
                offset = new BlockPos(dimensions.getX(), 0, 0);
                break;
            case CLOCKWISE_180:
                offset = new BlockPos(dimensions.getX(), 0, dimensions.getZ());
                break;
            case COUNTERCLOCKWISE_90:
                offset = new BlockPos(0, 0, dimensions.getZ());
                break;
        }

        if (template != null) {
            PlacementSettings placementsettings = (new PlacementSettings()).setMirror(mirrorIn)
                    .setRotation(rotationIn).setIgnoreEntities(false).setChunk(null)
                    .setIgnoreStructureBlock(false);
            template.addBlocksToWorldChunk(worldIn, pos.add(offset), placementsettings);

            for (BlockPos b : BlockPos.getAllInBox(pos.add(offset), pos.add(offset).add(getStructureDimensions(worldIn, structureName)))) {
                IBlockState iblockstate = worldIn.getBlockState(b);
                worldIn.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
            }
        }
    }

    @Nullable
    public static BlockPos getStructureDimensions(World worldIn, String structureName) {
        MinecraftServer minecraftServer = worldIn.getMinecraftServer();
        TemplateManager templateManager = ((WorldServer) worldIn).getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(FirstMod.MODID, structureName);
        Template template = templateManager.get(minecraftServer, resourceLocation);

        if (template != null) {
            return template.getSize();
        }

        return null;
    }

    public static BlockPos rotateRoomPlacementPoint(World worldIn, BlockPos pos, String structureName, Rotation rotation) {
        BlockPos roomOffset = getStructureDimensions(worldIn, structureName).add(-1, -1, -1);

        switch (rotation) {
            case CLOCKWISE_90:
                return pos.add(roomOffset.getX(), 0, 0);
            case CLOCKWISE_180:
                return pos.add(roomOffset.getX(), 0, roomOffset.getZ());
            case COUNTERCLOCKWISE_90:
                return pos.add(0, 0, roomOffset.getZ());
            default:
                return pos;
        }
    }

    public static ArrayList<BlockPos> findAllBlocksInArea(World worldIn, Block blockIn, BlockPos pos1, BlockPos pos2) {
        ArrayList<BlockPos> positions = new ArrayList<>();

        Pair<BlockPos, BlockPos> result = getEdges(pos1, pos2);
        BlockPos p1 = result.getA();
        BlockPos p2 = result.getB();

        for (int x = p1.getX(); x < p2.getX(); x++) {
            for (int y = p1.getY(); y < p2.getY() || y < 0 || y > 256; y++) {
                for (int z = p1.getZ(); z < p2.getZ(); z++) {
                    IBlockState block = worldIn.getBlockState(new BlockPos(x, y, z));
                    if (block.getBlock().getClass() == blockIn.getClass()) positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }

    public static BlockPos rotateAroundCenter(BlockPos point, Vector3d center, Rotation rotation) {
        int rotationDeg = 0;
        switch (rotation) {
            case CLOCKWISE_90:
                rotationDeg = 90;
                break;
            case CLOCKWISE_180:
                rotationDeg = 180;
                break;
            case COUNTERCLOCKWISE_90:
                rotationDeg = 270;
                break;
            default:
                return point;
        }
        double angle = rotationDeg * (Math.PI / 180);

        int rotatedX = (int) Math.round(Math.cos(angle) * (point.getX() - center.getX()) - Math.sin(angle) * (point.getZ() - center.getZ()) + center.getZ());
        int rotatedZ = (int) Math.round(Math.sin(angle) * (point.getX() - center.getX()) + Math.cos(angle) * (point.getZ() - center.getZ()) + center.getZ());

        return new BlockPos(rotatedX, 0, rotatedZ);
    }

    public static BlockPos negateBlockPos(BlockPos blockPos) {
        return new BlockPos(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
    }

    public static Pair<BlockPos, BlockPos> getEdges(BlockPos pos1, BlockPos pos2) {
        int x1;
        int x2;
        int y1;
        int y2;
        int z1;
        int z2;

        if (pos1.getX() < pos2.getX()) {
            x1 = pos1.getX();
            x2 = pos2.getX();
        } else {
            x1 = pos2.getX();
            x2 = pos1.getX();
        }
        if (pos1.getY() < pos2.getY()) {
            y1 = pos1.getY();
            y2 = pos2.getY();
        } else {
            y1 = pos2.getY();
            y2 = pos1.getY();
        }
        if (pos1.getZ() < pos2.getZ()) {
            z1 = pos1.getZ();
            z2 = pos2.getZ();
        } else {
            z1 = pos2.getZ();
            z2 = pos1.getZ();
        }

        BlockPos p1 = new BlockPos(x1, y1, z1);
        BlockPos p2 = new BlockPos(x2, y2, z2);
        return new Pair<>(p1, p2);
    }
}
