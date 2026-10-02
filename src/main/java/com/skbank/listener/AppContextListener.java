package com.skbank.listener;

import com.skbank.dao.SystemSettingsDAO;
import com.skbank.dao.impl.SystemSettingsDAOImpl;
import com.skbank.util.SystemSettingsUtil;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("SK BANK OF BAREILLY Application Starting Up...");
        try {
            SystemSettingsDAO dao = new SystemSettingsDAOImpl();
            Map<String, String> dbSettings = dao.loadAllAsMap();
            if (!dbSettings.isEmpty()) {
                SystemSettingsUtil.updateSettings(dbSettings);
                LOGGER.info("Loaded " + dbSettings.size() + " system settings from database.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load system settings on startup, using defaults", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("SK BANK OF BAREILLY Application Shutting Down.");
    }
}
