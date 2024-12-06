FROM eclipse-temurin:21-jre-alpine as builder

ARG JAR_FILE=application/target/application-*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar  application.jar extract --layers --launcher --destination /application

FROM eclipse-temurin:21-jre-alpine as runtime

ENV JDK_JAVA_OPTIONS="-XX:+UseParallelGC -XX:ParallelGCThreads=2 -Xms384m -Xmx512m -XX:MaxMetaspaceSize=192M -XX:MetaspaceSize=96M"

# Add IESE cert to trust store
ARG CERT_FILE=oneviewmed-iese-fhg-de.der
COPY ${CERT_FILE} iese-cert.der
RUN keytool -import -cacerts -alias iese-cert -noprompt -file iese-cert.der
# Create user and group for app
RUN addgroup -S backend && adduser -S backend -G backend --home /home/backend
USER backend:backend

# Create directories and copy stuff from builder
RUN mkdir -p /home/backend/app/unpackaged \
    && chown -R backend:backend /home/backend/app

WORKDIR /home/backend/app/unpackaged

COPY --from=builder --chown=backend:backend /application/dependencies/ ./
COPY --from=builder --chown=backend:backend /application/spring-boot-loader/ ./
COPY --from=builder --chown=backend:backend /application/snapshot-dependencies/ ./
COPY --from=builder --chown=backend:backend /application/application/ ./

WORKDIR /home/backend/app

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s CMD wget -q -T 5 --spider http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-server", "-cp", "./unpackaged/", "org.springframework.boot.loader.launch.JarLauncher"]
