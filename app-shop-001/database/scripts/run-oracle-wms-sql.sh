#!/bin/sh
set -e

SQL_FILE="/mnt/d/cursor_workspace/app-shop-001/database/oracle/07_wms_shipping.sql"

echo "Waiting for Oracle..."
for i in $(seq 1 30); do
  if echo "SELECT 1 FROM DUAL;" | docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 | tr -d ' \t\r\n' | grep -q '^1$'; then
    echo "Oracle is ready."
    break
  fi
  echo "  attempt $i/30..."
  sleep 10
  if [ "$i" -eq 30 ]; then
    echo "Oracle did not become ready in time." >&2
    exit 1
  fi
done

echo "Copying SQL file into container..."
docker cp "$SQL_FILE" oracle-db:/tmp/07_wms_shipping.sql

echo "Running 07_wms_shipping.sql..."
docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 @/tmp/07_wms_shipping.sql

echo "Verifying shipping_address column..."
echo "SELECT column_name FROM user_tab_columns WHERE table_name='ORDERS' AND column_name='SHIPPING_ADDRESS';" \
  | docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1

echo "Done."
