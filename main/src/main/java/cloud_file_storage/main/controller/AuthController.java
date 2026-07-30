package cloud_file_storage.main.controller;

import cloud_file_storage.main.controller.dto.AuthRequest;
import cloud_file_storage.main.controller.dto.MessageResponse;
import cloud_file_storage.main.service.UserService;
import cloud_file_storage.main.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
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
  public ResponseEntity<?> signUp(@Valid @RequestBody AuthRequest authRequest) {
    userService.signUp(authRequest.username(), authRequest.password());
    return ResponseEntity.status(201)
        .body(MessageResponse.builder().message("User is created").build());
  }

  @Operation(summary = "Авторизация")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "успешная авторизация"),
    @ApiResponse(responseCode = "400", description = "ошибка валидации"),
    @ApiResponse(responseCode = "401", description = "неверные данные"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/sign-in")
  public ResponseEntity<?> signIn(
      @Valid @RequestBody AuthRequest authRequest,
      HttpServletRequest request,
      HttpServletResponse response) {

    User user = userService.signIn(authRequest.username(), authRequest.password());

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            user.getUsername(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    SecurityContextRepository repository = new HttpSessionSecurityContextRepository();

    repository.saveContext(context, request, response);

    request.getSession().setAttribute("userId", user.getId().toString());

    return ResponseEntity.ok(MessageResponse.builder().message("Login is successful").build());
  }

  @Operation(summary = "Выход из аккаунта")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "успешный логаут"),
    @ApiResponse(responseCode = "401", description = "не авторизован"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/sign-out")
  public ResponseEntity<Void> signOut(HttpSession session) {
    session.invalidate();
    return ResponseEntity.noContent().build();
  }
}
