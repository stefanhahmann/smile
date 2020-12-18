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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Render system independent color. It also provides several generators for
 * heatmap color palette.
 *
 * @author Haifeng Li
 */
public class Color implements Serializable {
    private static final long serialVersionUID = 2L;

    /** Red component ranging from 0 to 1. */
    public final float red;
    /** Green component ranging from 0 to 1. */
    public final float green;
    /** Blue component ranging from 0 to 1. */
    public final float blue;
    /** Opacity ranging from 0 to 1. */
    public final float opacity;

    /**
     * Creates a new instance of color.
     * @param red the red component ranging from 0 to 1.
     * @param green the green component ranging from 0 to 1.
     * @param blue the blue component ranging from 0 to 1.
     */
    public Color(float red, float green, float blue) {
        this(red, green, blue, 1.0f);
    }

    /**
     * Creates a new instance of color.
     * @param red the red component ranging from 0 to 1.
     * @param green the green component ranging from 0 to 1.
     * @param blue the blue component ranging from 0 to 1.
     * @param opacity the opacity ranging from 0 to 1.
     */
    public Color(float red, float green, float blue, float opacity) {
        if (red < 0.0f || red > 1.0f) {
            throw new IllegalArgumentException("Invalid red component: " + red);
        }

        if (green < 0.0f || green > 1.0f) {
            throw new IllegalArgumentException("Invalid green component: " + green);
        }

        if (blue < 0.0f || blue > 1.0f) {
            throw new IllegalArgumentException("Invalid blue component: " + blue);
        }

        if (opacity < 0.0f || opacity > 1.0f) {
            throw new IllegalArgumentException("Invalid opacity: " + opacity);
        }

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.opacity = opacity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Color) {
            Color other = (Color) obj;
            return red == other.red
                    && green == other.green
                    && blue == other.blue
                    && opacity == other.opacity;
        } else return false;
    }

    @Override
    public int hashCode() {
        int r = (int) Math.round(red * 255.0);
        int g = (int) Math.round(green * 255.0);
        int b = (int) Math.round(blue * 255.0);
        int a = (int) Math.round(opacity * 255.0);

        int i = r;
        i = i << 8;
        i = i | g;
        i = i << 8;
        i = i | b;
        i = i << 8;
        i = i | a;
        return i;
    }

    @Override
    public String toString() {
        int r = (int) Math.round(red * 255.0);
        int g = (int) Math.round(green * 255.0);
        int b = (int) Math.round(blue * 255.0);
        int a = (int) Math.round(opacity * 255.0);
        return String.format("0x%02x%02x%02x%02x", r, g, b, a);
    }
    /**
     * Creates a color specified with an HTML or CSS attribute string.
     *
     * @param colorString the name or numeric representation of the color
     *                    in one of the supported formats.
     * @return the color.
     */
    public static Color web(String colorString) {
        return web(colorString, 1.0f);
    }

    /**
     * Creates a color specified with an HTML or CSS attribute string.
     *
     * @param colorString the name or numeric representation of the color
     *                    in one of the supported formats
     * @param opacity the opacity component in range from 0.0 (transparent)
     *                to 1.0 (opaque)
     * @return the color.
     */
    public static Color web(String colorString, float opacity) {
        if (colorString.isEmpty()) {
            throw new IllegalArgumentException("Invalid color specification");
        }

        if (opacity < 0.0f || opacity > 1.0f) {
            throw new IllegalArgumentException("Invalid opacity: " + opacity);
        }

        String color = colorString.toLowerCase(Locale.ROOT);

        if (color.startsWith("#")) {
            color = color.substring(1);
        } else if (color.startsWith("0x")) {
            color = color.substring(2);
        } else if (color.startsWith("rgb")) {
            if (color.startsWith("(", 3)) {
                return parseRGBColor(color, 4, false, opacity);
            } else if (color.startsWith("a(", 3)) {
                return parseRGBColor(color, 5, true, opacity);
            }
        } else if (color.startsWith("hsl")) {
            if (color.startsWith("(", 3)) {
                return parseHSLColor(color, 4, false, opacity);
            } else if (color.startsWith("a(", 3)) {
                return parseHSLColor(color, 5, true, opacity);
            }
        } else {
            Color col = NamedColors.get(color);
            if (col != null) {
                if (opacity == 1.0) {
                    return col;
                } else {
                    return new Color(col.red, col.green, col.blue, opacity);
                }
            }
        }

        int len = color.length();

        try {
            if (len == 3 || len == 4) {
                int red = Integer.parseInt(color.substring(0, 1), 16);
                int green = Integer.parseInt(color.substring(1, 2), 16);
                int blue = Integer.parseInt(color.substring(2, 3), 16);
                float alpha = opacity;
                if (len == 4) {
                    alpha = opacity * Integer.parseInt(color.substring(3, 4), 16) / 15.0f;
                }
                return new Color(red / 15.0f, green / 15.0f, blue / 15.0f, alpha);
            } else if (len == 6 || len == 8) {
                int red = Integer.parseInt(color.substring(0, 2), 16);
                int green = Integer.parseInt(color.substring(2, 4), 16);
                int blue = Integer.parseInt(color.substring(4, 6), 16);
                float alpha = opacity;
                if (len == 8) {
                    alpha = opacity * Integer.parseInt(color.substring(6, 8), 16) / 255.0f;
                }
                return new Color(red / 255.0f, green / 255.0f, blue / 255.0f, alpha);
            }
        } catch (NumberFormatException nfe) {}

        throw new IllegalArgumentException("Invalid color specification: " + colorString);
    }

    private static Color parseRGBColor(String color, int roff, boolean hasAlpha, float a) {
        try {
            int rend = color.indexOf(',', roff);
            int gend = rend < 0 ? -1 : color.indexOf(',', rend+1);
            int bend = gend < 0 ? -1 : color.indexOf(hasAlpha ? ',' : ')', gend+1);
            int aend = hasAlpha ? (bend < 0 ? -1 : color.indexOf(')', bend+1)) : bend;
            if (aend >= 0) {
                float r = parseComponent(color, roff, rend, PARSE_COMPONENT);
                float g = parseComponent(color, rend+1, gend, PARSE_COMPONENT);
                float b = parseComponent(color, gend+1, bend, PARSE_COMPONENT);
                if (hasAlpha) {
                    a *= parseComponent(color, bend+1, aend, PARSE_ALPHA);
                }
                return new Color(r, g, b, a);
            }
        } catch (NumberFormatException nfe) {}

        throw new IllegalArgumentException("Invalid color specification: " + color);
    }

    private static Color parseHSLColor(String color, int hoff, boolean hasAlpha, float a) {
        try {
            int hend = color.indexOf(',', hoff);
            int send = hend < 0 ? -1 : color.indexOf(',', hend+1);
            int lend = send < 0 ? -1 : color.indexOf(hasAlpha ? ',' : ')', send+1);
            int aend = hasAlpha ? (lend < 0 ? -1 : color.indexOf(')', lend+1)) : lend;
            if (aend >= 0) {
                float h = parseComponent(color, hoff, hend, PARSE_ANGLE);
                float s = parseComponent(color, hend+1, send, PARSE_PERCENT);
                float l = parseComponent(color, send+1, lend, PARSE_PERCENT);
                if (hasAlpha) {
                    a *= parseComponent(color, lend+1, aend, PARSE_ALPHA);
                }
                return Color.hsb(h, s, l, a);
            }
        } catch (NumberFormatException nfe) {}

        throw new IllegalArgumentException("Invalid color specification: " + color);
    }

    private static final int PARSE_COMPONENT = 0; // percent, or clamped to [0,255] => [0,1]
    private static final int PARSE_PERCENT = 1; // clamped to [0,100]% => [0,1]
    private static final int PARSE_ANGLE = 2; // clamped to [0,360]
    private static final int PARSE_ALPHA = 3; // clamped to [0.0,1.0]
    private static float parseComponent(String color, int off, int end, int type) {
        color = color.substring(off, end).trim();
        if (color.endsWith("%")) {
            if (type > PARSE_PERCENT) {
                throw new IllegalArgumentException("Invalid color specification");
            }
            type = PARSE_PERCENT;
            color = color.substring(0, color.length()-1).trim();
        } else if (type == PARSE_PERCENT) {
            throw new IllegalArgumentException("Invalid color specification");
        }

        float c = ((type == PARSE_COMPONENT)
                ? Integer.parseInt(color)
                : Float.parseFloat(color));
        switch (type) {
            case PARSE_ALPHA:
                return (c < 0.0f) ? 0.0f : ((c > 1.0f) ? 1.0f : c);
            case PARSE_PERCENT:
                return (c <= 0.0f) ? 0.0f : ((c >= 100.0f) ? 1.0f : (c / 100.0f));
            case PARSE_COMPONENT:
                return (c <= 0.0f) ? 0.0f : ((c >= 255.0f) ? 1.0f : (c / 255.0f));
            case PARSE_ANGLE:
                return ((c < 0.0f)
                        ? ((c % 360.0f) + 360.0f)
                        : ((c > 360.0f) ? (c % 360.0f) : c));
        }

        throw new IllegalArgumentException("Invalid color specification");
    }

    /**
     * Generate terrain color palette.
     * @param n the number of colors in the palette.
     * @return the color palette.
     */
    public static Color[] terrain(int n) {
        return terrain(n, 1.0f);
    }

    /**
     * Generate terrain color palette.
     * @param n the number of colors in the palette.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] terrain(int n, float alpha) {
        int k = n / 2;
        float[] H = {4 / 12f, 2 / 12f, 0 / 12f};
        float[] S = {1f, 1f, 0f};
        float[] V = {0.65f, 0.9f, 0.95f};

        Color[] palette = new Color[n];

        float h = H[0];
        float hw = (H[1] - H[0]) / (k - 1);

        float s = S[0];
        float sw = (S[1] - S[0]) / (k - 1);

        float v = V[0];
        float vw = (V[1] - V[0]) / (k - 1);

        for (int i = 0; i <
                k; i++) {
            palette[i] = hsb(h, s, v, alpha);
            h += hw;
            s += sw;
            v += vw;
        }

        h = H[1];
        hw = (H[2] - H[1]) / (n - k);

        s = S[1];
        sw = (S[2] - S[1]) / (n - k);

        v = V[1];
        vw = (V[2] - V[1]) / (n - k);

        for (int i = k; i < n; i++) {
            h += hw;
            s += sw;
            v += vw;
            palette[i] = hsb(h, s, v, alpha);
        }

        return palette;
    }

    /**
     * Generate topo color palette.
     * @param n the number of colors in the palette.
     * @return the color palette.
     */
    public static Color[] topo(int n) {
        return topo(n, 1.0f);
    }

    /**
     * Generate topo color palette.
     * @param n the number of colors in the palette.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] topo(int n, float alpha) {
        int j = n / 3;
        int k = n / 3;
        int i = n - j - k;

        Color[] palette = new Color[n];

        float h = 43 / 60.0f;
        float hw = (31 / 60.0f - h) / (i - 1);
        int l = 0;
        for (; l < i; l++) {
            palette[l] = hsb(h, 1.0f, 1.0f, alpha);
            h += hw;
        }

        h = 23 / 60.0f;
        hw = (11 / 60.0f - h) / (j - 1);
        for (; l < i + j; l++) {
            palette[l] = hsb(h, 1.0f, 1.0f, alpha);
            h += hw;
        }

        h = 10 / 60.0f;
        hw = (6 / 60.0f - h) / (k - 1);
        float s = 1.0f;
        float sw = (0.3f - s) / (k - 1);
        for (; l < n; l++) {
            palette[l] = hsb(h, s, 1.0f, alpha);
            h += hw;
            s += sw;
        }

        return palette;
    }

    /**
     * Generate jet color palette.
     * @param n the number of colors in the palette.
     * @return the color palette.
     */
    public static Color[] jet(int n) {
        return jet(n, 1.0f);
    }

    /**
     * Generate jet color palette.
     * @param n the number of colors in the palette.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] jet(int n, float alpha) {
        int m = (int) Math.ceil(n / 4.0);

        float[] u = new float[3 * m];
        for (int i = 0; i < u.length; i++) {
            if (i == 0) {
                u[i] = 0.0f;
            } else if (i <= m) {
                u[i] = i / (float) m;
            } else if (i <= 2 * m - 1) {
                u[i] = 1.0f;
            } else {
                u[i] = (3 * m - i) / (float) m;
            }

        }

        int m2 = m / 2 + m % 2;
        int mod = n % 4;
        int[] r = new int[n];
        int[] g = new int[n];
        int[] b = new int[n];
        for (int i = 0; i < u.length - 1; i++) {
            if (m2 - mod + i < n) {
                g[m2 - mod + i] = i + 1;
            }
            if (m2 - mod + i + m < n) {
                r[m2 - mod + i + m] = i + 1;
            }
            if (i > 0 && m2 - mod + i < u.length) {
                b[i] = m2 - mod + i;
            }
        }

        Color[] palette = new Color[n];
        for (int i = 0; i < n; i++) {
            palette[i] = new Color(u[r[i]], u[g[i]], u[b[i]], alpha);
        }

        return palette;
    }

    /**
     * Generate red-green color palette.
     * @param n the number of colors in the palette.
     * @return the color palette.
     */
    public static Color[] redgreen(int n) {
        return redgreen(n, 1.0f);
    }

    /**
     * Generate red-green color palette.
     * @param n the number of colors in the palette.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] redgreen(int n, float alpha) {
        Color[] palette = new Color[n];
        for (int i = 0; i < n; i++) {
            palette[i] = new Color((float) Math.sqrt((i + 1.0f) / n), (float) Math.sqrt(1 - (i + 1.0f) / n), 0.0f, alpha);
        }

        return palette;
    }

    /**
     * Generate red-blue color palette.
     * @param n the number of colors in the palette.
     * @return the color palette.
     */
    public static Color[] redblue(int n) {
        return redblue(n, 1.0f);
    }

    /**
     * Generate red-blue color palette.
     * @param n the number of colors in the palette.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] redblue(int n, float alpha) {
        Color[] palette = new Color[n];
        for (int i = 0; i < n; i++) {
            palette[i] = new Color((float) Math.sqrt((i + 1.0f) / n), 0.0f, (float) Math.sqrt(1 - (i + 1.0f) / n), alpha);
        }

        return palette;
    }

    /**
     * Generate heat color palette.
     * @param n the number of colors in the palette.
     * @return the color palette.
     */
    public static Color[] heat(int n) {
        return heat(n, 1.0f);
    }

    /**
     * Generate heat color palette.
     * @param n the number of colors in the palette.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] heat(int n, float alpha) {
        int j = n / 4;
        int k = n - j;
        float h = 1.0f / 6;

        Color[] c = rainbow(k, 0, h, alpha);

        Color[] palette = new Color[n];
        System.arraycopy(c, 0, palette, 0, k);

        float s = 1 - 1.0f / (2 * j);
        float end = 1.0f / (2 * j);
        float w = (end - s) / (j - 1);

        for (int i = k; i < n; i++) {
            palette[i] = hsb(h, s, 1.0f, alpha);
            s += w;
        }

        return palette;
    }

    /**
     * Generate rainbow color palette.
     * @param n the number of colors in the palette.
     * @return the color palette.
     */
    public static Color[] rainbow(int n) {
        return rainbow(n, 1.0f);
    }

    /**
     * Generate rainbow color palette.
     * @param n the number of colors in the palette.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] rainbow(int n, float alpha) {
        return rainbow(n, 0.0f, (float) (n - 1) / n, alpha);
    }

    /**
     * Generate rainbow color palette.
     * @param n the number of colors in the palette.
     * @param start the start of h in the HSV color model.
     * @param end the start of h in the HSV color model.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] rainbow(int n, float start, float end, float alpha) {
        return rainbow(n, start, end, 1.0f, 1.0f, alpha);
    }

    /**
     * Generate rainbow color palette.
     * @param n the number of colors in the palette.
     * @param start the start of h in the HSV color model.
     * @param end the start of h in the HSV color model.
     * @param s the s in the HSV color model.
     * @param v the v in the HSV color model.
     * @param alpha the parameter in [0,1] for transparency.
     * @return the color palette.
     */
    public static Color[] rainbow(int n, float start, float end, float s, float v, float alpha) {
        Color[] palette = new Color[n];
        float h = start;
        float w = (end - start) / (n - 1);
        for (int i = 0; i < n; i++) {
            palette[i] = hsb(h, s, v, alpha);
            h += w;
        }

        return palette;
    }

    /**
     * Creates a color based on HSV/HSB model.
     * @param hue the hue, in degrees
     * @param saturation the saturation, {@code 0.0 to 1.0}
     * @param brightness the brightness, {@code 0.0 to 1.0}
     * @param opacity the opacity, {@code 0.0 to 1.0}
     * @return the color palette.
     */
    public static Color hsb(float hue, float saturation, float brightness, float opacity) {
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;

        if (saturation == 0) {
            // this color in on the black white center line <=> h = UNDEFINED
            // Achromatic color, there is no hue
            r = brightness;
            g = brightness;
            b = brightness;
        } else {
            if (hue == 1.0f) {
                hue = 0.0f;
            }

            // h is now in [0,6)
            hue *= 6;

            int i = (int) Math.floor(hue);
            float f = hue - i; //f is fractional part of h
            float p = brightness * (1 - saturation);
            float q = brightness * (1 - (saturation * f));
            float t = brightness * (1 - (saturation * (1 - f)));

            switch (i) {
                case 0:
                    r = brightness;
                    g = t;
                    b = p;
                    break;

                case 1:
                    r = q;
                    g = brightness;
                    b = p;
                    break;

                case 2:
                    r = p;
                    g = brightness;
                    b = t;
                    break;

                case 3:
                    r = p;
                    g = q;
                    b = brightness;
                    break;

                case 4:
                    r = t;
                    g = p;
                    b = brightness;
                    break;

                case 5:
                    r = brightness;
                    g = p;
                    b = q;
                    break;

            }
        }

        return new Color(r, g, b, opacity);
    }

    /**
     * A fully transparent color with an ARGB value of #00000000.
     */
    public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);

    /**
     * The color alice blue with an RGB value of #F0F8FF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F0F8FF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color ALICEBLUE = new Color(0.9411765f, 0.972549f, 1.0f);

    /**
     * The color antique white with an RGB value of #FAEBD7
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FAEBD7;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color ANTIQUEWHITE = new Color(0.98039216f, 0.92156863f, 0.84313726f);

    /**
     * The color aqua with an RGB value of #00FFFF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FFFF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color AQUA = new Color(0.0f, 1.0f, 1.0f);

    /**
     * The color aquamarine with an RGB value of #7FFFD4
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#7FFFD4;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color AQUAMARINE = new Color(0.49803922f, 1.0f, 0.83137256f);

    /**
     * The color azure with an RGB value of #F0FFFF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F0FFFF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color AZURE = new Color(0.9411765f, 1.0f, 1.0f);

    /**
     * The color beige with an RGB value of #F5F5DC
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F5F5DC;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color BEIGE = new Color(0.9607843f, 0.9607843f, 0.8627451f);

    /**
     * The color bisque with an RGB value of #FFE4C4
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFE4C4;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color BISQUE = new Color(1.0f, 0.89411765f, 0.76862746f);

    /**
     * The color black with an RGB value of #000000
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#000000;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);

    /**
     * The color blanched almond with an RGB value of #FFEBCD
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFEBCD;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color BLANCHEDALMOND = new Color(1.0f, 0.92156863f, 0.8039216f);

    /**
     * The color blue with an RGB value of #0000FF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#0000FF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f);

    /**
     * The color blue violet with an RGB value of #8A2BE2
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#8A2BE2;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color BLUEVIOLET = new Color(0.5411765f, 0.16862746f, 0.8862745f);

    /**
     * The color brown with an RGB value of #A52A2A
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#A52A2A;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color BROWN = new Color(0.64705884f, 0.16470589f, 0.16470589f);

    /**
     * The color burly wood with an RGB value of #DEB887
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#DEB887;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color BURLYWOOD = new Color(0.87058824f, 0.72156864f, 0.5294118f);

    /**
     * The color cadet blue with an RGB value of #5F9EA0
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#5F9EA0;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color CADETBLUE = new Color(0.37254903f, 0.61960787f, 0.627451f);

    /**
     * The color chartreuse with an RGB value of #7FFF00
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#7FFF00;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color CHARTREUSE = new Color(0.49803922f, 1.0f, 0.0f);

    /**
     * The color chocolate with an RGB value of #D2691E
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#D2691E;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color CHOCOLATE = new Color(0.8235294f, 0.4117647f, 0.11764706f);

    /**
     * The color coral with an RGB value of #FF7F50
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF7F50;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color CORAL = new Color(1.0f, 0.49803922f, 0.3137255f);

    /**
     * The color cornflower blue with an RGB value of #6495ED
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#6495ED;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color CORNFLOWERBLUE = new Color(0.39215687f, 0.58431375f, 0.92941177f);

    /**
     * The color cornsilk with an RGB value of #FFF8DC
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFF8DC;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color CORNSILK = new Color(1.0f, 0.972549f, 0.8627451f);

    /**
     * The color crimson with an RGB value of #DC143C
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#DC143C;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color CRIMSON = new Color(0.8627451f, 0.078431375f, 0.23529412f);

    /**
     * The color cyan with an RGB value of #00FFFF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FFFF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color CYAN = new Color(0.0f, 1.0f, 1.0f);

    /**
     * The color dark blue with an RGB value of #00008B
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#00008B;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKBLUE = new Color(0.0f, 0.0f, 0.54509807f);

    /**
     * The color dark cyan with an RGB value of #008B8B
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#008B8B;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKCYAN = new Color(0.0f, 0.54509807f, 0.54509807f);

    /**
     * The color dark goldenrod with an RGB value of #B8860B
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#B8860B;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKGOLDENROD = new Color(0.72156864f, 0.5254902f, 0.043137256f);

    /**
     * The color dark gray with an RGB value of #A9A9A9
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#A9A9A9;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKGRAY = new Color(0.6627451f, 0.6627451f, 0.6627451f);

    /**
     * The color dark green with an RGB value of #006400
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#006400;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKGREEN = new Color(0.0f, 0.39215687f, 0.0f);

    /**
     * The color dark grey with an RGB value of #A9A9A9
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#A9A9A9;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKGREY             = DARKGRAY;

    /**
     * The color dark khaki with an RGB value of #BDB76B
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#BDB76B;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKKHAKI = new Color(0.7411765f, 0.7176471f, 0.41960785f);

    /**
     * The color dark magenta with an RGB value of #8B008B
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#8B008B;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKMAGENTA = new Color(0.54509807f, 0.0f, 0.54509807f);

    /**
     * The color dark olive green with an RGB value of #556B2F
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#556B2F;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKOLIVEGREEN = new Color(0.33333334f, 0.41960785f, 0.18431373f);

    /**
     * The color dark orange with an RGB value of #FF8C00
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF8C00;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKORANGE = new Color(1.0f, 0.54901963f, 0.0f);

    /**
     * The color dark orchid with an RGB value of #9932CC
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#9932CC;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKORCHID = new Color(0.6f, 0.19607843f, 0.8f);

    /**
     * The color dark red with an RGB value of #8B0000
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#8B0000;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKRED = new Color(0.54509807f, 0.0f, 0.0f);

    /**
     * The color dark salmon with an RGB value of #E9967A
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#E9967A;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKSALMON = new Color(0.9137255f, 0.5882353f, 0.47843137f);

    /**
     * The color dark sea green with an RGB value of #8FBC8F
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#8FBC8F;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKSEAGREEN = new Color(0.56078434f, 0.7372549f, 0.56078434f);

    /**
     * The color dark slate blue with an RGB value of #483D8B
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#483D8B;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKSLATEBLUE = new Color(0.28235295f, 0.23921569f, 0.54509807f);

    /**
     * The color dark slate gray with an RGB value of #2F4F4F
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#2F4F4F;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKSLATEGRAY = new Color(0.18431373f, 0.30980393f, 0.30980393f);

    /**
     * The color dark slate grey with an RGB value of #2F4F4F
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#2F4F4F;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKSLATEGREY        = DARKSLATEGRAY;

    /**
     * The color dark turquoise with an RGB value of #00CED1
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#00CED1;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKTURQUOISE = new Color(0.0f, 0.80784315f, 0.81960785f);

    /**
     * The color dark violet with an RGB value of #9400D3
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#9400D3;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DARKVIOLET = new Color(0.5803922f, 0.0f, 0.827451f);

    /**
     * The color deep pink with an RGB value of #FF1493
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF1493;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DEEPPINK = new Color(1.0f, 0.078431375f, 0.5764706f);

    /**
     * The color deep sky blue with an RGB value of #00BFFF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#00BFFF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DEEPSKYBLUE = new Color(0.0f, 0.7490196f, 1.0f);

    /**
     * The color dim gray with an RGB value of #696969
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#696969;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DIMGRAY = new Color(0.4117647f, 0.4117647f, 0.4117647f);

    /**
     * The color dim grey with an RGB value of #696969
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#696969;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DIMGREY              = DIMGRAY;

    /**
     * The color dodger blue with an RGB value of #1E90FF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#1E90FF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color DODGERBLUE = new Color(0.11764706f, 0.5647059f, 1.0f);

    /**
     * The color firebrick with an RGB value of #B22222
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#B22222;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color FIREBRICK = new Color(0.69803923f, 0.13333334f, 0.13333334f);

    /**
     * The color floral white with an RGB value of #FFFAF0
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFAF0;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color FLORALWHITE = new Color(1.0f, 0.98039216f, 0.9411765f);

    /**
     * The color forest green with an RGB value of #228B22
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#228B22;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color FORESTGREEN = new Color(0.13333334f, 0.54509807f, 0.13333334f);

    /**
     * The color fuchsia with an RGB value of #FF00FF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF00FF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color FUCHSIA = new Color(1.0f, 0.0f, 1.0f);

    /**
     * The color gainsboro with an RGB value of #DCDCDC
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#DCDCDC;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color GAINSBORO = new Color(0.8627451f, 0.8627451f, 0.8627451f);

    /**
     * The color ghost white with an RGB value of #F8F8FF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F8F8FF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color GHOSTWHITE = new Color(0.972549f, 0.972549f, 1.0f);

    /**
     * The color gold with an RGB value of #FFD700
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFD700;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color GOLD = new Color(1.0f, 0.84313726f, 0.0f);

    /**
     * The color goldenrod with an RGB value of #DAA520
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#DAA520;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color GOLDENROD = new Color(0.85490197f, 0.64705884f, 0.1254902f);

    /**
     * The color gray with an RGB value of #808080
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#808080;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color GRAY = new Color(0.5019608f, 0.5019608f, 0.5019608f);

    /**
     * The color green with an RGB value of #008000
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#008000;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color GREEN = new Color(0.0f, 0.5019608f, 0.0f);

    /**
     * The color green yellow with an RGB value of #ADFF2F
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#ADFF2F;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color GREENYELLOW = new Color(0.6784314f, 1.0f, 0.18431373f);

    /**
     * The color grey with an RGB value of #808080
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#808080;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color GREY                 = GRAY;

    /**
     * The color honeydew with an RGB value of #F0FFF0
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F0FFF0;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color HONEYDEW = new Color(0.9411765f, 1.0f, 0.9411765f);

    /**
     * The color hot pink with an RGB value of #FF69B4
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF69B4;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color HOTPINK = new Color(1.0f, 0.4117647f, 0.7058824f);

    /**
     * The color indian red with an RGB value of #CD5C5C
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#CD5C5C;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color INDIANRED = new Color(0.8039216f, 0.36078432f, 0.36078432f);

    /**
     * The color indigo with an RGB value of #4B0082
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#4B0082;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color INDIGO = new Color(0.29411766f, 0.0f, 0.50980395f);

    /**
     * The color ivory with an RGB value of #FFFFF0
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFFF0;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color IVORY = new Color(1.0f, 1.0f, 0.9411765f);

    /**
     * The color khaki with an RGB value of #F0E68C
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F0E68C;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color KHAKI = new Color(0.9411765f, 0.9019608f, 0.54901963f);

    /**
     * The color lavender with an RGB value of #E6E6FA
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#E6E6FA;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LAVENDER = new Color(0.9019608f, 0.9019608f, 0.98039216f);

    /**
     * The color lavender blush with an RGB value of #FFF0F5
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFF0F5;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LAVENDERBLUSH = new Color(1.0f, 0.9411765f, 0.9607843f);

    /**
     * The color lawn green with an RGB value of #7CFC00
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#7CFC00;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LAWNGREEN = new Color(0.4862745f, 0.9882353f, 0.0f);

    /**
     * The color lemon chiffon with an RGB value of #FFFACD
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFACD;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LEMONCHIFFON = new Color(1.0f, 0.98039216f, 0.8039216f);

    /**
     * The color light blue with an RGB value of #ADD8E6
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#ADD8E6;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTBLUE = new Color(0.6784314f, 0.84705883f, 0.9019608f);

    /**
     * The color light coral with an RGB value of #F08080
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F08080;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTCORAL = new Color(0.9411765f, 0.5019608f, 0.5019608f);

    /**
     * The color light cyan with an RGB value of #E0FFFF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#E0FFFF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTCYAN = new Color(0.8784314f, 1.0f, 1.0f);

    /**
     * The color light goldenrod yellow with an RGB value of #FAFAD2
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FAFAD2;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTGOLDENRODYELLOW = new Color(0.98039216f, 0.98039216f, 0.8235294f);

    /**
     * The color light gray with an RGB value of #D3D3D3
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#D3D3D3;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTGRAY = new Color(0.827451f, 0.827451f, 0.827451f);

    /**
     * The color light green with an RGB value of #90EE90
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#90EE90;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTGREEN = new Color(0.5647059f, 0.93333334f, 0.5647059f);

    /**
     * The color light grey with an RGB value of #D3D3D3
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#D3D3D3;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTGREY            = LIGHTGRAY;

    /**
     * The color light pink with an RGB value of #FFB6C1
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFB6C1;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTPINK = new Color(1.0f, 0.7137255f, 0.75686276f);

    /**
     * The color light salmon with an RGB value of #FFA07A
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFA07A;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTSALMON = new Color(1.0f, 0.627451f, 0.47843137f);

    /**
     * The color light sea green with an RGB value of #20B2AA
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#20B2AA;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTSEAGREEN = new Color(0.1254902f, 0.69803923f, 0.6666667f);

    /**
     * The color light sky blue with an RGB value of #87CEFA
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#87CEFA;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTSKYBLUE = new Color(0.5294118f, 0.80784315f, 0.98039216f);

    /**
     * The color light slate gray with an RGB value of #778899
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#778899;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTSLATEGRAY = new Color(0.46666667f, 0.53333336f, 0.6f);

    /**
     * The color light slate grey with an RGB value of #778899
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#778899;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTSLATEGREY       = LIGHTSLATEGRAY;

    /**
     * The color light steel blue with an RGB value of #B0C4DE
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#B0C4DE;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTSTEELBLUE = new Color(0.6901961f, 0.76862746f, 0.87058824f);

    /**
     * The color light yellow with an RGB value of #FFFFE0
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFFE0;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIGHTYELLOW = new Color(1.0f, 1.0f, 0.8784314f);

    /**
     * The color lime with an RGB value of #00FF00
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FF00;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIME = new Color(0.0f, 1.0f, 0.0f);

    /**
     * The color lime green with an RGB value of #32CD32
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#32CD32;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LIMEGREEN = new Color(0.19607843f, 0.8039216f, 0.19607843f);

    /**
     * The color linen with an RGB value of #FAF0E6
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FAF0E6;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color LINEN = new Color(0.98039216f, 0.9411765f, 0.9019608f);

    /**
     * The color magenta with an RGB value of #FF00FF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF00FF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MAGENTA = new Color(1.0f, 0.0f, 1.0f);

    /**
     * The color maroon with an RGB value of #800000
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#800000;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MAROON = new Color(0.5019608f, 0.0f, 0.0f);

    /**
     * The color medium aquamarine with an RGB value of #66CDAA
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#66CDAA;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMAQUAMARINE = new Color(0.4f, 0.8039216f, 0.6666667f);

    /**
     * The color medium blue with an RGB value of #0000CD
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#0000CD;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMBLUE = new Color(0.0f, 0.0f, 0.8039216f);

    /**
     * The color medium orchid with an RGB value of #BA55D3
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#BA55D3;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMORCHID = new Color(0.7294118f, 0.33333334f, 0.827451f);

    /**
     * The color medium purple with an RGB value of #9370DB
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#9370DB;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMPURPLE = new Color(0.5764706f, 0.4392157f, 0.85882354f);

    /**
     * The color medium sea green with an RGB value of #3CB371
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#3CB371;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMSEAGREEN = new Color(0.23529412f, 0.7019608f, 0.44313726f);

    /**
     * The color medium slate blue with an RGB value of #7B68EE
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#7B68EE;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMSLATEBLUE = new Color(0.48235294f, 0.40784314f, 0.93333334f);

    /**
     * The color medium spring green with an RGB value of #00FA9A
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FA9A;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMSPRINGGREEN = new Color(0.0f, 0.98039216f, 0.6039216f);

    /**
     * The color medium turquoise with an RGB value of #48D1CC
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#48D1CC;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMTURQUOISE = new Color(0.28235295f, 0.81960785f, 0.8f);

    /**
     * The color medium violet red with an RGB value of #C71585
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#C71585;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MEDIUMVIOLETRED = new Color(0.78039217f, 0.08235294f, 0.52156866f);

    /**
     * The color midnight blue with an RGB value of #191970
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#191970;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MIDNIGHTBLUE = new Color(0.09803922f, 0.09803922f, 0.4392157f);

    /**
     * The color mint cream with an RGB value of #F5FFFA
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F5FFFA;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MINTCREAM = new Color(0.9607843f, 1.0f, 0.98039216f);

    /**
     * The color misty rose with an RGB value of #FFE4E1
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFE4E1;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MISTYROSE = new Color(1.0f, 0.89411765f, 0.88235295f);

    /**
     * The color moccasin with an RGB value of #FFE4B5
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFE4B5;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color MOCCASIN = new Color(1.0f, 0.89411765f, 0.70980394f);

    /**
     * The color navajo white with an RGB value of #FFDEAD
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFDEAD;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color NAVAJOWHITE = new Color(1.0f, 0.87058824f, 0.6784314f);

    /**
     * The color navy with an RGB value of #000080
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#000080;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color NAVY = new Color(0.0f, 0.0f, 0.5019608f);

    /**
     * The color old lace with an RGB value of #FDF5E6
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FDF5E6;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color OLDLACE = new Color(0.99215686f, 0.9607843f, 0.9019608f);

    /**
     * The color olive with an RGB value of #808000
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#808000;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color OLIVE = new Color(0.5019608f, 0.5019608f, 0.0f);

    /**
     * The color olive drab with an RGB value of #6B8E23
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#6B8E23;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color OLIVEDRAB = new Color(0.41960785f, 0.5568628f, 0.13725491f);

    /**
     * The color orange with an RGB value of #FFA500
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFA500;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color ORANGE = new Color(1.0f, 0.64705884f, 0.0f);

    /**
     * The color orange red with an RGB value of #FF4500
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF4500;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color ORANGERED = new Color(1.0f, 0.27058825f, 0.0f);

    /**
     * The color orchid with an RGB value of #DA70D6
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#DA70D6;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color ORCHID = new Color(0.85490197f, 0.4392157f, 0.8392157f);

    /**
     * The color pale goldenrod with an RGB value of #EEE8AA
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#EEE8AA;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PALEGOLDENROD = new Color(0.93333334f, 0.9098039f, 0.6666667f);

    /**
     * The color pale green with an RGB value of #98FB98
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#98FB98;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PALEGREEN = new Color(0.59607846f, 0.9843137f, 0.59607846f);

    /**
     * The color pale turquoise with an RGB value of #AFEEEE
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#AFEEEE;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PALETURQUOISE = new Color(0.6862745f, 0.93333334f, 0.93333334f);

    /**
     * The color pale violet red with an RGB value of #DB7093
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#DB7093;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PALEVIOLETRED = new Color(0.85882354f, 0.4392157f, 0.5764706f);

    /**
     * The color papaya whip with an RGB value of #FFEFD5
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFEFD5;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PAPAYAWHIP = new Color(1.0f, 0.9372549f, 0.8352941f);

    /**
     * The color peach puff with an RGB value of #FFDAB9
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFDAB9;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PEACHPUFF = new Color(1.0f, 0.85490197f, 0.7254902f);

    /**
     * The color peru with an RGB value of #CD853F
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#CD853F;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PERU = new Color(0.8039216f, 0.52156866f, 0.24705882f);

    /**
     * The color pink with an RGB value of #FFC0CB
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFC0CB;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PINK = new Color(1.0f, 0.7529412f, 0.79607844f);

    /**
     * The color plum with an RGB value of #DDA0DD
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#DDA0DD;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PLUM = new Color(0.8666667f, 0.627451f, 0.8666667f);

    /**
     * The color powder blue with an RGB value of #B0E0E6
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#B0E0E6;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color POWDERBLUE = new Color(0.6901961f, 0.8784314f, 0.9019608f);

    /**
     * The color purple with an RGB value of #800080
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#800080;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color PURPLE = new Color(0.5019608f, 0.0f, 0.5019608f);

    /**
     * The color red with an RGB value of #FF0000
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF0000;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color RED = new Color(1.0f, 0.0f, 0.0f);

    /**
     * The color rosy brown with an RGB value of #BC8F8F
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#BC8F8F;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color ROSYBROWN = new Color(0.7372549f, 0.56078434f, 0.56078434f);

    /**
     * The color royal blue with an RGB value of #4169E1
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#4169E1;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color ROYALBLUE = new Color(0.25490198f, 0.4117647f, 0.88235295f);

    /**
     * The color saddle brown with an RGB value of #8B4513
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#8B4513;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SADDLEBROWN = new Color(0.54509807f, 0.27058825f, 0.07450981f);

    /**
     * The color salmon with an RGB value of #FA8072
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FA8072;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SALMON = new Color(0.98039216f, 0.5019608f, 0.44705883f);

    /**
     * The color sandy brown with an RGB value of #F4A460
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F4A460;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SANDYBROWN = new Color(0.95686275f, 0.6431373f, 0.3764706f);

    /**
     * The color sea green with an RGB value of #2E8B57
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#2E8B57;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SEAGREEN = new Color(0.18039216f, 0.54509807f, 0.34117648f);

    /**
     * The color sea shell with an RGB value of #FFF5EE
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFF5EE;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SEASHELL = new Color(1.0f, 0.9607843f, 0.93333334f);

    /**
     * The color sienna with an RGB value of #A0522D
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#A0522D;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SIENNA = new Color(0.627451f, 0.32156864f, 0.1764706f);

    /**
     * The color silver with an RGB value of #C0C0C0
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#C0C0C0;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SILVER = new Color(0.7529412f, 0.7529412f, 0.7529412f);

    /**
     * The color sky blue with an RGB value of #87CEEB
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#87CEEB;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SKYBLUE = new Color(0.5294118f, 0.80784315f, 0.92156863f);

    /**
     * The color slate blue with an RGB value of #6A5ACD
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#6A5ACD;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SLATEBLUE = new Color(0.41568628f, 0.3529412f, 0.8039216f);

    /**
     * The color slate gray with an RGB value of #708090
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#708090;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SLATEGRAY = new Color(0.4392157f, 0.5019608f, 0.5647059f);

    /**
     * The color slate grey with an RGB value of #708090
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#708090;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SLATEGREY            = SLATEGRAY;

    /**
     * The color snow with an RGB value of #FFFAFA
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFAFA;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SNOW = new Color(1.0f, 0.98039216f, 0.98039216f);

    /**
     * The color spring green with an RGB value of #00FF7F
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FF7F;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color SPRINGGREEN = new Color(0.0f, 1.0f, 0.49803922f);

    /**
     * The color steel blue with an RGB value of #4682B4
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#4682B4;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color STEELBLUE = new Color(0.27450982f, 0.50980395f, 0.7058824f);

    /**
     * The color tan with an RGB value of #D2B48C
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#D2B48C;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color TAN = new Color(0.8235294f, 0.7058824f, 0.54901963f);

    /**
     * The color teal with an RGB value of #008080
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#008080;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color TEAL = new Color(0.0f, 0.5019608f, 0.5019608f);

    /**
     * The color thistle with an RGB value of #D8BFD8
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#D8BFD8;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color THISTLE = new Color(0.84705883f, 0.7490196f, 0.84705883f);

    /**
     * The color tomato with an RGB value of #FF6347
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF6347;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color TOMATO = new Color(1.0f, 0.3882353f, 0.2784314f);

    /**
     * The color turquoise with an RGB value of #40E0D0
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#40E0D0;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color TURQUOISE = new Color(0.2509804f, 0.8784314f, 0.8156863f);

    /**
     * The color violet with an RGB value of #EE82EE
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#EE82EE;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color VIOLET = new Color(0.93333334f, 0.50980395f, 0.93333334f);

    /**
     * The color wheat with an RGB value of #F5DEB3
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F5DEB3;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color WHEAT = new Color(0.9607843f, 0.87058824f, 0.7019608f);

    /**
     * The color white with an RGB value of #FFFFFF
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFFFF;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);

    /**
     * The color white smoke with an RGB value of #F5F5F5
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#F5F5F5;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color WHITESMOKE = new Color(0.9607843f, 0.9607843f, 0.9607843f);

    /**
     * The color yellow with an RGB value of #FFFF00
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFF00;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color YELLOW = new Color(1.0f, 1.0f, 0.0f);

    /**
     * The color yellow green with an RGB value of #9ACD32
     * <div style="border:1px solid black;width:40px;height:20px;background-color:#9ACD32;float:right;margin: 0 10px 0 0"></div>
     */
    public static final Color YELLOWGREEN = new Color(0.6039216f, 0.8039216f, 0.19607843f);

    /** A color palette useful for scatter plot. */
    public static final Color[] palette = {
            RED,
            BLUE,
            GREEN,
            MAGENTA,
            CYAN,
            PURPLE,
            ORANGE,
            PINK,
            YELLOW,
            GOLD,
            BROWN,
            SALMON,
            TURQUOISE,
            PLUM,
            AQUA,
            BISQUE,
            BLUEVIOLET,
            BURLYWOOD,
            CADETBLUE,
            CHOCOLATE,
            CORAL,
            CORNFLOWERBLUE,
            CRIMSON,
            VIOLET,
            GOLDENROD,
            KHAKI,
            ORCHID,
            AQUAMARINE,
            SEAGREEN,
            SLATEBLUE,
            SKYBLUE,
            DARKBLUE,
            DARKCYAN,
            DARKGOLDENROD,
            DARKGRAY,
            DARKGREEN,
            DARKKHAKI,
            DARKMAGENTA,
            DARKOLIVEGREEN,
            DARKORANGE,
            DARKORCHID,
            DARKRED,
            DARKSALMON,
            DARKSEAGREEN,
            DARKSLATEBLUE,
            DARKSLATEGRAY,
            DARKTURQUOISE,
            DARKVIOLET,
            DEEPPINK,
            DEEPSKYBLUE,
            BLACK
    };

    /**
     * Named colors moved to nested class to initialize them only when they
     * are needed.
     */
    private static final Map<String, Color> NamedColors = new HashMap<>();
    static {
        NamedColors.put("aliceblue", ALICEBLUE);
        NamedColors.put("antiquewhite", ANTIQUEWHITE);
        NamedColors.put("aqua", AQUA);
        NamedColors.put("aquamarine", AQUAMARINE);
        NamedColors.put("azure", AZURE);
        NamedColors.put("beige", BEIGE);
        NamedColors.put("bisque", BISQUE);
        NamedColors.put("black", BLACK);
        NamedColors.put("blanchedalmond", BLANCHEDALMOND);
        NamedColors.put("blue", BLUE);
        NamedColors.put("blueviolet", BLUEVIOLET);
        NamedColors.put("brown", BROWN);
        NamedColors.put("burlywood", BURLYWOOD);
        NamedColors.put("cadetblue", CADETBLUE);
        NamedColors.put("chartreuse", CHARTREUSE);
        NamedColors.put("chocolate", CHOCOLATE);
        NamedColors.put("coral", CORAL);
        NamedColors.put("cornflowerblue", CORNFLOWERBLUE);
        NamedColors.put("cornsilk", CORNSILK);
        NamedColors.put("crimson", CRIMSON);
        NamedColors.put("cyan", CYAN);
        NamedColors.put("darkblue", DARKBLUE);
        NamedColors.put("darkcyan", DARKCYAN);
        NamedColors.put("darkgoldenrod", DARKGOLDENROD);
        NamedColors.put("darkgray", DARKGRAY);
        NamedColors.put("darkgreen", DARKGREEN);
        NamedColors.put("darkgrey", DARKGREY);
        NamedColors.put("darkkhaki", DARKKHAKI);
        NamedColors.put("darkmagenta", DARKMAGENTA);
        NamedColors.put("darkolivegreen", DARKOLIVEGREEN);
        NamedColors.put("darkorange", DARKORANGE);
        NamedColors.put("darkorchid", DARKORCHID);
        NamedColors.put("darkred", DARKRED);
        NamedColors.put("darksalmon", DARKSALMON);
        NamedColors.put("darkseagreen", DARKSEAGREEN);
        NamedColors.put("darkslateblue", DARKSLATEBLUE);
        NamedColors.put("darkslategray", DARKSLATEGRAY);
        NamedColors.put("darkslategrey", DARKSLATEGREY);
        NamedColors.put("darkturquoise", DARKTURQUOISE);
        NamedColors.put("darkviolet", DARKVIOLET);
        NamedColors.put("deeppink", DEEPPINK);
        NamedColors.put("deepskyblue", DEEPSKYBLUE);
        NamedColors.put("dimgray", DIMGRAY);
        NamedColors.put("dimgrey", DIMGREY);
        NamedColors.put("dodgerblue", DODGERBLUE);
        NamedColors.put("firebrick", FIREBRICK);
        NamedColors.put("floralwhite", FLORALWHITE);
        NamedColors.put("forestgreen", FORESTGREEN);
        NamedColors.put("fuchsia", FUCHSIA);
        NamedColors.put("gainsboro", GAINSBORO);
        NamedColors.put("ghostwhite", GHOSTWHITE);
        NamedColors.put("gold", GOLD);
        NamedColors.put("goldenrod", GOLDENROD);
        NamedColors.put("gray", GRAY);
        NamedColors.put("green", GREEN);
        NamedColors.put("greenyellow", GREENYELLOW);
        NamedColors.put("grey", GREY);
        NamedColors.put("honeydew", HONEYDEW);
        NamedColors.put("hotpink", HOTPINK);
        NamedColors.put("indianred", INDIANRED);
        NamedColors.put("indigo", INDIGO);
        NamedColors.put("ivory", IVORY);
        NamedColors.put("khaki", KHAKI);
        NamedColors.put("lavender", LAVENDER);
        NamedColors.put("lavenderblush", LAVENDERBLUSH);
        NamedColors.put("lawngreen", LAWNGREEN);
        NamedColors.put("lemonchiffon", LEMONCHIFFON);
        NamedColors.put("lightblue", LIGHTBLUE);
        NamedColors.put("lightcoral", LIGHTCORAL);
        NamedColors.put("lightcyan", LIGHTCYAN);
        NamedColors.put("lightgoldenrodyellow", LIGHTGOLDENRODYELLOW);
        NamedColors.put("lightgray", LIGHTGRAY);
        NamedColors.put("lightgreen", LIGHTGREEN);
        NamedColors.put("lightgrey", LIGHTGREY);
        NamedColors.put("lightpink", LIGHTPINK);
        NamedColors.put("lightsalmon", LIGHTSALMON);
        NamedColors.put("lightseagreen", LIGHTSEAGREEN);
        NamedColors.put("lightskyblue", LIGHTSKYBLUE);
        NamedColors.put("lightslategray", LIGHTSLATEGRAY);
        NamedColors.put("lightslategrey", LIGHTSLATEGREY);
        NamedColors.put("lightsteelblue", LIGHTSTEELBLUE);
        NamedColors.put("lightyellow", LIGHTYELLOW);
        NamedColors.put("lime", LIME);
        NamedColors.put("limegreen", LIMEGREEN);
        NamedColors.put("linen", LINEN);
        NamedColors.put("magenta", MAGENTA);
        NamedColors.put("maroon", MAROON);
        NamedColors.put("mediumaquamarine", MEDIUMAQUAMARINE);
        NamedColors.put("mediumblue", MEDIUMBLUE);
        NamedColors.put("mediumorchid", MEDIUMORCHID);
        NamedColors.put("mediumpurple", MEDIUMPURPLE);
        NamedColors.put("mediumseagreen", MEDIUMSEAGREEN);
        NamedColors.put("mediumslateblue", MEDIUMSLATEBLUE);
        NamedColors.put("mediumspringgreen", MEDIUMSPRINGGREEN);
        NamedColors.put("mediumturquoise", MEDIUMTURQUOISE);
        NamedColors.put("mediumvioletred", MEDIUMVIOLETRED);
        NamedColors.put("midnightblue", MIDNIGHTBLUE);
        NamedColors.put("mintcream", MINTCREAM);
        NamedColors.put("mistyrose", MISTYROSE);
        NamedColors.put("moccasin", MOCCASIN);
        NamedColors.put("navajowhite", NAVAJOWHITE);
        NamedColors.put("navy", NAVY);
        NamedColors.put("oldlace", OLDLACE);
        NamedColors.put("olive", OLIVE);
        NamedColors.put("olivedrab", OLIVEDRAB);
        NamedColors.put("orange", ORANGE);
        NamedColors.put("orangered", ORANGERED);
        NamedColors.put("orchid", ORCHID);
        NamedColors.put("palegoldenrod", PALEGOLDENROD);
        NamedColors.put("palegreen", PALEGREEN);
        NamedColors.put("paleturquoise", PALETURQUOISE);
        NamedColors.put("palevioletred", PALEVIOLETRED);
        NamedColors.put("papayawhip", PAPAYAWHIP);
        NamedColors.put("peachpuff", PEACHPUFF);
        NamedColors.put("peru", PERU);
        NamedColors.put("pink", PINK);
        NamedColors.put("plum", PLUM);
        NamedColors.put("powderblue", POWDERBLUE);
        NamedColors.put("purple", PURPLE);
        NamedColors.put("red", RED);
        NamedColors.put("rosybrown", ROSYBROWN);
        NamedColors.put("royalblue", ROYALBLUE);
        NamedColors.put("saddlebrown", SADDLEBROWN);
        NamedColors.put("salmon", SALMON);
        NamedColors.put("sandybrown", SANDYBROWN);
        NamedColors.put("seagreen", SEAGREEN);
        NamedColors.put("seashell", SEASHELL);
        NamedColors.put("sienna", SIENNA);
        NamedColors.put("silver", SILVER);
        NamedColors.put("skyblue", SKYBLUE);
        NamedColors.put("slateblue", SLATEBLUE);
        NamedColors.put("slategray", SLATEGRAY);
        NamedColors.put("slategrey", SLATEGREY);
        NamedColors.put("snow", SNOW);
        NamedColors.put("springgreen", SPRINGGREEN);
        NamedColors.put("steelblue", STEELBLUE);
        NamedColors.put("tan", TAN);
        NamedColors.put("teal", TEAL);
        NamedColors.put("thistle", THISTLE);
        NamedColors.put("tomato", TOMATO);
        NamedColors.put("transparent", TRANSPARENT);
        NamedColors.put("turquoise", TURQUOISE);
        NamedColors.put("violet", VIOLET);
        NamedColors.put("wheat", WHEAT);
        NamedColors.put("white", WHITE);
        NamedColors.put("whitesmoke", WHITESMOKE);
        NamedColors.put("yellow", YELLOW);
        NamedColors.put("yellowgreen", YELLOWGREEN);
    }
}
