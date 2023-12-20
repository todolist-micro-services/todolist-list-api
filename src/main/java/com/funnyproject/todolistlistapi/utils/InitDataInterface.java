package com.funnyproject.todolistlistapi.utils;

import todolist.database.DataInterface;
import todolist.database.mysql.Mysql;

public class InitDataInterface {

    public static DataInterface initDataInterface(String dbUrl, String dbUsername, String dbPassword) {
        return new Mysql(dbUrl, dbUsername, dbPassword);
    }
}
