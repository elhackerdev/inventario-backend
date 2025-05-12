FROM openjdk:17-jdk-slim

# Instalar PostgreSQL
RUN apt-get update && apt-get install -y postgresql postgresql-contrib

# Configurar PostgreSQL
USER postgres
RUN mkdir -p /var/run/postgresql && \
    chown -R postgres:postgres /var/run/postgresql && \
    /etc/init.d/postgresql start && \
    psql --command "CREATE USER remoto WITH PASSWORD 'pruebatecnica123*';" && \
    psql --command "CREATE DATABASE inventario_db WITH OWNER remoto ENCODING 'UTF8';" && \
    psql --command "GRANT ALL PRIVILEGES ON DATABASE inventario_db TO remoto;"

# Cambiar de vuelta al usuario root para la aplicación
USER root

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el JAR generado
COPY target/inventario-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto
EXPOSE 8080

# Script de inicio personalizado
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Comando para ejecutar
CMD ["/start.sh"]