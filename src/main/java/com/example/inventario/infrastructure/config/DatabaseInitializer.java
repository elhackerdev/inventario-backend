package com.example.inventario.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Componente encargado de inicializar la base de datos al arrancar la aplicación.
 * - Verifica si la base de datos y el usuario existen, y los crea si es necesario.
 * - Ejecuta los scripts de esquema y procedimientos SQL.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    @Value("${app.datasource.jdbc-url}")
    private String appDbUrl;

    @Value("${app.datasource.username}")
    private String appDbUser;

    @Value("${app.datasource.password}")
    private String appDbPassword;

    @Autowired
    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Método que se ejecuta automáticamente al iniciar la aplicación.
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            createDatabaseIfNotExists(); // Paso 1: crear BD y usuario si no existen
            initializeSchema();          // Paso 2: ejecutar scripts SQL
        } catch (Exception e) {
            System.err.println("Error durante la inicialización de la base de datos:");
            e.printStackTrace();
        }
    }

    /**
     * Crea la base de datos y el usuario si no existen.
     * Se conecta temporalmente a la base "postgres" como administrador.
     */
    private void createDatabaseIfNotExists() throws Exception {
        // Configura un DataSource temporal para conectarse a la base "postgres"
        HikariDataSource adminDataSource = new HikariDataSource();
        adminDataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres"); // Puerto personalizado
        adminDataSource.setUsername("remoto"); // Usuario administrador (ajustar si es necesario)
        adminDataSource.setPassword("pruebatecnica123*");

        try (Connection conn = adminDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // Verificar si la base de datos "inventario_db" ya existe
            boolean dbExists = false;
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = 'inventario_db'")) {
                dbExists = rs.next();
            }

            // Si no existe, crear la base de datos
            if (!dbExists) {
                stmt.executeUpdate("CREATE DATABASE inventario_db");
                System.out.println("Database 'inventario_db' created successfully");
            }

            // Verificar si el usuario ya existe
            boolean userExists = false;
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_roles WHERE rolname = '" + appDbUser + "'")) {
                userExists = rs.next();
            }

            // Si no existe, crear el usuario con la contraseña indicada
            if (!userExists) {
                stmt.executeUpdate("CREATE USER " + appDbUser + " WITH PASSWORD '" + appDbPassword + "'");
                System.out.println("User '" + appDbUser + "' created successfully");
            }

            // Conceder permisos al usuario sobre la base de datos
            stmt.executeUpdate("GRANT ALL PRIVILEGES ON DATABASE inventario_db TO " + appDbUser);

        } finally {
            // Cerrar el DataSource temporal
            adminDataSource.close();
        }
    }

    /**
     * Ejecuta los scripts SQL de inicialización del esquema y procedimientos almacenados.
     */
    private void initializeSchema() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            // Ejecutar el esquema de base de datos
           // ScriptUtils.executeSqlScript(conn, new ClassPathResource("schema.sql"));

            // Ejecutar los procedimientos almacenados
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("procedures.sql"));
        }
    }
}
