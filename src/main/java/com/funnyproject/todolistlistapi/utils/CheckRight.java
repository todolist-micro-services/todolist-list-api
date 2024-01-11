package com.funnyproject.todolistlistapi.utils;

import todolist.database.DataInterface;
import todolist.database.dataType.User;

import java.util.List;

public class CheckRight {
    public static boolean isLinkToProjectFromId(int userId, int projectId, DataInterface dataInterface) {
        List<User> users = dataInterface.retrieveAllUserLinkToProject(projectId);

        for (int  i = 0; i != users.size(); ++i)
            if (users.get(i).userId == userId)
                return true;
        return false;
    }

    public static boolean isLinkToProjectFomToken(String userToken, int projectId, DataInterface dataInterface) {
        User user = dataInterface.retrieveUserFromToken(userToken);
        List<User> users = dataInterface.retrieveAllUserLinkToProject(projectId);

        for (int  i = 0; i != users.size(); ++i)
            if (users.get(i).userId == user.userId)
                return true;
        return false;
    }
}
