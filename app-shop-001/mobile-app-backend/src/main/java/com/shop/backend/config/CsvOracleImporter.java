package com.shop.backend.config;

import com.shop.backend.mapper.ProductMapper;
import com.shop.backend.mapper.UserMapper;
import com.shop.backend.model.Product;
import com.shop.backend.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
@Profile("oracle")
@Order(1)
@Slf4j
public class CsvOracleImporter implements CommandLineRunner {

    private final CsvFileReader csvFileReader;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.oracle.import-csv-on-startup:true}")
    private boolean importCsvOnStartup;

    public CsvOracleImporter(
            CsvFileReader csvFileReader,
            UserMapper userMapper,
            ProductMapper productMapper,
            JdbcTemplate jdbcTemplate) {
        this.csvFileReader = csvFileReader;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!importCsvOnStartup) {
            log.info("CSV -> Oracle import is disabled (app.oracle.import-csv-on-startup=false)");
            return;
        }

        List<User> users = csvFileReader.readUsers();
        List<Product> products = csvFileReader.readProducts();

        if (users.isEmpty() && products.isEmpty()) {
            log.warn("No CSV data found to import into Oracle");
            return;
        }

        log.info("Importing CSV data into Oracle (users={}, products={})", users.size(), products.size());

        userMapper.deleteAll();
        productMapper.deleteAll();

        users.stream()
                .sorted(Comparator.comparing(User::getId))
                .forEach(userMapper::insertWithId);

        products.stream()
                .sorted(Comparator.comparing(Product::getId))
                .forEach(productMapper::insertWithId);

        resetSequence("users_seq", users.stream().map(User::getId).max(Long::compareTo).orElse(0L));
        resetSequence("products_seq", products.stream().map(Product::getId).max(Long::compareTo).orElse(0L));

        log.info("CSV import completed: {} users, {} products", userMapper.count(), productMapper.count());
    }

    private void resetSequence(String sequenceName, long maxId) {
        long nextValue = maxId + 1;
        jdbcTemplate.execute("DROP SEQUENCE " + sequenceName);
        jdbcTemplate.execute(
                "CREATE SEQUENCE " + sequenceName
                        + " START WITH " + nextValue
                        + " INCREMENT BY 1 NOCACHE NOCYCLE"
        );
        log.info("Reset sequence {} to start at {}", sequenceName, nextValue);
    }
}
