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
    /** The left margin, as a fraction of the figure width. */
    public final double left;
    /** The right margin, as a fraction of the figure width. */
    public final double right;
    /** The bottom margin, as a fraction of the figure height. */
    public final double bottom;
    /** The top margin, as a fraction of the figure height. */
    public final double top;
    /** The width of the padding between subplots, as a fraction of the average axes width. */
    public final double wspace;
    /** The height of the padding between subplots, as a fraction of the average axes height. */
    public final double hspace;

    /**
     * Constructor with default margins.
     */
    public Margin() {
        this(0.12, 0.15, 0.06, 0.12, 0.01, 0.01);
    }

    /**
     * Constructor.
     * @param bottom the bottom margin, as a fraction of the figure height.
     * @param left the left margin, as a fraction of the figure width.
     * @param top the top margin, as a fraction of the figure height.
     * @param right the right margin, as a fraction of the figure width.
     * @param wspace the width of the padding between subplots.
     * @param hspace the height of the padding between subplots.
     */
    public Margin(double left, double bottom, double right, double top, double wspace, double hspace) {
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.top = top;
        this.wspace = wspace;
        this.hspace = hspace;
    }
}