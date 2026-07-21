package cloud_file_storage.main.controller;

import cloud_file_storage.main.controller.dto.RequestDto;
import cloud_file_storage.main.controller.dto.ResponseDto;
import cloud_file_storage.main.service.UserService;
import cloud_file_storage.main.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Регистрация и авторизация")
public class AuthController {
  private final UserService userService;

  @Operation(summary = "Регистрация")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "успешная регистрация"),
    @ApiResponse(responseCode = "400", description = "ошибка валидации"),
    @ApiResponse(responseCode = "409", description = "username занят"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/auth/sign-up")
  public ResponseEntity<ResponseDto> signUp(@Valid @RequestBody RequestDto requestDto) {
    String token = userService.signUp(requestDto.username(), requestDto.password());
    return ResponseEntity.status(201).body(ResponseDto.builder().token(token).build());
  }

  @Operation(summary = "Авторизация")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "успешная авторизация"),
    @ApiResponse(responseCode = "400", description = "ошибка валидации"),
    @ApiResponse(responseCode = "401", description = "неверные данные"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/auth/sign-in")
  public ResponseEntity<ResponseDto> signIn(@Valid @RequestBody RequestDto requestDto) {
    String token = userService.signIn(requestDto.username(), requestDto.password());
    return ResponseEntity.ok().body(ResponseDto.builder().token(token).build());
  }

  @Operation(summary = "Выход из аккаунта")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "успешный логаут"),
    @ApiResponse(responseCode = "401", description = "не авторизован"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/auth/sign-out")
  public ResponseEntity<Void> signOut(@RequestHeader("Authorization") String token) {
    userService.signOut(token);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Текущий пользователь")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "данные пользователя получены"),
    @ApiResponse(responseCode = "401", description = "не авторизован"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping("/user/me")
  public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
    User user = userService.me(token);
    return ResponseEntity.ok(user);
  }
}
