// ***** This file is automatically generated from Bound.java.jpp

package daikon.inv.unary.scalar;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.sequence.*;
import daikon.inv.unary.scalar.*;
import daikon.inv.unary.*;
import daikon.inv.binary.sequenceScalar.*;
import daikon.inv.binary.twoSequence.*;
import daikon.derive.unary.*;
import utilMDE.*;
import org.apache.log4j.Category;

import java.util.*;

/**
 * UpperBoundFloat represents the invariant 'x < c', where c is a constant.
 * <p>
 * One reason not to combine LowerBound and UpperBound into a single range
 * invariant is that they have separate justifications:  one may be
 * justified when the other is not.
 **/
public class UpperBoundFloat
  extends SingleFloat
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff UpperBoundFloat invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;
  /**
   * Long integer.  Together with maximal_interesting, specifies the
   * range of the computed constant that is "intersting" --- the range
   * that should be reported.  For instance, setting minimal_interesting
   * to -1 and maximal_interesting to 2 would only permit output of
   * UpperBoundFloat invariants whose cutoff was one of (-1,0,1,2).
   **/
  public static long dkconfig_minimal_interesting = -1;
  /**
   * Long integer.  Together with minimal_interesting, specifies the
   * range of the computed constant that is "intersting" --- the range
   * that should be reported.  For instance, setting minimal_interesting
   * to -1 and maximal_interesting to 2 would only permit output of
   * UpperBoundFloat invariants whose cutoff was one of (-1,0,1,2).
   **/
  public static long dkconfig_maximal_interesting = 2;

  public UpperBoundCoreFloat core;

  private UpperBoundFloat(PptSlice ppt) {
    super(ppt);
    core = new UpperBoundCoreFloat(this);
  }

  protected Object clone() {
    UpperBoundFloat result = (UpperBoundFloat) super.clone();
    result.core = (UpperBoundCoreFloat) core.clone();
    result.core.wrapper = result;
    return result;
  }

  public static UpperBoundFloat instantiate(PptSlice ppt) {
    if (!dkconfig_enabled) return null;
    return new UpperBoundFloat(ppt);
  }

  public String repr() {
    return "UpperBoundFloat" + varNames() + ": "
      + core.repr();
  }

  public String format_using(OutputFormat format) {
    String name = var().name.name_using(format);

    if ((format == OutputFormat.DAIKON)
        || (format == OutputFormat.ESCJAVA)
        || (format == OutputFormat.IOA)
        || (format == OutputFormat.JAVA)
        || (format == OutputFormat.JML))
    {
      return name + " <= " + core.max1;
    }

    if (format == OutputFormat.SIMPLIFY) {
      return "(<= " + name + " " + core.max1 + ")";
    }

    return format_unimplemented(format);
  }

  // XXX need to flow invariant if bound changed
  public void add_modified(double value, int count) {
    // System.out.println("UpperBoundFloat" + varNames() + ": "
    //                    + "add(" + value + ", " + modified + ", " + count + ")");

    // I must always clone myself because the cores keep track of many
    // statistics, not just the bounds.
    cloneAndFlow();
    core.changed = false;

    core.add_modified(value, count);

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
    return core.isSameFormula(((UpperBoundFloat) other).core);
  }

  // XXX FIXME This looks like a hack that should be removed.  -MDE 6/13/2002
  public boolean isInteresting() {
    return (-1 < core.max1 && core.max1 < 2);
  }

  public boolean isObviousImplied() {
    // if the value is not in some range (like -1,0,1,2) then say that it is obvious
    if ((core.max1 < dkconfig_minimal_interesting) ||
        (core.max1 > dkconfig_maximal_interesting)) {
      return true;
    }
    OneOfFloat oo = OneOfFloat.find(ppt);
    if ((oo != null) && oo.enoughSamples() && oo.num_elts() > 0) {
      Assert.assertTrue (oo.var().isCanonical());
      // We could also use core.max1 == oo.max_elt(), since the LowerBound
      // will never have a core.max1 that does not appear in the OneOf.
      if (core.max1 >= oo.max_elt()) {
        return true;
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

      if (MemberFloat.isObviousMember(v, vi))
      {
        PptSlice1 other_slice = pptt.findSlice(vi);
        if (other_slice != null) {
          EltUpperBoundFloat eb = EltUpperBoundFloat.find(other_slice);
          if ((eb != null)
              && eb.enoughSamples()
              && eb.core.max1 == core.max1 ) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public boolean isExclusiveFormula(Invariant other) {
    if (other instanceof LowerBoundFloat) {
      if (core.max1 < ((LowerBoundFloat) other). core.min1)
        return true;
    }
    if (other instanceof OneOfFloat) {
      return other.isExclusiveFormula(this);
    }
    return false;
  }

  // Look up a previously instantiated invariant.
  public static UpperBoundFloat find(PptSlice ppt) {
    Assert.assertTrue(ppt.arity == 1);
    for (Iterator itor = ppt.invs.iterator(); itor.hasNext(); ) {
      Invariant inv = (Invariant) itor.next();
      if (inv instanceof UpperBoundFloat)
        return (UpperBoundFloat) inv;
    }
    return null;
  }

}
