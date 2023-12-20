package com.funnyproject.todolistlistapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    private final String serverPort;
    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;

    @Autowired
    public AppConfig(
            @Value("${server.port}") String serverPort,
            @Value("${db.url}") String dbUrl,
            @Value("${db.username}") String dbUserName,
            @Value("${db.password}") String dbPassword
    ) {
        this.serverPort = serverPort;
        this.dbUrl = dbUrl;
        this.dbPassword = dbPassword;
        this.dbUserName = dbUserName;
    }


    public String getServerPort() {
        return this.serverPort;
    }

    public String getDbUrl() {
        return this.dbUrl;
    }

    public String getDbUserName() {
        return this.dbUserName;
    }

    public String getDbPassword() {
        return this.dbPassword;
    }

}
