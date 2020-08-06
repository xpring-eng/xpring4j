package io.xpring.xrpl;

import io.xpring.xrpl.model.XrpAccountSet;
import org.junit.Test;

public class XrpAccountSetProtoConversionTest {
  @Test
  public void allFieldsSetTest() {
    final XrpAccountSet accountSet = XrpAccountSet.from(FakeXrpProtobufs.allFieldsAccountSet);
  }

  @Test
  public void oneFieldSetTest() {
    final XrpAccountSet accountSet = XrpAccountSet.from(FakeXrpProtobufs.oneFieldAccountSet);
  }
}
