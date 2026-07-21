package cloud_file_storage.main.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MockTokenRepository {
  private final Map<UUID, UUID> tokens = new HashMap<>();

  public UUID generate(UUID userId) {
    UUID token = UUID.randomUUID();
    tokens.put(token, userId);
    return token;
  }

  public UUID getUserId(UUID token) {
    return tokens.get(token);
  }

  public void deleteToken(UUID token) {
    tokens.replace(token, tokens.get(token));
  }
}
