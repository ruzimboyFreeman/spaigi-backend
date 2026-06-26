package uz.spaigi.contact.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequest(
    @NotBlank @Size(max = 80) String firstName,
    @NotBlank @Size(max = 80) String lastName,
    @NotBlank @Email @Size(max = 160) String email,
    @NotBlank @Size(max = 5000) String message,
    @Size(max = 200) String website
) {

  public boolean isSpamTrapFilled() {
    return website != null && !website.isBlank();
  }

  public String fullName() {
    return "%s %s".formatted(firstName.trim(), lastName.trim()).trim();
  }
}
