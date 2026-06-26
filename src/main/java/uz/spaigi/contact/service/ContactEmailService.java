package uz.spaigi.contact.service;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.HtmlUtils;
import uz.spaigi.contact.config.ContactEmailProperties;
import uz.spaigi.contact.web.ContactRequest;

@Service
public class ContactEmailService {

  private final ContactEmailProperties properties;
  private final RestClient restClient;

  public ContactEmailService(ContactEmailProperties properties, RestClient.Builder restClientBuilder) {
    this.properties = properties;
    this.restClient = restClientBuilder
        .baseUrl("https://api.resend.com")
        .build();
  }

  public void send(ContactRequest request) {
    validateConfiguration();

    String subject = "%s: message from %s".formatted(
        properties.resolvedSubjectPrefix(),
        request.fullName()
    );

    Map<String, Object> payload = Map.of(
        "from", properties.from(),
        "to", List.of(properties.to()),
        "reply_to", request.email().trim(),
        "subject", subject,
        "text", textBody(request),
        "html", htmlBody(request)
    );

    try {
      restClient.post()
          .uri("/emails")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.resendApiKey())
          .contentType(MediaType.APPLICATION_JSON)
          .body(payload)
          .retrieve()
          .toBodilessEntity();
    } catch (Exception error) {
      throw new EmailDeliveryException("Resend API request failed", error);
    }
  }

  private void validateConfiguration() {
    if (isBlank(properties.resendApiKey()) || isBlank(properties.from()) || isBlank(properties.to())) {
      throw new EmailDeliveryException(
          "Missing RESEND_API_KEY, CONTACT_FROM_EMAIL, or CONTACT_TO_EMAIL configuration"
      );
    }
  }

  private String textBody(ContactRequest request) {
    return """
        New SPAIGI contact form message

        Name: %s
        Email: %s

        Message:
        %s
        """.formatted(request.fullName(), request.email().trim(), request.message().trim());
  }

  private String htmlBody(ContactRequest request) {
    return """
        <h2>New SPAIGI contact form message</h2>
        <p><strong>Name:</strong> %s</p>
        <p><strong>Email:</strong> %s</p>
        <p><strong>Message:</strong></p>
        <p style="white-space: pre-line;">%s</p>
        """.formatted(
        HtmlUtils.htmlEscape(request.fullName()),
        HtmlUtils.htmlEscape(request.email().trim()),
        HtmlUtils.htmlEscape(request.message().trim())
    );
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
