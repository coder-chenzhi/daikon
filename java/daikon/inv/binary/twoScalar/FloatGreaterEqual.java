// ***** This file is automatically generated from IntComparisons.java.jpp

package daikon.inv.binary.twoScalar;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.sequence.*;
import daikon.inv.unary.scalar.*;
import daikon.inv.binary.sequenceScalar.*;
import daikon.inv.binary.twoSequence.*;
import daikon.derive.*;
import daikon.derive.unary.*;

import utilMDE.*;
import org.apache.log4j.Logger;
import java.util.*;

public final class FloatGreaterEqual
  extends TwoFloat 
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff FloatGreaterEqual invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;

  public static final Logger debug
    = Logger.getLogger("daikon.inv.binary.twoScalar.FloatGreaterEqual");

  private FloatValueTracker values_cache = new FloatValueTracker(8);

  protected Object clone() {
    FloatGreaterEqual result = (FloatGreaterEqual) super.clone();
    result.values_cache = (FloatValueTracker) values_cache.clone();
    return result;
  }

  protected FloatGreaterEqual(PptSlice ppt) {
    super(ppt);
  }

  public static FloatGreaterEqual instantiate(PptSlice ppt) {
    if (!dkconfig_enabled) return null;

    VarInfo var1 = ppt.var_infos[0];
    VarInfo var2 = ppt.var_infos[1];
    VarInfo seqvar1 = var1.isDerivedSequenceMember();
    VarInfo seqvar2 = var2.isDerivedSequenceMember();

    if (! (var1.file_rep_type.isFloat() && var2.file_rep_type.isFloat())) {
      return null;
    }

    if (debug.isDebugEnabled() || ppt.debugged) {
      debug.debug("FloatGreaterEqual.instantiate(" + ppt.name + ")"
                          + ", seqvar1=" + seqvar1
                          + ", seqvar2=" + seqvar2);
    }

    { // Tests involving sequence lengths.

      SequenceLength sl1 = null;
      if (var1.isDerived() && (var1.derived instanceof SequenceLength))
        sl1 = (SequenceLength) var1.derived;
      SequenceLength sl2 = null;
      if (var2.isDerived() && (var2.derived instanceof SequenceLength))
        sl2 = (SequenceLength) var2.derived;

      // Avoid "size(a)-1 cmp size(b)-1"; use "size(a) cmp size(b)" instead.
      if ((sl1 != null) && (sl2 != null)
          && ((sl1.shift == sl2.shift) && (sl1.shift != 0) || (sl2.shift != 0))) {
        // "size(a)-1 cmp size(b)-1"; should just use "size(a) cmp size(b)"
        return null;
      }
    }

    return new FloatGreaterEqual(ppt);
  }

  protected Invariant resurrect_done_swapped() {

    // we have no non-static member data, so we only need care about our type
    // As of now, the constructor chain is side-effect free;
    // let's hope it stays that way.
    return new FloatLessEqual(ppt);
  }

  // Look up a previously instantiated FloatGreaterEqual relationship.
  // Should this implementation be made more efficient?
  public static FloatGreaterEqual find(PptSlice ppt) {
    Assert.assertTrue(ppt.arity == 2);
    for (Iterator itor = ppt.invs.iterator(); itor.hasNext(); ) {
      Invariant inv = (Invariant) itor.next();
      if (inv instanceof FloatGreaterEqual)
        return (FloatGreaterEqual) inv;
    }
    return null;
  }

  public String repr() {
    return "FloatGreaterEqual" + varNames();
  }

  public String format_using(OutputFormat format) {
    String var1name = var1().name.name_using(format);
    String var2name = var2().name.name_using(format);

    if ((format == OutputFormat.DAIKON)
        || (format == OutputFormat.ESCJAVA)
        || (format == OutputFormat.JML)
        || (format == OutputFormat.JAVA)
        || (format == OutputFormat.IOA))
    {
      String comparator = ">=";

      return var1name + " " + comparator + " " + var2name;
    }

    if (format == OutputFormat.SIMPLIFY) {

    String comparator = ">=";

      return "(" + comparator + " " + var1name + " " + var2name + ")";
    }

    return format_unimplemented(format);
  }

  public void add_modified(double v1, double v2, int count) {
    // if (ppt.debugged) {
    //   System.out.println("FloatGreaterEqual" + ppt.varNames() + ".add_modified("
    //                      + v1 + "," + v2 + ", count=" + count + ")");
    // }
    if (!(v1 >= v2)) {
      destroyAndFlow();
      return;
    }

    values_cache.add(v1, v2);

  }

  // This is very tricky, because whether two variables are equal should
  // presumably be transitive, but it's not guaranteed to be so when using
  // this method and not dropping out all variables whose values are ever
  // missing.
  public double computeProbability() {
    if (falsified) {
      return Invariant.PROBABILITY_NEVER;
    }
    // Should perhaps check number of samples and be unjustified if too few
    // samples.

    // The reason for this multiplication is that there might be only a
    // very few possible values.  Example:  towers of hanoi has only 6
    // possible (pegA, pegB) pairs.
    return (Math.pow(.5, values_cache.num_values())
            * Math.pow(.99, ppt.num_mod_non_missing_samples()));

  }

  // For Comparison interface
  public double eq_probability() {
    if (isExact())
      return computeProbability();
    else
      return Invariant.PROBABILITY_NEVER;
  }

  public boolean isExact() {

    return false;
  }

  // // Temporary, for debugging
  // public void destroy() {
  //   if (debug.isDebugEnabled() || ppt.debugged) {
  //     System.out.println("FloatGreaterEqual.destroy(" + ppt.name + ")");
  //     System.out.println(repr());
  //     (new Error()).printStackTrace();
  //   }
  //   super.destroy();
  // }

  public void add(double v1, double v2, int mod_index, int count) {
    if (ppt.debugged) {
      System.out.println("FloatGreaterEqual" + ppt.varNames() + ".add("
                         + v1 + "," + v2
                         + ", mod_index=" + mod_index + ")"
                         + ", count=" + count + ")");
    }
    super.add(v1, v2, mod_index, count);
  }

  public boolean isSameFormula(Invariant other)
  {
    return true;
  }

  public boolean isExclusiveFormula(Invariant other)
  {
    // Also ought to check against LinearBinary, etc.

    if (other instanceof FloatLessThan)
      return true;

    return false;
  }

  public boolean isObviousImplied() {
    VarInfo var1 = ppt.var_infos[0];
    VarInfo var2 = ppt.var_infos[1];

    { // If we know x=y, then any x<=y or x>=y comparison is uninteresting
      FloatEqual ie = FloatEqual.find(ppt);
      if ((ie != null) /* && ie.enoughSamples() */ ) {
        return true;
      }
    }

    { // If we know x>y, then x>=y is uninteresting
      FloatGreaterThan igt = FloatGreaterThan.find(ppt);
      if ((igt != null) /* && igt.enoughSamples() */ ) {
        return true;
      }
    }

// #ifndef EQUAL
//     { // Check for comparisons against constants
//       if (var1.isConstant() || (var2.isConstant())) {
//         // One of the two variables is constant.  Figure out which one.
//         VarInfo varconst;
//         VarInfo varnonconst;
//         boolean var1const = var1.isConstant();
//         boolean can_be_lt;
//         boolean can_be_gt;
//         if (var1const) {
//           varconst = var1;
//           varnonconst = var2;
//           can_be_lt = core.can_be_gt;
//           can_be_gt = core.can_be_lt;
//         } else {
//           varconst = var2;
//           varnonconst = var1;
//           can_be_lt = core.can_be_lt;
//           can_be_gt = core.can_be_gt;
//         }
//         // Now "varconst" and "varnonconst" are set.
//         long valconst = ((Long) varconst.constantValue()).longValue();
//         PptSlice1 nonconstslice = ppt.parent.findSlice(varnonconst);
//         if (nonconstslice != null) {
//           if (can_be_lt) {
//             UpperBound ub = UpperBound.find(nonconstslice);
//             if ((ub != null) && ub.enoughSamples() && ub.core.max1 < valconst) {
//               return true;
//             }
//           } else {
//             LowerBound lb = LowerBound.find(nonconstslice);
//             if ((lb != null) && lb.enoughSamples() && lb.core.min1 > valconst) {
//               return true;
//             }
//           }
//         }
//       }
//     }
// #endif
    { // Sequence length tests

      SequenceLength sl1 = null;
      if (var1.isDerived() && (var1.derived instanceof SequenceLength))
        sl1 = (SequenceLength) var1.derived;
      SequenceLength sl2 = null;
      if (var2.isDerived() && (var2.derived instanceof SequenceLength))
        sl2 = (SequenceLength) var2.derived;

      // "size(a)-1 cmp size(b)-1" is never even instantiated;
      // use "size(a) cmp size(b)" instead.

      // This might never get invoked, as equality is printed out specially.
      VarInfo s1 = (sl1 == null) ? null : sl1.base;
      VarInfo s2 = (sl2 == null) ? null : sl2.base;
      if ((s1 != null) && (s2 != null)
          && (s1.equalitySet == s2.equalitySet)) {
        // lengths of equal arrays being compared
        return true;
      }

    }

//     { // Sequence sum tests
//       SequenceSum ss1 = null;
//       if (var1.isDerived() && (var1.derived instanceof SequenceSum))
//         ss1 = (SequenceSum) var1.derived;
//       SequenceSum ss2 = null;
//       if (var2.isDerived() && (var2.derived instanceof SequenceSum))
//         ss2 = (SequenceSum) var2.derived;
//       if ((ss1 != null) && (ss2 != null)) {
//         EltLowerBound lb = null;
//         EltUpperBound ub = null;
//         boolean shorter1 = false;
//         boolean shorter2 = false;
//         PptTopLevel parent = ppt.parent;
//         if (SubSequence.isObviousDerived(ss1.base, ss2.base)) {
//           lb = EltLowerBound.find(parent.findSlice(ss2.base));
//           ub = EltUpperBound.find(parent.findSlice(ss2.base));
//           shorter1 = true;
//         } else if (SubSequence.isObviousDerived(ss2.base, ss1.base)) {
//           lb = EltLowerBound.find(parent.findSlice(ss1.base));
//           ub = EltUpperBound.find(parent.findSlice(ss1.base));
//           shorter2 = true;
//         }
//         if ((lb != null) && (!lb.enoughSamples()))
//           lb = null;
//         if ((ub != null) && (!ub.enoughSamples()))
//           ub = null;
//         // We are comparing sum(a) to sum(b).
//         boolean shorter_can_be_lt;
//         boolean shorter_can_be_gt;
//         if (shorter1) {
//           shorter_can_be_lt = core.can_be_lt;
//           shorter_can_be_gt = core.can_be_gt;
//         } else {
//           shorter_can_be_lt = core.can_be_gt;
//           shorter_can_be_gt = core.can_be_lt;
//         }
//         if (shorter_can_be_lt
//             && (lb != null) && ((lb.core.min1 > 0)
//                                 || (core.can_be_eq && lb.core.min1 == 0))) {
//           return true;
//         }
//         if (shorter_can_be_gt
//             && (ub != null) && ((ub.core.max1 < 0)
//                                 || (core.can_be_eq && ub.core.max1 == 0))) {
//           return true;
//         }
//       }
//     }

//     {
//       // (Is this test ever true?  Aren't SeqINTEQUAL and
//       // FloatGreaterEqual instantiated at the same time?  Apparently not:  see
//       // the printStackTrace below.
//
//       // For each sequence variable, if this is an obvious member, and
//       // it has the same invariant, then this one is obvious.
//       PptTopLevel pptt = ppt.parent;
//       for (int i=0; i<pptt.var_infos.length; i++) {
//         VarInfo vi = pptt.var_infos[i];
//         if (Member.isObviousMember(var1, vi)) {
//           PptSlice2 other_slice = pptt.findSlice_unordered(vi, var2);
//           if (other_slice != null) {
//             SeqINTEQUAL sic = SeqINTEQUAL.find(other_slice);
//             if ((sic != null)
//                 && sic.enoughSamples()) {
//               // This DOES happen; verify by running on replace.c
//               // System.out.println("Surprise:  this can happen (var1 in INTEQUAL).");
//               // new Error().printStackTrace();
//               return true;
//             }
//           }
//         }
//         if (Member.isObviousMember(var2, vi)) {
//           PptSlice2 other_slice = pptt.findSlice_unordered(vi, var1);
//           if (other_slice != null) {
//             SeqINTEQUAL sic = SeqINTEQUAL.find(other_slice);
//             if ((sic != null)
//                 && sic.enoughSamples()) {
//               // This DOES happen
//               // System.out.println("Surprise:  this can happen (var2 in FloatGreaterEqual).");
//               // new Error().printStackTrace();
//               return true;
//             }
//           }
//         }
//       }
//     }

    return false;
  } // isObviousImplied

}
