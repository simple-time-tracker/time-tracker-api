# time-tracker-web

[![Run Status](https://api.shippable.com/projects/59fe1ab0e07b7707001c66e3/badge?branch=master)](https://app.shippable.com/github/dovydasvenckus/time-tracker)
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

To build app for first time you need to fetch JSPM packages.
Change directory to web module and run this command.
    
    jspm install
    
Run from root directory

    ./gradlew buildWithWeb
     
     
Build jar is stored in.

    time-tracker/api/build/libs/time-tracker-0.0.1-SNAPSHOT.jar


## Running app

Launch procedure is same as for other jars

    java -jar time-tracker-0.0.1-SNAPSHOT.jar
    
