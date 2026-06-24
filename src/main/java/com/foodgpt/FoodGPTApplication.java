package com.foodgpt;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.foodgpt.config.AppConfig;
import com.foodgpt.controller.*;
import com.foodgpt.mapper.*;
import com.foodgpt.service.*;
import com.foodgpt.service.impl.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class FoodGPTApplication extends Application {

    private SqlSessionFactory sqlSessionFactory;
    private DashboardController dashboardController;
    private BodyDataController bodyDataController;
    private WeightTrackController weightTrackController;
    private RecipeManageController recipeManageController;
    private RecipeSearchController recipeSearchController;
    private MealRecordController mealRecordController;
    private NutritionAnalysisController nutritionAnalysisController;
    private FemaleZoneController femaleZoneController;
    private AiAdvisorController aiAdvisorController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initDatabase();
        initServices();

        TabPane tabPane = new TabPane();

        Tab dashboardTab = new Tab("首页");
        dashboardTab.setContent(loadFxml("dashboard.fxml", dashboardController));
        tabPane.getTabs().add(dashboardTab);

        Tab bodyDataTab = new Tab("身体数据");
        bodyDataTab.setContent(loadFxml("bodyData.fxml", bodyDataController));
        tabPane.getTabs().add(bodyDataTab);

        Tab weightTrackTab = new Tab("体重追踪");
        weightTrackTab.setContent(loadFxml("weightTrack.fxml", weightTrackController));
        tabPane.getTabs().add(weightTrackTab);

        Tab recipeTab = new Tab("菜谱管理");
        recipeTab.setContent(loadFxml("recipeManage.fxml", recipeManageController));
        tabPane.getTabs().add(recipeTab);

        Tab recipeSearchTab = new Tab("搜索菜谱");
        recipeSearchTab.setContent(loadFxml("recipeSearch.fxml", recipeSearchController));
        tabPane.getTabs().add(recipeSearchTab);

        Tab mealRecordTab = new Tab("用餐记录");
        mealRecordTab.setContent(loadFxml("mealRecord.fxml", mealRecordController));
        tabPane.getTabs().add(mealRecordTab);

        Tab nutritionTab = new Tab("营养分析");
        nutritionTab.setContent(loadFxml("nutritionAnalysis.fxml", nutritionAnalysisController));
        tabPane.getTabs().add(nutritionTab);

        Tab femaleZoneTab = new Tab("女性专区");
        femaleZoneTab.setContent(loadFxml("femaleZone.fxml", femaleZoneController));
        tabPane.getTabs().add(femaleZoneTab);

        Tab aiAdvisorTab = new Tab("AI顾问");
        aiAdvisorTab.setContent(loadFxml("aiAdvisor.fxml", aiAdvisorController));
        tabPane.getTabs().add(aiAdvisorTab);

        Scene scene = new Scene(tabPane, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

        primaryStage.setTitle("食物语 - 智能饮食顾问");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (aiAdvisorController != null) {
            aiAdvisorController.shutdown();
        }
        super.stop();
    }

    private void initDatabase() throws Exception {
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }

        Properties props = new Properties();
        props.setProperty("driver", "org.sqlite.JDBC");
        props.setProperty("url", "jdbc:sqlite:data/foodgpt.db");

        PooledDataSourceFactory dataSourceFactory = new PooledDataSourceFactory();
        dataSourceFactory.setProperties(props);

        Environment environment = new Environment(
                "development",
                new JdbcTransactionFactory(),
                dataSourceFactory.getDataSource()
        );

        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());
        configuration.addMappers("com.foodgpt.mapper");

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        configuration.addInterceptor(interceptor);

        sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(configuration);

        executeSchema();
    }

    private void executeSchema() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            InputStream is = getClass().getResourceAsStream("/schema.sql");
            if (is != null) {
                String schema = new String(is.readAllBytes());
                String[] statements = schema.split(";");
                for (String stmt : statements) {
                    stmt = stmt.trim();
                    if (!stmt.isEmpty()) {
                        session.getConnection().createStatement().execute(stmt);
                    }
                }
                session.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initServices() {
        SqlSession session = sqlSessionFactory.openSession(true);

        BodyDataMapper bodyDataMapper = session.getMapper(BodyDataMapper.class);
        WeightRecordMapper weightRecordMapper = session.getMapper(WeightRecordMapper.class);
        RecipeMapper recipeMapper = session.getMapper(RecipeMapper.class);
        MealRecordMapper mealRecordMapper = session.getMapper(MealRecordMapper.class);
        NutritionRecordMapper nutritionRecordMapper = session.getMapper(NutritionRecordMapper.class);
        CycleRecordMapper cycleRecordMapper = session.getMapper(CycleRecordMapper.class);

        AppConfig appConfig = AppConfig.getInstance();

        BodyDataService bodyDataService = new BodyDataServiceImpl(bodyDataMapper);
        WeightTrackService weightTrackService = new WeightTrackServiceImpl(weightRecordMapper);
        RecipeService recipeService = new RecipeServiceImpl(recipeMapper);
        MealRecordService mealRecordService = new MealRecordServiceImpl(mealRecordMapper);
        NutritionService nutritionService = new NutritionServiceImpl(nutritionRecordMapper, mealRecordMapper, recipeMapper);
        CycleService cycleService = new CycleServiceImpl(cycleRecordMapper);
        AiAdvisorService aiAdvisorService = new AiAdvisorServiceImpl(appConfig);

        dashboardController = new DashboardController();
        dashboardController.setServices(bodyDataService, nutritionService);

        bodyDataController = new BodyDataController();
        bodyDataController.setService(bodyDataService);

        weightTrackController = new WeightTrackController();
        weightTrackController.setService(weightTrackService);

        recipeManageController = new RecipeManageController();
        recipeManageController.setService(recipeService);

        recipeSearchController = new RecipeSearchController();
        recipeSearchController.setService(recipeService);

        mealRecordController = new MealRecordController();
        mealRecordController.setServices(mealRecordService, recipeService);

        nutritionAnalysisController = new NutritionAnalysisController();
        nutritionAnalysisController.setServices(mealRecordService, recipeService);

        femaleZoneController = new FemaleZoneController();
        femaleZoneController.setService(cycleService);

        aiAdvisorController = new AiAdvisorController();
        aiAdvisorController.setService(aiAdvisorService);
    }

    private Parent loadFxml(String fxmlFile, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
        loader.setController(controller);
        return loader.load();
    }
}
