/*
 * Copyright (c) 2010-2024 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 */
package smile.vision;

import java.util.function.IntFunction;
import smile.deep.activation.ActivationFunction;
import smile.deep.activation.ReLU;
import smile.deep.layer.BatchNorm2dLayer;
import smile.deep.layer.Conv2dLayer;
import smile.deep.layer.SequentialBlock;
import smile.deep.tensor.Tensor;

/**
 * Convolution2d-Normalization-Activation block.
 *
 * @author Haifeng Li
 */
public class Conv2dNormActivation extends SequentialBlock {
    private final Conv2dLayer conv;
    private final BatchNorm2dLayer norm;
    private final ActivationFunction activation;

    /**
     * Conv2dNormActivation configurations.
     * @param in the number of input channels.
     * @param out the number of output channels/features.
     * @param kernel the window/kernel size.
     * @param stride controls the stride for the cross-correlation.
     * @param padding controls the amount of padding applied on both sides.
     * @param dilation controls the spacing between the kernel points.
     * @param groups controls the connections between inputs and outputs.
     *              The in channels and out channels must both be divisible by groups.
     * @param norm the functor to create the normalization layer.
     * @param activation the activation function.
     */
    public record Options(int in, int out, int kernel, int stride, int padding, int dilation, int groups,
                          IntFunction<BatchNorm2dLayer> norm,
                          ActivationFunction activation) {
        public Options {
            if (padding < 0) {
                padding = (kernel - 1) / 2 * dilation;
            }
        }

        public Options(int in, int out, int kernel) {
            this(in, out, kernel, channels -> new BatchNorm2dLayer(channels), new ReLU(true));
        }

        public Options(int in, int out, int kernel, IntFunction<BatchNorm2dLayer> norm, ActivationFunction activation) {
            this(in, out, kernel, 1, norm, activation);
        }

        public Options(int in, int out, int kernel, int stride, IntFunction<BatchNorm2dLayer> norm, ActivationFunction activation) {
            this(in, out, kernel, stride, 1, norm, activation);
        }

        public Options(int in, int out, int kernel, int stride, int groups, IntFunction<BatchNorm2dLayer> norm, ActivationFunction activation) {
            this(in, out, kernel, stride, -1, 1, groups, norm, activation);
        }
    }

    /**
     * Constructor.
     */
    public Conv2dNormActivation(Options options) {
        super("Conv2dNormActivation");

        this.conv = new Conv2dLayer(options.in, options.out, options.kernel, options.stride, options.padding,
                options.dilation, options.groups, false, "zeros");
        this.norm = options.norm.apply(options.out);
        this.activation = options.activation;
        add(conv);
        add(norm);
        if (activation != null) {
            add(activation);
        }
    }

    @Override
    public Tensor forward(Tensor input) {
        Tensor t1 = conv.forward(input);
        Tensor t2 = norm.forward(t1);
        t1.close();

        Tensor output = t2;
        if (activation != null) {
            output = activation.apply(t2);
            if (!activation.isInplace()) {
                t2.close();
            }
        }
        return output;
    }
}
