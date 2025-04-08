# MQTT-Broker
以下是一个完整的 `README.md` 文档，详细说明了如何运行代码并与 EMQX 交互。请根据你的项目实际情况调整内容。

---

## MQTT 数据发送与接收示例

本项目演示了如何使用 Eclipse Paho MQTT 客户端库与 EMQX Broker 进行交互，包括数据的发送和接收，并通过 RESTful API 查询统计数据。

### 环境要求

1. **Java 开发环境**：
   - JDK 8 或更高版本。
   - Maven（用于构建和管理依赖）。

2. **EMQX Broker**：
   - 安装并运行 EMQX（默认地址：`tcp://localhost:1883`）。
   - 默认用户名：`admin`，默认密码：`public`。

3. **其他工具**（可选）：
   - [MQTTX](https://mqttx.app/) 或其他 MQTT 测试工具，用于调试和验证。

---

### 快速开始

#### 1. 安装和启动 EMQX

##### 在 Linux 上安装 EMQX：
```bash
# 添加 EMQX 的官方仓库
sudo wget https://repos.emqx.io/emqx-ce/redhat/EMQ-CE.repo -O /etc/yum.repos.d/emqx-ce.repo

# 安装 EMQX
sudo yum install emqx

# 启动 EMQX
emqx start

# 验证是否启动成功
emqx_ctl status
```

##### 默认连接信息：
- **Broker URL**: `tcp://localhost:1883`
- **WebSocket URL**: `ws://localhost:8083/mqtt`
- **管理界面**: `http://localhost:18083`（默认用户名：`admin`，密码：`public`）

---

#### 2. 克隆代码并构建项目

##### 克隆代码：
```bash
git clone https://github.com/your-repo/mqtt-example.git
cd mqtt-example
```

##### 构建项目：
使用 Maven 构建项目：
```bash
mvn clean install
```

---

#### 3. 配置 EMQX 认证信息

在代码中，确保 `MqttConnectOptions` 中的用户名和密码正确：

```java
options.setUserName("admin");
options.setPassword("public".toCharArray());
```

如果 EMQX 使用了自定义认证，请修改为对应的用户名和密码。

---

#### 4. 运行代码

##### (1) 启动数据发送程序
运行以下命令启动数据发送程序：
```bash
java -cp target/mqtt-example-1.0-SNAPSHOT.jar MqttDataSender
```

该程序会模拟生成随机消息，并发布到 MQTT topic（默认为 `test/topic`）。

##### (2) 启动数据接收程序
运行以下命令启动数据接收程序：
```bash
java -cp target/mqtt-example-1.0-SNAPSHOT.jar MqttDataReceiver
```

该程序会订阅 `test/topic`，并将接收到的消息存储在内存中，同时启动一个 RESTful API 服务（默认端口：`4567`）。

---

#### 5. 查询统计数据

数据接收程序启动后，会提供一个 RESTful API 接口，用于查询指定时间范围内的统计数据。

#### 请求格式：
```bash
GET /stats?start=<start_time>&end=<end_time>
```

- `start`: 起始时间（单位：分钟，相对于当前时间）。
- `end`: 结束时间（单位：分钟，相对于当前时间）。

##### 示例请求：
```bash
curl "http://localhost:4567/stats?start=-10&end=0"
```

##### 响应示例：
```json
{
  "A": 5,
  "B": 3,
  "C": 2,
  "D": 1
}
```

---

### 项目结构

```
mqtt-example/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── MqttDataSender.java       # 模拟数据发送程序
│   │   │   ├── MqttDataReceiver.java     # 数据接收与统计程序
│   │   └── resources/
├── pom.xml                               # Maven 配置文件
└── README.md                             # 说明文档
```

---

### 常见问题排查

#### 1. **无法连接到 EMQX**
   - 确保 EMQX 已启动并正常运行。
   - 检查客户端代码中的 `BROKER_URL` 是否正确。
   - 确保防火墙允许通过 `1883` 端口的流量。

#### 2. **认证失败**
   - 如果 EMQX 启用了认证插件，请确保客户端代码中的用户名和密码正确。
   - 检查 EMQX 的日志文件（通常位于 `/var/log/emqx/`），查看是否有认证失败的错误信息。

#### 3. **消息未到达订阅者**
   - 确保发布者和订阅者的 topic 名称一致。
   - 检查 QoS 设置是否匹配。

---


通过以上步骤，你可以轻松运行本项目并与 EMQX 进行交互。
