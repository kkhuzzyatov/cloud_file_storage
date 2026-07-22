package cloud_file_storage.main.controller.dto;

import lombok.Builder;

@Builder
public record ResourceInformationResponse(String path, String name, Integer size, String type) {}
