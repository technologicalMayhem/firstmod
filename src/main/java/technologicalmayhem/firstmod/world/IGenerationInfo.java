/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.world;

import net.minecraft.world.World;

import java.util.function.Predicate;

public interface IGenerationInfo {
    int sizeX = 0;
    int sizeY = 0;
    int sizeZ = 0;
    String structureName = "";
    ExtensionPoint[] extensionPoints = new ExtensionPoint[0];
    Predicate<World> canGenerate = world -> true;
}
