package cloud_file_storage.main.exception;

public class UserIsNotExistException extends RuntimeException {
  public UserIsNotExistException(String message) {
    super(message);
  }
}
