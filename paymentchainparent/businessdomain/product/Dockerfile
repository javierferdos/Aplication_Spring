# syntax=docker/dockerfile:1

# Usar Temurin Java 21 (Alpine)
FROM eclipse-temurin:21-jre-alpine

# Argumento para pasar el JAR en el build (por ejemplo: --build-arg JAR_FILE=target/customer-0.0.1-SNAPSHOT.jar)
ARG JAR_FILE=app.jar
ENV APP_JAR=/tmp/app.jar
ENV APP_HOME=/tmp

WORKDIR ${APP_HOME}

# Crear grupo y usuario (Alpine: addgroup / adduser)
# -G devopsc asigna al grupo creado
# -D crea usuario sin password interactivo
# Opcional: fija UID/GID si lo deseas (ej: -g 1028 en addgroup y -u 1028 en adduser)
RUN addgroup -S devopsc \
 && adduser -D -G devopsc admin \
 && mkdir -p ${APP_HOME} \
 && chown -R admin:devopsc ${APP_HOME}

# Montaje temporal (opcional pero común para Spring boot temp files)
VOLUME /tmp

# Copiar el jar pasado por build-arg
COPY ${JAR_FILE} ${APP_JAR}

# Ajustar permisos y propiedad para el usuario no-root
RUN chmod 640 ${APP_JAR} \
 && chown admin:devopsc ${APP_JAR}

# Exponer puerto por convención (ajusta si tu app usa otro)
EXPOSE 8080

# Ejecutar como usuario no-root
USER admin

# JVM options por defecto (puedes sobreescribir en docker run con -e JAVA_OPTS=...)
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/urandom"

# Entry point: expandir JAVA_OPTS correctamente y ejecutar el jar
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar ${APP_JAR}"]
