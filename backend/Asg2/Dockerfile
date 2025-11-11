# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper + pom first (better caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests dependency:resolve dependency:resolve-plugins

# Copy sources and build a Boot (fat) jar
COPY src ./src
#force maven to refresh dependencies 
#RUN ./mvnw dependency:purge-local-repository -DactTransitively=false -DreResolve=false  
RUN ./mvnw -q -DskipTests package spring-boot:repackage


# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
