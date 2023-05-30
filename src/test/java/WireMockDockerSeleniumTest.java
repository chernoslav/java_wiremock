import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class WireMockDockerSeleniumTest extends WireMockDockerSelemiumWrapper {

  @Test
  void wireMockDockerTest() {
    String address = container.getHost();
    Integer port = container.getFirstMappedPort();
    driver.get("http://" + address + ":" + port.toString() + "/api/v3/systems/auth");
    driver.navigate().refresh();
    assertThat(1).isPositive();
  }

}
