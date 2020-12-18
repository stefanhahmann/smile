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
import java.util.Objects;

/**
 * The font to render text.
 *
 * @author Haifeng Li
 */
public class Font implements Serializable {
    private static final long serialVersionUID = 2L;

    /** Default font. */
    public static Font DEFAULT = new Font(14);

    /**
     * The full font name. This name includes both the family name
     * and the style variant within that family. For example, for a plain
     * Arial font this would be "Arial Regular" and for a bolded
     * Arial font this would be "Arial Bold".
     */
    public final String name;

    /**
     * The point size for this font. This may be a fractional value such as
     * {@code 11.5}. {@literal If the specified value is < 0 the default size will be
     * used.}
     */
    public final double size;

    /**
     * The cached hash code, used to improve performance in situations where
     * we cache fonts, such as in the CSS routines.
     */
    private int hash = 0;

    /**
     * Constructs a font using the default face "System".
     * The underlying font used is determined by the implementation
     * based on the typical UI font for the current UI environment.
     *
     * @param size the font size to use
     */
    public Font(double size) {
        this(null, size);
    }


    /**
     * Constructs a font using the specified full face name and size.
     * @param name full name of the font.
     * @param size the font size to use
     */
    public Font(String name, double size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("Font[name=%s, size=%f]", name, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Font) {
            Font other = (Font) obj;
            return Objects.equals(name, other.name) && size == other.size;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            long bits = 17L;
            bits = 37L * bits + name.hashCode();
            bits = 37L * bits + Double.doubleToLongBits(size);
            hash = (int) (bits ^ (bits >> 32));
        }
        return hash;
    }
}
