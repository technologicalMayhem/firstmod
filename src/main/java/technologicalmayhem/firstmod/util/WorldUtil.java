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

package technologicalmayhem.firstmod.util;

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
import java.util.function.Consumer;

public class WorldUtil {

    public static void generateStructure(World worldIn, BlockPos pos, String structureName, Mirror mirrorIn, Rotation rotationIn) {
        MinecraftServer minecraftServer = worldIn.getMinecraftServer();
        TemplateManager templateManager = ((WorldServer) worldIn).getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(FirstMod.MODID, structureName);
        Template template = templateManager.get(minecraftServer, resourceLocation);

        if (template != null) {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            worldIn.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
            PlacementSettings placementsettings = (new PlacementSettings()).setMirror(mirrorIn)
                    .setRotation(rotationIn).setIgnoreEntities(false).setChunk(null)
                    .setIgnoreStructureBlock(false);

            template.addBlocksToWorldChunk(worldIn, pos, placementsettings);
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
            y1 = pos1.getX();
            y2 = pos2.getX();
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

    public static void forEachBlockInArea(BlockPos pos1, BlockPos pos2, Consumer<BlockPos> consumer) {
        final Pair<BlockPos, BlockPos> edges = getEdges(pos1, pos2);

        for (int x = edges.A.getX(); x < edges.B.getX(); x++) {
            for (int y = edges.A.getY(); y < edges.B.getY(); y++) {
                for (int z = edges.A.getZ(); z < edges.B.getZ(); z++) {
                    consumer.accept(new BlockPos(x, y, z));
                }
            }
        }
    }
}
