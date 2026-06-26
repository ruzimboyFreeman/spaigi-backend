package uz.spaigi.contact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ContactApplication {

  public static void main(String[] args) {
    SpringApplication.run(ContactApplication.class, args);
  }
}
