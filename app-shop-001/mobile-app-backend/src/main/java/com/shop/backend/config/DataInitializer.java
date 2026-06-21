package com.shop.backend.config;

import com.shop.backend.model.Product;
import com.shop.backend.model.User;
import com.shop.backend.repository.ProductRepository;
import com.shop.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("csv")
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        checkAndInitializeProducts();
        log.info("数据初始化完成");
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("初始化用户数据...");
            
            LocalDateTime now = LocalDateTime.now();
            
            List<User> users = Arrays.asList(
                    User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("admin123"))
                            .email("admin@shop.com")
                            .role(User.Role.ADMIN)
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),
                    User.builder()
                            .username("staff")
                            .password(passwordEncoder.encode("staff123"))
                            .email("staff@shop.com")
                            .role(User.Role.STAFF)
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),
                    User.builder()
                            .username("viewer")
                            .password(passwordEncoder.encode("viewer123"))
                            .email("viewer@shop.com")
                            .role(User.Role.VIEWER)
                            .createdAt(now)
                            .updatedAt(now)
                            .build()
            );
            
            users.forEach(userRepository::save);
            log.info("创建了 {} 个用户", users.size());
        }
    }
    
    private void checkAndInitializeProducts() {
        long productCount = productRepository.count();
        log.info("当前数据库中有 {} 个商品", productCount);
        
        // 如果数据库中没有商品，检查 CSV 文件并导入
        if (productCount == 0) {
            log.info("尝试从 CSV 文件导入商品数据...");
            
            // 从 CSV 读取所有商品
            List<Product> products = productRepository.findAll();
            
            if (products.isEmpty()) {
                log.warn("CSV 文件中没有商品数据，将创建默认商品数据");
                createDefaultProducts();
            } else {
                log.info("从 CSV 文件中读取到 {} 个商品", products.size());
                
                // 将 CSV 中的商品保存到数据库
                for (Product product : products) {
                    // 确保商品有创建和更新时间
                    if (product.getCreatedAt() == null) {
                        product.setCreatedAt(LocalDateTime.now());
                    }
                    if (product.getUpdatedAt() == null) {
                        product.setUpdatedAt(LocalDateTime.now());
                    }
                    productRepository.save(product);
                }
                log.info("成功从 CSV 文件导入 {} 个商品", products.size());
            }
        } else {
            log.info("数据库中已有商品数据，跳过初始化");
        }
    }
    
    private void createDefaultProducts() {
        log.info("创建默认商品数据...");
        
        LocalDateTime now = LocalDateTime.now();
        
        List<Product> products = Arrays.asList(
                Product.builder()
                        .name("智能手机")
                        .description("高性能智能手机，配备最新处理器和摄像头")
                        .price(new BigDecimal("2999.99"))
                        .stock(50)
                        .category("电子产品")
                        .imageUrl("/api/files/product-1-smartphone.png")
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Product.builder()
                        .name("笔记本电脑")
                        .description("轻薄便携的笔记本电脑，适合商务和娱乐")
                        .price(new BigDecimal("5999.99"))
                        .stock(30)
                        .category("电子产品")
                        .imageUrl("/api/files/product-2-laptop.png")
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Product.builder()
                        .name("T恤衫")
                        .description("纯棉舒适T恤，多种颜色可选")
                        .price(new BigDecimal("99.99"))
                        .stock(200)
                        .category("服装")
                        .imageUrl("/api/files/product-3-tshirt.png")
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Product.builder()
                        .name("牛仔裤")
                        .description("经典款牛仔裤，修身设计")
                        .price(new BigDecimal("199.99"))
                        .stock(150)
                        .category("服装")
                        .imageUrl("/api/files/product-4-jeans.png")
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Product.builder()
                        .name("咖啡机")
                        .description("全自动咖啡机，一键制作美味咖啡")
                        .price(new BigDecimal("899.99"))
                        .stock(25)
                        .category("家用电器")
                        .imageUrl("/api/files/product-5-coffee-maker.png")
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Product.builder()
                        .name("蓝牙耳机")
                        .description("无线蓝牙耳机，降噪功能")
                        .price(new BigDecimal("399.99"))
                        .stock(80)
                        .category("电子产品")
                        .imageUrl("/api/files/product-6-earbuds.png")
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Product.builder()
                        .name("运动鞋")
                        .description("轻便运动鞋，适合跑步和日常穿着")
                        .price(new BigDecimal("299.99"))
                        .stock(120)
                        .category("鞋类")
                        .imageUrl("/api/files/product-7-sneakers.png")
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Product.builder()
                        .name("背包")
                        .description("多功能背包，大容量设计")
                        .price(new BigDecimal("149.99"))
                        .stock(90)
                        .category("箱包")
                        .imageUrl("/api/files/product-8-backpack.png")
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );
        
        products.forEach(productRepository::save);
        log.info("创建了 {} 个默认商品", products.size());
    }
}
