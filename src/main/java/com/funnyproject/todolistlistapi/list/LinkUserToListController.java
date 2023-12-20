package com.funnyproject.todolistlistapi.list;

import com.funnyproject.todolistlistapi.AppConfig;
import com.funnyproject.todolistlistapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;

@RestController
@RequestMapping(value = "/lists", produces = "application/json")
public class LinkUserToListController {

    private final DataInterface dataInterface;

    public LinkUserToListController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(),
                appConfig.getDbPassword());
    }

    @PostMapping("/link")
    public ResponseEntity<Object> addLinkBetweenUserAndList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody LinkUserToListRequest linkUserToListRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(linkUserToListRequest);
        if (response != null)
            return response;
        final String dbResponse = this.dataInterface.linkUserToList(Integer.parseInt(linkUserToListRequest.getUser()), Integer.parseInt(linkUserToListRequest.getList()));
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"Error\": \"Internal server error\"}");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("{\"Link\": \"Successful\"}");
    }

    @DeleteMapping("/link")
    public ResponseEntity<Object> deleteLinkBetweenUserAndList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody LinkUserToListRequest linkUserToListRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(linkUserToListRequest);
        if (response != null)
            return response;
        final String dbResponse = this.dataInterface.unLinkUserToList(Integer.parseInt(linkUserToListRequest.getUser()), Integer.parseInt(linkUserToListRequest.getList()));
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"Error\": \"Internal server error\"}");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("{\"Unlink\": \"Successful\"}");
    }

    private ResponseEntity<Object> checkParameters(LinkUserToListRequest linkUserToListRequest) {
        try {
            validateProjectCreationRequest(linkUserToListRequest);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\": \"Missing parameters, needs : user and project\"}");
        }
        return null;
    }

    private void validateProjectCreationRequest(LinkUserToListRequest linkUserToListRequest) {
        if (linkUserToListRequest == null ||
                linkUserToListRequest.getList() == null ||
                linkUserToListRequest.getUser() == null)
            throw new IllegalArgumentException("Missing required parameters");
    }
}
