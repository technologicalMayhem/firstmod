/*
 * This class was created by technologicalMayhem and is distributed as part of firstmod.
 * Get the full source code here:
 * http://github.com/technologicalMayhem/firstmod/
 *
 * firstmod is Open Source and distributed under the MIT License.
 */

package technologicalmayhem.firstmod.util;

public class Pair<T, U> {

    private T A;
    private U B;

    public Pair(T A, U B) {
        this.A = A;
        this.B = B;
    }

    public U getB() {
        return B;
    }

    public T getA() {
        return A;
    }
}
