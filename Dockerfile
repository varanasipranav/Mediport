# ---- build ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline
COPY src src
RUN ./mvnw -q -B -DskipTests package && cp target/webapp-*.jar /app.jar

# ---- run ----
FROM eclipse-temurin:25-jre
RUN useradd --system --uid 10001 --create-home mediport
WORKDIR /app
COPY --from=build /app.jar /app/app.jar
USER mediport
ENV TZ=Asia/Kolkata \
    JAVA_OPTS="-Xms128m -Xmx450m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://localhost:${PORT:-8081}/health || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
