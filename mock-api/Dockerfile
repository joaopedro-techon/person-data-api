# =========================
# Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /app

# Copia apenas o pom para cache
# O contexto do build é a raiz, então copiamos de ./mock-api/
COPY ./mock-api/pom.xml .
RUN mvn dependency:go-offline -B

# Copia código
COPY ./mock-api/src ./src
RUN mvn clean package -DskipTests

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:11-jre
WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Copia o JAR da aplicação
COPY --from=build /app/target/*.jar app.jar

# Copia o OpenTelemetry Java Agent da raiz do projeto
# O contexto do build agora é a raiz (.), então podemos copiar diretamente
ARG OTEL_AGENT_PATH=./opentelemetry-javaagent.jar
COPY ${OTEL_AGENT_PATH} /app/opentelemetry-javaagent.jar

# JVM Options
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

EXPOSE 8089

HEALTHCHECK --interval=10s --timeout=5s --retries=3 --start-period=30s \
  CMD curl -f http://localhost:8089/actuator/health || exit 1

# Executa com OpenTelemetry Agent
ENTRYPOINT ["sh", "-c", "java -javaagent:/app/opentelemetry-javaagent.jar $JAVA_OPTS -jar app.jar"]


