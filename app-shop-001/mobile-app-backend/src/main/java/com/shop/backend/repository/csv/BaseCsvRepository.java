package com.shop.backend.repository.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Slf4j
public abstract class BaseCsvRepository<T> {
    
    @Value("${app.csv.data-dir}")
    private String dataDir;
    
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Path csvFilePath;
    
    protected abstract Class<T> getEntityClass();
    protected abstract String getCsvFileName();
    protected abstract String[] getCsvHeaders();
    protected abstract Long getEntityId(T entity);
    protected abstract void setEntityId(T entity, Long id);
    
    @PostConstruct
    public void init() {
        try {
            Path dataPath = Paths.get(dataDir);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            
            csvFilePath = dataPath.resolve(getCsvFileName());
            if (!Files.exists(csvFilePath)) {
                createCsvFileWithHeaders();
            }
        } catch (IOException e) {
            log.error("Failed to initialize CSV repository for {}", getCsvFileName(), e);
            throw new RuntimeException("Failed to initialize CSV repository", e);
        }
    }
    
    private void createCsvFileWithHeaders() throws IOException {
        lock.writeLock().lock();
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath.toFile()))) {
            writer.writeNext(getCsvHeaders());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public List<T> findAll() {
        lock.readLock().lock();
        try (Reader reader = new FileReader(csvFilePath.toFile())) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(getEntityClass())
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            
            return csvToBean.parse();
        } catch (IOException e) {
            log.error("Failed to read from CSV file: {}", csvFilePath, e);
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public T save(T entity) {
        lock.writeLock().lock();
        try {
            List<T> entities = findAll();
            
            Long entityId = getEntityId(entity);
            if (entityId == null) {
                // Generate new ID
                Long maxId = entities.stream()
                        .map(this::getEntityId)
                        .filter(id -> id != null)
                        .max(Long::compareTo)
                        .orElse(0L);
                setEntityId(entity, maxId + 1);
            } else {
                // Update existing entity
                entities.removeIf(e -> getEntityId(e).equals(entityId));
            }
            
            entities.add(entity);
            writeAll(entities);
            
            return entity;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void deleteById(Long id) {
        lock.writeLock().lock();
        try {
            List<T> entities = findAll();
            entities.removeIf(entity -> getEntityId(entity).equals(id));
            writeAll(entities);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void writeAll(List<T> entities) {
        try (Writer writer = new FileWriter(csvFilePath.toFile())) {
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(getEntityClass());
            
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(strategy)
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .build();
            
            beanToCsv.write(entities);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Failed to write to CSV file: {}", csvFilePath, e);
            throw new RuntimeException("Failed to write to CSV file", e);
        }
    }
    
    protected Path getCsvFilePath() {
        return csvFilePath;
    }
}