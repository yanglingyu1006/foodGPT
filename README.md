# 食物语 FoodGPT

**食物语（FoodGPT）** 是一款基于 JavaFX 的桌面智能饮食顾问应用。它不仅仅是菜谱管理工具，更是一个懂你身体、懂你目标、懂你周期的私人饮食管家，覆盖了 **记录 → 分析 → 推荐 → 干预** 的完整健康管理闭环。

---

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21+ |
| UI 框架 | JavaFX | 21 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | SQLite | 3.44 |
| HTTP 客户端 | OkHttp | 4.12.0 |
| JSON 处理 | Jackson | 2.16.0 |
| 图标库 | Ikonli | 12.3.1 |
| 日志 | SLF4J + Logback | 2.0.9 |
| 简化代码 | Lombok | 1.18.42 |
| 构建工具 | Maven | 3.x |

---

## 功能特性

### 健康仪表盘
- 录入身高、体重、年龄、活动量等身体数据
- 自动计算 BMI（身体质量指数）和 BMR（基础代谢率）
- 体重趋势折线图、三大营养素进度条、均衡度评分

### 菜谱管理
- 菜谱搜索、分类浏览、分页展示
- 新建和删除菜谱
- 每道菜谱包含食材清单、烹饪步骤、营养素含量

### 用餐记录
- 按日期和餐次（早餐/午餐/晚餐/加餐）记录饮食
- 份量调整，自动计算营养素摄入
- 今日营养汇总（热量、蛋白质、碳水、脂肪）

### 营养分析
- 各餐次营养素摄入分组柱状图
- 三大营养素（蛋白质/碳水/脂肪）进度追踪
- 总热量和均衡度评分

### 女性专区
- 生理周期管理与阶段追踪（经期/卵泡期/排卵期/黄体期）
- 健康目标设定（减脂/增肌/维持）
- **专属食谱**：根据周期阶段和目标智能推荐菜谱
- **专属匹配**：根据偏好食材和营养缺口个性化匹配
- **营养缺口分析**：对比实际摄入 vs 目标，生成补充建议

### AI 健康顾问
- 接入 DeepSeek API，提供智能饮食咨询
- 对话式交互，结合用户身体数据给出个性化建议

---

## 架构设计

采用 **MVC + 分层架构**：

```
用户操作 → View (FXML) → Controller → Service → Mapper → SQLite
                                    │
                                    └──→ 外部 API (DeepSeek / 菜谱搜索)
```

| 层级 | 职责 | 技术实现 |
|------|------|----------|
| **View 层** | 界面展示与用户交互 | JavaFX FXML + CSS |
| **Controller 层** | 事件处理与视图逻辑 | JavaFX Controller 类 |
| **Service 层** | 业务逻辑处理 | Service 接口 + 实现类 |
| **Mapper 层** | 数据库访问 | MyBatis-Plus BaseMapper |
| **Entity 层** | 实体类，对应数据表 | POJO（Lombok） |
| **Config 层** | 配置管理 | JSON 配置文件 |
| **Util 层** | 工具类支持 | 静态方法类（BMI/BMR 计算等） |

---

## 项目结构

```
foodGPT/
├── src/main/
│   ├── java/com/foodgpt/
│   │   ├── FoodGPTApplication.java        # 启动入口
│   │   ├── Launcher.java                  # 启动器
│   │   ├── controller/                    # 控制器层
│   │   │   ├── MainLayoutController.java  # 主布局（侧边栏+导航）
│   │   │   ├── DashboardController.java   # 首页仪表盘
│   │   │   ├── WeightTrackController.java # 体重追踪
│   │   │   ├── RecipeManageController.java# 菜谱管理
│   │   │   ├── RecipeSearchController.java# 菜谱搜索
│   │   │   ├── RecipeDetailController.java # 菜谱详情
│   │   │   ├── MealRecordController.java  # 用餐记录
│   │   │   ├── NutritionAnalysisController.java # 营养分析
│   │   │   ├── FemaleZoneController.java  # 女性专区
│   │   │   └── AiAdvisorController.java   # AI 顾问
│   │   ├── service/                       # 服务层
│   │   │   ├── BodyDataService.java
│   │   │   ├── WeightTrackService.java
│   │   │   ├── RecipeService.java
│   │   │   ├── MealRecordService.java
│   │   │   ├── NutritionService.java
│   │   │   ├── CycleService.java
│   │   │   ├── AiAdvisorService.java
│   │   │   ├── ExternalRecipeService.java
│   │   │   ├── HealthGoalService.java
│   │   │   ├── UserPreferenceService.java
│   │   │   └── impl/                      # 服务实现
│   │   │       ├── BodyDataServiceImpl.java
│   │   │       ├── WeightTrackServiceImpl.java
│   │   │       ├── RecipeServiceImpl.java
│   │   │       ├── MealRecordServiceImpl.java
│   │   │       ├── NutritionServiceImpl.java
│   │   │       ├── CycleServiceImpl.java
│   │   │       ├── AiAdvisorServiceImpl.java
│   │   │       ├── ExternalRecipeServiceImpl.java
│   │   │       ├── HealthGoalServiceImpl.java
│   │   │       └── UserPreferenceServiceImpl.java
│   │   ├── mapper/                        # 数据访问层
│   │   │   ├── BodyDataMapper.java
│   │   │   ├── WeightRecordMapper.java
│   │   │   ├── RecipeMapper.java
│   │   │   ├── MealRecordMapper.java
│   │   │   ├── NutritionRecordMapper.java
│   │   │   ├── CycleRecordMapper.java
│   │   │   ├── HealthGoalMapper.java
│   │   │   └── UserPreferenceMapper.java
│   │   ├── entity/                        # 实体类
│   │   │   ├── BodyData.java
│   │   │   ├── WeightRecord.java
│   │   │   ├── Recipe.java
│   │   │   ├── MealRecord.java
│   │   │   ├── NutritionRecord.java
│   │   │   ├── CycleRecord.java
│   │   │   ├── UserPreference.java
│   │   │   └── HealthGoal.java
│   │   ├── config/                        # 配置类
│   │   │   ├── AppConfig.java
│   │   │   ├── ApiConfig.java
│   │   │   └── DatabaseConfig.java
│   │   ├── enums/                         # 枚举类
│   │   │   ├── ActivityLevel.java
│   │   │   ├── CyclePhase.java
│   │   │   ├── HealthGoalType.java
│   │   │   ├── MealType.java
│   │   │   └── RecipeCategory.java
│   │   └── util/                          # 工具类
│   │       ├── BmiBmrCalculator.java
│   │       ├── NutritionCalculator.java
│   │       ├── JsonUtil.java
│   │       └── OkHttpUtil.java
│   └── resources/
│       ├── fxml/                          # FXML 布局文件
│       │   ├── mainLayout.fxml
│       │   ├── dashboard.fxml
│       │   ├── weightTrack.fxml
│       │   ├── recipeManage.fxml
│       │   ├── recipeSearch.fxml
│       │   ├── recipeDetail.fxml
│       │   ├── mealRecord.fxml
│       │   ├── nutritionAnalysis.fxml
│       │   ├── femaleZone.fxml
│       │   └── aiAdvisor.fxml
│       ├── css/
│       │   ├── main.css                   # 全局样式
│       │   ├── dashboard.css              # 仪表盘样式
│       │   ├── femaleZone.css             # 女性专区样式
│       │   └── nutrition.css              # 营养分析样式
│       ├── mybatis-config.xml             # MyBatis 配置
│       ├── app-config.json                # 默认应用配置
│       └── schema.sql                     # 数据库建表脚本
├── config/
│   └── app-config.json                    # 用户配置（API 密钥等）
├── data/
│   └── foodgpt.db                         # SQLite 数据库（自动创建）
├── docs/
│   ├── design.md                          # 设计文档
│   └── requirements.md                    # 需求文档
├── pom.xml
├── run.bat                                # Windows 启动脚本
└── run.sh                                 # Linux/Mac 启动脚本
```

---

## 数据库设计

共 8 张数据表，使用 SQLite 嵌入式数据库，存储于 `data/foodgpt.db`。

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `body_data` | 身体数据 | height, weight, age, activity_level, bmi, bmr |
| `weight_record` | 体重记录 | weight, record_date, update_time |
| `recipe` | 菜谱库 | name, category, ingredients(JSON), protein, carbohydrate, fat, calories, update_time |
| `meal_record` | 用餐记录 | recipe_id, meal_type, portion, record_date, update_time |
| `nutrition_record` | 营养素记录 | record_date, protein, carbohydrate, fat, calories, update_time |
| `cycle_record` | 生理周期 | start_date, cycle_length, phase, update_time |
| `user_preference` | 用户偏好 | preference_type, content, update_time |
| `health_goal` | 健康目标 | goal_type, target_weight, target_date, current_progress, is_active |

---

## UI 设计规范

### 色彩方案

| 用途 | 色值 | 说明 |
|------|------|------|
| 主色（背景） | `#FDF5E6` | 奶油白 |
| 辅助色（标题/按钮） | `#8B5A2B` | 焦糖棕 |
| 点缀色（文字/分割线） | `#4A2C1A` | 深巧克力 |
| 进度条/装饰 | `#E8B86D` | 橙黄 |

### 布局结构

- **左侧边栏**：120px 宽度，品牌竖排文字"食物语·知味"，底部标签"食·养·记"
- **顶部导航栏**：首页 / 菜谱 / 记录 / 分析 / 专区 / AI顾问，选中高亮
- **底部状态栏**：今日摄入 / 蛋白质 / 碳水 / 脂肪
- **卡片样式**：圆角 16px，白色背景，阴影 `rgba(74,44,26,0.05)`

---

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+

### 构建与运行

```bash
# 克隆项目
git clone <repo-url>
cd foodGPT

# 编译
mvn compile

# 运行
mvn javafx:run
```

### 配置 API 密钥

编辑 `config/app-config.json`，填入 DeepSeek API 密钥：

```json
{
  "api": {
    "deepseek": {
      "apiKey": "your-api-key-here"
    }
  }
}
```

---

## 许可证

本项目仅供个人学习使用。