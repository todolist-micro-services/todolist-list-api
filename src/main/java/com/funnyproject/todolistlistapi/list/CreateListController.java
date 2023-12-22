package com.funnyproject.todolistlistapi.list;

import com.funnyproject.todolistlistapi.AppConfig;
import com.funnyproject.todolistlistapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.Event;
import todolist.database.dataType.List;
import todolist.database.dataType.Project;
import todolist.database.dataType.User;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/lists", produces = "application/json")
public class CreateListController {

    private final DataInterface dataInterface;

    public CreateListController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(),
                appConfig.getDbPassword());
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CreateListRequest createListRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(createListRequest);
        if (response != null)
            return response;
        final User user = new User(Integer.parseInt(createListRequest.getCreator()), "", "", "", "");
        final Project project = new Project(Integer.parseInt(createListRequest.getProject()), "", "", LocalDateTime.now(), user);
        final List parent = createListRequest.getParent().equals("null") ? null : new List(Integer.parseInt(createListRequest.getParent()), "", "", null, null, null);
        final List list = new List(0, createListRequest.getName(), createListRequest.getDescription(), parent, user, project);
        final String dbResponse = this.dataInterface.createProjectList(list);
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
        final List createdList = this.dataInterface.retrieveProjectListByName(Integer.parseInt(createListRequest.getProject()), createListRequest.getName());
        if (createdList == null)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        final String formatOutput = String.format("{\"list\": \"%s\"}", String.valueOf(createdList.listId));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(formatOutput);
    }

    private ResponseEntity<Object> checkParameters(CreateListRequest createListRequest) {
        try {
            validateProjectCreationRequest(createListRequest);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\": \"Missing parameters, needs : name, description, parent, creator, project\"}");
        }
        return null;
    }

    private void validateProjectCreationRequest(CreateListRequest createListRequest) {
        if (createListRequest == null ||
                createListRequest.getCreator() == null ||
                createListRequest.getParent() == null ||
                createListRequest.getDescription() == null ||
                createListRequest.getProject() == null ||
                createListRequest.getName() == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }
    }
}
