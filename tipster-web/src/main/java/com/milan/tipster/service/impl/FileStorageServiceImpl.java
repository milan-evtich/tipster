package com.milan.tipster.service.impl;

import com.milan.tipster.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Component
public class FileStorageServiceImpl implements FileStorageService {

    @Override
    public void saveToFileIfNotExists(Document document, String fileName, boolean timestampFlag) throws IOException {
        try {
            Path newFilePath = Paths.get(fileName + (timestampFlag ? new Date().getTime() : "") + ".html");
            Files.createFile(newFilePath);
            Files.write(newFilePath, document.body().text().getBytes());
            log.info("New file {} saved", newFilePath);
        } catch (FileAlreadyExistsException faee) {
            log.info("File already saved | fileName: {}", fileName);
        }
    }

    @Override
    public void moveFileToDir(String fileName, String dirPath) {
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(dirPath);

        Path source = Paths.get(fileName);
        Path newDir = Paths.get(dirPath);
        // if file name has error term games in its name
        Path target = (fileName.contains("games") ?
                Paths.get(fileName.replace("games", "")) : source);

        try {
            Files.move(source, newDir.resolve(target.getFileName()), REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Couldn't move file {} to dir {}", fileName, dirPath, e);
        }
    }
}
