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

package smile

import java.util.function.{Function, ToIntFunction}
import smile.data.Tuple
import smile.util.time

/** Sequence labeling algorithms.
  *
  * @author Haifeng Li
  */
package object sequence {
  /** First-order Hidden Markov Model. A hidden Markov model (HMM) is a statistical
    * Markov model in which the system being modeled is assumed to be a Markov
    * process with unobserved (hidden) states. An HMM can be considered as the
    * simplest dynamic Bayesian network. <p> In a regular Markov model, the state
    * is directly visible to the observer, and therefore the state transition
    * probabilities are the only parameters. In a hidden Markov model, the state is
    * not directly visible, but output, dependent on the state, is visible. Each
    * state has a probability distribution over the possible output tokens.
    * Therefore the sequence of tokens generated by an HMM gives some information
    * about the sequence of states.
    *
    * @param observations the observation sequences, of which symbols take
    *                     values in [0, n), where n is the number of unique symbols.
    * @param labels the state labels of observations, of which states take
    *               values in [0, p), where p is the number of hidden states.
    */
  def hmm(observations: Array[Array[Int]], labels: Array[Array[Int]]): HMM = time("Hidden Markov Model") {
    HMM.fit(observations, labels)
  }

  /** Trains a first-order Hidden Markov Model.
    *
    * @param observations the observation sequences, of which symbols take
    *                     values in [0, n), where n is the number of unique symbols.
    * @param labels the state labels of observations, of which states take
    *               values in [0, p), where p is the number of hidden states.
    */
  def hmm[T <: AnyRef](observations: Array[Array[T]], labels: Array[Array[Int]], ordinal: ToIntFunction[T]): HMMLabeler[T] = time("Hidden Markov Model") {
    HMMLabeler.fit(observations, labels, ordinal)
  }

  /** First-order linear conditional random field. A conditional random field is a
    * type of discriminative undirected probabilistic graphical model. It is most
    * often used for labeling or parsing of sequential data.
    *
    * A CRF is a Markov random field that was trained discriminatively. Therefore it is not necessary
    * to model the distribution over always observed variables, which makes it
    * possible to include arbitrarily complicated features of the observed
    * variables into the model.
    *
    * ====References:====
    *  - J. Lafferty, A. McCallum and F. Pereira. Conditional random fields: Probabilistic models for segmenting and labeling sequence data. ICML, 2001.</li>
    *  - Thomas G. Dietterich, Guohua Hao, and Adam Ashenfelter. Gradient Tree Boosting for Training Conditional Random Fields. JMLR, 2008.
    *
    * @param sequences the observation attribute sequences.
    * @param labels sequence labels.
    * @param ntrees the number of trees/iterations.
    * @param maxDepth the maximum depth of the tree.
    * @param maxNodes the maximum number of leaf nodes in the tree.
    * @param nodeSize the number of instances in a node below which the tree will
    * not split, setting nodeSize = 5 generally gives good results.
    * @param shrinkage the shrinkage parameter in (0, 1] controls the learning rate of procedure.
    */
  def crf(sequences: Array[Array[Tuple]], labels: Array[Array[Int]], ntrees: Int = 100, maxDepth: Int = 20, maxNodes: Int = 100, nodeSize: Int = 5, shrinkage: Double = 1.0): CRF = time("CRF") {
    CRF.fit(sequences, labels, ntrees, maxDepth, maxNodes, nodeSize, shrinkage)
  }

  /** First-order linear conditional random field. A conditional random field is a
    * type of discriminative undirected probabilistic graphical model. It is most
    * often used for labeling or parsing of sequential data.
    *
    * A CRF is a Markov random field that was trained discriminatively. Therefore it is not necessary
    * to model the distribution over always observed variables, which makes it
    * possible to include arbitrarily complicated features of the observed
    * variables into the model.
    *
    * ====References:====
    *  - J. Lafferty, A. McCallum and F. Pereira. Conditional random fields: Probabilistic models for segmenting and labeling sequence data. ICML, 2001.</li>
    *  - Thomas G. Dietterich, Guohua Hao, and Adam Ashenfelter. Gradient Tree Boosting for Training Conditional Random Fields. JMLR, 2008.
    *
    * @param sequences the observation attribute sequences.
    * @param labels sequence labels.
    * @param ntrees the number of trees/iterations.
    * @param maxDepth the maximum depth of the tree.
    * @param maxNodes the maximum number of leaf nodes in the tree.
    * @param nodeSize the number of instances in a node below which the tree will
    * not split, setting nodeSize = 5 generally gives good results.
    * @param shrinkage the shrinkage parameter in (0, 1] controls the learning rate of procedure.
    */
  def gcrf[T <: AnyRef](sequences: Array[Array[T]], labels: Array[Array[Int]], features: Function[T, Tuple], ntrees: Int = 100, maxDepth: Int = 20, maxNodes: Int = 100, nodeSize: Int = 5, shrinkage: Double = 1.0): CRFLabeler[T] = time("CRF") {
    CRFLabeler.fit(sequences, labels, features, ntrees, maxDepth, maxNodes, nodeSize, shrinkage)
  }

  /** Hacking scaladoc [[https://github.com/scala/bug/issues/8124 issue-8124]].
    * The user should ignore this object. */
  object $dummy
}
