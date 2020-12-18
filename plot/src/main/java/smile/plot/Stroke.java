/*
 * Copyright (c) 2010-2020 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 */

package smile.plot;

import java.io.Serializable;

/**
 * The decorated outlines of graphics primitives.
 *
 * @author Haifeng Li
 */
public class Stroke implements Serializable {
    private static final long serialVersionUID = 2L;

    /** The width of stroke. */
    public final float width;
    /** The dashing pattern. */
    public final float[] dash;
    /** The offset to start the dashing pattern. */
    public final float dashPhase;

    /**
     * Constructs a new <code>Stroke</code> with the specified
     * attributes.
     * @param width the width of this <code>BStroke</code>. The
     *         width must be greater than or equal to 0.0f. If width is
     *         set to 0.0f, the stroke is rendered as the thinnest
     *         possible line for the target device and the antialias
     *         hint setting.
     * @param dash the array representing the dashing pattern.
     * @param dashPhase the offset to start the dashing pattern.
     * @throws IllegalArgumentException if <code>width</code> is negative
     * @throws IllegalArgumentException if <code>dashPhase</code>
     *         is negative and <code>dash</code> is not <code>null</code>
     * @throws IllegalArgumentException if the length of
     *         <code>dash</code> is zero
     * @throws IllegalArgumentException if dash lengths are all zero.
     */
    public Stroke(float width, float[] dash, float dashPhase) {
        if (width < 0.0f) {
            throw new IllegalArgumentException("negative width");
        }

        if (dash != null) {
            if (dashPhase < 0.0f) {
                throw new IllegalArgumentException("negative dash phase");
            }
            boolean allzero = true;
            for (float d : dash) {
                if (d > 0.0) {
                    allzero = false;
                } else if (d < 0.0) {
                    throw new IllegalArgumentException("negative dash length");
                }
            }
            if (allzero) {
                throw new IllegalArgumentException("dash lengths all zero");
            }
        }

        this.width = width;
        this.dash = dash;
        this.dashPhase = dashPhase;
    }

    @Override
    public int hashCode() {
        int hash = Float.floatToIntBits(width);
        if (dash != null) {
            hash = hash * 31 + Float.floatToIntBits(dashPhase);
            for (float d : dash) {
                hash = hash * 31 + Float.floatToIntBits(d);
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Stroke)) {
            return false;
        }

        Stroke bs = (Stroke) obj;
        if (width != bs.width) {
            return false;
        }

        if (dash != null) {
            if (dashPhase != bs.dashPhase) {
                return false;
            }

            return java.util.Arrays.equals(dash, bs.dash);
        }

        return bs.dash == null;
    }
}
