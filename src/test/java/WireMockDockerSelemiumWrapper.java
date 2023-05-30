import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@TestInstance(PER_CLASS)
public class WireMockDockerSelemiumWrapper {

  static WebDriver driver;
  String address;
  Integer port;

  @Container
  public GenericContainer container = new GenericContainer(DockerImageName.parse("wiremock/wiremock:2.32.0"))
      .withExposedPorts(8080);

  public void setUp() throws IOException {
    container.start();

    address = container.getHost();
    port = container.getFirstMappedPort();
    System.out.println("http://" + address + ":" + port + "/__admin/mappings");

    URL url = new URL("http://" + address + ":" + port.toString() + "/__admin/mappings");
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    con.setRequestProperty("Accept", "application/json");
    con.setRequestMethod("POST");

    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

    var wiremock_config_all = "{ "
        + "        \"request\": { "
        + "            \"urlPattern\": \"[^_]+.*\" "
        + "        }, "
        + "        \"response\": { "
        + "            \"additionalProxyRequestHeaders\": { "
        + "                \"AuthHeader\": \"AuthToken\" "
        + "            }, "
        + "            \"proxyBaseUrl\": \"http://example.com:5004\" "
        + "        } "
        + "    }";
    wr.write(wiremock_config_all);
    wr.flush();

    var inputStream = con.getInputStream();  // Фактически, это инициирует запрос. Без этой строки wiremock не инициализирован

  }

  @BeforeEach
  public void beforeEach() throws IOException {
    setUp();
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
  }

  @AfterEach
  public void afterEach() {
    driver.close();
  }


  @BeforeAll
  public void beforeAll() {
  }

  @AfterAll
  public void afterAll() {
  }
}

