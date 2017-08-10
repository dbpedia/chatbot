FROM maven:3.3-jdk-8
RUN mkdir -p /app
WORKDIR /app
COPY repo /app/repo
COPY pom.xml /app
COPY . /app
RUN mvn clean install

FROM java:8
WORKDIR /app
COPY --from=0 /app .
CMD java $JAVA_OPTS -Dserver.port=$PORT -jar target/*.jar
