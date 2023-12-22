package com.funnyproject.todolistlistapi.list;

import com.funnyproject.todolistlistapi.AppConfig;
import com.funnyproject.todolistlistapi.dto.EventDto;
import com.funnyproject.todolistlistapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.Event;
import todolist.database.dataType.Project;
import todolist.database.dataType.User;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/lists", produces = MediaType.APPLICATION_JSON_VALUE)
public class UpdateListController {

    private final DataInterface dataInterface;

    public UpdateListController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateListRequest updateListRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");
        ResponseEntity<Object> checkBodyError = this.checkBody(updateListRequest);
        User databaseUser;

        if (checkBodyError != null)
            return checkBodyError;
        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        final ResponseEntity<Object> responseRight = this.isUserLinkToProject(Integer.parseInt(updateListRequest.getProject()), databaseUser.userId);
        if (responseRight != null)
            return responseRight;
        return getNewEvent(updateListRequest);
    }

    private ResponseEntity<Object> getNewEvent(UpdateListRequest updateListRequest) {
        final User user = new User(Integer.parseInt(updateListRequest.getCreator()), "", "", "", "");
        final Project project = new Project(Integer.parseInt(updateListRequest.getProject()), "", "", LocalDateTime.now(), user);
        final todolist.database.dataType.List parent = updateListRequest.getParent().equals("null") ? null : new todolist.database.dataType.List(Integer.parseInt(updateListRequest.getParent()), "", "", null, null, null);
        final todolist.database.dataType.List list = new todolist.database.dataType.List(Integer.parseInt(updateListRequest.getId()), updateListRequest.getName(), updateListRequest.getDescription(), parent, user, project);
        final String dbResponse = this.dataInterface.updateProjectList(list);
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(list);
    }

    private ResponseEntity<Object> isUserLinkToProject(int projectId, int userId) {
        List<User> users = this.dataInterface.retrieveAllUserLinkToProject(projectId);
        for (int i = 0; i != users.size(); ++i)
            if (users.get(i).userId == userId)
                return null;
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User must be link to project\"}");
    }

    private ResponseEntity<Object> returnNewUser(final String token) {
        EventDto user = new EventDto();
        User databaseUser = this.dataInterface.retrieveUserFromToken(token);

        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private ResponseEntity<Object> checkBody(UpdateListRequest updateListRequest) {
        try {
            this.validateUpdateRequest(updateListRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"error\": \"Missing parameters, needs : firstname, lastname\"}");
        }
        return null;
    }

    private void validateUpdateRequest(UpdateListRequest updateListRequest) {
        if (updateListRequest == null ||
                updateListRequest.getId() == null ||
                updateListRequest.getName() == null ||
                updateListRequest.getDescription() == null ||
                updateListRequest.getParent() == null ||
                updateListRequest.getCreator() == null ||
                updateListRequest.getProject() == null)
            throw new IllegalArgumentException("Missing required parameters");
    }

}
