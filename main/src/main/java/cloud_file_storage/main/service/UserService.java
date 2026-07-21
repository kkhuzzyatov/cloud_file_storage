package cloud_file_storage.main.service;

import cloud_file_storage.main.exception.SessionNotFoundException;
import cloud_file_storage.main.exception.UserAlreadyExistsException;
import cloud_file_storage.main.exception.UserIsNotExistException;
import cloud_file_storage.main.session.MockTokenRepository;
import cloud_file_storage.main.user.User;
import cloud_file_storage.main.user.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final MockTokenRepository mockTokenRepository;

  public String signUp(String username, String rawPassword) {
    if (userRepository.existsByUsername(username)) {
      throw new UserAlreadyExistsException("пользователь с таким username уже существует");
    }

    User user =
        User.builder().username(username).passwordHash(passwordEncoder.encode(rawPassword)).build();

    userRepository.save(user);
    Optional<User> userOptional = userRepository.findByUsername(username);

    User userFromDb =
        userOptional.orElseThrow(() -> new UserIsNotExistException("внутрення ошибка"));

    return mockTokenRepository.generate(userFromDb.getId()).toString();
  }

  public String signIn(String username, String rawPassword) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("неверный username или пароль"));

    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("неверный username или пароль");
    }

    return mockTokenRepository.generate(user.getId()).toString();
  }

  public void signOut(String token) {
    mockTokenRepository.deleteToken(UUID.fromString(token));
  }

  public User me(String token) {
    UUID userId = mockTokenRepository.getUserId(UUID.fromString(token));
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new SessionNotFoundException("токен не валиден"));
    return user;
  }
}
