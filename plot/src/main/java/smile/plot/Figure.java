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

import java.util.ArrayList;
import java.util.List;

/**
 * The top level container for all the plot elements.
 *
 * @author Haifeng Li
 */
public class Figure implements Shape {
    /** The axes stack. */
    private List<Axes> axes;
    /** The margin around the figure. */
    private Margin margin;

    public Figure() {
        axes = new ArrayList<>();
        margin = new Margin();
    }

    @Override
    public void paint(Renderer renderer) {

    }
}
