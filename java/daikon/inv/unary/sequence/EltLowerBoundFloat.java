// ***** This file is automatically generated from Bound.java.jpp

package daikon.inv.unary.sequence;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.sequence.*;
import daikon.inv.unary.scalar.*;
import daikon.inv.unary.*;
import daikon.inv.binary.sequenceScalar.*;
import daikon.inv.binary.twoSequence.*;
import daikon.derive.unary.*;
import utilMDE.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * EltLowerBoundFloat represents the invariant 'x > c', where c is a constant.
 * <p>
 * One reason not to combine LowerBound and UpperBound into a single range
 * invariant is that they have separate justifications:  one may be
 * justified when the other is not.
 **/
public class EltLowerBoundFloat
  extends SingleFloatSequence
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff EltLowerBoundFloat invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;
  /**
   * Long integer.  Together with maximal_interesting, specifies the
   * range of the computed constant that is "intersting" --- the range
   * that should be reported.  For instance, setting minimal_interesting
   * to -1 and maximal_interesting to 2 would only permit output of
   * EltLowerBoundFloat invariants whose cutoff was one of (-1,0,1,2).
   **/
  public static long dkconfig_minimal_interesting = -1;
  /**
   * Long integer.  Together with minimal_interesting, specifies the
   * range of the computed constant that is "intersting" --- the range
   * that should be reported.  For instance, setting minimal_interesting
   * to -1 and maximal_interesting to 2 would only permit output of
   * EltLowerBoundFloat invariants whose cutoff was one of (-1,0,1,2).
   **/
  public static long dkconfig_maximal_interesting = 2;

  public LowerBoundCoreFloat core;

  private EltLowerBoundFloat(PptSlice ppt) {
    super(ppt);
    core = new LowerBoundCoreFloat(this);
  }

  protected Object clone() {
    EltLowerBoundFloat result = (EltLowerBoundFloat) super.clone();
    result.core = (LowerBoundCoreFloat) core.clone();
    result.core.wrapper = result;
    return result;
  }

  public static EltLowerBoundFloat instantiate(PptSlice ppt) {
    if (!dkconfig_enabled) return null;
    return new EltLowerBoundFloat(ppt);
  }

  public String repr() {
    return "EltLowerBoundFloat" + varNames() + ": "
      + core.repr();
  }

  public String format_using(OutputFormat format) {
    if (format == OutputFormat.DAIKON) {
      return format_daikon();
    } else if (format == OutputFormat.IOA) {
      return format_ioa();
    } else if (format == OutputFormat.SIMPLIFY) {
      return format_simplify();
    } else if (format == OutputFormat.JAVA) {
      return format_java();
    } else if (format == OutputFormat.ESCJAVA) {
      return format_esc();
    } else if (format == OutputFormat.JML) {
      return format_jml();
    }

    return format_unimplemented(format);
  }
  // ELTLOWEr || ELTUPPEr
  public String format_daikon() {
    return var().name.name() + " elements >= " + core.min1;
  }

  public String format_esc() {
    String[] form =
      VarInfoName.QuantHelper.format_esc(new VarInfoName[]
        { var().name });
    return form[0] + "(" + form[1] + " >= " + core.min1 + ")" + form[2];
  }

  public String format_jml() {
    String[] form =
      VarInfoName.QuantHelper.format_jml(new VarInfoName[]
        { var().name });
    return form[0] + "(" + form[1] + " >= " + core.min1 + ")" + form[2];
  }

  public String format_ioa() {
    VarInfoName.QuantHelper.IOAQuantification quant = new VarInfoName.QuantHelper.IOAQuantification (var());
    String result = quant.getQuantifierExp() + quant.getMembershipRestriction(0) +
      " => " + quant.getVarIndexed(0) + " >= " + core.min1 + quant.getClosingExp();
    return result;
  }

  public String format_simplify() {
    String[] form =
      VarInfoName.QuantHelper.format_simplify(new VarInfoName[]
        { var().name });
    return form[0] + "(>= " + form[1] + " " + core.min1 + ")" + form[2];
  }

  public String format_java() {
    String[] form = VarInfoName.QuantHelper.format_java(new VarInfoName[]
      { var().name });
    return form[0] + "(" + form[1] + " >= " + core.min1 + ")" + form[2];
  }

  // XXX need to flow invariant if bound changed
  public void add_modified(double[] value, int count) {
    // System.out.println("EltLowerBoundFloat" + varNames() + ": "
    //                    + "add(" + value + ", " + modified + ", " + count + ")");

    // I must always clone myself because the cores keep track of many
    // statistics, not just the bounds.
    cloneAndFlow();
    core.changed = false;

    for (int i=0; i<value.length; i++) {
      core.add_modified(value[i], count);
      if (falsified)
        return;
    }

  }

  public boolean enoughSamples() {
    return core.enoughSamples();
  }

  protected double computeProbability() {
    return core.computeProbability();
  }

  public boolean isExact() {
    return core.isExact();
  }

  public boolean isSameFormula(Invariant other)
  {
    return core.isSameFormula(((EltLowerBoundFloat) other).core);
  }

  public boolean isObviousImplied() {
    // if the value is not in some range (like -1,0,1,2) then say that it is obvious
    if ((core.min1 < dkconfig_minimal_interesting) ||
        (core.min1 > dkconfig_maximal_interesting)) {
      return true;
    }
    EltOneOfFloat oo = EltOneOfFloat.find(ppt);
    if ((oo != null) && oo.enoughSamples() && oo.num_elts() > 0) {
      Assert.assertTrue (oo.var().isCanonical());
      // We could also use core.min1 == oo.min_elt(), since the LowerBound
      // will never have a core.min1 that does not appear in the OneOf.
      if (core.min1 <= oo.min_elt()) {
        return true;
      }
    }

    VarInfo v = var();
    // Look for the same property over a supersequence of this one.
    PptTopLevel pptt = ppt.parent;
    for (Iterator inv_itor = pptt.invariants_iterator(); inv_itor.hasNext(); ) {
      Invariant inv = (Invariant) inv_itor.next();
      if (inv == this) {
        continue;
      }
      if (inv instanceof EltLowerBoundFloat) {
        EltLowerBoundFloat other = (EltLowerBoundFloat) inv;
        if (isSameFormula(other)
            && SubSequenceFloat.isObviousDerived(v, other.var())) {
          return true;
        }
      }
    }

    return super.isObviousImplied();
  }

  public boolean isObviousDerived() {
    VarInfo v = var();
    if (v.isDerived() && (v.derived instanceof SequenceLength)) {
      int vshift = ((SequenceLength) v.derived).shift;
      if (vshift != 0) {
        return true;
      }
    }

    // For each sequence variable, if this is an obvious member/subsequence, and
    // it has the same invariant, then this one is obvious.
    PptTopLevel pptt = ppt.parent;
    for (int i=0; i<pptt.var_infos.length; i++) {
      VarInfo vi = pptt.var_infos[i];

      if (SubSequenceFloat.isObviousDerived(v, vi))
      {
        PptSlice1 other_slice = pptt.findSlice(vi);
        if (other_slice != null) {
          EltLowerBoundFloat eb = EltLowerBoundFloat.find(other_slice);
          if ((eb != null)
              && eb.enoughSamples()
              && eb.core.min1 == core.min1) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public boolean isExclusiveFormula(Invariant other) {
    if (other instanceof EltUpperBoundFloat) {
      if (core.min1 > ((EltUpperBoundFloat) other).core.max1)
        return true;
    }
    if (other instanceof OneOfFloat) {
      return other.isExclusiveFormula(this);
    }
    return false;
  }

  // Look up a previously instantiated invariant.
  public static EltLowerBoundFloat find(PptSlice ppt) {
    Assert.assertTrue(ppt.arity == 1);
    for (Iterator itor = ppt.invs.iterator(); itor.hasNext(); ) {
      Invariant inv = (Invariant) itor.next();
      if (inv instanceof EltLowerBoundFloat)
        return (EltLowerBoundFloat) inv;
    }
    return null;
  }

}
