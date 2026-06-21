# admin-shop-manage

店铺管理后台（master data）。与 `mobile-app-backend` 共用 Oracle 数据库。

## 技术栈

- Spring Boot 2.7 + Thymeleaf + jQuery
- MyBatis + Oracle
- 单页式 UI（侧边栏切换视图）

## 功能

- **商品业务**：登录/更新（含图片上传）、一览查询、无效化（逻辑删除）
- **用户业务**：登录/更新（含头像上传、密码 8-12 位三种混合）、一览查询、无效化
- **最近注文履歴**：最近 50 条订单
- **统计总价格**：销售总额、订单数、有效商品/用户数

## 启动前准备

1. 确保 Oracle 已运行且 schema 已创建（`database/oracle/02_schema.sql` 等）
2. 执行 admin 扩展脚本：

```bash
docker exec -i oracle-db sqlplus -s shop/ShopPassword123@XEPDB1 @/tmp/06_admin_extensions.sql
```

3. 默认使用 `admin` / `admin123`（需与 users 表中已有 admin 用户密码一致；初始 CSV 导入的 admin 密码为 bcrypt 哈希，请用已知密码登录）

## 启动

```bash
cd admin-shop-manage
mvn spring-boot:run
```

访问 http://localhost:8081 ，使用 ADMIN 或 STAFF 角色用户登录。

## 端口

- admin-shop-manage: **8081**
- mobile-app-backend: **8080**

## 数据字段（与 mobile-app 一致）

| 实体 | 字段 |
|------|------|
| Product | id, name, description, price, stock, category, imageUrl, active |
| User | id, username, password, email, role, avatarUrl, active |
| Order | id, orderNumber, userId, totalAmount, status, createdAt |

图片保存在 `../data/images`，通过 `/files/{filename}` 访问。
