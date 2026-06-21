-- Orders schema for shop app
-- Run: docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 @/tmp/04_orders_schema.sql

CREATE SEQUENCE orders_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE order_items_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE orders (
  id            NUMBER(19)      PRIMARY KEY,
  order_number  VARCHAR2(30)    NOT NULL,
  user_id       NUMBER(19)      NOT NULL,
  total_amount  NUMBER(12, 2)   NOT NULL,
  status        VARCHAR2(20)    NOT NULL,
  created_at    TIMESTAMP,
  updated_at    TIMESTAMP,
  CONSTRAINT uk_orders_order_number UNIQUE (order_number),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT ck_orders_status CHECK (status IN ('PENDING', 'PAID', 'SHIPPING', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE order_items (
  id            NUMBER(19)      PRIMARY KEY,
  order_id      NUMBER(19)      NOT NULL,
  product_id    NUMBER(19)      NOT NULL,
  product_name  VARCHAR2(200)   NOT NULL,
  unit_price    NUMBER(12, 2)   NOT NULL,
  quantity      NUMBER(10)      NOT NULL,
  line_total    NUMBER(12, 2)   NOT NULL,
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);

EXIT;
