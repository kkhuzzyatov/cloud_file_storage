package cloud_file_storage.main.exception;

public class FileIsAlreadyExistException extends RuntimeException {
  public FileIsAlreadyExistException(String message) {
    super(message);
  }
}
