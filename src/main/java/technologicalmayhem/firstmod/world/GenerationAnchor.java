package technologicalmayhem.firstmod.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class GenerationAnchor {

    private BlockPos pos;
    private EnumFacing facing;
    private int removedFromMaster;
    private boolean isMaster;
    private GenerationAnchor parent;

    public GenerationAnchor(BlockPos pos, EnumFacing facing) {
        this.setPos(pos);
        this.setFacing(facing);
        this.setRemovedFromMaster(0);
        this.setMaster(true);
        this.setParent(null);
    }

    private GenerationAnchor(BlockPos pos, EnumFacing facing, GenerationAnchor parent) {
        this.setPos(pos);
        this.setFacing(facing);
        this.setParent(parent);
        this.setMaster(false);

        int removed = 0;
        GenerationAnchor instance = this;

        while (!instance.isMaster()) {
            removed++;
            instance = instance.getParent();
        }

        this.setRemovedFromMaster(removed);
    }

    public GenerationAnchor createChild(BlockPos pos, EnumFacing facing) {
        return new GenerationAnchor(pos, facing, this);
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public int getRemovedFromMaster() {
        return removedFromMaster;
    }

    public void setRemovedFromMaster(int removedFromMaster) {
        this.removedFromMaster = removedFromMaster;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public GenerationAnchor getParent() {
        return parent;
    }

    public void setParent(GenerationAnchor parent) {
        this.parent = parent;
    }
}
