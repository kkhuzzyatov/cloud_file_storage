package cloud_file_storage.main.controller;

import cloud_file_storage.main.controller.dto.ResourceInformationResponse;
import cloud_file_storage.main.resource.Resource;
import cloud_file_storage.main.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Tag(name = "Resource", description = "Управление ресурсами")
@Slf4j
public class ResourceController {
  private final ResourceService resourceService;
  private final PathValidator pathValidator;

  @Operation(summary = "Загрузка")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "ресурс загружен"),
    @ApiResponse(responseCode = "400", description = "невалидное тело запроса"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "409", description = "файл уже существует"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> upload(
      @RequestParam String path, @RequestParam("file") MultipartFile file, HttpSession session)
      throws IOException {
    if (!pathValidator.isPathValid(path)) {
      return ResponseEntity.status(400).body("параметр пути не корректен");
    }
    path = session.getAttribute("userId") + "/" + path;
    byte[] bytes = file.getBytes();
    String name = file.getOriginalFilename();
    String[] nameParts = name.split("/");
    if (nameParts.length > 1) {
      for (int i = 0; i < nameParts.length - 2; i++) {
        path += nameParts[i];
      }
      name = nameParts[nameParts.length - 1];
    }
    Resource resource = new Resource(name, bytes);
    ResourceInformationResponse resourceInformationResponse =
        ResourceInformationResponse.builder()
            .path(concatExceptFirst(path.split("/")))
            .name(name)
            .size(bytes.length)
            .type("FILE")
            .build();
    resourceService.createResource(path, resource);
    return ResponseEntity.status(201).body(List.of(resourceInformationResponse));
  }

  @Operation(summary = "Получение информации о ресурсе")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "информация о ресурсе"),
    @ApiResponse(responseCode = "400", description = "невалидный или отсутствующий путь"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "ресурс не найден"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping
  public ResponseEntity<?> getInfo(@RequestParam String path, HttpSession session) {
    if (!pathValidator.isPathValid(path)) {
      return ResponseEntity.status(400).body("параметр path не корректен");
    }
    path = session.getAttribute("userId") + "/" + path;
    return ResponseEntity.ok(resourceService.getInfo(path));
  }

  @Operation(summary = "Удаление ресурса")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "ресурс удален"),
    @ApiResponse(responseCode = "400", description = "невалидный или отсутствующий путь"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "ресурс не найден"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @DeleteMapping
  public ResponseEntity<?> delete(@RequestParam String path, HttpSession session) {
    if (!pathValidator.isPathValid(path)) {
      return ResponseEntity.status(400).body("параметр path не корректен");
    }
    path = session.getAttribute("userId") + "/" + path;
    resourceService.delete(path);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Скачивание ресурса")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "ресурс скачан"),
    @ApiResponse(responseCode = "400", description = "невалидный или отсутствующий путь"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "ресурс не найден"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping("/download")
  public ResponseEntity<?> download(@RequestParam String path, HttpSession session) {
    if (!pathValidator.isPathValid(path)) {
      return ResponseEntity.status(400).body("параметр path не корректен");
    }
    path = session.getAttribute("userId") + "/" + path;
    return ResponseEntity.ok(resourceService.getResource(path).bytes());
  }

  @Operation(summary = "Переименование/перемещение ресурса")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "ресурс перемещен"),
    @ApiResponse(responseCode = "400", description = "невалидный или отсутствующий путь"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "ресурс не найден"),
    @ApiResponse(responseCode = "409", description = "ресурс, лежащий по пути to уже существует"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/move")
  public ResponseEntity<?> move(
      @RequestParam String from, @RequestParam String to, HttpSession session) {
    if (!pathValidator.isPathValid(from)) {
      return ResponseEntity.status(400).body("параметр from не корректен");
    }
    if (!pathValidator.isPathValid(to)) {
      return ResponseEntity.status(400).body("параметр to не корректен");
    }
    from = session.getAttribute("userId") + "/" + from;
    to = session.getAttribute("userId") + "/" + to;
    resourceService.move(from, to);
    return ResponseEntity.status(200).build();
  }

  @Operation(summary = "Поиск")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "ресурс найден"),
    @ApiResponse(
        responseCode = "400",
        description = "невалидный или отсутствующий поисковый запрос"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping("/search")
  public ResponseEntity<?> search(@RequestParam String query, HttpSession session) {
    if (query.contains(" ")) {
      return ResponseEntity.status(400).body("поисковый запрос не может содержать пробелов");
    }
    return ResponseEntity.ok(
        resourceService.search((String) session.getAttribute("userId"), query));
  }

  private String concatExceptFirst(String[] array) {
    if (array == null || array.length <= 1) {
      return "";
    }
    return String.join("/", java.util.Arrays.copyOfRange(array, 1, array.length));
  }
}
