package ru.filden;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.filden.Endpoints.Endpoints;
import ru.filden.dao.*;
import ru.filden.db.dbConnection;
import ru.filden.db.dbInit;
import ru.filden.utils.AppConfig;
import spark.Spark;

import java.io.*;

import java.util.Properties;


public class App {
    static Properties properties;
    static File propFile;
    static Logger logger;
    static AppConfig config;
    static dbConnection db;
    static DutyHistoryDAO historyDAO;
    static GroupDAO groupDAO;
    static RoleDAO roleDAO;
    static StudentDAO studentDAO;
    static TeacherDAO teacherDAO;
    static TeacherGroupDAO teacherGroupDAO;
    static UserDAO userDAO;
    static Endpoints endpoints;
    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(App.class);
        logger.info("Server init...");


        if(args.length!=0){
            if(args[0].contains(".properties")){
                try {
                    config = AppConfig.loadConfigFromPath(args[0]);
                }
                catch (IOException e){
                    logger.error("{}", e.getMessage());
                }
            }
            else logger.warn("Unknows arguments:{}", (Object) args);
        }
        else{
            try {
                File configFile = new File("app.config");
                if (!configFile.exists()){
                    configFile.createNewFile();
                }
                config = AppConfig.loadConfigFromPath(configFile.getPath());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        try{
            db = dbConnection.getInstance(config.hikariConfig());

            dbInit.Init(db);

            historyDAO = new DutyHistoryDAO(db);
            groupDAO = new GroupDAO(db);
            roleDAO = new RoleDAO(db);
            studentDAO = new StudentDAO(db);
            teacherDAO = new TeacherDAO(db);
            teacherGroupDAO = new TeacherGroupDAO(db);
            userDAO = new UserDAO(db);

            endpoints = new Endpoints(roleDAO, userDAO, studentDAO, groupDAO, teacherDAO, historyDAO, teacherGroupDAO);


            Spark.port(config.port);
            Spark.threadPool(config.max_thread,config.min_thread, config.timeout);
            endpoints.registerEndpoints();
            logger.info("Server started : port:{}, ip:{}",config.port,"0.0.0.0");
            Spark.init();
        }
        catch (Exception e){
            logger.error("Unexcepted error!!! {}",e.getMessage());
        }
    }
}
