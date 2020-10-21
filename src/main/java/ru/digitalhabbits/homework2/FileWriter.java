package ru.digitalhabbits.homework2;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

import static java.lang.Thread.currentThread;
import static org.slf4j.LoggerFactory.getLogger;

public class FileWriter
        implements Runnable {
    private static final Logger logger = getLogger(FileWriter.class);

    private Exchanger<List<String>> exchanger;
    private String resultFileName;

    public FileWriter(Exchanger<List<String>> exchanger, String resultFileName) {
        this.exchanger = exchanger;
        this.resultFileName = resultFileName;
    }


    @Override
    public void run() {
        logger.info("Started writer thread {}", currentThread().getName());
        final File file = new File(resultFileName);
        try (BufferedWriter outputWriter = new BufferedWriter(new java.io.FileWriter(file))) {
            List<String> resultLineList = new ArrayList<>();
            while (!currentThread().isInterrupted()) {
                try {
                    resultLineList = exchanger.exchange(resultLineList);
                } catch (InterruptedException e) {
                    break;
                }
                for (String line : resultLineList) {
                    outputWriter.write(line);
                    outputWriter.newLine();
                }
            }
        } catch (IOException exception) {
            logger.error("", exception);
        }
        logger.info("Finish writer thread {}", currentThread().getName());
    }
}
