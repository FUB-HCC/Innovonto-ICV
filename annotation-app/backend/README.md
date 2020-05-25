# Interactive Concept Validation Annotation App

Goal: Get Batches of Idea Texts Annotated by crowdsourcing workers.

## Building
### Building the standalone jar:

    ./gradlew bootJar
   
Creates a boot jar in /build/libs/icv-annotation-app-backend-0.0.1-SNAPSHOT.jar

### Building a docker container
    
    ./gradlew jibDockerBuild
    
    
## Debugging
### Run local

Running the standalone server created by the gradle bootJar task:
        
        java -jar icv-annotation-app-backend-0.0.1-SNAPSHOT.jar
        
        curl "http://localhost:9002/api"        

For debugging, there is a swagger ui running at:

        http://localhost:9002/swagger-ui.html
        
Furthermore, there is a h2 console running at:

    http://localhost:9002/h2-console/
   
The Debug Config for it is:

    Driver Class: org.h2.Driver
    JDBC URL: jdbc:h2:mem:testdb
    User Name: sa
    Password: <empty>


## Deployment

    ./gradlew jib
