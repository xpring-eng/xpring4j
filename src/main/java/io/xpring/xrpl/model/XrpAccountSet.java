package io.xpring.xrpl.model;

import io.xpring.xrpl.model.ImmutableXrpAccountSet;
import org.immutables.value.Value;

@Value.Immutable
public interface XrpAccountSet {
    static ImmutableXrpAccountSet.Builder build() { return ImmutableXrpAccountSet.builder(); }

    int clearFlag();

    java.lang.String domain();

    // Convert this in ::from from the pb itself
    byte[] emailHash();

    // Convert this in ::from from the pb itself
    byte[] messageKey();

    Integer setFlag();

    Integer transferRate();

    Integer tickSize();
}
