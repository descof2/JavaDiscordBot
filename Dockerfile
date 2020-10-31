FROM maven:3.6.3-jdk-14
 
# copy the source tree and the pom.xml to our new container
COPY ./app
 
# package our application code
RUN mvn clean package
 
# set the startup command to execute the jar
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
