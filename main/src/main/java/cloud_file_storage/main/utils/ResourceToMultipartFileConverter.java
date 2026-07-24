package cloud_file_storage.main.utils;

import cloud_file_storage.main.resource.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ResourceToMultipartFileConverter implements Converter<Resource, MultipartFile> {

  @Override
  public MultipartFile convert(Resource source) {
    return new ByteArrayMultipartFile(source.name(), source.bytes());
  }

  private record ByteArrayMultipartFile(String name, byte[] content) implements MultipartFile {

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getOriginalFilename() {
      return name;
    }

    @Override
    public String getContentType() {
      return null;
    }

    @Override
    public boolean isEmpty() {
      return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
      return content.length;
    }

    @Override
    public byte[] getBytes() {
      return content;
    }

    @Override
    public InputStream getInputStream() {
      return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException {
      java.nio.file.Files.write(dest.toPath(), content);
    }
  }
}
