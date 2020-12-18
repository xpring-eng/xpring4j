package io.xpring.codec.addresses;

import static org.assertj.core.api.Assertions.assertThat;

import io.xpring.xrpl.ClassicAddress;
import org.junit.Test;

/**
 * Test cases modeled after the example under the "Encoding Example" section of the X-Address spec:
 * https://github.com/xrp-community/standards-drafts/issues/6
 */
public class XAddressCodecTest {

  @Test
  public void testEncodeMainnetNoTag() {
    // GIVEN a classic address for mainnet without a tag
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .isTest(false)
        .build();
    // WHEN encoded
    // THEN it should match the expected address per the spec examples
    assertThat(XAddressCodec.encode(address)).isEqualTo("XVLhHMPHU98es4dbozjVtdWzVrDjtV5fdx1mHp98tDMoQXb");
  }

  @Test
  public void testEncodeMainnetWithTag() {
    // GIVEN a classic address for mainnet with a tag
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(1)
        .isTest(false)
        .build();
    // WHEN encoded
    // THEN it should match the expected address per the spec examples
    assertThat(XAddressCodec.encode(address)).isEqualTo("XVLhHMPHU98es4dbozjVtdWzVrDjtV8xvjGQTYPiAx6gwDC");
  }

  @Test
  public void testEncodeMainnetWithLargeTag() {
    // GIVEN a classic address for mainnet with a long tag
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(16781933)
        .isTest(false)
        .build();
    // WHEN encoded
    // THEN it should match the expected address per the spec examples
    assertThat(XAddressCodec.encode(address)).isEqualTo("XVLhHMPHU98es4dbozjVtdWzVrDjtVqrDUk2vDpkTjPsY73");
  }

  @Test
  public void testDecodeMainnetNoTag() {
    ClassicAddress expected = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .isTest(false)
        .build();
    // GIVEN an X-address representing a mainnet address without a tag
    // WHEN decoded
    // THEN it should match the expected classic address from the spec examples
    assertThat(XAddressCodec.decode("XVLhHMPHU98es4dbozjVtdWzVrDjtV5fdx1mHp98tDMoQXb"))
        .isEqualTo(expected);
  }

  @Test
  public void testDecodeMainnetWithTag() {
    ClassicAddress expected = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(1)
        .isTest(false)
        .build();
    // GIVEN an X-address representing a mainnet address with a tag
    // WHEN decoded
    // THEN it should match the expected classic address from the spec examples
    assertThat(XAddressCodec.decode("XVLhHMPHU98es4dbozjVtdWzVrDjtV8xvjGQTYPiAx6gwDC"))
        .isEqualTo(expected);
  }

  @Test
  public void testDecodeMainnetWithLargeTag() {
    ClassicAddress expected = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(16781933)
        .isTest(false)
        .build();
    // GIVEN an X-address representing a mainnet address with a large tag
    // WHEN decoded
    // THEN it should match the expected classic address from the spec examples
    assertThat(XAddressCodec.decode("XVLhHMPHU98es4dbozjVtdWzVrDjtVqrDUk2vDpkTjPsY73"))
        .isEqualTo(expected);
  }

  @Test
  public void testEncodeTestnetNoTag() {
    // GIVEN a classic address for testnet without a tag
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .isTest(true)
        .build();
    // WHEN encoded
    // THEN it should match the expected address per the spec examples
    assertThat(XAddressCodec.encode(address)).isEqualTo("TVE26TYGhfLC7tQDno7G8dGtxSkYQn49b3qD26PK7FcGSKE");
  }

  @Test
  public void testEncodeTestnetWithTag() {
    // GIVEN a classic address for testnet with a tag
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(1)
        .isTest(true)
        .build();
    // WHEN encoded
    // THEN it should match the expected address per the spec examples
    assertThat(XAddressCodec.encode(address)).isEqualTo("TVE26TYGhfLC7tQDno7G8dGtxSkYQnSz1uDimDdPYXzSpyw");
  }

  @Test
  public void testEncodeTestnetWithLargeTag() {
    // GIVEN a classic address for testnet with a long tag
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(16781933)
        .isTest(true)
        .build();
    // WHEN encoded
    // THEN it should match the expected address per the spec examples
    assertThat(XAddressCodec.encode(address)).isEqualTo("TVE26TYGhfLC7tQDno7G8dGtxSkYQnVsw45sDtGHhLi27Qa");
  }

  @Test
  public void testDecodeTestnetNoTag() {
    ClassicAddress expected = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .isTest(true)
        .build();
    // GIVEN an X-address representing a testnet address without a tag
    // WHEN decoded
    // THEN it should match the expected classic address from the spec examples
    assertThat(XAddressCodec.decode("TVE26TYGhfLC7tQDno7G8dGtxSkYQn49b3qD26PK7FcGSKE"))
        .isEqualTo(expected);
  }

  @Test
  public void testDecodeTestnetWithTag() {
    ClassicAddress expected = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(1)
        .isTest(true)
        .build();
    // GIVEN an X-address representing a testnet address with a tag
    // WHEN decoded
    // THEN it should match the expected classic address from the spec examples
    assertThat(XAddressCodec.decode("TVE26TYGhfLC7tQDno7G8dGtxSkYQnSz1uDimDdPYXzSpyw"))
        .isEqualTo(expected);
  }

  @Test
  public void testDecodeTestnetWithLargeTag() {
    ClassicAddress expected = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(16781933)
        .isTest(true)
        .build();
    // GIVEN an X-address representing a testnet address with a large tag
    // WHEN decoded
    // THEN it should match the expected classic address from the spec examples
    assertThat(XAddressCodec.decode("TVE26TYGhfLC7tQDno7G8dGtxSkYQnVsw45sDtGHhLi27Qa"))
        .isEqualTo(expected);
  }

}
