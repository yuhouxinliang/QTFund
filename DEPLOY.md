# QTFund 部署指南

本文档介绍了在修改代码后，如何重新部署 QTFund 应用程序。

## 前置条件
- 已通过 SSH 连接到服务器。
- 当前用户具有 root 权限或 sudo 权限。

## 快速重新部署流程

如果您已经配置好了环境，只需要执行以下命令即可完成更新：

```bash
# 1. 进入项目目录
cd /root/code/QTFund

# 2. 拉取最新代码 (如果是本地修改上传，请跳过此步；如果是 git 管理，请执行)
git pull origin deploy_test

# 3. 停止当前服务
systemctl stop qtfund

# 4. 重新打包应用 (使用 Java 21)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
mvn clean package -DskipTests

# 5. 启动服务
systemctl start qtfund

# 6. 查看日志确认启动成功
journalctl -u qtfund -f
```

---

## 常用命令速查

### 服务管理
| 操作 | 命令 |
| :--- | :--- |
| **启动服务** | `systemctl start qtfund` |
| **停止服务** | `systemctl stop qtfund` |
| **重启服务** | `systemctl restart qtfund` |
| **查看状态** | `systemctl status qtfund` |
| **查看日志** | `journalctl -u qtfund -f` (按 Ctrl+C 退出) |

### 构建命令
如果遇到 Maven 版本或 Java 版本问题，请确保设置了正确的 `JAVA_HOME`：

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
mvn clean package -DskipTests
```

## 故障排查

### 1. 端口被占用
如果启动失败，提示端口被占用，可以先停止服务，或者强制杀掉进程：
```bash
# 查找占用 8080 端口的进程
netstat -nlp | grep 8080

# 杀掉进程 (将 PID 替换为实际进程号)
kill -9 <PID>
```

### 2. 数据库连接失败
检查 MongoDB 服务状态：
```bash
systemctl status mongod
```
如果未运行，请启动：
```bash
systemctl start mongod
```

### 3. Maven 编译报错 "release version 21 not supported"
这是因为 Maven 默认使用了 Java 17。**必须**执行 `export JAVA_HOME=/usr/lib/jvm/java-21-openjdk` 后再运行 mvn 命令。
