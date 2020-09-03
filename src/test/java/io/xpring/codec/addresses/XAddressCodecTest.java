package io.xpring.codec.addresses;

import static org.assertj.core.api.Assertions.assertThat;

import io.xpring.xrpl.ClassicAddress;
import org.junit.Test;

public class XAddressCodecTest {

  @Test
  public void testEncodeMainnetNoTag() {
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .isTest(false)
        .build();
    assertThat(XAddressCodec.encode(address)).isEqualTo("XVLhHMPHU98es4dbozjVtdWzVrDjtV5fdx1mHp98tDMoQXb");
  }

  @Test
  public void testEncodeMainnetWithTag() {
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(1)
        .isTest(false)
        .build();
    assertThat(XAddressCodec.encode(address)).isEqualTo("XVLhHMPHU98es4dbozjVtdWzVrDjtV8xvjGQTYPiAx6gwDC");
  }

  @Test
  public void testEncodeMainnetWithLargeTag() {
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(16781933)
        .isTest(false)
        .build();
    assertThat(XAddressCodec.encode(address)).isEqualTo("XVLhHMPHU98es4dbozjVtdWzVrDjtVqrDUk2vDpkTjPsY73");
  }

  @Test
  public void testDecodeMainnetNoTag() {
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .isTest(false)
        .build();
    assertThat(XAddressCodec.decode("XVLhHMPHU98es4dbozjVtdWzVrDjtV5fdx1mHp98tDMoQXb"))
        .isEqualTo(address);
  }

  @Test
  public void testDecodeMainnetWithTag() {
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(1)
        .isTest(false)
        .build();
    assertThat(XAddressCodec.decode("XVLhHMPHU98es4dbozjVtdWzVrDjtV8xvjGQTYPiAx6gwDC"))
        .isEqualTo(address);
  }

  @Test
  public void testDecodeMainnetWithLargeTag() {
    ClassicAddress address = ClassicAddress.builder().address("rGWrZyQqhTp9Xu7G5Pkayo7bXjH4k4QYpf")
        .tag(16781933)
        .isTest(false)
        .build();
    assertThat(XAddressCodec.decode("XVLhHMPHU98es4dbozjVtdWzVrDjtVqrDUk2vDpkTjPsY73"))
        .isEqualTo(address);
  }

}
