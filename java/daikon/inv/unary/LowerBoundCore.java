// ***** This file is automatically generated from BoundCore.java.jpp

package daikon.inv.unary;

import daikon.*;
import daikon.inv.*;
import daikon.derive.unary.*;
import java.text.DecimalFormat;

import java.util.*;
import java.io.Serializable;

// One reason not to combine LowerBound and Upperbound is that they have
// separate justifications:  one may be justified when the other is not.

// What should we do if there are few values in the range?
// This can make justifying that invariant easier, because with few values
// naturally there are more instances of each value.
// This might also make justifying that invariant harder, because to get more
// than (say) twice the expected number of samples (under the assumption of
// uniform distribution) requires many samples.
// Which of these dominates?  Is the behavior what I want?

public class LowerBoundCore
  implements Serializable, Cloneable
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  final static int required_samples = 5; // for enoughSamples
  final static int required_samples_at_bound = 3; // for justification

  // min1  <  min2  <  min3
  public long  min1  = Long.MAX_VALUE ;
  int num_min1  = 0;
  long  min2  = Long.MAX_VALUE ;
  int num_min2  = 0;
  long  min3  = Long.MAX_VALUE ;
  int num_min3  = 0;
  long  max  = Long.MIN_VALUE ;

  int samples = 0;

  public Invariant wrapper;

  public LowerBoundCore (Invariant wrapper) {
    this.wrapper = wrapper;
  }

  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Error(); // can't happen
    }
  }

  private static DecimalFormat two_decimals = new java.text.DecimalFormat("#.##");

  public String repr() {
    long  modulus = calc_modulus();
    long  range = calc_range();
    double avg_samples_per_val = calc_avg_samples_per_val(modulus, range);
    return "min1=" + min1
      + ", num_min1=" + num_min1
      + ", min2=" + min2
      + ", num_min2=" + num_min2
      + ", min3=" + min3
      + ", num_min3=" + num_min3
      + ", max=" + max  + ", range=" + range + ", " +
      "avg_samp=" + two_decimals.format(avg_samples_per_val);
  }

  private double calc_avg_samples_per_val(long  modulus, double range) {
    double avg_samples_per_val =
      ((double) wrapper.ppt.num_mod_non_missing_samples()) * modulus / range;
    avg_samples_per_val = Math.min(avg_samples_per_val, 100);

    if (min1  == 0) {
      avg_samples_per_val /= 5;
    }

    return avg_samples_per_val;
  }

  private long  calc_range() {
    // If I used Math.abs, the order of arguments to minus would not matter.
    return  (max  - min1 ) + 1;
  }

  private long  calc_modulus() {
    // Need to reinstate this at some point.
    // {
    //   for (Iterator itor = wrapper.ppt.invs.iterator(); itor.hasNext(); ) {
    //     Invariant inv = (Invariant) itor.next();
    //     if ((inv instanceof Modulus) && inv.enoughSamples()) {
    //       modulus = ((Modulus) inv).modulus;
    //       break;
    //     }
    //   }
    // }
    return 1;
  }

  public void add_modified(long  value, int count) {
    samples += count;

    // System.out.println("LowerBoundCore"  + varNames() + ": "
    //                    + "add(" + value + ", " + modified + ", " + count + ")");

    long  v = value;

    if (v >  max ) max  = v;

    if (v == min1 ) {
      num_min1  += count;
    } else if (v <  min1 ) {
      min3  = min2 ;
      num_min3  = num_min2 ;
      min2  = min1 ;
      num_min2  = num_min1 ;
      min1  = v;
      num_min1  = count;
    } else if (v == min2 ) {
      num_min2  += count;
    } else if (v <  min2 ) {
      min3  = min2 ;
      num_min3  = num_min2 ;
      min2  = v;
      num_min2  = count;
    } else if (v == min3 ) {
      num_min3  += count;
    } else if (v <  min3 ) {
      min3  = v;
      num_min3  = count;
    }
  }

  public boolean enoughSamples() {
    return samples > required_samples;
  }

  // Convenience methods; avoid need for "Invariant." prefix.
  private final double prob_is_ge(double x, double goal) {
    return Invariant.prob_is_ge(x, goal);
  }
  private final double prob_and(double p1, double p2) {
    return Invariant.prob_and(p1, p2);
  }
  private final double prob_or(double p1, double p2) {
    return Invariant.prob_or(p1, p2);
  }

  public double computeProbability() {
    // The bound is justified if both of two conditions is satisfied:
    //  a. there are at least required_samples_at_bound samples at the bound
    //  b. one of the following holds:
    //      c. the bound has five times the expected number of samples
    //      d. the bound and the two next elements all have at least half
    //         the expected number of samples
    // The expected number of samples is the total number of samples
    // divided by the range of the samples; it is the average number
    // of samples at each point.

    // Rather than making the probability transition suddenly from 0 to 1,
    // we compute all the values (graded from 0 to 1), then combine them
    // according to the following formula:
    //  result = a + b - ab
    //  b = min(c, d)

    /// Compute value "a" from above.
    // This value is 0 if enough samples have been seen, 1 if only 1 sample
    // has been seen, otherwides grades between
    double bound_samples_prob = prob_is_ge(num_min1 , required_samples_at_bound);
    utilMDE.Assert.assertTrue(0 <= bound_samples_prob && bound_samples_prob <= 1, "bad bound_samples_prob = " + bound_samples_prob);

    long  modulus = calc_modulus();

    // Accept a bound if:
    //  * it contains more than twice as many elements as it ought to by
    //    chance alone, and that number is at least 3.
    //  * it and its predecessor/successor both contain more than half
    //    as many elements as they ought to by chance alone, and at
    //    least 3.

    double range = calc_range();
    double avg_samples_per_val = calc_avg_samples_per_val(modulus, range);

    // Value "c" from above
    double trunc_prob = prob_is_ge(num_min1 , 5*avg_samples_per_val);

    // Value "d" from above
    boolean unif_mod_OK = (( (min3  - min2 ) == modulus)
                           && ( (min2  - min1 ) == modulus));
    double unif_prob = 1;
    if (unif_mod_OK) {
      double half_avg_samp = avg_samples_per_val/2;
      double unif_prob_1 = prob_is_ge(num_min1 , half_avg_samp);
      double unif_prob_2 = prob_is_ge(num_min2 , half_avg_samp);
      double unif_prob_3 = prob_is_ge(num_min3 , half_avg_samp);
      unif_prob = Invariant.prob_and(unif_prob_1, unif_prob_2, unif_prob_3);
      // System.out.println("Unif_probs: " + unif_prob + " <-- " + unif_prob_1 + " " + unif_prob_2 + " " + unif_prob_3);
    }

    // Value "b" from above
    double bound_prob = prob_or(trunc_prob, unif_prob);

    // Final result
    double result = prob_and(bound_samples_prob, bound_prob);

    // System.out.println("LowerBoundCore.computeProbability(): ");
    // System.out.println("  " + repr());
    // System.out.println("  ppt=" + wrapper.ppt.name
    //                    + ", wrapper.ppt.num_mod_non_missing_samples()="
    //                    + wrapper.ppt.num_mod_non_missing_samples()
    //                    // + ", values=" + values
    //                    + ", avg_samples_per_val=" + avg_samples_per_val
    //                    + ", result = " + result
    //                    + ", bound_samples_prob=" + bound_samples_prob
    //                    + ", bound_prob=" + bound_prob
    //                    + ", trunc_prob=" + trunc_prob
    //                    + ", unif_prob=" + unif_prob);
    // PptSlice pptsg = (PptSlice) ppt;
    // System.out.println("  " + ppt.name + " ppt.values_cache.tuplemod_samples_summary()="
    //                    + pptsg.tuplemod_samples_summary());

    return result;
  }

  public boolean isSameFormula(LowerBoundCore  other)
  {
    return min1  == other. min1 ;
  }

  public boolean isExact() {
    return false;
  }

}
