package org.gms;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.gms.util.RequireUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;

/**
 * 服务器启动类
 * Spring Boot应用入口，负责自动创建数据库并启动服务
 */
@SpringBootApplication
@MapperScan("org.gms.dao.mapper")
@Slf4j
public class ServerApplication {
    /**
     * Spring Boot 应用主入口
     * 先自动创建数据库，再启动服务
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            initDb(args);
        } catch (Exception e) {
            log.error("自动创建数据库失败：", e);
            return;
        }
        SpringApplication.run(ServerApplication.class, args);
    }

    /**
     * 修复PreDataSourceConfig优先级不够，导致在创建数据库之前获取连接，进而无法正常启动
     * 以下组件FlywayAutoConfiguration、HibernateJpaAutoConfiguration在启动的时候会获取数据库连接，因为库名不存在，进而一直报错
     * 无法解决在获取MybatisFlexProperties之后，在以上自动配置之前执行，进而手动解析yml来自动创建库
     *
     * @param args 命令行参数
     * @throws Exception 数据库连接或创建失败时抛出
     */
    private static void initDb(String[] args) throws Exception {
        InputStream resource = null;

        String location = getStartParam(args, "spring.config.location");
        if (location != null) {
            Path path = Path.of(location);
            if (!Files.exists(path)) {
                return;
            }
            resource = Files.newInputStream(path);
        }
        if (resource == null) {
            // 解析jar包自带的yml
            resource = ServerApplication.class.getClassLoader().getResourceAsStream("application.yml");
        }

        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> property = yaml.load(resource);
        JSONObject mybatisFlex = JSONObject.parse(JSONObject.toJSONString(property.get("mybatis-flex")));
        JSONObject datasource = mybatisFlex.getJSONObject("datasource").getJSONObject("mysql");
        String driver = getStartParam(args, "mybatis-flex.datasource.mysql.driver-class-name");
        if (driver == null) driver = datasource.getString("driver-class-name");
        String dbUrl = getStartParam(args, "mybatis-flex.datasource.mysql.url");
        if (dbUrl == null) dbUrl = datasource.getString("url");
        String username = getStartParam(args, "mybatis-flex.datasource.mysql.username");
        if (username == null) username = datasource.getString("username");
        String password = getStartParam(args, "mybatis-flex.datasource.mysql.password");
        if (password == null) password = datasource.getString("password");
        String urlPrefix = dbUrl.split("\\?")[0];
        String[] dbSplit = urlPrefix.split("/");
        String dbName = dbSplit[dbSplit.length - 1];
        String dbPrefix = urlPrefix.substring(0, urlPrefix.length() - dbName.length());
        try (Connection connection = getConnection(driver, dbPrefix + "mysql", username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SHOW DATABASES LIKE '" + dbName + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return;
            }
            resultSet.close();
            preparedStatement = connection.prepareStatement("CREATE DATABASE " + dbName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }

    /**
     * 获取数据库连接
     *
     * @param driver   驱动类名
     * @param url      数据库连接URL
     * @param username 用户名
     * @param password 密码
     * @return 数据库连接
     * @throws Exception 驱动找不到或连接失败时抛出
     */
    private static Connection getConnection(String driver, String url, String username, String password) throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 获取启动参数值（按优先级：JVM参数 → Spring Boot参数 → 环境变量）
     *
     * @param args      命令行参数
     * @param paramName 参数名
     * @return 参数值，未找到返回 null
     */
    private static String getStartParam(String[] args, String paramName) {
        // 第一优先级 jvm参数
        String property = System.getProperty(paramName);
        if (property != null) {
            return property;
        }
        // 第二优先级 springboot参数
        for (String arg : args) {
            if (arg.startsWith("--" + paramName)) {
                return arg.split("=")[1];
            }
        }
        // 第三优先级 环境变量
        return System.getenv(paramName.replaceAll("\\.", "_"));
    }
}