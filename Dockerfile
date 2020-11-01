FROM maven:3.6.3-jdk-14
 
COPY . /app
WORKDIR /app

RUN mvn clean package
 
CMD ["java", "-jar", "/app/target/JavaDiscordBot-1.0-SNAPSHOT-jar-with-dependencies.jar"]
