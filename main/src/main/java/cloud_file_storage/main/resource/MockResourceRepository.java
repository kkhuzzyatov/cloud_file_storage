package cloud_file_storage.main.resource;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MockResourceRepository {
  private final List<String> directories = new ArrayList<>();
  private final Map<String, List<Resource>> resources = new HashMap<>();
  private final ZipUtils zipUtils;

  public boolean isDirectoryExisted(String path) {
    return directories.contains(path);
  }

  @SneakyThrows
  public Resource getAllResourcesOfDirectory(String path) {
    String[] parts = path.split("/");
    String parentPath = String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1));
    return new Resource(parts[parts.length - 2], zipUtils.createZip(resources.get(parentPath)));
  }

  public Resource getResource(String path) {
    String[] parts = path.split("/");
    String parentPath = String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1));
    String resourceName = parts[parts.length - 1];
    for (Resource resource : resources.get(parentPath)) {
      if (resource.name().equals(resourceName)) {
        return resource;
      }
    }
    return null;
  }

  public void createDirectory(String path) {
    directories.add(path);
  }

  public void createResource(String path, Resource resource) {
    List<Resource> value = resources.get(path) == null ? new ArrayList<>() : resources.get(path);
    value.add(resource);
    resources.put(path, value);
  }

  public void deleteResource(String path) {
    resources.replace(path, resources.get(path));
  }

  public void deleteDirectory(String path) {
    resources.replace(path, resources.get(path));
    directories.remove(path);
  }

  public Map<String, Resource> search(String query) {
    Map<String, Resource> result = new HashMap<>();
    for (Map.Entry<String, List<Resource>> entry : resources.entrySet()) {
      String folderPath = entry.getKey();
      ;
      List<Resource> value = entry.getValue();
      for (Resource resource : value) {
        if ((folderPath + "/" + resource.name()).contains(query)) {
          result.put(folderPath + "/" + resource.name(), resource);
        }
      }
    }
    return result;
  }
}
