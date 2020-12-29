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
 * A single line text.
 *
 * @author Haifeng Li
 */
public class Text implements Shape {
    /**
     * The text of label.
     */
    final String text;
    /**
     * The coordinates of label.
     */
    final double[] coordinates;
    /**
     * The reference position of coordinates respected to dimension of text.
     * (0.5, 0.5) is center, (0, 0) is lower left, (0, 1) is upper left, etc.
     */
    final double horizontalReference;
    /**
     * The reference position of coordinates respected to dimension of text.
     * (0.5, 0.5) is center, (0, 0) is lower left, (0, 1) is upper left, etc.
     */
    final double verticalReference;
    /**
     * The rotation angel of text.
     */
    final double rotation;

    /**
     * Constructor.
     */
    public Text(String text, double[] coordinates, double horizontalReference, double verticalReference, double rotation, Font font, Color color) {
        this.text = text;
        this.coordinates = coordinates;
        this.horizontalReference = horizontalReference;
        this.verticalReference = verticalReference;
        this.rotation = rotation;
    }

    @Override
    public void paint(Renderer renderer) {
        //renderer.drawText(text, coordinates, horizontalReference, verticalReference, rotation);
    }

    /**
     * Returns the string representation of coordinates.
     */
    private static String stringOf(double... coordinates) {
        if (coordinates.length == 2) {
            return String.format("(%.2f, %.2f)", coordinates[0], coordinates[1]);
        } else if (coordinates.length == 3) {
            return String.format("(%.2f, %.2f, %.2f)", coordinates[0], coordinates[1], coordinates[2]);
        } else {
            throw new IllegalArgumentException("Unsupported coordinates length: " + coordinates.length);
        }
    }

    /**
     * Creates a black label with coordinates as text.
     */
    public static Text of(double[] coordinates) {
        return Text.of(stringOf(coordinates), coordinates, 0.5, 0.5, 0.0);
    }

    /**
     * Creates a black label centered at the coordinates.
     */
    public static Text of(String text, double[] coordinates) {
        return Text.of(text, coordinates, 0.5, 0.5, 0.0);
    }

    /**
     * Creates a black label with system default font.
     */
    public static Text of(String text, double[] coordinates, double horizontalReference, double verticalReference, double rotation) {
        return new Text(text, coordinates, horizontalReference, verticalReference, rotation, null, Color.BLACK);
    }
}
