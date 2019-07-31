package technologicalmayhem.firstmod.util;

public enum EnumFurnaceIgnitionResult {
    SUCCESS(""),
    FUELMISSING("Not enough fuel."),
    FUELWARNIGN("You have more fuel than required! Right click again to ignite anyway.");

    public String message = "";

    EnumFurnaceIgnitionResult(String message) {
        this.message = message;
    }
}
