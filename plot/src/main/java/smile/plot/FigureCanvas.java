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
public abstract class FigureCanvas {
    /** The figure on the canvas. */
    private final Figure figure;

    /**
     * Constructor.
     * @param figure the figure on the canvas.
     */
    public FigureCanvas(Figure figure) {
        this.figure = figure;
    }

    /**
     * Returns the figure.
     * @return the figure.
     */
    public Figure figure() {
        return figure;
    }

    /** Paints the figure on the canvas. */
    public void paint() {
        figure.paint(renderer());
    }

    /**
     * Returns the renderer of the canvas.
     * @return the renderer of the canvas.
     */
    public abstract Renderer renderer();
}
