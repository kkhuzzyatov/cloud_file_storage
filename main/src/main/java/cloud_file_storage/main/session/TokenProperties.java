package cloud_file_storage.main.session;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.token")
public record TokenProperties(Duration ttl) {}
