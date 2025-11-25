package com.mycompany.loginproject.clases;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.ServiceUnavailableException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener para obtener las credenciales de la DB de un archivo en una ruta en
 * el contenedor
 *
 * @author Isacc-smth
 */
@WebListener
public class ConnectionPoolListener implements ServletContextListener {
    private static final Properties dbProps = new Properties();

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public ConnectionPoolListener() {}

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            dbProps.load(getClass().getResourceAsStream("/db_sia.properties"));

            config.setDriverClassName("org.mariadb.jdbc.Driver");
            String HOST = dbProps.getProperty("sia_host");
            String USER = dbProps.getProperty("sia_user");
            String PASSWORD = dbProps.getProperty("sia_password");

            // config.setJdbcUrl("jdbc:mariadb://" + HOST + "/" + DB_NAME);

            // NOTE: El servidor no soprta ssl. 
            config.setJdbcUrl("jdbc:mariadb://" + HOST + "/ctndb?useSSL=false");
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(20);

            // Capaz se tenga que ajustar, porque a veces hay timeouts.
            config.setIdleTimeout(36_000_000);       
            config.setMaxLifetime(40_000_000);        
            config.setConnectionTimeout(35_000_000); 
            config.setLeakDetectionThreshold(2_000);

            config.setAutoCommit(true);

            ds = new HikariDataSource(config);
            sce.getServletContext().setAttribute("datasource", ds);
        } catch (IOException e) {
            throw new IllegalStateException("Error while getting database credentials", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (ds != null) {
            ds.close();
        }
    }

    public static Connection getCon() throws SQLException {
        return ds.getConnection();
    }
}
