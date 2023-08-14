FROM amazoncorretto:17-alpine3.18

ENV APP_NAME rabbit-mq

COPY ./target/${APP_NAME}*.jar /app/${APP_NAME}.jar
COPY ./docker-entrypoint.sh /app/docker-entrypoint.sh

WORKDIR /app

VOLUME /app/config

ENTRYPOINT [ "./docker-entrypoint.sh" ]
