import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import org.junit.jupiter.api.BeforeEach;

public class WiremockJavaWrapper {

  @BeforeEach
  void beforeEach() {
    // wiremock перенаправляет все запросы, не начинающиеся с подчёркивания на адрес реального сервера
    // при этом добавляется авторизационный заголовок.
    // Больше данных тут https://wiremock.org/docs/
    // todo: не получилось сходу научиться перенаправлять трафик на https://example.com:443
    stubFor(any(urlMatching("[^_]+.*"))
        .willReturn(aResponse()
            .proxiedFrom("http://example.com:5004")
            .withAdditionalRequestHeader("AuthHeader", "AuthToken")));
  }
}
