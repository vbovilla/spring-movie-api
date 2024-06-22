package in.dminc.service.impl;

import in.dminc.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // get name of the file
        String filename = file.getOriginalFilename();

        // to get the file path
        String filePath = path + File.separator + filename;

        // create file object
        File f = new File(path);

        if (!f.exists()) {
            boolean mkdir = f.mkdir();
            if (mkdir) {
                log.debug("directory {} created", path);
            } else {
                log.debug("directory {} already exists", path);
            }
        }

        // copy the file or upload to the above directory
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    @Override
    public InputStream downloadFile(String path, String fileName) throws FileNotFoundException {
        if (!Files.exists(Paths.get(path + File.separator + fileName))) {
            throw new FileNotFoundException("File not found: " + path + File.separator + fileName);
        }
        String filePath = path + File.separator + fileName;
        return new FileInputStream(filePath);
    }
}
