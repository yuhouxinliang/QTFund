# QTFund 量化监控看板部署指南

## 1. 系统架构
本项目采用前后端分离（逻辑上）但打包部署在一起的架构：
- **后端**: Java Spring Boot (v3.2+)
- **前端**: 原生 HTML/Vue3/ElementPlus (单文件集成)
- **数据库**: MongoDB

## 2. 代码位置
- **前端代码**: `src/main/resources/static/index.html`
  - 这是一个单文件的 SPA 应用，直接由 Spring Boot 的静态资源处理器服务。
- **后端代码**: `src/main/java/com/makemoney/qtfund/`
  - Controller: `controller/StockAnalysisResultController.java`
  - Service: `service/StockAnalysisResultService.java`
  - Entity: `entity/StockAnalysisResult.java`

## 3. 部署前准备

### 3.1 环境要求
- JDK 21 或更高版本
- Maven 3.6+
- MongoDB 5.0+ (需在部署机器上运行或可远程访问)

### 3.2 配置文件
打开 `src/main/resources/application.properties`，确保 MongoDB 连接配置正确：
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/qtfund
# 如果有用户名密码: mongodb://user:password@ip:27017/qtfund
```

## 4. 编译与打包

在项目根目录下运行以下命令生成可执行 JAR 包：

**Windows (PowerShell):**
```powershell
./mvnw clean package -DskipTests
```

**Linux/Mac:**
```bash
./mvnw clean package -DskipTests
```

构建成功后，会在 `target/` 目录下生成 `QTFund-0.0.1-SNAPSHOT.jar`。

## 5. 部署运行

### 5.1 启动服务
将生成的 `.jar` 文件复制到目标机器，运行：

```bash
java -jar QTFund-0.0.1-SNAPSHOT.jar
```

### 5.2 验证部署
打开浏览器访问：
- **监控看板**: `http://localhost:8080` 或 `http://localhost:8080/index.html`
- **API 测试**: `http://localhost:8080/api/stock-analysis/latest`

## 6. 常见问题
- **404 Not Found**: 确保 `index.html` 位于 `src/main/resources/static/` 目录下，且 JAR 包打包正确。
- **连接数据库失败**: 检查 `application.properties` 中的 MongoDB 地址，以及目标机器防火墙设置。
- **日期显示不正确**: 确保服务器和浏览器的时区设置正确（代码已做时区自适应处理）。

