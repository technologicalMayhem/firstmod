package technologicalmayhem.firstmod.util;

import net.minecraft.util.IStringSerializable;

public enum EnumFurnacePhase implements IStringSerializable {
    INACTIVE(0f),
    ACTIVE(0f),
    PHASE1(0.5f),
    PHASE2(0.8f),
    PHASE3(1f);

    public Float percentage = 0f;

    EnumFurnacePhase(Float percentage) {
        this.percentage = percentage;
    }

    public EnumFurnacePhase getNextPhase() {
        switch (this) {
            case INACTIVE:
                return ACTIVE;
            case ACTIVE:
                return PHASE1;
            case PHASE1:
                return PHASE2;
            case PHASE2:
            case PHASE3:
                return PHASE3;
            default:
                return INACTIVE;
        }
    }

    @Override
    public String getName() {
        return this.name();
    }
}