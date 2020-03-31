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

    /**
     * Constructs an {@link XRPPath} from a {@link org.xrpl.rpc.v1.Payment.Path}
     * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L237">
     *     Path protocol buffer</a>
     *
     * @param path a {@link org.xrpl.rpc.v1.Payment.Path} (protobuf object) whose field values will be used
     *                 to construct an {@link XRPPath}
     * @return an {@link XRPPath} with its fields set via the analogous protobuf fields.
     */
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