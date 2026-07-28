package cloud_file_storage.main.session;

import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TokenRepository {

  private static final String PREFIX = "token:";

  private final StringRedisTemplate redisTemplate;
  private final TokenProperties properties;

  public TokenRepository(StringRedisTemplate redisTemplate, TokenProperties properties) {
    this.redisTemplate = redisTemplate;
    this.properties = properties;
  }

  public UUID generate(UUID userId) {
    UUID token = UUID.randomUUID();

    redisTemplate.opsForValue().set(PREFIX + token, userId.toString(), properties.ttl());

    return token;
  }

  public UUID getUserId(UUID token) {
    String value = redisTemplate.opsForValue().get(PREFIX + token);

    if (value == null) {
      return null;
    }

    return UUID.fromString(value);
  }

  public void deleteToken(UUID token) {
    redisTemplate.delete(PREFIX + token);
  }
}
