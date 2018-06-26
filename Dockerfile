FROM openjdk:8-jdk-alpine
MAINTAINER Dovydas Venckus "dovydas.venckus@live.com"
ENV APP_ROOT=/home/time-tracker \
    APP_NAME=time-tracker-0.0.1-SNAPSHOT.jar \
    ACTIVE_PROFILES=default

WORKDIR $APP_ROOT
COPY [".", "${APP_ROOT}/source/"]
RUN cd source && \
    ./gradlew :api:build && \
    cp api/build/libs/$APP_NAME .. && \
    cd .. && \
    rm -r source && \
    rm -r /root/.gradle && \
    rm -r /var/cache/apk
USER nobody
ENTRYPOINT ["/bin/sh", "-c", \
 "java -Dspring.profiles.active=${ACTIVE_PROFILES} -jar $APP_NAME"]
EXPOSE 8080