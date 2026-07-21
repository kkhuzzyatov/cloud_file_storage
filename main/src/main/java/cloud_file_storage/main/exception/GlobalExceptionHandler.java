package cloud_file_storage.main.exception;

import jakarta.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Ошибка некорректного аргумента");

    return buildResponse(ex, HttpStatus.BAD_REQUEST, "Некорректный запрос");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Ошибка некорректного аргумента");

    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse("Некорректный запрос");

    return buildResponse(ex, HttpStatus.BAD_REQUEST, message);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(
      UserAlreadyExistsException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Ошибка конфликта с существующем пользователем");

    return buildResponse(ex, HttpStatus.CONFLICT, "Ошибка конфликта с существующем пользователем");
  }

  @ExceptionHandler(UserIsNotExistException.class)
  public ResponseEntity<Map<String, String>> handleUserIsNotExistException(
      UserIsNotExistException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Пользователь не найден");

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Пользователь не найден");
  }

  @ExceptionHandler(SessionNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleSessionNotFoundException(
      SessionNotFoundException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Токен не действителен");

    return buildResponse(ex, HttpStatus.BAD_REQUEST, "Токен не действителен");
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, String>> handleAuthenticationException(
      AuthenticationException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Ошибка аутентификации");

    return buildResponse(ex, HttpStatus.UNAUTHORIZED, "Не авторизован");
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Ошибка доступа");

    return buildResponse(ex, HttpStatus.FORBIDDEN, "Доступ запрещён");
  }

  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Ошибка безопасности");

    return buildResponse(ex, HttpStatus.FORBIDDEN, "Ошибка безопасности: доступ запрещён");
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, String>> handleNoSuchElementException(
      NoSuchElementException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Элемент не найден");

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Запрашиваемый элемент не найден");
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleEntityNotFoundException(
      EntityNotFoundException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Сущность не найдена");

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Сущность не найдена");
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<Map<String, String>> handleEmptyResultDataAccessException(
      EmptyResultDataAccessException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Данные не найдены");

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Данные не найдены");
  }

  @ExceptionHandler(SQLException.class)
  public ResponseEntity<Map<String, String>> handleSqlException(SQLException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .addKeyValue("sqlState", ex.getSQLState())
        .addKeyValue("кодОшибки", ex.getErrorCode())
        .log("Ошибка SQL");

    return buildResponse(ex, HttpStatus.CONFLICT, "Ошибка базы данных. Попробуйте позже");
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<Map<String, String>> handleDuplicateKeyException(DuplicateKeyException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Конфликт уникального ключа");

    return buildResponse(ex, HttpStatus.CONFLICT, "Конфликт данных: запись уже существует");
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Некорректное состояние приложения");

    return buildResponse(
        ex, HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера. Попробуйте ещё раз");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleException(Exception ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("исключение", ex.getClass().getSimpleName())
        .addKeyValue("сообщение", ex.getMessage())
        .log("Непредвиденная ошибка");

    return buildResponse(
        ex, HttpStatus.INTERNAL_SERVER_ERROR, "Непредвиденная ошибка. Попробуйте позже");
  }

  private ResponseEntity<Map<String, String>> buildResponse(
      Exception ex, HttpStatus status, String defaultMessage) {
    String rawMessage = ex.getMessage();

    String message;
    if (rawMessage != null && rawMessage.startsWith("message: ")) {
      message = rawMessage.replace("message: ", "");
    } else {
      message = defaultMessage;
    }

    return ResponseEntity.status(status).body(Map.of("message", message));
  }
}
