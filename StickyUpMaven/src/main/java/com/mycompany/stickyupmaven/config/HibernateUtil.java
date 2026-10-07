package com.mycompany.stickyupmaven.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration()
                    .configure("hibernate.cfg.xml");

            String databaseUrl = environment("DB_URL");
            String databaseHost = environment("DB_HOST");

            if (databaseUrl != null) {
                configuration.setProperty("hibernate.connection.url", databaseUrl);
            } else if (databaseHost != null) {
                String databasePort = environmentOrDefault("DB_PORT", "3306");
                String databaseName = environmentOrDefault("DB_NAME", "stickyup");
                String jdbcUrl = "jdbc:mysql://" + databaseHost + ":"
                        + databasePort + "/" + databaseName
                        + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                configuration.setProperty("hibernate.connection.url", jdbcUrl);
            }

            setPropertyFromEnvironment(configuration,
                    "hibernate.connection.username", "DB_USER");
            setPropertyFromEnvironment(configuration,
                    "hibernate.connection.password", "DB_PASSWORD");

            if ("production".equalsIgnoreCase(environment("APP_ENV"))) {
                configuration.setProperty("hibernate.show_sql", "false");
            }

            return configuration.buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("Hibernate SessionFactory creation failed.");
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void setPropertyFromEnvironment(
            Configuration configuration, String property, String environmentVariable) {
        String value = environment(environmentVariable);
        if (value != null) {
            configuration.setProperty(property, value);
        }
    }

    private static String environmentOrDefault(String name, String defaultValue) {
        String value = environment(name);
        return value == null ? defaultValue : value;
    }

    private static String environment(String name) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? null : value.trim();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}