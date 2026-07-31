package cloud_file_storage.main.config;

import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

  @Before("within(cloud_file_storage.main.controller..*)")
  public void logRequest(JoinPoint joinPoint) {

    CodeSignature signature = (CodeSignature) joinPoint.getSignature();

    String[] names = signature.getParameterNames();
    Object[] values = joinPoint.getArgs();

    Map<String, Object> params = new LinkedHashMap<>();

    for (int i = 0; i < names.length; i++) {
      Object value = values[i];

      if (value instanceof MultipartFile file) {
        params.put(names[i] + ".name", file.getOriginalFilename());
        params.put(names[i] + ".size", file.getSize());
      } else if (value instanceof HttpSession session) {
        params.put("userId", session.getAttribute("userId"));
      } else {
        params.put(names[i], value);
      }
    }

    log.info(
        "Request: {}.{} {}",
        signature.getDeclaringType().getSimpleName(),
        signature.getName(),
        params);
  }
}
