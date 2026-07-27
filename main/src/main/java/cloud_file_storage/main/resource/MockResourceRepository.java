package cloud_file_storage.main.resource;

import cloud_file_storage.main.exception.ResourceNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MockResourceRepository {

  @Value("${app.data-dir-path}")
  private String dataDir;

  public boolean isResourceExisted(String path, String fileName) {
    Path resourcePath = resolvePath(path, fileName);
    return Files.exists(resourcePath) && Files.isRegularFile(resourcePath);
  }

  public boolean isDirectoryExist(String path) {
    Path directoryPath = resolvePath(path);
    return Files.exists(directoryPath) && Files.isDirectory(directoryPath);
  }

  public List<Resource> getAllResourcesOfDirectory(String path) {
    Path directory = resolvePath(path);

    if (!Files.exists(directory) || !Files.isDirectory(directory)) {
      throw new ResourceNotFoundException("Directory not found: " + directory);
    }

    try {
      List<Resource> files;

      try (Stream<Path> stream = Files.list(directory)) {
        files =
            stream
                .filter(Files::isRegularFile)
                .map(
                    file ->
                        Resource.builder()
                            .name(file.getFileName().toString())
                            .bytes(readFile(file))
                            .build())
                .collect(Collectors.toList());
      }

      return files;

    } catch (IOException e) {
      throw new RuntimeException("Cannot get files of directory: " + directory, e);
    }
  }

  public Resource getResource(String path, String fileName) {
    Path file = resolvePath(path, fileName);

    if (!Files.exists(file) || !Files.isRegularFile(file)) {
      throw new ResourceNotFoundException("Resource not found: " + file);
    }

    return Resource.builder().name(fileName).bytes(readFile(file)).build();
  }

  public void createDirectory(String path) {
    Path directory = resolvePath(path);

    try {
      Files.createDirectories(directory);
    } catch (IOException e) {
      throw new RuntimeException("Cannot create directory: " + directory, e);
    }
  }

  public void createResource(String path, Resource resource) {
    Path file = resolvePath(path, resource.name());

    try {
      Files.createDirectories(file.getParent());
      Files.write(file, resource.bytes(), StandardOpenOption.CREATE_NEW);
    } catch (FileAlreadyExistsException e) {
      throw new RuntimeException("Resource already exists: " + file, e);
    } catch (IOException e) {
      throw new RuntimeException("Cannot create resource: " + file, e);
    }
  }

  public void deleteResource(String path, String fileName) {
    Path file = resolvePath(path, fileName);

    if (!Files.exists(file)) {
      throw new ResourceNotFoundException("Resource not found: " + file);
    }

    try {
      Files.delete(file);
    } catch (IOException e) {
      throw new RuntimeException("Cannot delete resource: " + file, e);
    }
  }

  public void deleteDirectory(String path) {
    Path directory = resolvePath(path);

    if (!Files.exists(directory)) {
      throw new ResourceNotFoundException("Directory not found: " + directory);
    }

    try (Stream<Path> stream = Files.walk(directory)) {
      stream.sorted(Comparator.reverseOrder()).forEach(this::deletePath);

    } catch (IOException e) {
      throw new RuntimeException("Cannot delete directory: " + directory, e);
    }
  }

  public Map<String, Resource> search(String query) {
    Path root = resolvePath();

    if (!Files.exists(root)) {
      return Map.of();
    }

    Map<String, Resource> result = new HashMap<>();

    try (Stream<Path> stream = Files.walk(root)) {
      stream
          .filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().contains(query))
          .forEach(
              file -> {
                String relativePath = root.relativize(file).toString().replace("\\", "/");

                result.put(
                    relativePath,
                    Resource.builder()
                        .name(file.getFileName().toString())
                        .bytes(readFile(file))
                        .build());
              });

    } catch (IOException e) {
      throw new RuntimeException("Cannot search resources", e);
    }

    return result;
  }

  public Path resolvePath(String... parts) {
    Path result = Paths.get(dataDir);

    for (String part : parts) {
      result = result.resolve(part);
    }

    return result.normalize();
  }

  public List<String> getSubDirectories(String path) {
    Path directory = resolvePath(path);

    if (!Files.exists(directory) || !Files.isDirectory(directory)) {
      throw new ResourceNotFoundException("Directory not found: " + directory);
    }

    try (Stream<Path> stream = Files.list(directory)) {
      return stream
          .filter(Files::isDirectory)
          .map(dir -> dir.getFileName().toString())
          .sorted()
          .toList();
    } catch (IOException e) {
      throw new RuntimeException("Cannot get subdirectories of: " + directory, e);
    }
  }

  private byte[] readFile(Path path) {
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new RuntimeException("Cannot read file: " + path, e);
    }
  }

  private void deletePath(Path path) {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new RuntimeException("Cannot delete: " + path, e);
    }
  }
}
