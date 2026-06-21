-- Run as SHOP user on XEPDB1
-- Example:
--   docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 @/tmp/02_schema.sql

CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE users (
  id          NUMBER(19)      PRIMARY KEY,
  username    VARCHAR2(50)    NOT NULL,
  password    VARCHAR2(100)   NOT NULL,
  email       VARCHAR2(100)   NOT NULL,
  role        VARCHAR2(20)    NOT NULL,
  avatar_url  VARCHAR2(500),
  active      NUMBER(1)       DEFAULT 1 NOT NULL,
  created_at  TIMESTAMP,
  updated_at  TIMESTAMP,
  CONSTRAINT uk_users_username UNIQUE (username),
  CONSTRAINT uk_users_email UNIQUE (email),
  CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'STAFF', 'VIEWER'))
);

CREATE SEQUENCE products_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE products (
  id          NUMBER(19)      PRIMARY KEY,
  name        VARCHAR2(200)   NOT NULL,
  description VARCHAR2(1000),
  price       NUMBER(12, 2),
  stock       NUMBER(10),
  category    VARCHAR2(100),
  image_url   VARCHAR2(500),
  active      NUMBER(1)       DEFAULT 1 NOT NULL,
  created_at  TIMESTAMP,
  updated_at  TIMESTAMP
);

CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_name ON products (name);

EXIT;
