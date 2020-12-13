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

package smile.validation;

import java.util.Arrays;
import java.util.function.BiFunction;
import smile.classification.Classifier;
import smile.classification.DataFrameClassifier;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.math.MathEx;
import smile.regression.Regression;
import smile.regression.DataFrameRegression;
import smile.util.IntSet;

/**
 * The bootstrap is a general tool for assessing statistical accuracy. The basic
 * idea is to randomly draw samples with replacement from the training data,
 * each samples the same size as the original training set. This is done many
 * times (say k = 100), producing k bootstrap datasets. Then we refit the model
 * to each of the bootstrap datasets and examine the behavior of the fits over
 * the k replications.
 *
 * @author Haifeng Li
 */
public interface Bootstrap {
    /**
     * Bootstrap sampling.
     * @param n the number of samples.
     * @param k the number of rounds of bootstrap.
     */
    static Bag[] of(int n, int k) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid sample size: " + n);
        }

        if (k < 0) {
            throw new IllegalArgumentException("Invalid number of bootstrap: " + k);
        }

        Bag[] bags = new Bag[k];

        for (int j = 0; j < k; j++) {
            boolean[] hit = new boolean[n];
            int hits = 0;

            int[] train = new int[n];
            for (int i = 0; i < n; i++) {
                int r = MathEx.randomInt(n);
                train[i] = r;
                if (!hit[r]) {
                    hits++;
                    hit[r] = true;
                }
            }

            int[] test = new int[n - hits];
            for (int i = 0, p = 0; i < n; i++) {
                if (!hit[i]) {
                    test[p++] = i;
                }
            }

            bags[j] = new Bag(train, test);
        }

        return bags;
    }


    /**
     * Stratified bootstrap sampling.
     *
     * @param category the strata labels.
     * @param k the number of rounds of bootstrap.
     */
    static Bag[] of(int[] category, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("Invalid number of bootstrap: " + k);
        }

        int[] unique = MathEx.unique(category);
        int m = unique.length;

        Arrays.sort(unique);
        IntSet encoder = new IntSet(unique);

        int n = category.length;
        int[] y = category;
        if (unique[0] != 0 || unique[m-1] != m-1) {
            y = new int[n];
            for (int i = 0; i < n; i++) {
                y[i] = encoder.indexOf(category[i]);
            }
        }

        // # of samples in each strata
        int[] ni = new int[m];
        for (int i : y) ni[i]++;

        // samples in each strata
        int[][] strata = new int[m][];
        for (int i = 0; i < m; i++) {
            strata[i] = new int[ni[i]];
        }

        int[] pos = new int[m];
        for (int i = 0; i < n; i++) {
            int j = y[i];
            strata[j][pos[j]++] = i;
        }

        Bag[] bags = new Bag[k];
        for (int round = 0; round < k; round++) {
            boolean[] hit = new boolean[n];
            int hits = 0;

            int l = 0;
            int[] train = new int[n];
            for (int i = 0; i < m; i++) {
                int[] stratum = strata[i];
                int size = ni[i];
                for (int j = 0; j < size; j++) {
                    int sample = stratum[MathEx.randomInt(size)];
                    train[l++] = sample;
                    if (!hit[sample]) {
                        hits++;
                        hit[sample] = true;
                    }
                }
            }

            int[] test = new int[n - hits];
            for (int i = 0, p = 0; i < n; i++) {
                if (!hit[i]) {
                    test[p++] = i;
                }
            }

            bags[round] = new Bag(train, test);
        }

        return bags;
    }

    /**
     * Runs classification bootstrap validation.
     * @param k k-fold bootstrap sampling.
     * @param x the samples.
     * @param y the sample labels.
     * @param trainer the lambda to train a model.
     * @return the validation results.
     */
    static <T, M extends Classifier<T>> ClassificationValidations<M> classification(int k, T[] x, int[] y, BiFunction<T[], int[], M> trainer) {
        return ClassificationValidation.of(of(x.length, k), x, y, trainer);
    }

    /**
     * Runs classification bootstrap validation.
     * @param k k-fold bootstrap sampling.
     * @param formula the model specification.
     * @param data the training/validation data.
     * @param trainer the lambda to train a model.
     * @return the validation results.
     */
    static <M extends DataFrameClassifier> ClassificationValidations<M> classification(int k, Formula formula, DataFrame data, BiFunction<Formula, DataFrame, M> trainer) {
        return ClassificationValidation.of(of(data.size(), k), formula, data, trainer);
    }

    /**
     * Runs regression bootstrap validation.
     * @param k k-fold bootstrap sampling.
     * @param x the samples.
     * @param y the response variable.
     * @param trainer the lambda to train a model.
     * @return the validation results.
     */
    static <T, M extends Regression<T>> RegressionValidations<M> regression(int k, T[] x, double[] y, BiFunction<T[], double[], M> trainer) {
        return RegressionValidation.of(of(x.length, k), x, y, trainer);
    }

    /**
     * Runs regression bootstrap validation.
     * @param k k-fold bootstrap sampling.
     * @param formula the model specification.
     * @param data the training/validation data.
     * @param trainer the lambda to train a model.
     * @return the validation results.
     */
    static <M extends DataFrameRegression> RegressionValidations<M> regression(int k, Formula formula, DataFrame data, BiFunction<Formula, DataFrame, M> trainer) {
        return RegressionValidation.of(of(data.size(), k), formula, data, trainer);
    }
}
