/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class EntityDistanceComparator implements Comparator<Entity> {

    private final BlockPos pos;

    public EntityDistanceComparator(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public int compare(Entity a, Entity b) {
        double aDist = a.getDistance(pos.getX(), pos.getY(), pos.getZ());
        double bDist = b.getDistance(pos.getX(), pos.getY(), pos.getZ());
        return Double.compare(aDist, bDist);
    }
}
