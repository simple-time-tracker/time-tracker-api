# time-tracker-web

[![Run Status](https://api.shippable.com/projects/59fe1ab0e07b7707001c66e3/badge?branch=master)]() 
## Description
Simple time tracking application. Not ready for
production. I have started this project just to try out
Aurelia web framework.

You can build api jar or build jar that includes api and web.
To build single jar follow directions bellow.

## Missing parts
  * Authorization
  * Ability to add new projects via web
  * Reports
  * Many more
  
## Build api

    ./gradlew build

## Build jar with api and web
Run from root directory

    ./gradlew buildWithWeb
     
     
Build jar is stored in.

    time-tracker/api/build/libs/time-tracker-0.0.1-SNAPSHOT.jar


## Running app

Launch procedure is same as for other jars

    java -jar time-tracker-0.0.1-SNAPSHOT.jar
    

## Run in docker

### Dev profile (with in memory DB)
    docker run -it -e ACTIVE_PROFILES=dev -p 8080:8080 --name time-tracker-api dovydasvenckus/time-tracker

### Production profile (with real DB)    
    docker run -it --rm \
    -e ACTIVE_PROFILES=prod \
    -e DB_URL="jdbc:postgresql://jdbcUrlToPosgreSQLDB" \
    -e DB_USERNAME="myDBUserName" \
    -e DB_PASSWORD="mySecretPassword" \
    -e DB_DIALECT="org.hibernate.dialect.PostgreSQLDialect" \
    -e CORS_ALLOW='' \
     dovydasvenckus/time-tracker-api
     
### Launching API + Frontend app
You can use docker-compose to launch whole stack.
 Docker compose file is located in [time-tracker-env](https://github.com/dovydasvenckus/time-tracker-env)
 repository.
