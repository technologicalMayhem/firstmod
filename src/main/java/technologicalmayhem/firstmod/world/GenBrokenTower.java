/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.IWorldGenerator;
import technologicalmayhem.firstmod.FirstMod;
import technologicalmayhem.firstmod.util.WorldUtil;

import java.util.Random;

public class GenBrokenTower extends WorldGenerator implements IWorldGenerator {

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        return false;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (random.nextInt(200) != 0) return;

        final int x = chunkX * 16 + random.nextInt(16) - 16;
        final int z = chunkZ * 16 + random.nextInt(16) - 16;
        final int y = world.getHeight(x, z);
        final BlockPos position = new BlockPos(x, y, z);
        final String structureName = "brokentower";

        final BlockPos pos = position.down(2);
        final Mirror mirror = Mirror.values()[random.nextInt(Mirror.values().length)];
        final Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
        final BlockPos cornerOffset = new BlockPos(9, 18, 9);
        final BlockPos corner = pos.add(cornerOffset.rotate(rotation)).add(-1, -1, -1);

        WorldUtil.generateStructure(world, position.down(2), structureName, mirror, rotation);
        WorldUtil.forEachBlockInArea(pos, corner, blockPos -> {
            Block block = world.getBlockState(blockPos).getBlock();
            if (block instanceof BlockStoneBrick && random.nextInt(30) == 0) {
                switch (random.nextInt(3)) {
                    case 0:
                        world.setBlockState(blockPos, Blocks.STONEBRICK.getStateFromMeta(1));
                        break;
                    case 1:
                        world.setBlockState(blockPos, Blocks.STONEBRICK.getStateFromMeta(2));
                        break;
                    case 2:
                        world.setBlockToAir(blockPos);
                        break;
                }
            }
            if (block instanceof BlockChest) {
                TileEntityChest chest = (TileEntityChest) world.getTileEntity(blockPos);
                chest.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, random.nextLong());
            }
        });
        FirstMod.logger.info("Generated structure at: " + x + ", " + y + ", " + z);
    }
}
