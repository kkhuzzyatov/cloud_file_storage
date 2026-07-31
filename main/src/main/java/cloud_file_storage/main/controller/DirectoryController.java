package cloud_file_storage.main.controller;

import cloud_file_storage.main.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
@Tag(name = "Directory", description = "Управление папками")
public class DirectoryController {
  private final ResourceService resourceService;
  private final PathValidator pathValidator;

  @Operation(summary = "Папки")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "данные о папке"),
    @ApiResponse(responseCode = "400", description = "невалидный или отсутствующий путь"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "папка не существует"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping
  public ResponseEntity<?> getDirectoryFilesInfo(@RequestParam String path, HttpSession session) {
    if (!pathValidator.isPathValid(path)) {
      return ResponseEntity.status(400).body("параметр path не корректен");
    }
    path = session.getAttribute("userId") + "/" + path;
    return ResponseEntity.ok(resourceService.getAllDirectoryFilesInfo(path));
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
  @PostMapping
  public ResponseEntity<?> createDirectory(@RequestParam String path, HttpSession session) {
    if (!pathValidator.isPathValid(path)) {
      return ResponseEntity.status(400).body("параметр path не корректен");
    }
    path = session.getAttribute("userId") + "/" + path;
    resourceService.createDirectory(path);
    return ResponseEntity.status(201).build();
  }
}
