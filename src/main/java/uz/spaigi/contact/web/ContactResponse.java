package uz.spaigi.contact.web;

public record ContactResponse(boolean ok, String error) {

  public static ContactResponse success() {
    return new ContactResponse(true, null);
  }

  public static ContactResponse error(String message) {
    return new ContactResponse(false, message);
  }
}
