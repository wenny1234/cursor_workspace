-- Import from D:\cursor_workspace\app-shop-001\data\csv\users.csv and products.csv
-- Run: docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 @/tmp/03_import_data.sql

DELETE FROM products;
DELETE FROM users;
COMMIT;

INSERT INTO users (id, username, password, email, role, created_at, updated_at) VALUES
(1, 'admin', '$2a$10$oKTFqC9Wu8e3a9xChYibjemI8FX3RxfEJLRyw0h7C5AulBkNPj.GG', 'admin@shop.com', 'ADMIN',
 TIMESTAMP '2026-03-07 22:34:01.0427955', TIMESTAMP '2026-03-07 22:34:01.0427955');

INSERT INTO users (id, username, password, email, role, created_at, updated_at) VALUES
(2, 'staff', '$2a$10$X6TzOcXQdX3sVaaey3pG6ONLRhsFarsanf0zAJ8cV9P8yw1XFuTam', 'staff@shop.com', 'STAFF',
 TIMESTAMP '2026-03-07 22:34:01.0427955', TIMESTAMP '2026-03-07 22:34:01.0427955');

INSERT INTO users (id, username, password, email, role, created_at, updated_at) VALUES
(3, 'viewer', '$2a$10$FHWxH6ITUeFHSpcvsJ9bKe7YLkzJf5xDqVfPSEeI3Wa0MbQZdHig2', 'viewer@shop.com', 'VIEWER',
 TIMESTAMP '2026-03-07 22:34:01.0427955', TIMESTAMP '2026-03-07 22:34:01.0427955');

INSERT INTO users (id, username, password, email, role, created_at, updated_at) VALUES
(4, 'testuser', '$2a$10$EJeKLJ.P6di.3TWLwoPw9uy9Kw94Lq/IwD1CyZ0RUX8jnWUndYDEe', 'test@example.com', 'VIEWER',
 TIMESTAMP '2026-03-29 17:05:56.2458942', TIMESTAMP '2026-03-29 17:05:56.2458942');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(1, UNISTR('\667a\80fd\624b\673a'), UNISTR('\9ad8\6027\80fd\667a\80fd\624b\673a\ff0c\914d\5907\6700\65b0\5904\7406\5668\548c\6444\50cf\5934'),
 2999.99, 50, UNISTR('\7535\5b50\4ea7\54c1'), '/api/files/images/test1.jpg',
 TIMESTAMP '2026-03-07 22:34:01.4382657', TIMESTAMP '2026-03-07 22:34:01.4382657');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(2, UNISTR('\7b14\8bb0\672c\7535\8111'), UNISTR('\8f7b\8584\4fbf\643a\7684\7b14\8bb0\672c\7535\8111\uff0c\9002\5408\5546\52a1\548c\5a31\4e50'),
 5999.99, 30, UNISTR('\7535\5b50\4ea7\54c1'), '/api/files/images/test1.jpg',
 TIMESTAMP '2026-03-07 22:34:01.4382657', TIMESTAMP '2026-03-07 22:34:01.4382657');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(3, UNISTR('T\6064\886b'), UNISTR('\7eaf\68c9\8212\9002T\6064\uff0c\591a\79cd\989c\8272\53ef\9009'),
 99.99, 200, UNISTR('\670d\88c5'), '/api/files/images/test1.jpg',
 TIMESTAMP '2026-03-07 22:34:01.4382657', TIMESTAMP '2026-03-07 22:34:01.4382657');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(4, UNISTR('\725b\4ed4\88e4'), UNISTR('\7ecf\5178\6b3e\725b\4ed4\88e4\uff0c\4fee\8eab\8bbe\8ba1'),
 199.99, 150, UNISTR('\670d\88c5'), '/api/files/images/test1.jpg',
 TIMESTAMP '2026-03-07 22:34:01.4382657', TIMESTAMP '2026-03-07 22:34:01.4382657');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(5, UNISTR('\5496\5561\673a'), UNISTR('\5168\81ea\52a8\5496\5561\673a\uff0c\4e00\952e\5236\4f5c\7f8e\5473\5496\5561'),
 899.99, 25, UNISTR('\5bb6\7528\7535\5668'), '/api/files/images/test1.jpg',
 TIMESTAMP '2026-03-07 22:34:01.4382657', TIMESTAMP '2026-03-07 22:34:01.4382657');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(6, UNISTR('\84dd\7259\8033\673a'), UNISTR('\65e0\7ebf\84dd\7259\8033\673a\uff0c\964d\566a\529f\80fd'),
 399.99, 80, UNISTR('\7535\5b50\4ea7\54c1'), '/api/files/images/test1.jpg',
 TIMESTAMP '2026-03-07 22:34:01.4382657', TIMESTAMP '2026-03-07 22:34:01.4382657');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(7, UNISTR('\8fd0\52a8\978b'), UNISTR('\8f7b\4fbf\8fd0\52a8\978b\uff0c\9002\5408\8dd1\6b65\548c\65e5\5e38\7a7f\7740'),
 299.99, 120, UNISTR('\978b\7c7b'), '/api/files/images/test1.jpg',
 TIMESTAMP '2026-03-07 22:34:01.4382657', TIMESTAMP '2026-03-07 22:34:01.4382657');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(8, UNISTR('\80cc\5305'), UNISTR('\591a\529f\80fd\80cc\5305\uff0c\5927\5bb9\91cf\8bbe\8ba1'),
 149.99, 90, UNISTR('\7bb1\5305'), '/api/files/images/test1.jpg',
 TIMESTAMP '2026-03-07 22:34:01.4382657', TIMESTAMP '2026-03-07 22:34:01.4382657');

INSERT INTO products (id, name, description, price, stock, category, image_url, created_at, updated_at) VALUES
(9, 'New Test Product', 'Created with auth', 50, 100, 'Test', NULL,
 TIMESTAMP '2026-03-22 17:46:53.8120641', TIMESTAMP '2026-03-22 17:46:53.8120641');

COMMIT;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE users_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
CREATE SEQUENCE users_seq START WITH 5 INCREMENT BY 1 NOCACHE NOCYCLE;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE products_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
CREATE SEQUENCE products_seq START WITH 10 INCREMENT BY 1 NOCACHE NOCYCLE;

SELECT 'users' AS tbl, COUNT(*) AS cnt FROM users
UNION ALL
SELECT 'products', COUNT(*) FROM products;

EXIT;
