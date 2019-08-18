package technologicalmayhem.firstmod.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class GenerationAnchor {

    private BlockPos pos;
    private Rotation rotation;
    private int removedFromMaster;
    private boolean isMaster;
    private GenerationAnchor parent;

    public GenerationAnchor(BlockPos pos, Rotation rotation) {
        this.pos = pos;
        this.rotation = rotation;
        this.removedFromMaster = 0;
        this.isMaster = true;
        this.parent = null;
    }

    private GenerationAnchor(BlockPos pos, EnumFacing facing, GenerationAnchor parent) {
        this.pos = pos;
        this.rotation = getRotation();
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

    public BlockPos getPos() {
        return pos;
    }

    public Rotation getRotation() {
        return rotation;
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
