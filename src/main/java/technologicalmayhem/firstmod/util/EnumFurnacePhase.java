package technologicalmayhem.firstmod.util;

import net.minecraft.util.IStringSerializable;

public enum EnumFurnacePhase implements IStringSerializable {
    INACTIVE(1f),
    ACTIVE(1f),
    PHASE_1(0.5f),
    PHASE_2(0.2f),
    PHASE_3(0f);

    public Float percentage = 0f;

    EnumFurnacePhase(Float percentage) {
        this.percentage = percentage;
    }

    public EnumFurnacePhase getNextPhase() {
        switch (this) {
            case INACTIVE:
                return ACTIVE;
            case ACTIVE:
                return PHASE_1;
            case PHASE_1:
                return PHASE_2;
            case PHASE_2:
            case PHASE_3:
                return PHASE_3;
            default:
                return INACTIVE;
        }
    }

    @Override
    public String getName() {
        return this.name().toLowerCase();
    }
}