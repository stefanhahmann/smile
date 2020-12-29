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

/**
 * The plots have margins surrounding them that separate the main plotting space
 * from the area where the axes, labels and additional text lie. The margins are
 * specified in the percentage of canvas width and height, respectively.
 */
public class Margin {
    /** The bottom margin. */
    public final double bottom;
    /** The left margin. */
    public final double left;
    /** The top margin. */
    public final double top;
    /** The right margin. */
    public final double right;

    /**
     * Constructor with default margins.
     */
    public Margin() {
        this(0.15, 0.12, 0.12, 0.06);
    }

    /**
     * Constructor.
     * @param bottom the bottom margin.
     * @param left the left margin.
     * @param top the top margin.
     * @param right the right margin.
     */
    public Margin(double bottom, double left, double top, double right) {
        this.bottom = bottom;
        this.left = left;
        this.top = top;
        this.right = right;
    }
}