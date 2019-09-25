package com.milan.tipster.service;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface FileStorageService {

    void saveToFileIfNotExists(Document document, String fileName, boolean timestampFlag) throws IOException;

    void moveFileToDir(String fileName, String dirPath);

}
