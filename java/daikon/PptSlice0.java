package daikon;

import daikon.inv.*;

import utilMDE.*;

// This is a fake PptSlice for use with Implication invariants.

public class PptSlice0 extends PptSlice {

  PptSlice0(PptTopLevel parent) {
     super(parent, new VarInfo[0]);
  }

  void init_po() {
    throw new Error("Shouldn't get called");
  }

  public void addInvariant(Invariant inv) {
    Assert.assert(inv != null);
    // Assert.assert(inv instanceof Implication);
    invs.add(inv);
  }

  // I need to figure out how to set these.
  public int num_samples() { return 2222; }
  public int num_mod_non_missing_samples() { return 2222; }
  public int num_values() { return 2222; }
  public String tuplemod_samples_summary() { return "tuplemod_samples_summary for PptSlice0 " + name; }

  void instantiate_invariants() {
    throw new Error("Shouldn't get called");
  }

  void add(ValueTuple vt, int count) {
    throw new Error("Shouldn't get called");
  }

}
