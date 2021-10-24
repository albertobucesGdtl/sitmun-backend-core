package org.sitmun.plugin.core.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SimpleServiceCapabilitiesExtractor test")
class SimpleServiceCapabilitiesExtractorTest {

  private final SimpleServiceCapabilitiesExtractor extractor = new SimpleServiceCapabilitiesExtractor();

  @Test
  @DisplayName("Extract from a GetCapabilities request to a WMS 1.3.0")
  public void extractKnownWMSService130() {
    ExtractedMetadata doc = extractor.extract("https://www.ign.es/wms-inspire/ign-base?request=GetCapabilities");
    assertNotNull(doc);
    assertTrue(doc.getSuccess());
    assertEquals("OGC:WMS 1.3.0", doc.getType());
    assertNull(doc.getReason());
    assertNotNull(doc.getAsText());
    assertThat(doc.getAsText()).startsWith("<?xml");
    assertNotNull(doc.getAsJson());
    assertTrue(doc.getAsJson().containsKey("WMS_Capabilities"));
  }

  @Test
  @DisplayName("Extract from a GetCapabilities request to a WMS 1.1.1")
  public void extractKnownWMSService111() {
    ExtractedMetadata doc = extractor.extract("https://www.ign.es/wms-inspire/ign-base?request=GetCapabilities&version=1.1.1");
    assertNotNull(doc);
    assertTrue(doc.getSuccess());
    assertEquals("OGC:WMS 1.1.1", doc.getType());
    assertNull(doc.getReason());
    assertNotNull(doc.getAsText());
    assertThat(doc.getAsText()).startsWith("<?xml");
    assertNotNull(doc.getAsJson());
    assertTrue(doc.getAsJson().containsKey("WMT_MS_Capabilities"));
  }

  @Test
  @DisplayName("Extract from a bad request to a WMS")
  public void extractFailedService() {
    ExtractedMetadata doc = extractor.extract("https://www.ign.es/wms-inspire/ign-base?");
    assertNotNull(doc);
    assertFalse(doc.getSuccess());
    assertEquals("Not a standard OGC:WMS Capabilities response", doc.getReason());
    assertNotNull(doc.getAsText());
    assertThat(doc.getAsText()).startsWith("<?xml");
    assertNotNull(doc.getAsJson());
    assertTrue(doc.getAsJson().containsKey("ServiceExceptionReport"));
  }

  @Test
  @DisplayName("Extract from a request to HTML page")
  public void extractHtmlPage() {
    ExtractedMetadata doc = extractor.extract("https://www.ign.es/");
    assertNotNull(doc);
    assertFalse(doc.getSuccess());
    assertEquals("Not a standard OGC:WMS Capabilities response", doc.getReason());
    assertNotNull(doc.getAsText());
    assertThat(doc.getAsText()).startsWith("<!DOCTYPE");
  }

  @Test
  @DisplayName("Extract from a request to a not found page")
  public void extract404Page() {
    ExtractedMetadata doc = extractor.extract("https://www.ign.es/not-found");
    assertNotNull(doc);
    assertFalse(doc.getSuccess());
    assertEquals("Not a well formed XML", doc.getReason());
    assertNotNull(doc.getAsText());
    assertThat(doc.getAsText()).startsWith("<html");
  }

  @Test
  @DisplayName("Extract from a request to an nonexistent domain")
  public void extractNonExistentDomain() {
    ExtractedMetadata doc = extractor.extract("https://fake");
    assertNotNull(doc);
    assertFalse(doc.getSuccess());
    assertEquals("fake: nodename nor servname provided, or not known", doc.getReason());
    assertNull(doc.getAsText());
    assertNull(doc.getAsJson());
  }
}