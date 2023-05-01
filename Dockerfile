FROM openjdk:17.0.1-jdk
ARG JAR_FILE=build/libs/expense-telegram-bot.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]