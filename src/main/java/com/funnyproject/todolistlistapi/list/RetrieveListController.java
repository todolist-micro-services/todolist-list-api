package com.funnyproject.todolistlistapi.list;

import com.funnyproject.todolistlistapi.AppConfig;
import com.funnyproject.todolistlistapi.dto.EventDto;
import com.funnyproject.todolistlistapi.dto.UserDto;
import com.funnyproject.todolistlistapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.Event;
import todolist.database.dataType.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/lists", produces = MediaType.APPLICATION_JSON_VALUE)
public class RetrieveListController {

    private final DataInterface dataInterface;

    public RetrieveListController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @GetMapping("/all/{projectId}")
    public ResponseEntity<Object> retrieveAllProjectList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String projectId
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        User databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        try {
            if (isUserLinkToProject(Integer.parseInt(projectId), databaseUser.userId) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"user must be link to project\"}");
            }
            List<todolist.database.dataType.List> dbLists = this.dataInterface.retrieveAllProjectLists(Integer.parseInt(projectId));
            return ResponseEntity.status(HttpStatus.OK).body(dbLists == null ? new ArrayList<>() : dbLists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"projectId must be an integer\"}");
        }
    }

    private static EventDto getEventDto(Event dbEvent) {
        EventDto tmp = new EventDto();
        tmp.setId(dbEvent.eventId);
        tmp.setName(dbEvent.name);
        tmp.setDescription(dbEvent.description);
        tmp.setStartDate(dbEvent.startDateTime.toString());
        tmp.setEndDate(dbEvent.endDateTime.toString());
        tmp.setCreator(String.valueOf(dbEvent.creator.userId));
        tmp.setProject(String.valueOf(dbEvent.project.projectId));
        return tmp;
    }

    @GetMapping("/users/{eventId}")
    public ResponseEntity<Object> retrieveAllUserLinkToList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String eventId
    ) {
        List<UserDto> users = new ArrayList<>();
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        User databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        try {
            List<User> dbUsers = this.dataInterface.retrieveAllUserLinkToList(Integer.parseInt(eventId));
            if (dbUsers != null)
                for (int i = 0; i != dbUsers.size(); ++i)
                    users.add(new UserDto(dbUsers.get(i).userId, dbUsers.get(i).firstname, dbUsers.get(i).lastname, dbUsers.get(i).email));
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"eventId must be an integer\"}");
        }
    }

    private ResponseEntity<Object> isUserLinkToProject(int projectId, int userId) {
        List<User> users = this.dataInterface.retrieveAllUserLinkToProject(projectId);
        for (int i = 0; i != users.size(); ++i)
            if (users.get(i).userId == userId)
                return null;
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User must be link to project\"}");
    }
}
