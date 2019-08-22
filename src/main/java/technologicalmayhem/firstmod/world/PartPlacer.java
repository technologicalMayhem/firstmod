/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import technologicalmayhem.firstmod.util.WorldUtil;
import technologicalmayhem.firstmod.world.building.EnumBuildingPart;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class PartPlacer {
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private String structureName;
    private ArrayList<ExtensionPoint> extensionPoints;
    private Predicate<World> canGenerate;

    private ExtensionPoint selectedPoint;
    private BlockPos extensionPos;
    private BlockPos generationPos;
    private Rotation generationRot;

    public PartPlacer(EnumBuildingPart generationInfo) {
        this.sizeX = generationInfo.sizeX;
        this.sizeY = generationInfo.sizeY;
        this.sizeZ = generationInfo.sizeZ;
        this.structureName = generationInfo.structureName;
        this.extensionPoints = new ArrayList<>(Arrays.asList(generationInfo.extensionPoints));
        this.canGenerate = generationInfo.canGenerate;
    }

    public boolean canGenerate() {
        return true;
    }

    //TODO: This needs to be tested
    public GenerationAnchor[] generate(World world, GenerationAnchor anchor) {
        if (!canGenerate() || !canGenerate.test(world)) return null;
        selectedPoint = extensionPoints.remove(new Random().nextInt(extensionPoints.size()));
        generationPos = anchor.getPos();
        generationRot = anchor.getRotation();
        extensionPos = generationPos.add(WorldUtil.rotateAroundCenter(selectedPoint.getOffset(), getCenter(), selectedPoint.getRotation().add(generationRot)));
        //connect extensions points
        move(anchor.getPos().subtract(extensionPos));
        move(anchor.getFacing());
        WorldUtil.generateStructure(world, generationPos, structureName, Mirror.NONE, selectedPoint.getRotation().add(generationRot));

        world.setBlockState(generationPos, Blocks.WOOL.getStateFromMeta(2));
        world.setBlockState(extensionPos, Blocks.WOOL.getStateFromMeta(3));

        List<GenerationAnchor> anchorList = new ArrayList<>();
        for (ExtensionPoint e : extensionPoints) {
            anchorList.add(anchor.createChild(generationPos.add(WorldUtil.rotateAroundCenter(e.getOffset(), getCenter(), selectedPoint.getRotation())), generationRot.add(e.getRotation())));
        }

        return anchorList.toArray(new GenerationAnchor[0]);
    }

    private Vector3d getCenter() {
        return new Vector3d(sizeX / 2, sizeY / 2, sizeZ / 2);
    }


    public void move(BlockPos blockPos) {
        generationPos = generationPos.add(blockPos);
        extensionPos = extensionPos.add(blockPos);
    }

    public void move(EnumFacing facing) {
        generationPos = generationPos.offset(facing);
        extensionPos = extensionPos.offset(facing);
    }

}
