package com.milan.tipster.util;

import com.milan.tipster.dao.TipRepository;
import com.milan.tipster.model.Tip;
import com.milan.tipster.service.FetchingService;
import com.milan.tipster.service.TipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.milan.tipster.util.Constants.DEFAULT_FILE_STORAGE_DIR;
import static com.milan.tipster.util.Constants.GAMES_DIR_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class TipUpdaterCommandLineRunner implements CommandLineRunner {

    @Autowired
    private TipService tipService;

    @Autowired
    private FetchingService fetchingService;

    @Override
    public void run(String... args) throws Exception {

// if needed uncomment
//        updateAllTipsFromFiles();
    }

    private void updateAllTipsFromFiles() throws IOException {
        int count = 0;
        File folder = new File(DEFAULT_FILE_STORAGE_DIR + GAMES_DIR_NAME);
//        File folder = new File(Utils.trimLastChar(DEFAULT_FILE_STORAGE_DIR));
        Objects.requireNonNull(folder, "Folder!");
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                String fileName = fileEntry.getName();
                String absoluteFilePath = fileEntry.getAbsolutePath();
                log.info("Fetching tip from file: {}", fileEntry.getAbsolutePath());
                Document tipDoc = fetchingService.fetchDocByUrlOrPath(absoluteFilePath, true);
                Boolean tipUpdated = tipService.updateTipFromFile(tipDoc, fileName, absoluteFilePath);
                if (tipUpdated) {
                    log.info("Successfully updated tip {} from file {}", fileName);
                    count++;
                } else {
                    log.warn("No tip been updated from file {}", fileName);
                }
            }
        }
        log.info("\n\n------- {} number of tips has been updated! -------\n\n", count);

    }
}
