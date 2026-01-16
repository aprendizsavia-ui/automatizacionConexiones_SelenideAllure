package co.empresa.qa.automation.infrastructure;

import co.empresa.qa.automation.config.ConfigManager;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestor centralizado de la configuración e inicialización del navegador mediante Selenide.
 * Aplica ajustes desde {@code ConfigManager} (tiempos, navegador, modo headless, etc.)
 * y expone métodos estáticos para abrir y cerrar el driver sin exponer el WebDriver directamente.
 *
 * @author David
 */

public class DriverManager {
    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

    private DriverManager() {
        // Utilidad: no instanciable
    }

    public static void initDriver() {
        Configuration.browser = ConfigManager.getBrowser();
        Configuration.browserSize = "maximize";
        Configuration.timeout = ConfigManager.getElementTimeout() * 1000;
        Configuration.pageLoadTimeout = ConfigManager.getPageTimeout() * 1000;
        Configuration.savePageSource = false; // Reduce ruido en allure
        Configuration.screenshots = true;
        Configuration.savePageSource = false;
        Configuration.reportsFolder = "target/allure-results";
        Configuration.baseUrl = ConfigManager.getBaseUrl(); // para usar Selenide.open() sin URL
        Configuration.reportsFolder = "target/allure-results";
        Configuration.fastSetValue = true; // mejora velocidad en inputs

        // Headless si se configura así (CI)
        if ("headless".equalsIgnoreCase(ConfigManager.getBrowser())) {
            Configuration.headless = true;
        }

        log.info("🔧 WebDriver inicializado: browser={}, headless={}",
                ConfigManager.getBrowser(), Configuration.headless);
    }

    public static void openBaseUrl() {
        log.info("🌐 Abriendo URL: {}", ConfigManager.getBaseUrl());
        Selenide.open(ConfigManager.getBaseUrl());
    }

    public static void quitDriver() {
        log.info("🛑 Cerrando navegador");
        Selenide.closeWebDriver();
    }
}