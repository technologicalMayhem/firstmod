/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.util.enums;

public enum EnumFurnaceIgnitionResult {
    SUCCESS(""),
    FUELMISSING("Not enough fuel."),
    FUELWARNIGN("You have more fuel than required! Right click again to ignite anyway.");

    public String message = "";

    EnumFurnaceIgnitionResult(String message) {
        this.message = message;
    }
}
