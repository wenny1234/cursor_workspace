-- Admin extensions: soft delete + user avatar
-- Run: docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 @/tmp/06_admin_extensions.sql

ALTER TABLE products ADD (active NUMBER(1) DEFAULT 1 NOT NULL);
ALTER TABLE users ADD (active NUMBER(1) DEFAULT 1 NOT NULL);
ALTER TABLE users ADD (avatar_url VARCHAR2(500));

UPDATE products SET active = 1 WHERE active IS NULL;
UPDATE users SET active = 1 WHERE active IS NULL;

CREATE INDEX idx_products_active ON products (active);
CREATE INDEX idx_users_active ON users (active);

EXIT;
