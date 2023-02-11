FROM openjdk:17-slim-buster AS build-jar
ENV BUILD_ROOT=/home/build-space
WORKDIR $BUILD_ROOT
COPY [".", "${BUILD_ROOT}"]
RUN ./gradlew assemble

FROM openjdk:17-slim-buster
MAINTAINER Dovydas Venckus "dovydas.venckus@live.com"
ENV APP_ROOT=/home/time-tracker \
    APP_NAME=time-tracker-api-0.0.1-SNAPSHOT.jar \
    ACTIVE_PROFILES=default
    ROOT_CERT=rootCa.pem
WORKDIR $APP_ROOT
COPY --from=build-jar /home/build-space/build/libs/${APP_NAME} .
COPY $ROOT_CERT /$APP_ROOT
RUN id \
    cd $APP_ROOT \
    && keytool -importcert -file $ROOT_CERT -cacerts -alias rootCert  -storepass changeit --noprompt

USER nobody
ENTRYPOINT ["/bin/sh", "-c", \
 "java -Dspring.profiles.active=${ACTIVE_PROFILES} -jar $APP_NAME"]
EXPOSE 8080
