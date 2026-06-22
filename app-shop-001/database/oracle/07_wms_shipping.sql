-- WMS shipping address column
-- Run: docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 @/tmp/07_wms_shipping.sql

ALTER TABLE orders ADD (
  shipping_address VARCHAR2(500)
);

EXIT;
