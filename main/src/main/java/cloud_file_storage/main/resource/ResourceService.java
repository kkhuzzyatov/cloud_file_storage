package cloud_file_storage.main.resource;

import cloud_file_storage.main.controller.dto.ResourceInformationResponse;
import cloud_file_storage.main.exception.FileIsAlreadyExistException;
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
    if (path == null || path.isBlank()) {
      throw new IllegalArgumentException("Path cannot be empty");
    }

    // Directory
    if (isDirectoryPath(path)) {
      String normalizedPath = removeTrailingSlash(path);

      return ResourceInformationResponse.builder()
          .path(normalizedPath + "/")
          .name(getLastPart(normalizedPath))
          .type("DIRECTORY")
          .build();
    }

    // File
    String folderPath = getParentPath(path);
    String fileName = getFileName(path);

    Resource resource = mockResourceRepository.getResource(folderPath, fileName);

    return ResourceInformationResponse.builder()
        .path(path)
        .name(fileName)
        .size(resource.bytes().length)
        .type("FILE")
        .build();
  }

  public void delete(String path) {
    if (isDirectoryPath(path)) {
      mockResourceRepository.deleteDirectory(removeTrailingSlash(path));
      return;
    }

    String folderPath = getParentPath(path);
    String fileName = getFileName(path);

    mockResourceRepository.deleteResource(folderPath, fileName);
  }

  public Resource getResource(String path) {
    if (isDirectoryPath(path)) {
      return mockResourceRepository.getAllResourcesOfDirectory(removeTrailingSlash(path));
    }

    String folderPath = getParentPath(path);
    String fileName = getFileName(path);

    return mockResourceRepository.getResource(folderPath, fileName);
  }

  public void move(String from, String to) {
    Resource resource = getResource(from);
    delete(from);
    createResource(to, resource);
  }

  public void createResource(String path, Resource resource) {
    if (mockResourceRepository.isResourceExisted(path, resource.name())) {
      throw new FileIsAlreadyExistException("Файл с таким именем уже существует");
    }

    mockResourceRepository.createResource(path, resource);
  }

  public List<ResourceInformationResponse> search(String query) {
    List<ResourceInformationResponse> result = new ArrayList<>();

    Map<String, Resource> resources = mockResourceRepository.search(query);

    for (String path : resources.keySet()) {
      Resource resource = resources.get(path);

      result.add(
          ResourceInformationResponse.builder()
              .path(path)
              .name(resource.name())
              .size(resource.bytes().length)
              .type("FILE")
              .build());
    }

    return result;
  }

  public void createDirectory(String path) {
    mockResourceRepository.createDirectory(removeTrailingSlash(path));
  }

  private boolean isDirectoryPath(String path) {
    return path.endsWith("/");
  }

  private String removeTrailingSlash(String path) {
    if (path.endsWith("/")) {
      return path.substring(0, path.length() - 1);
    }

    return path;
  }

  private String getParentPath(String path) {
    int index = path.lastIndexOf("/");

    if (index == -1) {
      return "";
    }

    return path.substring(0, index);
  }

  private String getFileName(String path) {
    int index = path.lastIndexOf("/");

    if (index == -1) {
      return path;
    }

    return path.substring(index + 1);
  }

  private String getLastPart(String path) {
    int index = path.lastIndexOf("/");

    if (index == -1) {
      return path;
    }

    return path.substring(index + 1);
  }
}
