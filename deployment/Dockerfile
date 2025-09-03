FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY applications/app-service/build/libs/autenticacion.jar ./autenticacion.jar
ENTRYPOINT [ "java", "-jar", "autenticacion.jar" ]
