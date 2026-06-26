package uz.spaigi.contact.web;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uz.spaigi.contact.service.ContactEmailService;
import uz.spaigi.contact.service.EmailDeliveryException;

@RestController
public class ContactController {

  private static final Logger log = LoggerFactory.getLogger(ContactController.class);

  private final ContactEmailService emailService;

  public ContactController(ContactEmailService emailService) {
    this.emailService = emailService;
  }

  @PostMapping("/api/contact")
  public ContactResponse sendContactMessage(@Valid @RequestBody ContactRequest request) {
    if (request.isSpamTrapFilled()) {
      return ContactResponse.success();
    }

    emailService.send(request);
    return ContactResponse.success();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ContactResponse> handleValidationError() {
    return ResponseEntity
        .badRequest()
        .body(ContactResponse.error("Please fill in all required fields correctly."));
  }

  @ExceptionHandler(EmailDeliveryException.class)
  public ResponseEntity<ContactResponse> handleEmailDeliveryError(EmailDeliveryException error) {
    log.error("Contact email delivery failed", error);
    return ResponseEntity
        .status(HttpStatus.BAD_GATEWAY)
        .body(ContactResponse.error("The message could not be sent. Please try again later."));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ContactResponse> handleUnexpectedError(Exception error) {
    log.error("Unexpected contact form error", error);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ContactResponse.error("The message could not be sent. Please try again later."));
  }
}
