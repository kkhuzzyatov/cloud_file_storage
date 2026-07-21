package cloud_file_storage.main.exception;

public class SessionNotFoundException extends RuntimeException {
  public SessionNotFoundException(String message) {
    super(message);
  }
}
