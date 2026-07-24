package cloud_file_storage.main.controller;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PathValidator {

  private static final Pattern SEGMENT_PATTERN = Pattern.compile("^[A-Za-z0-9._-]+$");

  public boolean isPathValid(String path) {
    if (path == null || path.isBlank()) {
      return false;
    }

    if (path.contains("//")) {
      return false;
    }

    String normalized = path;

    if (normalized.endsWith("/")) {
      normalized = normalized.substring(0, normalized.length() - 1);
    }

    if (normalized.isEmpty()) {
      return false;
    }

    for (String segment : normalized.split("/")) {
      if (segment.isEmpty()) {
        return false;
      }

      if (segment.equals(".") || segment.equals("..")) {
        return false;
      }

      if (!SEGMENT_PATTERN.matcher(segment).matches()) {
        return false;
      }
    }

    return true;
  }
}
