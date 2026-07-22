package cloud_file_storage.main.controller;

import cloud_file_storage.main.controller.dto.UserResponse;
import cloud_file_storage.main.service.UserService;
import cloud_file_storage.main.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Данные пользователя")
public class UserController {
  private final UserService userService;

  @Operation(summary = "Текущий пользователь")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "данные пользователя получены"),
    @ApiResponse(responseCode = "401", description = "не авторизован"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
    User user = userService.me(token);
    UserResponse userResponse =
        UserResponse.builder().id(user.getId()).username(user.getUsername()).build();
    return ResponseEntity.ok(userResponse);
  }
}
