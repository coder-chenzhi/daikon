// ***** This file is automatically generated from SequencesPredicateFactory.java.jpp

package daikon.derive.binary;

import daikon.*;
import daikon.inv.binary.twoScalar.*; // for IntComparison
import daikon.inv.unary.scalar.*; // for LowerBound

import utilMDE.*;
import org.apache.log4j.Category;
import java.util.*;

/**
 * Factory for SequencesPredicate derived variables.
 **/
public final class SequencesPredicateFactoryFloat   extends BinaryDerivationFactory {

  /**
   * Debug tracer
   **/
  public static final Category debug =
    Category.getInstance ("daikon.derive.binary.SequencesPredicateFactoryFloat" );

  public BinaryDerivation[] instantiate(VarInfo var1, VarInfo var2) {
    boolean enabled = SequencesPredicateFloat.dkconfig_enabled;
    if (!enabled) return null;

    if (debug.isDebugEnabled()) {
      debug.debug ("Trying to instantiate " + var1.name + " and " + var2.name);
    }

    if (!(var1.rep_type.isArray()) ||
        !(var2.rep_type.isArray())) {
      return null;
    }

    if (!var1.aux.getFlag(VarInfoAux.HAS_ORDER) ||
        !var2.aux.getFlag(VarInfoAux.HAS_ORDER)) {
      // Order doesn't matter, then predication is meaningless
      return null;
    }

    if (SequencesPredicateFloat.dkconfig_boolOnly) {
      if (!(var2.file_rep_type == ProglangType.BOOLEAN_ARRAY)) {
        return null;
      }
    }

    if (var1.derived != null || var2.derived != null) {
      // From derived variables.  Don't derive.
      return null;
    }

    if (SequencesPredicateFloat.dkconfig_fieldOnly) {

      if (!(var1.name instanceof VarInfoName.Field) ||
          !(var2.name instanceof VarInfoName.Field))   return null;

      VarInfoName.Field name1 = (VarInfoName.Field) var1.name;
      VarInfoName.Field name2 = (VarInfoName.Field) var2.name;

      // Must be from same original data structure
      if (!name1.term.equals(name2.term)) return null;
    } else {
      // There may be predications that aren't of the x.a and x.b type
    }

    // Now we finally can derive

    if (!var1.domainTypeIOA().equals(var2.domainTypeIOA())) {
      debug.debug ("Incorrect domains");
      return null;
    }

    if (debug.isDebugEnabled()) {
      debug.debug (var1.ppt + ": " + var1.name + " and " +
                   var2.name + " are worth deriving from");
    }

    return new BinaryDerivation[] {
      new SequencesPredicateFloat  (var1, var2, 0, "false"),
      new SequencesPredicateFloat  (var1, var2, 1, "true"),
      //new SequencesPredicate (var1, var2, 0, "nonNull", false),
    };

  }

  /*

  public BinaryDerivation[] instantiate(VarInfo var1, VarInfo var2) {
    boolean enabled = SequencesPredicate.dkconfig_enabled;
    if (!enabled) return null;

    if (!(var1.rep_type.isArray()) ||
        !(var2.rep_type.isArray())) {
      return null;
    }

    if (!(var2.file_rep_type == ProglangType.BOOLEAN_ARRAY)) {
      return null;
    }

    if (var1.derived != null || var2.derived != null) {
      // From derived variables.  Don't derive.
      return null;
    }

    if (var1.name.equals(var2.name)) return null;

    if (!(var1.name instanceof VarInfoName.Field) ||
        !(var2.name instanceof VarInfoName.Field))   return null;

    VarInfoName.Field name1 = (VarInfoName.Field) var1.name;
    VarInfoName.Field name2 = (VarInfoName.Field) var2.name;

    // Must be from same original data structure
    if (!name1.term.equals(name2.term)) return null;

    // Now we finally can derive

    // Assert.assertTrue(var1.isCanonical()); // [INCR]
    // Assert.assertTrue(var2.isCanonical()); // [INCR]

    if (debug.isDebugEnabled()) {
      debug.debug (var1.ppt + ": " + var1.name + " and " +
                   var2.name + " are worth deriving from");
    }

    return new BinaryDerivation[] {
      new SequencesPredicate (var1, var2, 0, "false"),
      new SequencesPredicate (var1, var2, 1, "true"),
    };
  }
  */
}
