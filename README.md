# 校园二手交易平台 🏫

SpringBoot + MyBatis-Plus + Vue 全栈项目，集成 DeepSeek AI 实现智能定价、语义搜索、商品合规审查。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | SpringBoot 3.4.1 |
| ORM | MyBatis-Plus 3.5.5 |
| 安全 | Spring Security + JWT (jjwt 0.12.3) |
| 数据库 | MySQL 8.0 |
| 前端 | Vue 3 + Element Plus |
| AI | DeepSeek API (chat/completions) |
| API 文档 | SpringDoc OpenAPI + Swagger UI |

## 功能模块

### 核心业务
- 用户注册/登录（JWT 认证 + BCrypt 密码加密）
- 商品发布、编辑、删除、浏览（分页 + 分类筛选 + 关键词搜索）
- 商品分类管理
- 订单创建与状态追踪（PENDING → PAID → SHIPPED → COMPLETED → CANCELED）
- 买卖双方站内信（实时轮询 + 未读计数）
- 交易评价体系（订单完成后互评，1-5 星）
- 图片上传（MIME 白名单 + 5MB 限制）

### 权限控制
- JWT 认证拦截器 — 未登录拦截 401
- Admin 角色拦截器 — /api/admin/** 强制管理员角色
- 敏感信息保护 — 用户列表去密码 + 手机号脱敏（138****1234）

### AI 功能
| 接口 | 说明 |
|------|------|
| `POST /api/ai/price-suggest` | 智能定价 — 根据商品信息、成色、分类给出建议价格和理由 |
| `GET /api/ai/search/semantic?q=` | 语义搜索 — 口语化查询扩展为多关键词，提升长尾搜索命中率 |
| `POST /api/ai/review-desc` | 商品合规检查 — 审核商品描述是否含违禁/虚假内容 |

## 快速启动

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Node.js 18+

### 1. 初始化数据库
```sql
CREATE DATABASE second_hand_trade CHARACTER SET utf8mb4;
```
执行 `database/schema.sql`（含建表 + 测试数据）

### 2. 配置 DeepSeek API Key
```bash
# Windows PowerShell
$env:DEEPSEEK_API_KEY="sk-your-key"

# 或在 application.yml 中配置
```

### 3. 启动后端
```bash
cd backend
mvn spring-boot:run
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 5. 访问
- 前端页面：http://localhost:5173
- API 文档：http://localhost:8080/swagger-ui/index.html

### 测试账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| stu001 | 123456 | 普通用户 |

## 项目结构

```
backend/src/main/java/com/campus/trade/
├── common/              # 通用（Result、BusinessException、GlobalExceptionHandler）
├── config/              # 配置（Security、JWT拦截器、Admin拦截器、CORS、RestTemplate、MyBatis-Plus）
├── controller/          # 控制器（User、Product、Order、Review、Message、Admin、Ai、Upload）
├── dto/                 # 数据传输对象（Login、Register、CreateOrder、PriceRequest/Response 等）
├── entity/              # 实体类（User、Product、Order、Category、Review、Message）
├── mapper/              # MyBatis-Plus Mapper 接口
├── service/             # 业务接口
│   └── impl/            # 业务实现
└── util/                # 工具类（JwtUtil）

frontend/src/
├── api/                 # Axios 请求封装
├── router/              # Vue Router 路由配置
├── utils/               # 工具函数
└── views/               # 页面组件
```
