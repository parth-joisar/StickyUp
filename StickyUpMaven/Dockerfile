FROM maven:3.9.16-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress -DskipTests clean package

FROM tomcat:10.1.60-jre17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/StickyUp.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]