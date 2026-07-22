package cloud_file_storage.main.controller;

import cloud_file_storage.main.controller.dto.AuthRequest;
import cloud_file_storage.main.controller.dto.TokenResponse;
import cloud_file_storage.main.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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
  @PostMapping("/sign-up")
  public ResponseEntity<TokenResponse> signUp(@Valid @RequestBody AuthRequest authRequest) {
    String token = userService.signUp(authRequest.username(), authRequest.password());
    return ResponseEntity.status(201).body(TokenResponse.builder().token(token).build());
  }

  @Operation(summary = "Авторизация")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "успешная авторизация"),
    @ApiResponse(responseCode = "400", description = "ошибка валидации"),
    @ApiResponse(responseCode = "401", description = "неверные данные"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/sign-in")
  public ResponseEntity<TokenResponse> signIn(@Valid @RequestBody AuthRequest authRequest) {
    String token = userService.signIn(authRequest.username(), authRequest.password());
    return ResponseEntity.ok().body(TokenResponse.builder().token(token).build());
  }

  @Operation(summary = "Выход из аккаунта")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "успешный логаут"),
    @ApiResponse(responseCode = "401", description = "не авторизован"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/sign-out")
  public ResponseEntity<Void> signOut(@RequestHeader("Authorization") String token) {
    userService.signOut(token);
    return ResponseEntity.noContent().build();
  }
}
