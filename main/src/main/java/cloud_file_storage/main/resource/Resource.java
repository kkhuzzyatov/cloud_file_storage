package cloud_file_storage.main.resource;

import lombok.Builder;

@Builder
public record Resource(String name, byte[] bytes) {}
