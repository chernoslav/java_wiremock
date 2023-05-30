import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@TestInstance(PER_CLASS)
public class WireMockDockerWrapper {

  String address;
  Integer port;

  @Container
  public GenericContainer container = new GenericContainer(DockerImageName.parse("wiremock/wiremock:2.32.0"))
      .withExposedPorts(8080);

  @BeforeEach
  public void beforeEach() throws IOException {
    setUp();
  }


  public void setUp() throws IOException {
    container.start();

    address = container.getHost();
    port = container.getFirstMappedPort();

    URL url = new URL("http://" + address + ":" + port.toString() + "/__admin/mappings");
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    con.setRequestProperty("Accept", "application/json");
    con.setRequestMethod("POST");

    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

    // тут вполне корректно работает перенаправление на https сервера
    var wiremock_config_all = "{ "
        + "        \"request\": { "
        + "            \"urlPattern\": \"[^_]+.*\" "
        + "        }, "
        + "        \"response\": { "
        + "            \"additionalProxyRequestHeaders\": { "
        + "                \"AuthHeader\": \"AuthToken\" "
        + "            }, "
        + "            \"proxyBaseUrl\": \"https://example.com:443\" " //
        + "        } "
        + "    }";
    wr.write(wiremock_config_all);
    wr.flush();

    var inputStream = con.getInputStream();  // Фактически, это инициирует запрос. Без этой строки wiremock не инициализирован
  }

}

