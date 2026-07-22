package cloud_file_storage.main.resource;

import cloud_file_storage.main.controller.dto.ResourceInformationResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResourceService {
  private final MockResourceRepository mockResourceRepository;

  public ResourceInformationResponse getInfo(String path) {
    char lastChar = path.toCharArray()[path.toCharArray().length - 1];
    if (lastChar == '/') {
      path = path.substring(0, path.length() - 2);
      return ResourceInformationResponse.builder()
          .path(path + "/")
          .name(path.split("/")[path.split("/").length - 1])
          .type("DIRECTORY")
          .build();
    }
    return ResourceInformationResponse.builder()
        .path(path)
        .name(path.split("/")[path.split("/").length - 1])
        .size(mockResourceRepository.getResource(path).bytes().length)
        .type("FILE")
        .build();
  }

  public void delete(String path) {
    char lastChar = path.toCharArray()[path.toCharArray().length - 1];
    if (lastChar == '/') {
      path = path.substring(0, path.length() - 2);
      mockResourceRepository.deleteDirectory(path);
    }
    mockResourceRepository.deleteResource(path);
  }

  public Resource getResource(String path) {
    char lastChar = path.toCharArray()[path.toCharArray().length - 1];
    if (lastChar == '/') {
      path = path.substring(0, path.length() - 2);
      return mockResourceRepository.getAllResourcesOfDirectory(path);
    }
    return mockResourceRepository.getResource(path);
  }

  public void move(String from, String to) {
    Resource resource = mockResourceRepository.getResource(from);
    mockResourceRepository.deleteResource(from);
    mockResourceRepository.createResource(to, resource);
  }

  public void createResource(String path, Resource resource) {
    mockResourceRepository.createResource(path, resource);
  }

  public List<ResourceInformationResponse> search(String query) {
    List<ResourceInformationResponse> result = new ArrayList<>();
    Map<String, Resource> resources = mockResourceRepository.search(query);
    for (String path : resources.keySet()) {
      Resource value = resources.get(path);
      result.add(
          ResourceInformationResponse.builder()
              .path(path)
              .name(value.name())
              .size(value.bytes().length)
              .type("FILE")
              .build());
    }
    return result;
  }

  public void createDirectory(String path) {
    mockResourceRepository.createDirectory(path);
  }
}
