#!/bin/sh

exec java ${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom -jar ${APP_NAME}.jar --spring.config.name=application