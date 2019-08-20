package technologicalmayhem.firstmod.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class GenerationAnchor {

    private BlockPos pos;
    private EnumFacing facing;
    private int removedFromMaster;
    private boolean isMaster;
    private GenerationAnchor parent;

    public GenerationAnchor(BlockPos pos, EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
        this.removedFromMaster = 0;
        this.isMaster = true;
        this.parent = null;
    }

    private GenerationAnchor(BlockPos pos, EnumFacing facing, GenerationAnchor parent) {
        this.pos = pos;
        this.facing = facing;
        this.isMaster = true;
        this.parent = null;

        int removed = 0;
        GenerationAnchor instance = this;

        while (!instance.isMaster()) {
            removed++;
            instance = instance.getParent();
        }

        this.removedFromMaster = removed;
    }

    public GenerationAnchor createChild(BlockPos pos, EnumFacing facing) {
        return new GenerationAnchor(pos, facing, this);
    }

    public Rotation getRotation() {
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

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public int getRemovedFromMaster() {
        return removedFromMaster;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public GenerationAnchor getParent() {
        return parent;
    }
}
