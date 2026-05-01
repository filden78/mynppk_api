package ru.filden.utils;

import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class AppConfig {
    private static Logger logger = LoggerFactory.getLogger("Config");
    public int port;
    public int min_thread;
    public int max_thread;
    public int timeout;


    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private int dbMaxPoolSize;
    private int dbMinIdle;

    private Properties prop;

    private AppConfig(Properties properties){
        prop = properties;
        try {
            port = Integer.parseInt(prop.getProperty("server.port", "8080"));
            min_thread = Integer.parseInt(prop.getProperty("server.min_thread", "1"));
            max_thread = Integer.parseInt(prop.getProperty("server.max_thread", "4"));
            timeout = Integer.parseInt(prop.getProperty("server.timeout", "30000"));

            dbUrl = prop.getProperty("db.url", "jdbc:sqlserver://192.168.1.102:1433;databaseName=restdb_new;encrypt=true;trustServerCertificate=true");
            dbUser = prop.getProperty("db.user", "sa");
            dbPassword = prop.getProperty("db.password", "Qwe123");
            dbMaxPoolSize = Integer.parseInt(prop.getProperty("db.maxPoolSize", "8"));
            dbMinIdle = Integer.parseInt(prop.getProperty("db.MinIdle", "80000"));
        }
        catch (NullPointerException e){
            logger.error("Property not found! :{}", e.getMessage());
        }
    }
    public static AppConfig loadConfigFromPath(String configPath) throws IOException {
        Properties properties = new Properties();
        try{
            File configFile = new File(configPath);
            if(!configFile.exists()){
                configFile.createNewFile();
                try(OutputStream os = new FileOutputStream(configFile)){
                    LoadDefaultConfig(properties);
                    properties.store(os, "default configs");
                    return new AppConfig(properties);
                }
                catch (Exception e)
                {
                    logger.error(e.getMessage());
                }
            }
            try(InputStream is = new FileInputStream(configFile)){
                properties.load(is);
                if(properties.isEmpty()){
                    LoadDefaultConfig(properties);
                }
                return new AppConfig(properties);
            }
            catch (Exception e)
            {
                logger.error(e.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return new AppConfig(properties);
    }
    public static AppConfig loadConfigFromProperty(Properties properties) {
        logger.info("Load config from custom properties");
        if(properties.isEmpty()){
            LoadDefaultConfig(properties);
        }
        return new AppConfig(properties);
    }
    private static void LoadDefaultConfig(Properties prop){
        prop.put("server.port",8080);
        prop.put("server.min_thread",1);
        prop.put("server.max_thread",4);
        prop.put("server.timeout",30000);

        prop.put("db.url","jdbc:sqlserver://192.168.1.102:1433;databaseName=restdb_new;encrypt=true;trustServerCertificate=true");
        prop.put("db.user", "sa");
        prop.put("db.password", "Qwe123");
        prop.put("db.maxPoolSize", "8");
        prop.put("db.MinIdle", "80000");
    }
    public HikariConfig hikariConfig(){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbUrl);
        hikariConfig.setUsername(dbUser);
        hikariConfig.setPassword(dbPassword);
        hikariConfig.setMaximumPoolSize(dbMaxPoolSize);
        hikariConfig.setMinimumIdle(dbMinIdle);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        return hikariConfig;
    }

}
