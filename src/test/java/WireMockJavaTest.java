import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

// proxyMode = true используется в случае, когда в урле передаётся несуществующее доменное имя
@WireMockTest(proxyMode = true)
class WireMockJavaTest extends WiremockJavaWrapper {

  @Test
  void wireMockJavaTest() throws Exception {
    assertThat(getContent("http://pofig.kakoy.domain/api/v3/systems/auth")).isEqualTo("expected response");
  }

  // технические методы
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