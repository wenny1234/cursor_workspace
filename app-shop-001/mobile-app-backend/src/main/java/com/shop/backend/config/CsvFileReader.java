package com.shop.backend.config;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.shop.backend.model.Product;
import com.shop.backend.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class CsvFileReader {

    @Value("${app.csv.data-dir}")
    private String dataDir;

    public List<User> readUsers() {
        return readCsv("users.csv", User.class);
    }

    public List<Product> readProducts() {
        return readCsv("products.csv", Product.class);
    }

    private <T> List<T> readCsv(String fileName, Class<T> type) {
        Path csvPath = Paths.get(dataDir).resolve(fileName).toAbsolutePath().normalize();
        if (!Files.exists(csvPath)) {
            log.warn("CSV file not found: {}", csvPath);
            return Collections.emptyList();
        }

        try (Reader reader = new FileReader(csvPath.toFile())) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<T> rows = csvToBean.parse();
            log.info("Read {} rows from {}", rows.size(), csvPath);
            return rows;
        } catch (Exception e) {
            log.error("Failed to read CSV file: {}", csvPath, e);
            throw new IllegalStateException("Failed to read CSV file: " + csvPath, e);
        }
    }
}
