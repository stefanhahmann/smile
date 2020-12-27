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
 * The canvas that the figure renders into.
 *
 * @author Haifeng Li
 */
public interface FigureCanvas {
    /**
     * Returns the figure.
     * @return the figure.
     */
    Figure figure();

    /**
     * Returns the renderer of the canvas.
     * @return the renderer of the canvas.
     */
    Renderer renderer();

    /** Paints the figure on the canvas. */
    default void paint() {
        figure().paint(renderer());
    }
}
