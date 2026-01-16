package co.empresa.qa.automation.infrastructure.hooks;

import co.empresa.qa.automation.infrastructure.DriverManager;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Hooks de JUnit 5 para gestión automática del driver y captura de evidencias.
 * Inicializa y cierra el navegador en cada prueba, y adjunta screenshots y fuente de página
 * a los reportes de Allure en caso de éxito o fallo.
 *
 * @author David
 */

public class DriverHooks implements BeforeEachCallback, AfterEachCallback, TestWatcher {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        DriverManager.initDriver();
        DriverManager.openBaseUrl();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        DriverManager.quitDriver();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        attachScreenshot("Resultado exitoso");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        attachScreenshot("Fallo detectado");
        attachPageSource();
    }

    @Attachment(value = "{name}", type = "image/png")
    public byte[] attachScreenshot(String name) {
        try {
            return ((TakesScreenshot) webdriver().object()).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo tomar screenshot: " + e.getMessage());
            return new byte[0];
        }
    }

    @Attachment(value = "Page source", type = "text/html")
    public byte[] attachPageSource() {
        try {
            return webdriver().object().getPageSource().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo obtener page source: " + e.getMessage());
            return new byte[0];
        }
    }
}