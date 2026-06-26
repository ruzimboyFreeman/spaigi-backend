package uz.spaigi.contact.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "contact.email")
public record ContactEmailProperties(
    String resendApiKey,
    String from,
    String to,
    String subjectPrefix
) {

  public String resolvedSubjectPrefix() {
    return subjectPrefix == null || subjectPrefix.isBlank() ? "SPAIGI Website" : subjectPrefix;
  }
}
