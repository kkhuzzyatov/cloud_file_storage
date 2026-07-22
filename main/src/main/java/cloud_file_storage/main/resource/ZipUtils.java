package cloud_file_storage.main.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Component;

@Component
public class ZipUtils {

  public byte[] createZip(List<Resource> files) throws IOException {
    try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(stream)) {

      for (Resource file : files) {
        ZipEntry entry = new ZipEntry(file.name());

        zos.putNextEntry(entry);
        zos.write(file.bytes());
        zos.closeEntry();
      }

      zos.finish();
      return stream.toByteArray();
    }
  }
}
