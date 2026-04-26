package ru.filden.utils;

import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    public Properties prop;

    private AppConfig(Properties properties){
        prop = properties;
        try {
            port = Integer.parseInt(prop.getProperty("server.port", "8080"));
            min_thread = Integer.parseInt(prop.getProperty("server.min_thread", "1"));
            max_thread = Integer.parseInt(prop.getProperty("server.port", "4"));
            timeout = Integer.parseInt(prop.getProperty("server.port", "30000"));

            dbUrl = prop.getProperty("db.url", "jdbc:sqlserver://192.168.1.102:1433;databaseName=restdb_new;encrypt=true;trustServerCertificate=true");
            dbUser = prop.getProperty("db.user", "sa");
            dbPassword = prop.getProperty("db.password", "Qwe123");
            dbMaxPoolSize = Integer.parseInt(prop.getProperty("db.maxPoolSize", "8"));
            dbMinIdle = Integer.parseInt(prop.getProperty("db.MinIdle", "80000"));
        }
        catch (NullPointerException e){
            logger.error("property not found! :{}", e.getMessage());
        }
    }
    public static AppConfig loadConfigFromPath(String configPath) throws IOException {
        Properties p = new Properties();
        logger.info("Load config from custom path");
        try(InputStream is = new FileInputStream(configPath)){
            p.load(is);
            if(p.isEmpty()){
                logger.warn("Empty config file! Load default config.");
                LoadDefaultConfig(p);
            }
            try(OutputStream os = new FileOutputStream(configPath)){
                p.store(os,"");
            }
            return new AppConfig(p);
        }
        catch (Exception e){
            logger.error("File not found! :{}", e.getMessage());
            File propFile = new File(configPath);
            propFile.createNewFile();
            try(OutputStream os = new FileOutputStream(propFile)){
                LoadDefaultConfig(p);
                p.store(os,"");
                return new AppConfig(p);
            }
            catch (FileNotFoundException ex) {
                logger.error("Non-writeable file! :{}", e.getMessage());
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
    public static AppConfig loadConfigFromProperty(Properties properties) {
        logger.info("Load config from custom properties");
        if(properties.isEmpty()){
            LoadDefaultConfig(properties);
        }
        return new AppConfig(properties);
    }
    private static void LoadDefaultConfig(Properties prop){
        prop.put("port",8080);
        prop.put("min_thread",1);
        prop.put("max_thread",4);
        prop.put("timeout",30000);

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
