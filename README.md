![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

![pipeline](https://gitlab.com/todolist-micro-services/todolist-event-api/badges/main/pipeline.svg)

# todolist-event-api

api to handle events

Java = 21

Gradle = 8.5.0

## Routes

```
----------------------------------

Header: {
    Authorization: Bearer + token
}

----------------------------------

- GET {api_url}/events/all/{projectId}      -> retrieve all user's event
- GET {api_url}/events/{eventId}            -> retrieve one user event
- GET {api_url}/events/users/{eventId}      -> retrieve all users link to an event
- POST {api_url}/events/create              -> retrieve one user event
- UPDATE {api_url}/events/update            -> update event
- DELETE {api_url}/events/delete/{eventId}  -> delete event
```

[API documentation](https://area-api.postman.co/workspace/Pad'workplace~c06a04b9-d1ce-4a4d-8dc0-20c453ca7fae/api/ce31f1a3-1513-4d82-8868-239cb39c227a?action=share&creator=15037258)

## build

```bash
./gradlew build # --info

gradle build
```

## config

add ```application.properties``` in ```src/main/resources``` with this content:

```properties
EVENT_API_PORT=
DB_URL=
DB_USERNAME=
DB_PASSWORD=
```

## run

```bash
./gradlew bootRun # -> curl localhost:{EVENT_API_PORT}
```

## test

```bash
./gradlew test # --info
```
