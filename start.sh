#!/bin/bash

# Iniciar PostgreSQL en segundo plano
service postgresql start

# Esperar a que PostgreSQL esté listo
until pg_isready -h localhost -p 5432 -U remote -d springdb; do
  echo "Esperando a que PostgreSQL esté listo..."
  sleep 1
done

# Ejecutar la aplicación Spring Boot
exec java -jar app.jar