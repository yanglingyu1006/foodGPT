# 食物语（FoodGPT）项目设计文档

## 1. 架构设计

### 1.1 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 25 |
| 框架 | JavaFX | 21 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | SQLite | 3.x |
| HTTP客户端 | OpenAI SDK | 4.12.0 |
| JSON处理 | Jackson | 2.16.0 |
| 图标库 | Ikonli | 12.3.1 |

### 1.2 架构风格

采用 **MVC架构**，结合 **分层设计**：

- **View层**：FXML + Controller，负责界面展示和用户交互
- **Controller层**：处理界面事件，调用Service层（接收/返回DTO）
- **DTO层**：数据传输对象，定义Controller与Service之间的数据结构
- **Service层**：业务逻辑处理
- **Mapper层**：数据访问接口，基于MyBatis-Plus
- **Entity层**：实体类，对应数据库表结构
- **Config层**：配置管理（数据库连接、API密钥等）
- **Util层**：工具类（JSON解析、计算工具等）

### 1.3 架构设计图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         食物语（FoodGPT）桌面应用                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                         View 层 (JavaFX)                        │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │   │
│  │  │ Dashboard│ │ MealRec  │ │ Recipe   │ │ Nutrition│           │   │
│  │  │   FXML   │ │   FXML   │ │   FXML   │ │   FXML   │    ...    │   │
│  │  │ +Controller│ +Controller│ +Controller│ +Controller│           │   │
│  │  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘           │   │
│  │       │            │            │            │                   │   │
│  └───────┼────────────┼────────────┼────────────┼───────────────────┘   │
│          │            │            │            │                       │
│  ┌───────▼────────────▼────────────▼────────────▼───────────────────┐   │
│  │                       Controller 层                              │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐              │   │
│  │  │ BodyDataCtrl │ │ MealRecordCtrl│ │ RecipeCtrl   │    ...       │   │
│  │  └──────┬───────┘ └──────┬───────┘ └──────┬───────┘              │   │
│  │         │                │                │                      │   │
│  └─────────┼────────────────┼────────────────┼───────────────────────┘   │
│            │                │                │                          │
│  ┌─────────▼────────────────▼────────────────▼───────────────────────┐   │
│  │                        DTO 层                                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │   │
│  │  │RecipeSearch │ │ MealInput   │ │NutritionSum │ │ AiChatDTO   │ │   │
│  │  │    DTO      │ │    DTO      │ │   maryDTO   │ │             │ │   │
│  │  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ │   │
│  │         │               │               │               │        │   │
│  └─────────┼───────────────┼───────────────┼───────────────┼────────┘   │
│            │               │               │               │            │
│  ┌─────────▼───────────────▼───────────────▼───────────────▼────────┐   │
│  │                        Service 层                                 │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │   │
│  │  │BodyDataServ │ │RecipeService│ │NutritionServ│ │ AiAdvisor   │ │   │
│  │  │WeightTrack  │ │MealRecordServ│ │ CycleService│ │   Service   │ │   │
│  │  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ │   │
│  │         │               │               │               │        │   │
│  └─────────┼───────────────┼───────────────┼───────────────┼────────┘   │
│            │               │               │               │            │
│  ┌─────────▼───────────────▼───────────────▼───────────────▼────────┐   │
│  │                        Mapper 层 (MyBatis-Plus)                  │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │   │
│  │  │BodyMapper│ │RecipeMap │ │MealMapper│ │CycleMapper│    ...     │   │
│  │  └─────┬────┘ └─────┬────┘ └─────┬────┘ └─────┬────┘            │   │
│  └─────────┼───────────┼───────────┼─────────────┼──────────────────┘   │
│            │           │           │             │                      │
│  ┌─────────▼───────────▼───────────▼─────────────▼──────────────────┐   │
│  │                    Config / Util / Entity 层                      │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │   │
│  │  │AppConfig │ │ApiConfig │ │JsonUtil  │ │ Calculator│    ...     │   │
│  │  │Entity    │ │          │ │          │ │           │            │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘            │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                         │
├─────────────────────────────────────────────────────────────────────────┤
│                              外部依赖                                    │
│  ┌──────────────────┐           ┌──────────────────┐                   │
│  │   SQLite 数据库   │           │   DeepSeek API   │                   │
│  │  data/foodgpt.db │           │   (AI咨询)        │                   │
│  └──────────────────┘           └──────────────────┘                   │
│                                    ┌──────────────────┐                 │
│                                    │   菜谱搜索API    │                 │
│                                    └──────────────────┘                 │
└─────────────────────────────────────────────────────────────────────────┘
```

**架构层次说明**：

| 层级 | 职责 | 技术实现 | 关键组件 |
|------|------|----------|----------|
| **View层** | 界面展示与用户交互 | JavaFX FXML + Controller | Dashboard、MealRecord、Recipe等FXML视图 |
| **Controller层** | 事件处理与视图逻辑，接收/返回DTO | JavaFX Controller类 | BodyDataCtrl、MealRecordCtrl、AiAdvisorCtrl等 |
| **DTO层** | 数据传输对象，定义Controller与Service之间的数据结构 | POJO类 | RecipeSearchDTO、MealInputDTO、NutritionSummaryDTO、AiChatDTO等 |
| **Service层** | 业务逻辑处理 | Spring Service | BodyDataService、NutritionService、AiAdvisorService等 |
| **Mapper层** | 数据库访问 | MyBatis-Plus BaseMapper | BodyDataMapper、RecipeMapper、CycleRecordMapper等 |
| **Entity层** | 实体类，对应数据库表结构 | POJO类 | BodyData、Recipe、MealRecord、CycleRecord等 |
| **Config层** | 配置管理 | JSON配置文件 + Java类 | AppConfig、ApiConfig、DatabaseConfig |
| **Util层** | 工具类支持 | 静态方法类 | BmiBmrCalculator、JsonUtil、OkHttpUtil |

### 1.4 目录结构

```
foodGPT/                              # 项目根目录
├── src/
│   └── main/
│       ├── java/
│       │   └── com/foodgpt/
│       │       ├── FoodGPTApplication.java    # 启动类
│       │       ├── controller/                # 控制器层
│       │       │   ├── DashboardController.java
│       │       │   ├── BodyDataController.java
│       │       │   ├── WeightTrackController.java
│       │       │   ├── RecipeManageController.java
│       │       │   ├── RecipeSearchController.java
│       │       │   ├── MealRecordController.java
│       │       │   ├── NutritionAnalysisController.java
│       │       │   ├── FemaleZoneController.java
│       │       │   └── AiAdvisorController.java
│       │       ├── service/                   # 服务层
│       │       │   ├── BodyDataService.java
│       │       │   ├── WeightTrackService.java
│       │       │   ├── RecipeService.java
│       │       │   ├── MealRecordService.java
│       │       │   ├── NutritionService.java
│       │       │   ├── CycleService.java
│       │       │   └── AiAdvisorService.java
│       │       ├── mapper/                    # 数据访问层
│       │       │   ├── BodyDataMapper.java
│       │       │   ├── WeightRecordMapper.java
│       │       │   ├── RecipeMapper.java
│       │       │   ├── MealRecordMapper.java
│       │       │   ├── NutritionRecordMapper.java
│       │       │   └── CycleRecordMapper.java
│       │       ├── entity/                    # 实体类
│       │       │   ├── BodyData.java
│       │       │   ├── WeightRecord.java
│       │       │   ├── Recipe.java
│       │       │   ├── MealRecord.java
│       │       │   ├── NutritionRecord.java
│       │       │   ├── CycleRecord.java
│       │       │   ├── UserPreference.java
│       │       │   └── HealthGoal.java
│       │       ├── config/                    # 配置层
│       │       │   ├── DatabaseConfig.java
│       │       │   ├── ApiConfig.java
│       │       │   └── AppConfig.java
│       │       ├── util/                      # 工具类
│       │       │   ├── BmiBmrCalculator.java
│       │       │   ├── NutritionCalculator.java
│       │       │   ├── JsonUtil.java
│       │       │   └── OkHttpUtil.java
│       │       ├── dto/                       # 数据传输对象
│       │       │   ├── RecipeSearchDTO.java
│       │       │   ├── MealInputDTO.java
│       │       │   ├── NutritionSummaryDTO.java
│       │       │   └── AiChatDTO.java
│       │       └── enums/                     # 枚举类
│       │           ├── ActivityLevel.java
│       │           ├── MealType.java
│       │           ├── RecipeCategory.java
│       │           ├── HealthGoalType.java
│       │           └── CyclePhase.java
│       └── resources/
│           ├── fxml/                          # FXML布局文件
│           │   ├── dashboard.fxml
│           │   ├── bodyData.fxml
│           │   ├── weightTrack.fxml
│           │   ├── recipeManage.fxml
│           │   ├── recipeSearch.fxml
│           │   ├── mealRecord.fxml
│           │   ├── nutritionAnalysis.fxml
│           │   ├── femaleZone.fxml
│           │   └── aiAdvisor.fxml
│           ├── css/                           # CSS样式文件
│           │   ├── main.css
│           │   ├── dashboard.css
│           │   ├── card.css
│           │   └── button.css
│           ├── icons/                         # 图标配置
│           │   └── ikonli-fonts.properties
│           ├── mybatis-plus/                  # MyBatis配置
│           │   └── MybatisPlusConfig.java
│           └── application.yml                # 应用配置
├── config/                                    # 运行时配置目录（程序根目录）
│   └── app-config.json                        # 应用配置文件
├── data/                                      # 数据目录（程序根目录）
│   └── foodgpt.db                             # SQLite数据库文件
└── pom.xml                                    # Maven依赖配置
```

### 1.5 数据流向

```
用户操作 → Controller → DTO → Service → Mapper → SQLite数据库
                                    │
                                    └──→ API调用（搜索/AI）
                                              │
                                              ↓
                                    返回数据 → Service → DTO → Controller → View展示
```

**数据流转路径**：

```
用户操作
    │
    ▼
[View层] FXML界面接收用户输入
    │
    ▼
[Controller层] 处理事件，调用Service（接收/返回DTO）
    │
    ▼
[DTO层] 数据传输对象（Controller与Service之间的数据结构）
    │
    ▼
[Service层] 执行业务逻辑
    │
    ├─→ [Mapper层] 查询/更新数据库
    │           │
    │           ▼
    │      SQLite数据库
    │
    └─→ [外部API] 调用DeepSeek/菜谱搜索（Service层直接调用）
                │
                ▼
           API响应数据
                │
                ▼
返回数据 → Service → DTO → Controller → View更新展示
```

---

## 2. 数据库设计

### 2.1 数据库概述

- **数据库类型**：SQLite（轻量级嵌入式数据库）
- **存储位置**：`data/foodgpt.db`（程序根目录下）
- **连接方式**：JDBC + MyBatis-Plus

### 2.2 表结构设计

#### 2.2.1 body_data（身体数据表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键ID |
| user_id | INTEGER | NOT NULL DEFAULT 1 | 用户ID（单用户默认1） |
| height | REAL | NOT NULL | 身高（cm），范围100-250 |
| weight | REAL | NOT NULL | 体重（kg），范围30-200 |
| age | INTEGER | NOT NULL | 年龄（岁），范围10-100 |
| activity_level | VARCHAR(20) | NOT NULL | 活动量：SEDENTARY/MILD/MODERATE/ACTIVE |
| bmi | REAL | NULL | 计算的BMI值 |
| bmr | REAL | NULL | 计算的BMR值（kcal） |
| recommended_calories_min | INTEGER | NULL | 推荐热量最小值（kcal） |
| recommended_calories_max | INTEGER | NULL | 推荐热量最大值（kcal） |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 更新时间 |

#### 2.2.2 weight_record（体重记录表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键ID |
| user_id | INTEGER | NOT NULL DEFAULT 1 | 用户ID |
| weight | REAL | NOT NULL | 体重（kg） |
| record_date | DATE | NOT NULL UNIQUE | 记录日期（YYYY-MM-DD） |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 2.2.3 recipe（菜谱表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键ID |
| name | VARCHAR(100) | NOT NULL | 菜谱名称 |
| category | VARCHAR(20) | NOT NULL | 分类：BREAKFAST/LUNCH/DINNER/SNACK/OTHER |
| ingredients | TEXT | NOT NULL | 食材列表（JSON格式） |
| steps | TEXT | NOT NULL | 烹饪步骤（JSON格式） |
| protein | REAL | NULL | 蛋白质含量（g/份） |
| carbohydrate | REAL | NULL | 碳水化合物含量（g/份） |
| fat | REAL | NULL | 脂肪含量（g/份） |
| calories | INTEGER | NULL | 热量（kcal/份） |
| image_url | VARCHAR(500) | NULL | 图片URL |
| description | TEXT | NULL | 菜谱简介 |
| source | VARCHAR(100) | NULL | 来源（如联网搜索） |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

**JSON字段处理说明**：

- ingredients 和 steps 字段以 JSON 数组格式存储（如 ["鸡蛋", "番茄"]）

- 在 MyBatis-Plus 中需配置 JacksonTypeHandler 实现自动映射

**实体类写法**：
```java
@TableField(typeHandler = JacksonTypeHandler.class)
private List<String> ingredients;

@TableField(typeHandler = JacksonTypeHandler.class)
private List<String> steps;
```

#### 2.2.4 meal_record（用餐记录表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键ID |
| user_id | INTEGER | NOT NULL DEFAULT 1 | 用户ID |
| recipe_id | INTEGER | NOT NULL | 关联菜谱ID |
| meal_type | VARCHAR(20) | NOT NULL | 餐次：BREAKFAST/LUNCH/DINNER/SNACK |
| portion | REAL | NOT NULL | 份量（默认1份） |
| portion_unit | VARCHAR(10) | DEFAULT '份' | 份量单位：份/克/毫升 |
| record_date | DATE | NOT NULL | 记录日期（YYYY-MM-DD） |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 更新时间 |

#### 2.2.5 nutrition_record（营养素记录表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键ID |
| user_id | INTEGER | NOT NULL DEFAULT 1 | 用户ID |
| record_date | DATE | NOT NULL | 记录日期（YYYY-MM-DD） |
| meal_type | VARCHAR(20) | NOT NULL | 餐次 |
| protein | REAL | NOT NULL | 蛋白质摄入（g） |
| carbohydrate | REAL | NOT NULL | 碳水摄入（g） |
| fat | REAL | NOT NULL | 脂肪摄入（g） |
| calories | INTEGER | NOT NULL | 热量摄入（kcal） |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 2.2.6 cycle_record（生理周期记录表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键ID |
| user_id | INTEGER | NOT NULL DEFAULT 1 | 用户ID |
| start_date | DATE | NOT NULL | 周期开始日期 |
| end_date | DATE | NULL | 周期结束日期 |
| cycle_length | INTEGER | DEFAULT 28 | 周期长度（天） |
| phase | VARCHAR(20) | NULL | 当前阶段：MENSTRUATION/FOLLICULAR/OVULATION/LUTEAL |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 更新时间 |

#### 2.2.7 user_preference（用户偏好表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键ID |
| user_id | INTEGER | NOT NULL DEFAULT 1 | 用户ID |
| preference_type | VARCHAR(20) | NOT NULL | 偏好类型：LIKE/DISLIKE/DIETARY |
| content | VARCHAR(100) | NOT NULL | 偏好内容（如：香菜、低脂、素食） |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 2.2.8 health_goal（健康目标表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 主键ID |
| user_id | INTEGER | NOT NULL DEFAULT 1 | 用户ID |
| goal_type | VARCHAR(20) | NOT NULL | 目标类型：LOSS_GAIN/MUSCLE_GAIN/MAINTAIN |
| target_weight | REAL | NULL | 目标体重（kg） |
| target_calories_min | INTEGER | NULL | 目标热量最小值 |
| target_calories_max | INTEGER | NULL | 目标热量最大值 |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 更新时间 |

### 2.3 索引设计

| 表名 | 索引字段 | 索引类型 | 说明 |
|------|----------|----------|------|
| weight_record | record_date | INDEX | 加速日期查询 |
| meal_record | record_date, meal_type | INDEX | 加速按日期和餐次查询 |
| nutrition_record | record_date | INDEX | 加速日期查询 |
| recipe | category | INDEX | 加速分类筛选 |

---

## 3. 配置文件设计

### 3.1 配置文件概述

- **配置文件格式**：JSON
- **存储位置**：`config/app-config.json`（程序根目录下）
- **加载时机**：应用启动时加载

### 3.2 配置文件结构

```json
{
  "database": {
    "path": "data/foodgpt.db",
    "autoCreate": true
  },
  "api": {
    "deepseek": {
      "baseUrl": "https://api.deepseek.com/chat/completions",
      "apiKey": "",
      "model": "deepseek-v4-pro",
      "maxTokens": 4096,
      "temperature": 1.0
    },
    "recipeSearch": {
      "baseUrl": "",
      "apiKey": ""
    }
  },
  "app": {
    "theme": "light",
    "language": "zh_CN",
    "autoCheckUpdate": true,
    "dataBackupInterval": 7
  },
  "user": {
    "id": 1,
    "name": ""
  }
}
```

### 3.3 配置字段说明

#### database 配置

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| path | String | data/foodgpt.db | 数据库文件路径 |
| autoCreate | Boolean | true | 是否自动创建数据库 |

#### api.deepseek 配置

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| baseUrl | String | https://api.deepseek.com/chat/completions | API基础URL |
| apiKey | String | 空 | DeepSeek API密钥 |
| model | String | deepseek-v4-pro | 模型名称 |
| maxTokens | Integer | 4096 | 最大生成token数 |
| temperature | Double | 1.0 | 温度参数（0-2） |

#### api.recipeSearch 配置

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| baseUrl | String | 空 | 菜谱搜索API地址 |
| apiKey | String | 空 | 菜谱搜索API密钥 |

#### app 配置

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| theme | String | light | 主题：light/dark |
| language | String | zh_CN | 语言：zh_CN/en_US |
| autoCheckUpdate | Boolean | true | 是否自动检查更新 |
| dataBackupInterval | Integer | 7 | 数据备份间隔（天） |

---

## 4. 界面设计

### 4.1 设计规范

#### 4.1.1 色彩规范

| 颜色名称 | Hex值 | 用途 |
|----------|-------|------|
| 主色调 | #FF6B8A | 按钮、选中状态、强调色 |
| 主色调浅 | #FFE4EA | 卡片背景、输入框焦点 |
| 成功色 | #67C23A | 达标状态、完成标记 |
| 警告色 | #E6A23C | 未达标状态、警告提示 |
| 危险色 | #F56C6C | 删除按钮、错误提示 |
| 文字主色 | #303133 | 主要文字 |
| 文字次色 | #606266 | 次要文字 |
| 文字浅色 | #909399 | 提示文字 |
| 边框色 | #EBEEF5 | 分隔线、输入框边框 |
| 背景色 | #F5F7FA | 页面背景 |
| 卡片背景 | #FFFFFF | 卡片背景 |

#### 4.1.2 字体规范

| 用途 | 字体 | 大小 | 样式 |
|------|------|------|------|
| 标题 | System | 20px | Bold |
| 卡片标题 | System | 16px | Bold |
| 正文 | System | 14px | Regular |
| 次要文字 | System | 12px | Regular |
| 数字 | System | 24px | Bold（大数值） |

#### 4.1.3 圆角规范

| 元素 | 圆角 |
|------|------|
| 卡片 | 16px |
| 按钮 | 20px |
| 输入框 | 8px |
| 进度条 | 6px |

#### 4.1.4 图标规范

- **图标库**：Ikonli（FontIcon）
- **图标尺寸**：24px（常规）、32px（强调）
- **图标颜色**：与文字颜色一致

### 4.2 界面布局设计

#### 4.2.1 主界面（Dashboard）

**文件路径**：`src/main/resources/fxml/dashboard.fxml`

**布局结构**：

```
┌─────────────────────────────────────────────────────┐
│  顶部导航栏                                          │
│  ┌────────────┬───────────────────────┬───────────┐ │
│  │食物语Logo  │  标签页切换            │ 用户菜单  │ │
│  │  🍽️       │ 首页|菜谱|记录|分析|专区│  设置     │ │
│  └────────────┴───────────────────────┴───────────┘ │
├─────────────────────────────────────────────────────┤
│  主体区域 - 仪表盘                                   │
│  ┌───────────────────────────────────────────────┐ │
│  │  我的身体数据卡片（左输入区+右BMI/BMR显示）     │ │
│  └───────────────────────────────────────────────┘ │
│  ┌───────────────┬───────────────┬───────────────┐ │
│  │ 体重趋势卡片  │ 今日营养进度   │ 均衡度评分     │ │
│  │ （折线图）    │ （三个进度条） │ （圆形评分）   │ │
│  └───────────────┴───────────────┴───────────────┘ │
│  ┌───────────────────────────────────────────────┐ │
│  │ 快捷操作入口（记录用餐/搜索菜谱/查看报告）      │ │
│  └───────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────┤
│  底部状态栏                                          │
│  今日摄入：XXkcal | 蛋白质XXg | 碳水XXg | 脂肪XXg   │
└─────────────────────────────────────────────────────┘
```

**关键组件**：

| 组件 | 类型 | ID | 说明 |
|------|------|-----|------|
| bodyDataCard | VBox | bodyDataCard | 身体数据卡片容器 |
| heightSpinner | Spinner | heightSpinner | 身高输入（100-250） |
| weightSpinner | Spinner | weightSpinner | 体重输入（30-200） |
| ageSpinner | Spinner | ageSpinner | 年龄输入（10-100） |
| activityComboBox | ComboBox | activityComboBox | 活动量选择 |
| bmiIndicator | ProgressIndicator | bmiIndicator | BMI圆形指示器 |
| bmrLabel | Label | bmrLabel | BMR数值显示 |
| saveBtn | Button | saveBtn | 保存按钮（粉色） |
| editBtn | Button | editBtn | 修改按钮（边框） |

#### 4.2.2 体重追踪界面

**文件路径**：`src/main/resources/fxml/weightTrack.fxml`

**布局结构**：

```
┌─────────────────────────────────────────────────────┐
│  顶部工具栏                                          │
│  标题 + 时间范围切换（7天/30天/90天）+ 添加记录按钮   │
├─────────────────────────────────────────────────────┤
│  折线图区域                                          │
│  JavaFX LineChart（X轴日期，Y轴体重）                │
├─────────────────────────────────────────────────────┤
│  记录列表                                            │
│  最近3条记录（日期/体重/编辑/删除图标）              │
└─────────────────────────────────────────────────────┘
```

#### 4.2.3 菜谱管理界面

**文件路径**：`src/main/resources/fxml/recipeManage.fxml`

**布局结构**：

```
┌─────────────────────────────────────────────────────┐
│  顶部工具栏                                          │
│  搜索框 + 分类筛选下拉框                             │
├─────────────────────────────────────────────────────┤
│  菜谱卡片网格                                        │
│  每行3张卡片（图片/名称/分类/食材/热量/操作按钮）    │
├─────────────────────────────────────────────────────┤
│  分页控件                                            │
└─────────────────────────────────────────────────────┘
```

#### 4.2.4 联网搜索界面

**文件路径**：`src/main/resources/fxml/recipeSearch.fxml`

**布局结构**：

```
┌─────────────────────────────────────────────────────┐
│  搜索区域                                            │
│  输入框（提示"输入食材，如：番茄 鸡蛋"）+ 搜索按钮    │
│  偏好标签行（已应用偏好：[标签]）                     │
├─────────────────────────────────────────────────────┤
│  搜索结果列表                                        │
│  卡片纵向排列（图片80×80/名称/食材/简介/热量/保存）  │
├─────────────────────────────────────────────────────┤
│  底部状态                                            │
│  加载动画/结果数量/错误提示+重试按钮                  │
└─────────────────────────────────────────────────────┘
```

#### 4.2.5 用餐记录界面

**文件路径**：`src/main/resources/fxml/mealRecord.fxml`

**布局结构**：

```
┌─────────────────────────────────────────────────────┐
│  顶部工具栏                                          │
│  日期选择器 + 添加记录按钮                            │
│  餐次切换标签（早餐/午餐/晚餐/加餐）                  │
├─────────────────────────────────────────────────────┤
│  记录列表                                            │
│  当前餐次记录卡片（菜品/份量/营养素/编辑/删除）       │
├─────────────────────────────────────────────────────┤
│  底部汇总条                                          │
│  今日摄入：XXkcal | 蛋白质XXg | 碳水XXg | 脂肪XXg   │
└─────────────────────────────────────────────────────┘
```

#### 4.2.6 营养素分析界面

**文件路径**：`src/main/resources/fxml/nutritionAnalysis.fxml`

**布局结构**：

```
┌─────────────────────────────────────────────────────┐
│  顶部工具栏                                          │
│  日期选择器 + 刷新按钮                               │
├─────────────────────────────────────────────────────┤
│  图表区域                                            │
│  分组柱状图（X轴餐次，Y轴克数，三色柱状）            │
├─────────────────────────────────────────────────────┤
│  数据卡片                                            │
│  三大营养素横向卡片（名称/摄入/目标/进度条）          │
├─────────────────────────────────────────────────────┤
│  明细列表（可折叠）                                   │
│  按餐次展开，显示菜品/份量/营养素数值                │
└─────────────────────────────────────────────────────┘
```

#### 4.2.7 女性专区界面

**文件路径**：`src/main/resources/fxml/femaleZone.fxml`

**布局结构**：

```
┌─────────────────────────────────────────────────────┐
│  顶部区域                                            │
│  健康目标卡片（减脂/增肌/维持选择）                   │
│  热量目标显示                                        │
├─────────────────────────────────────────────────────┤
│  周期管理                                            │
│  阶段指示器 + 日历 + 周期信息                        │
├─────────────────────────────────────────────────────┤
│  功能入口                                            │
│  专属食谱 / 专属匹配 / AI健康顾问 / 营养缺口分析     │
└─────────────────────────────────────────────────────┘
```

#### 4.2.8 AI健康顾问界面

**文件路径**：`src/main/resources/fxml/aiAdvisor.fxml`

**布局结构**：

```
┌─────────────────────────────────────────────────────┐
│  顶部区域                                            │
│  标题 + 新建咨询按钮 + 用户状态标签行                 │
├─────────────────────────────────────────────────────┤
│  对话消息列表                                        │
│  用户消息靠右 / AI回复靠左卡片（含头像）              │
│  加载动画（AI正在思考...）                           │
├─────────────────────────────────────────────────────┤
│  底部输入区域                                        │
│  输入框 + 发送按钮（粉色）                           │
└─────────────────────────────────────────────────────┘
```

### 4.3 CSS样式文件

#### 4.3.1 main.css（全局样式）

```css
.root {
    -fx-background-color: #F5F7FA;
    -fx-font-family: "System";
}

.card {
    -fx-background-color: #FFFFFF;
    -fx-background-radius: 16px;
    -fx-border-radius: 16px;
    -fx-padding: 24px;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);
}

.btn-primary {
    -fx-background-color: #FF6B8A;
    -fx-text-fill: #FFFFFF;
    -fx-background-radius: 20px;
    -fx-padding: 8px 24px;
    -fx-font-size: 14px;
    -fx-font-weight: bold;
}

.btn-outline {
    -fx-background-color: transparent;
    -fx-text-fill: #FF6B8A;
    -fx-border-color: #FF6B8A;
    -fx-border-radius: 20px;
    -fx-background-radius: 20px;
    -fx-padding: 8px 24px;
    -fx-font-size: 14px;
}

.text-field, .combo-box, .spinner {
    -fx-background-radius: 8px;
    -fx-border-radius: 8px;
    -fx-border-color: #EBEEF5;
    -fx-padding: 8px 12px;
}
```

---

## 5. 核心类设计

### 5.1 实体类（Entity）

#### 5.1.1 BodyData.java

```java
package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("body_data")
public class BodyData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId = 1;
    private Double height;
    private Double weight;
    private Integer age;
    private String activityLevel;
    private Double bmi;
    private Double bmr;
    private Integer recommendedCaloriesMin;
    private Integer recommendedCaloriesMax;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

#### 5.1.2 Recipe.java

```java
package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("recipe")
public class Recipe {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> ingredients;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> steps;
    private Double protein;
    private Double carbohydrate;
    private Double fat;
    private Integer calories;
    private String imageUrl;
    private String description;
    private String source;
    private LocalDateTime createTime;
}
```

#### 5.1.3 MealRecord.java

```java
package com.foodgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.util.List;

@Data
@TableName("meal_record")
public class MealRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId = 1;
    private Long recipeId;
    private String mealType;
    private Double portion;
    private String portionUnit = "份";
    private LocalDate recordDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### 5.2 Mapper接口

#### 5.2.1 BodyDataMapper.java

```java
package com.foodgpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodgpt.entity.BodyData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BodyDataMapper extends BaseMapper<BodyData> {
}
```

#### 5.2.2 RecipeMapper.java

```java
package com.foodgpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodgpt.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {
    @Select("SELECT * FROM recipe WHERE category = #{category}")
    List<Recipe> selectByCategory(String category);

    @Select("SELECT * FROM recipe WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<Recipe> searchByName(String keyword);
}
```

### 5.3 Service类

#### 5.3.1 BodyDataService.java

```java
package com.foodgpt.service;

import com.foodgpt.entity.BodyData;

public interface BodyDataService {
    BodyData getBodyData();
    void saveBodyData(BodyData bodyData);
    void updateBodyData(BodyData bodyData);
    double calculateBmi(double height, double weight);
    double calculateBmr(double weight, double height, int age, String activityLevel);
}
```

#### 5.3.2 NutritionService.java

```java
package com.foodgpt.service;

import com.foodgpt.dto.NutritionSummaryDTO;

import java.time.LocalDate;

public interface NutritionService {
    NutritionSummaryDTO getDailyNutrition(LocalDate date);
    double calculateBalanceScore(NutritionSummaryDTO summary);
}
```

### 5.4 Controller类

#### 5.4.1 DashboardController.java

```java
package com.foodgpt.controller;

import com.foodgpt.service.BodyDataService;
import com.foodgpt.service.NutritionService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DashboardController {
    @FXML private Spinner<Double> heightSpinner;
    @FXML private Spinner<Double> weightSpinner;
    @FXML private Spinner<Integer> ageSpinner;
    @FXML private ComboBox<String> activityComboBox;
    @FXML private ProgressIndicator bmiIndicator;
    @FXML private Label bmrLabel;
    @FXML private Button saveBtn;
    @FXML private Button editBtn;

    private BodyDataService bodyDataService;
    private NutritionService nutritionService;

    @FXML
    public void initialize() {
        initSpinners();
        initComboBox();
        loadBodyData();
    }

    @FXML
    public void handleSave() {
        // 保存身体数据逻辑
    }

    @FXML
    public void handleEdit() {
        // 修改数据逻辑
    }
}
```

#### 5.4.2 AiAdvisorController.java

```java
package com.foodgpt.controller;

import com.foodgpt.service.AiAdvisorService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class AiAdvisorController {
    @FXML private VBox messageList;
    @FXML private TextArea inputArea;
    @FXML private Button sendBtn;
    @FXML private Button newChatBtn;

    private AiAdvisorService aiAdvisorService;

    @FXML
    public void initialize() {
        loadUserStatus();
    }

    @FXML
    public void handleSend() {
        // 发送消息并获取AI回复
    }

    @FXML
    public void handleNewChat() {
        // 新建咨询会话
    }
}
```

### 5.5 枚举类

#### 5.5.1 ActivityLevel.java

```java
package com.foodgpt.enums;

public enum ActivityLevel {
    SEDENTARY("久坐", 1.2),
    MILD("轻度", 1.375),
    MODERATE("中度", 1.55),
    ACTIVE("活跃", 1.725);

    private final String label;
    private final double factor;

    ActivityLevel(String label, double factor) {
        this.label = label;
        this.factor = factor;
    }

    public String getLabel() { return label; }
    public double getFactor() { return factor; }
}
```

#### 5.5.2 MealType.java

```java
package com.foodgpt.enums;

public enum MealType {
    BREAKFAST("早餐"),
    LUNCH("午餐"),
    DINNER("晚餐"),
    SNACK("加餐");

    private final String label;

    MealType(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
}
```

### 5.6 DTO类

#### 5.6.1 NutritionSummaryDTO.java

```java
package com.foodgpt.dto;

import lombok.Data;

@Data
public class NutritionSummaryDTO {
    private double protein;
    private double carbohydrate;
    private double fat;
    private int calories;
    private double proteinTarget;
    private double carbohydrateTarget;
    private double fatTarget;
    private int caloriesTarget;
}
```

---

## 6. DeepSeek API调用设计

### 6.1 API调用封装

#### 6.1.1 AiChatDTO.java

```java
package com.foodgpt.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiChatDTO {
    private List<Message> messages;
    private String model;
    private Thinking thinking;
    private String reasoningEffort;
    private Integer maxTokens;
    private ResponseFormat responseFormat;
    private Boolean stream;
    private Double temperature;
    private Double topP;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String content;
        private String role;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Thinking {
        private String type;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseFormat {
        private String type;
    }
}
```

#### 6.1.2 AiAdvisorService.java

```java
package com.foodgpt.service;

public interface AiAdvisorService {
    String getAdvice(String userMessage);
    String getNutritionAnalysis(String context);
}
```

#### 6.1.3 AiAdvisorServiceImpl.java

```java
package com.foodgpt.service.impl;

import com.foodgpt.config.ApiConfig;
import com.foodgpt.service.AiAdvisorService;
import com.foodgpt.config.AppConfig;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatCompletionMessage;
import com.openai.models.ChatCompletionMessageParam;

import java.util.List;

public class AiAdvisorServiceImpl implements AiAdvisorService {

    private final OpenAIClient client;
    private final ApiConfig apiConfig;

    public AiAdvisorServiceImpl() {
        this.apiConfig = AppConfig.load().getApi();
        this.client = OpenAIOkHttpClient.builder()
                .baseUrl(apiConfig.getDeepseek().getBaseUrl())
                .apiKey(apiConfig.getDeepseek().getApiKey())
                .build();
    }

    @Override
    public String getAdvice(String userMessage) {
        String systemPrompt = buildSystemPrompt();

        // 构建消息列表
        List<ChatCompletionMessageParam> messages = List.of(
                ChatCompletionMessageParam.builder()
                        .content(systemPrompt)
                        .role(ChatCompletionMessage.Role.SYSTEM)
                        .build(),
                ChatCompletionMessageParam.builder()
                        .content(userMessage)
                        .role(ChatCompletionMessage.Role.USER)
                        .build()
        );

        // 构建请求参数
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(apiConfig.getDeepseek().getModel())
                .messages(messages)
                .maxCompletionTokens(apiConfig.getDeepseek().getMaxTokens())
                .temperature(apiConfig.getDeepseek().getTemperature())
                .build();

        // 发送请求并获取响应
        ChatCompletion completion = client.chat().completions().create(params);

        // 提取回复内容
        return completion.choices().get(0).message().content().orElse("抱歉，我暂时无法提供建议。");
    }

    private String buildSystemPrompt() {
        return "你是一位专业的女性健康饮食顾问。请根据用户的身体数据、生理周期阶段、健康目标和饮食偏好，提供个性化的饮食建议。建议内容要具体、实用，包含食材推荐和摄入量建议。";
    }

    @Override
    public String getNutritionAnalysis(String context) {
        return getAdvice("请分析以下营养摄入情况并给出建议：\n" + context);
    }
}
```

### 6.2 API响应解析

**DeepSeek API响应结构**：

```json
{
  "id": "chatcmpl-xxx",
  "object": "chat.completion",
  "created": 1704064200,
  "model": "deepseek-v4-pro",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "你的个性化饮食建议..."
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 100,
    "completion_tokens": 200,
    "total_tokens": 300
  }
}
```

---

## 7. 工具类设计

### 7.1 BmiBmrCalculator.java

```java
package com.foodgpt.util;

import com.foodgpt.enums.ActivityLevel;

public class BmiBmrCalculator {
    
    public static double calculateBmi(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }
    
    public static double calculateBmr(double weightKg, double heightCm, int age) {
        // Mifflin-St Jeor 公式（女性）
        return 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
    }
    
    public static double calculateDailyCalories(double bmr, ActivityLevel activityLevel) {
        return bmr * activityLevel.getFactor();
    }
}
```

### 7.2 JsonUtil.java

```java
package com.foodgpt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    public static String extractValue(String json, String path) {
        try {
            JsonNode node = mapper.readTree(json);
            String[] parts = path.split("\\.");
            for (String part : parts) {
                if (part.contains("[")) {
                    String field = part.substring(0, part.indexOf("["));
                    int index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));
                    node = node.get(field).get(index);
                } else {
                    node = node.get(part);
                }
                if (node == null) return null;
            }
            return node.asText();
        } catch (Exception e) {
            throw new RuntimeException("JSON路径解析失败", e);
        }
    }
}
```

---

## 8. 配置管理

### 8.1 ApiConfig.java

```java
package com.foodgpt.config;

import lombok.Data;

@Data
public class ApiConfig {
    private DeepseekConfig deepseek = new DeepseekConfig();
    private RecipeSearchConfig recipeSearch = new RecipeSearchConfig();

    @Data
    public static class DeepseekConfig {
        private String baseUrl = "https://api.deepseek.com/chat/completions";
        private String apiKey = "";
        private String model = "deepseek-v4-pro";
        private Integer maxTokens = 4096;
        private Double temperature = 1.0;
    }

    @Data
    public static class RecipeSearchConfig {
        private String baseUrl = "";
        private String apiKey = "";
    }
}
```

### 8.2 AppConfig.java

```java
package com.foodgpt.config;

import com.foodgpt.util.JsonUtil;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
public class AppConfig {
    private static final String CONFIG_PATH = "config/app-config.json";
    
    private DatabaseConfig database = new DatabaseConfig();
    private ApiConfig api = new ApiConfig();
    private AppSettings app = new AppSettings();
    private UserConfig user = new UserConfig();

    @Data
    public static class DatabaseConfig {
        private String path = "data/foodgpt.db";
        private boolean autoCreate = true;
    }

    @Data
    public static class AppSettings {
        private String theme = "light";
        private String language = "zh_CN";
        private boolean autoCheckUpdate = true;
        private int dataBackupInterval = 7;
    }

    @Data
    public static class UserConfig {
        private int id = 1;
        private String name = "";
    }

    public static AppConfig load() {
        try {
            Path path = Paths.get(CONFIG_PATH);
            if (Files.exists(path)) {
                String content = Files.readString(path);
                return JsonUtil.fromJson(content, AppConfig.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AppConfig();
    }

    public void save() {
        try {
            File configDir = new File("config");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            String content = JsonUtil.toJson(this);
            Files.writeString(Paths.get(CONFIG_PATH), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

---

## 9. 启动类

### 9.1 FoodGPTApplication.java

```java
package com.foodgpt;

import com.foodgpt.config.AppConfig;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.io.File;

public class FoodGPTApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        initApplication();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        
        primaryStage.setTitle("食物语 - FoodGPT");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initApplication() {
        AppConfig config = AppConfig.load();
        
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
                // 初始化数据库（建表）
        String dbUrl = "jdbc:sqlite:data/foodgpt.db";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            
            // 创建身体数据表
            stmt.execute("CREATE TABLE IF NOT EXISTS body_data (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL DEFAULT 1," +
                    "height REAL NOT NULL," +
                    "weight REAL NOT NULL," +
                    "age INTEGER NOT NULL," +
                    "activity_level VARCHAR(20) NOT NULL," +
                    "bmi REAL," +
                    "bmr REAL," +
                    "recommended_calories_min INTEGER," +
                    "recommended_calories_max INTEGER," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            // 创建其他表（weight_record、recipe、meal_record、cycle_record、user_preference、health_goal）
            // ... 类似格式
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
```

---
