// ***** This file is automatically generated from SequencesUnion.java.jpp

package daikon.derive.binary;

import daikon.*;
import daikon.derive.*;

import utilMDE.*;

public final class SequenceFloatUnion
  extends BinaryDerivation
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff SequenceFloatUnion  derived variables should be generated.
   **/
  public static boolean dkconfig_enabled = false;

  public SequenceFloatUnion (VarInfo vi1, VarInfo vi2) {
    super(vi1, vi2);
  }

  public ValueAndModified computeValueAndModified(ValueTuple full_vt) {
    int mod1 = base1.getModified(full_vt);
    if (mod1 == ValueTuple.MISSING)
      return ValueAndModified.MISSING;
    int mod2 = base2.getModified(full_vt);
    if (mod2 == ValueTuple.MISSING)
      return ValueAndModified.MISSING;
    Object val1 = base1.getValue(full_vt);
    if (val1 == null)
      return ValueAndModified.MISSING;
    double [] val1_array = (double []) val1;
    Object val2 = base2.getValue(full_vt);
    if (val2 == null)
      return ValueAndModified.MISSING;
    double [] val2_array = (double []) val2;

    double [] tmp = new double [val1_array.length+val2_array.length];
    int size = 0;
    for (int i=0; i<val1_array.length; i++) {
      double  v = val1_array[i];
      if ((size==0) ||
          (ArraysMDE.indexOf(ArraysMDE.subarray(tmp, 0, size), v)==-1))
        tmp[size++] = v;
    }
    for (int i=0; i<val2_array.length; i++) {
      double  v = val2_array[i];
      if ((size==0) ||
          (ArraysMDE.indexOf(ArraysMDE.subarray(tmp, 0, size), v)==-1))
        tmp[size++] = v;
    }

    double [] union = ArraysMDE.subarray(tmp, 0, size);
    union = (double  []) Intern.intern(union);

    int mod = (((mod1 == ValueTuple.UNMODIFIED)
                && (mod2 == ValueTuple.UNMODIFIED))
               ? ValueTuple.UNMODIFIED
               : ValueTuple.MODIFIED);
    return new ValueAndModified(union, mod);
  }

  protected VarInfo makeVarInfo() {
    VarInfoName name = base1.name.applyUnion(base2.name).applyElements();
    ProglangType type = base1.type;
    ProglangType file_rep_type = base1.file_rep_type;
    VarComparability compar = base1.comparability.elementType();
    VarInfoAux aux = base1.aux;
    return new VarInfo(name, type, file_rep_type, compar, aux);
  }

  public  boolean isSameFormula(Derivation other) {
    return (other instanceof SequenceFloatUnion);
  }

}
