# time-tracker-api
    
[![Run Status](https://api.shippable.com/projects/59fe1ab0e07b7707001c66e3/badge?branch=master)](https://app.shippable.com/github/dovydasvenckus/time-tracker-api/dashboard) [![Known Vulnerabilities](https://snyk.io//test/github/simple-time-tracker/time-tracker-api/badge.svg?targetFile=build.gradle)](https://snyk.io//test/github/simple-time-tracker/time-tracker-api?targetFile=build.gradle)  
## Description
Simple time tracking application. Not ready for
production

## Missing parts
  * Authorization
  * Many more
  
## Build api

    ./gradlew build

## Running app locally
### Start DB container
Launch postgres DB inside container

    cd local-db
    docker-compose up -d

### Start application
There are few different ways to start application

#### Start JAR from shell    
Launch procedure is same as for other jars

    java -Dspring.profiles.active=dev -jar time-tracker-api-0.0.1-SNAPSHOT.jar
    
#### From Intellij
Add VM argument `-Dspring.profiles.active=dev` for TimeTrackerApplication class

#### Docker
Launch API container:
  
    docker run -it -e ACTIVE_PROFILES=dev -p 8080:8080 --name time-tracker-api dovydasvenckus/time-tracker

#### Production profile (with real DB)    
    docker run -it --rm \
    -e ACTIVE_PROFILES=prod \
    -e DB_URL="jdbc:postgresql://jdbcUrlToPosgreSQLDB" \
    -e DB_USERNAME="myDBUserName" \
    -e DB_PASSWORD="mySecretPassword" \
    -e DB_DIALECT="org.hibernate.dialect.PostgreSQLDialect" \
    -e CORS_ALLOW='' \
     dovydasvenckus/time-tracker-api
     
## Launching API + Frontend app
You can use docker-compose to launch whole stack.
 Docker compose file is located in [time-tracker-env](https://github.com/dovydasvenckus/time-tracker-env)
 repository.
