// ***** This file is automatically generated from SubSequence.java.jpp

package daikon.inv.binary.twoSequence;

import daikon.*;
import daikon.inv.*;
import daikon.derive.*;
import daikon.derive.unary.*;
import daikon.derive.binary.*;
import daikon.inv.unary.sequence.EltOneOfFloat;
import daikon.VarInfoName.QuantHelper;
import daikon.VarInfoName.QuantHelper.QuantifyReturn;

import utilMDE.*;
import org.apache.log4j.Category;
import java.util.*;

public class SubSequenceFloat
  extends TwoSequenceFloat
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  private static final Category debug =
    Category.getInstance("daikon.inv.binary.twoSequence.SubSequenceFloat" );

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff SubSequence invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;

  public boolean var1_in_var2 = true;
  public boolean var2_in_var1 = true;

  protected SubSequenceFloat (PptSlice ppt) {
    super(ppt);
  }

  public static SubSequenceFloat  instantiate(PptSlice ppt) {
    if (!dkconfig_enabled) return null;

    VarInfo var1 = ppt.var_infos[0];
    VarInfo var2 = ppt.var_infos[1];
    // System.out.println("SubSequence.isObviousDerived(" + format() + ") = "
    //                    + ((SubSequence.isObviousDerived(var1(), var2()))
    //                       || (SubSequence.isObviousDerived(var2(), var1()))));
    if ((SubSequenceFloat.isObviousDerived(var1, var2))
        || (SubSequenceFloat.isObviousDerived(var2, var1))) {
      Global.implied_noninstantiated_invariants++;
      return null;
    }

    if (!var1.aux.getFlag(VarInfoAux.HAS_ORDER) ||
        !var2.aux.getFlag(VarInfoAux.HAS_ORDER)) {
      // Doesn't make sense to instantitate if order doens't matter
      return null;
    }

    if (debug.isDebugEnabled()) {
      debug.debug ("Instantiating " + var1.name + " and " + var2.name);
    }

    return new SubSequenceFloat (ppt);
  }

  protected Invariant resurrect_done_swapped() {
    // was a swap
    boolean tmp = var1_in_var2;
    var1_in_var2 = var2_in_var1;
    var2_in_var1 = tmp;
    return this;
  }

  public String repr() {
    return "SubSequenceFloat"  + varNames() + ": "
      + "1in2=" + var1_in_var2
      + ",2in1=" + var2_in_var1
      + ",falsified=" + falsified;
  }

  public String format_using(OutputFormat format) {
    if (format == OutputFormat.DAIKON) return format_daikon();
    if (format == OutputFormat.IOA) return format_ioa();
    // disable simplify format for now; may be buggy
    // if (format == OutputFormat.SIMPLIFY) return format_simplify();
    if (format == OutputFormat.JML) return format_jml();

    return format_unimplemented(format);
  }

  public String format_daikon() {
    String v1 = var1().name.name();
    String v2 = var2().name.name();
    if (var1_in_var2 && var2_in_var1) {
      return v1 + " is a {sub,super}sequence of " + v2;
    } else {
      String subvar = (var1_in_var2 ? v1 : v2);
      String supervar = (var1_in_var2 ? v2 : v1);
      return subvar + " is a subsequence of " + supervar;
    }
  }

  /* IOA */
  public String format_ioa() {
    String result;
    String v1 = var1().name.ioa_name();
    String v2 = var2().name.ioa_name();
    if (var1_in_var2 && var2_in_var1) {
      result = "("+v1+" \\subseteq "+v2+") /\\ ("+v2+" \\subseteq "+v1+")";
    } else {
      String subvar = (var1_in_var2 ? v1 : v2);
      String supervar = (var1_in_var2 ? v2 : v1);
      result = subvar + " \\subseteq " + supervar;
    }

    if (var1().isIOAArray() || var2().isIOAArray()) {
      result += " *** (Invalid syntax for arrays)";
    }

    return result;
  }

  public String format_simplify() {
    VarInfo subvar = (var1_in_var2 ? var1() : var2());
    VarInfo supervar = (var1_in_var2 ? var2() : var1());
    // (exists k s.t. (forall i, j; (i bounds & j bounds & (i = j + k)) ==> ...))

    QuantifyReturn qret = QuantHelper.quantify(new VarInfoName[] { subvar.name, supervar.name} );
    Assert.assertTrue(qret.bound_vars.size() == 2);
    Assert.assertTrue(qret.root_primes.length == 2);

    // These variables are, in order: Example element, free Index
    // variable, Lower bound, Upper bound, Span
    String aE, aI, aL, aH, aS; // a = subsequence
    String bE, bI, bL, bH, bS; // b = supersequence
    {
      VarInfoName[] boundv;

      boundv = (VarInfoName[]) qret.bound_vars.get(0);
      aE = qret.root_primes[0].simplify_name();
      aI = boundv[0].simplify_name();
      aL = boundv[1].simplify_name();
      aH = boundv[2].simplify_name();
      aS = "(+ (- " + aH + " " + aL + ") 1)";

      boundv = (VarInfoName[]) qret.bound_vars.get(1);
      bE = qret.root_primes[1].simplify_name();
      bI = boundv[0].simplify_name();
      bL = boundv[1].simplify_name();
      bH = boundv[2].simplify_name();
      bS = "(+ (- " + bH + " " + bL + ") 1)";
    }

    // This invariant would not have been given data if a value was
    // missing - for example, if a slice had a negative length.  We
    // must predicate this invariant on the values being sensible.

    String sensible = "(AND (>= " + aS + " 0) (>= " + bS + " 0))";

    // This invariant would have been falsified if the subsequence A
    // length was ever zero.  Also, this invariant would have been
    // falsified if the subsequence A was ever longer than the
    // supersequence B.

    String length_stmt = "(AND (NEQ " + aS + " 0) (>= " + bS + " " + aS + "))";

    // Subsequence means that there exists an offset in supersequence
    // B, where (1) the offset is non-negative, (2) the offset doesn't
    // cause the matching to push past the end of B, and (3) for all
    // indices less than the span of subsequence A, (4) the elements
    // starting from A_low and B_low+offset are equal.

    String index = "|__index|";
    String shift = "|__shift|";
    String subseq_stmt =
      "(EXISTS (" + shift + ") (AND " +
      "(<= 0 " + shift + ") " +                          // 1
      "(<= (+ " + shift + " " + aS + ") " + bS + ") " +  // 2
      "(FORALL (" + index + ") (IMPLIES (AND (<= 0 " + index + ") (< " + index + " " + aS + ")) " + // 3
      "(EQ " +
      UtilMDE.replaceString(aE, aI, "(+ " + aL + " " + index + ")") + " " +
      UtilMDE.replaceString(bE, bI, "(+ (+ " + bL + " " + index +") " + shift + ")") + ")))))";

    // So, when this in sensible, we know that both the length and
    // subseq statements hold.

    String result = "(IMPLIES " + sensible + " (AND " + length_stmt + " " + subseq_stmt + "))";
    return result;
  }

  public String format_jml() {
    // This appears to be right, but returns the wrong results... swapping these vars
    // VarInfo subvar = (var1_in_var2 ? var1() : var2());
    // VarInfo supervar = (var2_in_var1 ? var1() : var2());

    VarInfo supervar = (var1_in_var2 ? var1() : var2());
    VarInfo subvar = (var2_in_var1 ? var1() : var2());

    // System.out.println("subvar: "+subvar.name.name());
    // System.out.println("supervar: "+supervar.name.name());

    // Bound the following quantification
    QuantifyReturn superQuantifyReturn = QuantHelper.quantify(new VarInfoName[] {supervar.name});
    VarInfoName superIndexName = ((VarInfoName [])superQuantifyReturn.bound_vars.get(0))[0];
    String superQuantifyResults[] = QuantHelper.format_jml(superQuantifyReturn,false,false);

    QuantifyReturn subQuantifyReturn = QuantHelper.quantify(new VarInfoName[] {subvar.name});
    VarInfoName subIndexGroup[] = (VarInfoName [])subQuantifyReturn.bound_vars.get(0);

    VarInfoName subIndexName = subIndexGroup[0];

    // If indicies have same name modify the quantify return before forming string
    if (superIndexName.equals(subIndexName)) {
      subIndexName = subIndexGroup[0] = VarInfoName.parse(new String(new char [] {(char)((int)superIndexName.name().charAt(0)+1)})); // could cause name conflict... unsure of what to do
    }

    String subQuantifyResults[] = QuantHelper.format_jml(subQuantifyReturn);

    VarInfoName superName = supervar.name;
    String superTermName = "";

    if (superName instanceof VarInfoName.Elements) {
      VarInfoName.Elements el = (VarInfoName.Elements)superName;
      superTermName = el.term.jml_name();
    } else if (superName instanceof VarInfoName.Slice) {
      VarInfoName.Slice sl = (VarInfoName.Slice)superName;
      superTermName = sl.sequence.term.jml_name();
    }

    return superQuantifyResults[0] + subQuantifyResults[0] + superTermName + "[" + superIndexName.jml_name() + "+" + subIndexName.jml_name() + "] == " +
      subQuantifyResults[1] + subQuantifyResults[2] + superQuantifyResults[2];
  }

  public void add_modified(double [] a1, double [] a2, int count) {
    Assert.assertTrue(var1_in_var2 || var2_in_var1);

    boolean new_var1_in_var2 = var1_in_var2;
    boolean new_var2_in_var1 = var2_in_var1;
    boolean changed = false;
    if (var1_in_var2 && (ArraysMDE.indexOf(a2, a1) == -1)) {
      new_var1_in_var2 = false;
      changed = true;
    }
    if (var2_in_var1 && (ArraysMDE.indexOf(a1, a2) == -1)) {
      new_var1_in_var2 = false;
      changed = true;
    }

    if (! changed)
      return;

    if (! (new_var1_in_var2 || new_var2_in_var1)) {
      flowThis();
      destroy();
      return;
    }

    // changed == true but not dead yet
    flowClone();
    var1_in_var2 = new_var1_in_var2;
    var2_in_var1 = new_var2_in_var1;
  }

  protected double computeProbability() {
    if (falsified)
      return Invariant.PROBABILITY_NEVER;
    else if (var1_in_var2 && var2_in_var1)
      return Invariant.PROBABILITY_UNJUSTIFIED;
    else
      return Invariant.PROBABILITY_JUSTIFIED;
  }

  // Convenience name to make this easier to find.
  public static boolean isObviousSubSequence(VarInfo subvar, VarInfo supervar) {
    return isObviousDerived(subvar, supervar);
  }

  /**
   * Returns true if the two original variables are related in a way
   * that makes subsequence or subset detection not informative.
   **/
  // This is abstracted out so it can be called by SuperSequence as well.
  public static boolean isObviousDerived(VarInfo subvar, VarInfo supervar) {

     if (debug.isDebugEnabled()) {
      debug.debug("static SubSequenceFloat.isObviousDerived(" + subvar.name +
                  ", " + supervar.name + ") " + subvar.isDerivedSubSequenceOf() +
                  " " + supervar.isDerivedSubSequenceOf());
    }

        // For unions and intersections, it probably doesn't make sense to
    // do subsequence or subset detection.  This is mainly to prevent
    // invariants of the form (x subset of union(x, y)) but this means
    // we also miss those of the form (z subset of union(x,y)) which
    // might be useful.  Subsequence, however, seems totally useless
    // on unions and intersections.
    if (supervar.derived instanceof  SequenceFloatIntersection  ||
        supervar.derived instanceof SequenceFloatUnion  ||
        subvar.derived instanceof SequenceFloatIntersection  ||
        subvar.derived instanceof SequenceFloatUnion  ) {
      debug.debug ("Returning true because of union or intersection");
      return true;
    }

    if (subvar.derived instanceof SequencesPredicateFloat) {
      // It's not useful that predicate(x[], b[]) is a subsequence or subset
      // of x[]
      SequencesPredicateFloat  derived = (SequencesPredicateFloat) subvar.derived;
      if (derived.var1().equals(supervar)) {
        debug.debug ("Returning true because of predicate slicing");
        return true;
      }
    }

    VarInfo subvar_super = subvar.isDerivedSubSequenceOf();
    if (subvar_super == null)
      // If it's not a union, intersection or a subsequence, it's not obvious
      return false;

    if (subvar_super == supervar) {
      // System.out.println("SubSequence.isObviousDerived(" + subvar.name + ", " + supervar.name + ") = true");
      // System.out.println("  details: subvar_super=" + subvar_super.name + "; supervar_super=" + supervar.isDerivedSubSequenceOf() == null ? "null" : supervar.isDerivedSubSequenceOf().name);
      return true;
    }

    VarInfo supervar_super = supervar.isDerivedSubSequenceOf();
    if (subvar_super == supervar_super) {
      // both sequences are derived from the same supersequence
      if ((subvar.derived instanceof SequenceFloatSubsequence)
          && (supervar.derived instanceof SequenceFloatSubsequence)) {
        SequenceFloatSubsequence  sss1 = (SequenceFloatSubsequence) subvar.derived;
        SequenceFloatSubsequence  sss2 = (SequenceFloatSubsequence) supervar.derived;
        VarInfo index1 = sss1.sclvar();
        int shift1 = sss1.index_shift;
        boolean start1 = sss1.from_start;
        VarInfo index2 = sss2.sclvar();
        int shift2 = sss2.index_shift;
        boolean start2 = sss2.from_start;
        if (start1 == start2)
          if (VarInfo.compare_vars(index1, shift1, index2, shift2, start1)) {
            // System.out.println("Obvious subsequence: " + subvar.name + " " + supervar.name + "; " + index1.name + " " + index2.name);
            return true;
          }
      } else if ((subvar.derived instanceof SequenceStringSubsequence)
                 && (supervar.derived instanceof SequenceStringSubsequence)) {
        // Copied from just above
        SequenceStringSubsequence  sss1 = (SequenceStringSubsequence) subvar.derived;
        SequenceStringSubsequence  sss2 = (SequenceStringSubsequence) supervar.derived;
        VarInfo index1 = sss1.sclvar();
        int shift1 = sss1.index_shift;
        boolean start1 = sss1.from_start;
        VarInfo index2 = sss2.sclvar();
        int shift2 = sss2.index_shift;
        boolean start2 = sss2.from_start;
        if (start1 == start2)
          if (VarInfo.compare_vars(index1, shift1, index2, shift2, start1)) {
            // System.out.println("Obvious subsequence: " + subvar.name + " " + supervar.name);
            return true;
          }
      } else {
        Assert.assertTrue(false, "how can this happen? " + subvar.name + " " + subvar.derived.getClass() + " " + supervar.name + " " + supervar.derived.getClass());
      }

    }

    /// To finish later.
    // VarInfo supervar_super = supervar.isDerivedSubSequenceOf();
    // if (supervar_super == null)
    //   return false;
    // if (subvar_super == supervar_super) {
    //   // both variables are derived from the same sequence; eg,
    //   // a[0..i] and a[0..j-1]
    //   // Use compare_vars to determine whether the relationship

    return false;
  }

  // Look up a previously instantiated SubSequence relationship.
  public static SubSequenceFloat  find(PptSlice ppt) {
    Assert.assertTrue(ppt.arity == 2);
    for (Iterator itor = ppt.invs.iterator(); itor.hasNext(); ) {
      Invariant inv = (Invariant) itor.next();
      if (inv instanceof SubSequenceFloat)
        return (SubSequenceFloat) inv;
    }
    return null;
  }

  // Two ways to go about this:
  //   * look at all subseq relationships, see if one is over a variable of
  //     interest
  //   * look at all variables derived from the

  // (Seems overkill to check for other transitive relationships.
  // Eventually that is probably the right thing, however.)
  public boolean isObviousImplied() {

    // System.out.println("checking isObviousImplied for: " + format());

    if (var1_in_var2 && var2_in_var1) {
      // Suppress this invariant; we should get an equality invariant from
      // elsewhere.
      return true;
    } else {
      VarInfo subvar = (var1_in_var2 ? var1() : var2());
      VarInfo supervar = (var1_in_var2 ? var2() : var1());

      PptTopLevel ppt_parent = (PptTopLevel) ppt.parent;

      // If the elements of supervar are always the same (EltOneOf),
      // we aren't going to learn anything new from this invariant,
      // since each sequence should have an EltOneOf over it.
      if (false) {
        System.out.println("Checking " + format());
        PptSlice1 slice = ppt_parent.findSlice(supervar);
        if (slice == null) {
          System.out.println("No slice: ppt=" + ppt + " parent =" + ppt.parent);
        } else {
          System.out.println("Slice var =" + slice.var_info);
          Iterator superinvs = slice.invs.iterator();
          while (superinvs.hasNext()) {
            Object superinv = superinvs.next();
            System.out.println("Inv = " + superinv);
            if (superinv instanceof EltOneOfFloat) {
              EltOneOfFloat  eltinv = (EltOneOfFloat) superinv;
              if (eltinv.num_elts() > 0) {
                System.out.println(format() + " obvious because of " + eltinv.format());
                return true;
              }
            }
          }
        }
      }

      // Also need to check A[0..i] subseq A[0..j] via compare_vars.

      // A subseq B[0..n] => A subseq B

      List derivees = supervar.derivees();
      // For each variable derived from supervar ("B")
      for (int i=0; i<derivees.size(); i++) {
        Derivation der = (Derivation) derivees.get(i);
        if (der instanceof SequenceScalarSubsequence) {
          // If that variable is "B[0..n]"
          VarInfo supervar_part = der.getVarInfo();
          // if (supervar_part.isCanonical()) // [INCR]
          {
            PptSlice ss_ppt = ppt_parent.findSlice_unordered(subvar, supervar_part);
            // System.out.println("  ... considering " + supervar_part.name);
            // if (ss_ppt == null) {
            //   System.out.println("      no ppt for " + subvar.name + " " + supervar_part.name);
            //   Assert.assertTrue(ppt.parent.findSlice_unordered(supervar_part, subvar) == null);
            // }
            if (ss_ppt != null) {
              SubSequenceFloat  ss = SubSequenceFloat.find(ss_ppt);
              if ((ss != null) && ss.enoughSamples()) {
                return true;
              }
            }
          }
        }
      }
      return false;
    }
  }

  public boolean isSameFormula(Invariant other)
  {
    Assert.assertTrue(other instanceof SubSequenceFloat);
    return true;
  }

}
