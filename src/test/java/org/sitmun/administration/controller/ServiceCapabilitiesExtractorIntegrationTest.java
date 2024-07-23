package org.sitmun.administration.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sitmun.test.BaseTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("Service Capabilities Extractor integration test")
class ServiceCapabilitiesExtractorIntegrationTest extends BaseTest {

  private static final String URI_TEMPLATE = "/api/helpers/capabilities?url={0}";

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("A request with a percent-encoded ampersand succeeds")
  void usePercentEncodedAmpersand() throws Exception {
    mvc.perform(get(URI_TEMPLATE, "https://sitmun.diba.cat/wms/servlet/ACE1M?request=GetCapabilities%26service=WMS"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.type").value("OGC:WMS 1.3.0"))
      .andExpect(jsonPath("$.asText", startsWith("<?xml")))
      .andExpect(jsonPath("$.asJson.WMS_Capabilities").isMap())
      .andExpect(jsonPath("$.asJson.WMS_Capabilities").isNotEmpty());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Extract from a GetCapabilities request to a WMS 1.3.0")
  void extractKnownWMSService130() throws Exception {
    mvc.perform(get(URI_TEMPLATE,
        "https://www.ign.es/wms-inspire/ign-base?request=GetCapabilities"
      ))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.type").value("OGC:WMS 1.3.0"))
      .andExpect(jsonPath("$.asText", startsWith("<?xml")))
      .andExpect(jsonPath("$.asJson.WMS_Capabilities").exists());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Extract from a GetCapabilities request to a WMS 1.1.1")
  void extractKnownWMSService111() throws Exception {
    mvc.perform(get(URI_TEMPLATE,
        "https://www.ign.es/wms-inspire/ign-base?request=GetCapabilities&version=1.1.1"
      ))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.type").value("OGC:WMS 1.1.1"))
      .andExpect(jsonPath("$.asText", startsWith("<?xml")))
      .andExpect(jsonPath("$.asJson.WMT_MS_Capabilities").exists());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Extract from a bad request to a WMS")
  void extractFailedService() throws Exception {
    mvc.perform(get(URI_TEMPLATE,
        "https://www.ign.es/wms-inspire/ign-base?"
      ))
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.reason").value("Not a standard OGC:WMS Capabilities response"))
      .andExpect(jsonPath("$.asText", startsWith("<?xml")))
      .andExpect(jsonPath("$.asJson.ServiceExceptionReport").exists());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Extract from a request to HTML page")
  void extractHtmlPage() throws Exception {
    mvc.perform(get(URI_TEMPLATE,
        "https://www.ign.es/"
      ))
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.reason").value("Not a standard OGC:WMS Capabilities response"))
      .andExpect(jsonPath("$.asText", startsWith("<!DOCTYPE")));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Extract from a request to a not found page")
  void extract404Page() throws Exception {
    mvc.perform(get(URI_TEMPLATE,
        "https://www.ign.es/not-found"
      ))
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.reason").value("Not a well formed XML"))
      .andExpect(jsonPath("$.asText", startsWith("<html")));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Extract from a request to an nonexistent domain")
  void extractNonExistentDomain() throws Exception {
    mvc.perform(get(URI_TEMPLATE,
        "https://fake"
      ))
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.reason", startsWith("UnknownHostException: fake")));
  }

}