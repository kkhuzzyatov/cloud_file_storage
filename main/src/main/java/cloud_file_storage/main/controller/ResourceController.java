package cloud_file_storage.main.controller;

import cloud_file_storage.main.resource.Resource;
import cloud_file_storage.main.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Resource", description = "Управление ресурсами")
public class ResourceController {
  private final ResourceService resourceService;

  @Operation(summary = "Получение информации о ресурсе")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "информация о ресурсе"),
    @ApiResponse(responseCode = "400", description = "невалидный или отсутствующий путь"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "ресурс не найден"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping("/resource")
  public ResponseEntity<?> getInfo(@RequestParam String path) {
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
  @DeleteMapping("/resource")
  public ResponseEntity<?> delete(@RequestParam String path) {
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
  @GetMapping("/resource/download")
  public ResponseEntity<?> download(@RequestParam String path) {
    return ResponseEntity.ok(resourceService.getResource(path));
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
  @PostMapping("/resource/move")
  public ResponseEntity<?> move(@RequestParam String from, @RequestParam String to) {
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
  @GetMapping("/resource/search")
  public ResponseEntity<?> search(@RequestParam String query) {
    return ResponseEntity.ok(resourceService.search(query));
  }

  @Operation(summary = "Загрузка")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "ресурс загружен"),
    @ApiResponse(responseCode = "400", description = "невалидное тело запроса"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "409", description = "файл уже существует"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/resource")
  public ResponseEntity<?> upload(
      @RequestParam String path, @RequestParam("file") MultipartFile file) throws IOException {
    byte[] bytes = file.getBytes();
    String name = file.getName();
    String[] nameParts = name.split("/");
    if (nameParts.length > 1) {
      for (int i = 0; i < nameParts.length - 2; i++) {
        path += nameParts[i];
      }
      name = nameParts[nameParts.length - 1];
    }
    Resource resource = new Resource(name, bytes);
    resourceService.createResource(path, resource);
    return ResponseEntity.status(201).build();
  }

  @Operation(summary = "Папки")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "данные о папке"),
    @ApiResponse(responseCode = "400", description = "невалидный или отсутствующий путь"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "папка не существует"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping("/directory")
  public ResponseEntity<?> getDirectoryInfo(@RequestParam String path) {
    return ResponseEntity.ok(resourceService.getInfo(path));
  }

  @Operation(summary = "Создание пустой папки")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "папка создана"),
    @ApiResponse(responseCode = "400", description = "невалидный или отсутствующий путь"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "Родительская папка не существует"),
    @ApiResponse(responseCode = "409", description = "папка уже существует"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/directory")
  public ResponseEntity<?> createDirectory(@RequestParam String path) {
    resourceService.createDirectory(path);
    return ResponseEntity.status(201).build();
  }
}
