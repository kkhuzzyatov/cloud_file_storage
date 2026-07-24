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

  private final ZipUtils zipUtils;

  @Value("${app.data-dir-path}")
  private String dataDir;

  /** Checks if resource exists: dataDir/path/fileName */
  public boolean isResourceExisted(String path, String fileName) {
    Path resourcePath = resolvePath(path, fileName);
    return Files.exists(resourcePath) && Files.isRegularFile(resourcePath);
  }

  /**
   * Returns zipped content of all files inside the lowest directory. Example:
   * path=folder1/folder2/folder3 result name=folder3 result bytes=zip of files inside folder3
   */
  public Resource getAllResourcesOfDirectory(String path) {
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

      return Resource.builder()
          .name(directory.getFileName().toString())
          .bytes(zipUtils.createZip(files))
          .build();

    } catch (IOException e) {
      throw new RuntimeException("Cannot create zip for directory: " + directory, e);
    }
  }

  /** Returns a single file resource. */
  public Resource getResource(String path, String fileName) {
    Path file = resolvePath(path, fileName);

    if (!Files.exists(file) || !Files.isRegularFile(file)) {
      throw new ResourceNotFoundException("Resource not found: " + file);
    }

    return Resource.builder().name(fileName).bytes(readFile(file)).build();
  }

  /** Creates directory and all missing parent directories. */
  public void createDirectory(String path) {
    Path directory = resolvePath(path);

    try {
      Files.createDirectories(directory);
    } catch (IOException e) {
      throw new RuntimeException("Cannot create directory: " + directory, e);
    }
  }

  /** Creates file and missing parent directories. */
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

  /** Deletes file but keeps parent directories. */
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

  /** Deletes directory recursively including all files and subdirectories. */
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

  /**
   * Searches recursively in data directory. File is added if its name contains query. Key =
   * relative path to resource from data directory.
   */
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

  private Path resolvePath(String... parts) {
    Path result = Paths.get(dataDir);

    for (String part : parts) {
      result = result.resolve(part);
    }

    return result.normalize();
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
