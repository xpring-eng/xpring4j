package io.xpring.xrpl.model;

import org.xrpl.rpc.v1.Payment.Path;
import org.immutables.value.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A path in the XRP Ledger.
 * @see "https://xrpl.org/paths.html"
 */
@Value.Immutable
public interface XRPPath {
    static ImmutableXRPPath.Builder builder() {
        return ImmutableXRPPath.builder();
    }

    /**
     * @return List of XRPPathElements that make up this XRPPath
     */
    List<XRPPathElement> pathElements();

    static XRPPath from(Path path) {
        List<XRPPathElement> pathElements = path.getElementsList()
                                            .stream()
                                            .map(pathElement -> XRPPathElement.from(pathElement))
                                            .collect(Collectors.toList());
        return builder()
                .pathElements(pathElements)
                .build();
    }
}