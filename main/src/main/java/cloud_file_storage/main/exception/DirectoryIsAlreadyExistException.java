package cloud_file_storage.main.exception;

public class DirectoryIsAlreadyExistException extends RuntimeException {
  public DirectoryIsAlreadyExistException(String message) {
    super(message);
  }
}
