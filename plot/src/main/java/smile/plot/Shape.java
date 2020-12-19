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
 * Abstract base class for objects that render into a {@code FigureCanvas}.
 * Typically, all visible elements in a figure are subclasses of {@code Shape}.
 * <p>
 * There are two types of Shapes: primitives and containers. The primitives
 * represent the standard graphical objects we want to paint onto our canvas:
 * Line2D, Rectangle, Text, etc., and the containers are places to put them
 * (Axis, Axes and Figure). The standard use is to create a Figure instance,
 * use the Figure to create one or more Axes or Subplot instances, and use
 * the Axes instance helper methods to create the primitives.
 *
 * @author Haifeng Li
 */
public interface Shape {
    /**
     * Paints the shape.
     */
    void paint(Renderer g);
}
