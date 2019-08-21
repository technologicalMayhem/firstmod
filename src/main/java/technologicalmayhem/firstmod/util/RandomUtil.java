/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.util;

import technologicalmayhem.firstmod.util.enums.IWeightable;

public class RandomUtil {

    public static IWeightable weightedRandomness(IWeightable[] weightables) {

        double totalWeight = 0.0d;
        for (IWeightable i : weightables) {
            totalWeight += i.getWeight();
        }

        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < weightables.length; ++i) {
            random -= weightables[i].getWeight();
            if (random <= 0.0d) {
                randomIndex = i;
                break;
            }
        }
        return weightables[randomIndex];
    }
}
