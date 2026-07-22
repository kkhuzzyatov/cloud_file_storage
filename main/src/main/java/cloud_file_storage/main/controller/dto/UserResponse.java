package cloud_file_storage.main.controller.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponse(UUID id, String username) {}
