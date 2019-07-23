package technologicalmayhem.firstmod.util;

import net.minecraft.util.IStringSerializable;

public enum EnumFurnacePhase implements IStringSerializable {
    INACTIVE,
    ACTIVE,
    PHASE1,
    PHASE2,
    PHASE3;

    @Override
    public String getName() {
        return this.toString();
    }
}