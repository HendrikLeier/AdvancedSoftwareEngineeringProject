FROM gradle:7.0.2-jdk11

WORKDIR /code
COPY ./src /code/src
COPY build.gradle /code/
COPY settings.gradle /code/
COPY gradlew /code/
COPY build_and_run.sh /code/

CMD ["sh", "build_and_run.sh"]
