package co.empresa.qa.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**

 Gestor de configuración que carga propiedades desde {@code config.properties} al inicio.
 Proporciona acceso estático a parámetros comunes como URL base, credenciales, navegador y timeouts.
 Lanza excepciones si el archivo de configuración no está presente en el classpath.
 @author David
 */
public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("❌ Archivo config.properties no encontrado en classpath");
            }
            properties.load(input);
            log.info("✅ Configuración cargada: baseUrl={}, browser={}",
                    getBaseUrl(), getBrowser());
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar config.properties", e);
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("baseUrl");
    }

    public static String getUsername() {
        return properties.getProperty("username");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }

    public static String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public static long getElementTimeout() {
        return Long.parseLong(properties.getProperty("timeout.element", "10"));
    }

    public static long getPageTimeout() {
        return Long.parseLong(properties.getProperty("timeout.page", "30"));
    }
}