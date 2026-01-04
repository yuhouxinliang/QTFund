# QTFund API 文档

本文档描述了如何调用 QTFund 服务接口进行数据录入和查询。

## 基础信息

- **服务地址**: `http://<你的服务器IP>:8080` (请替换为实际服务器 IP)
- **数据格式**: JSON
- **认证方式**: 目前接口受 Spring Security 保护，调用 API 时需要通过 Basic Auth 或 Form Login 获取 Session。
  - **开发环境/脚本调用建议**: 可以在请求头中添加 Basic Auth 认证信息（如果配置了 HTTP Basic），或者先调用登录接口获取 Cookie。
  - **注意**: 如果使用脚本批量插入，建议先在 Postman 或浏览器中登录获取 `JSESSIONID`，然后在请求头中带上 `Cookie: JSESSIONID=...`。
  - **临时方案**: 如果是纯内网环境或为了方便录入，可以临时配置允许 API 路径匿名访问（但当前生产环境默认为需要认证）。

## 1. 录入/更新股票分析数据

该接口用于插入新的分析结果。如果数据库中已存在相同 `exchangeId` + `instrumentId` + `targetDate` 的记录，则会更新现有记录。

- **URL**: `/api/stock-analysis`
- **Method**: `POST`
- **Content-Type**: `application/json`

### 请求体字段说明

| 字段名 | 类型 | 必填 | 说明 | 示例 |
| :--- | :--- | :--- | :--- | :--- |
| `exchangeId` | String | 是 | 交易所代码 | "SH", "SZ" |
| `instrumentId` | String | 是 | 证券代码 | "510300" |
| `instrumentName` | String | 是 | 证券名称 | "沪深300ETF" |
| `stockType` | String | 是 | 类型：`STOCK` (股票/ETF) 或 `INDEX` (指数) | "STOCK" |
| `targetDate` | String | 是 | 数据日期 (ISO 8601 格式) | "2024-01-15T00:00:00.000+00:00" |
| `close` | Number | 否 | 收盘价 | 3.456 |
| `amount` | Number | 否 | 成交额 (元) | 1000000.0 |
| `score` | Number | 否 | 动量评分 (0-100) | 85.5 |
| `ranking` | Integer | 否 | 排名 | 10 |
| `scoreChange` | Number | 否 | 分数变化 | 2.5 |
| `rankingChange` | Integer | 否 | 排名变化 (负数表示排名下降) | -2 |

### 请求示例 (JSON)

```json
{
  "exchangeId": "SH",
  "instrumentId": "510300",
  "instrumentName": "沪深300ETF",
  "stockType": "STOCK",
  "targetDate": "2026-01-04T00:00:00.000+08:00",
  "close": 3.456,
  "amount": 500000000.0,
  "score": 88.5,
  "ranking": 5,
  "scoreChange": 1.2,
  "rankingChange": 3
}
```

### 响应示例

成功 (200 OK 或 201 Created):

```json
{
  "id": "65ba...",
  "exchangeId": "SH",
  "instrumentId": "510300",
  "instrumentName": "沪深300ETF",
  "stockType": "STOCK",
  "targetDate": "2026-01-04T00:00:00.000+08:00",
  ...
}
```

## 2. 批量录入建议

建议编写 Python 或 Java 脚本循环调用上述接口。

**Python 示例脚本**:

```python
import requests
import json

url = "http://<你的服务器IP>:8080/api/stock-analysis"
# 请确保使用正确的用户名密码
auth = ('qtfund', 'T*wNC!UX8TDGu3fx') 

data = {
    "exchangeId": "SZ",
    "instrumentId": "159915",
    "instrumentName": "创业板ETF",
    "stockType": "STOCK",
    "targetDate": "2026-01-04T00:00:00.000+08:00",
    "close": 1.88,
    "amount": 200000000.0,
    "score": 75.0,
    "ranking": 12
}

try:
    # 如果启用了CSRF或者Form登录，可能需要先创建一个Session登录
    session = requests.Session()
    # 登录 (假设 Spring Security 配置了 Form Login，或者使用 Basic Auth)
    # response = session.post('http://<IP>:8080/login', data={'username': '...', 'password': '...'})
    
    # 这里直接尝试 Basic Auth (取决于 SecurityConfig 配置，当前配置可能需要 Cookie)
    # 如果当前 SecurityConfig 配置了 formLogin() 且没有 httpBasic()，
    # 最好先用浏览器登录拿到 JSESSIONID，然后放在 headers 里:
    # headers = {'Cookie': 'JSESSIONID=<你的SESSION_ID>'}
    
    response = requests.post(url, json=data, auth=auth) # 尝试 Basic Auth
    
    # 如果 Basic Auth 不行，改用 Session 登录方式
    if response.status_code == 401 or response.status_code == 403:
        login_url = "http://<你的服务器IP>:8080/login"
        login_data = {'username': 'qtfund', 'password': 'T*wNC!UX8TDGu3fx'}
        s = requests.Session()
        s.post(login_url, data=login_data)
        response = s.post(url, json=data)

    print(f"Status: {response.status_code}, Response: {response.text}")

except Exception as e:
    print(f"Error: {e}")
```

## 3. 查询最新数据 (调试用)

- **URL**: `/api/stock-analysis/latest`
- **Method**: `GET`
- **Params**: 
  - `stockType`: 可选，`STOCK` 或 `INDEX`

## 注意事项

1. **日期格式**: 请务必保证 `targetDate` 包含时区信息或为 UTC 时间，以避免日期偏差。
2. **唯一性**: 系统根据 `exchangeId` + `instrumentId` + `targetDate` 唯一确定一条记录。重复插入即为更新。



