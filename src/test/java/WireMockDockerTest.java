import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

public class WireMockDockerTest extends WireMockDockerWrapper {

  @Test
  void wireMockDockerTest() throws Exception {
    String address = container.getHost();
    Integer port = container.getFirstMappedPort();
    String url = "http://" + address + ":" + port.toString() + "/api/v1/test_method";

    assertThat(getContent(url)).isEqualTo("some expected_text");
  }

  private String getContent(String url) throws Exception {
    try (InputStream is = new URL(url).openStream()) {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      return readAll(rd);
    }
  }

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

}
