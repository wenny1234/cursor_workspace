-- Update product image URLs to match generated images in data/images/
UPDATE products SET image_url = '/api/files/product-1-smartphone.png' WHERE id = 1;
UPDATE products SET image_url = '/api/files/product-2-laptop.png' WHERE id = 2;
UPDATE products SET image_url = '/api/files/product-3-tshirt.png' WHERE id = 3;
UPDATE products SET image_url = '/api/files/product-4-jeans.png' WHERE id = 4;
UPDATE products SET image_url = '/api/files/product-5-coffee-maker.png' WHERE id = 5;
UPDATE products SET image_url = '/api/files/product-6-earbuds.png' WHERE id = 6;
UPDATE products SET image_url = '/api/files/product-7-sneakers.png' WHERE id = 7;
UPDATE products SET image_url = '/api/files/product-8-backpack.png' WHERE id = 8;
UPDATE products SET image_url = '/api/files/product-9-test.png' WHERE id = 9;
COMMIT;
EXIT;
