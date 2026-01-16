package co.empresa.qa.automation.tests;

import co.empresa.qa.automation.config.ConfigManager;
import co.empresa.qa.automation.flows.LoginFlow;
import co.empresa.qa.automation.infrastructure.hooks.DriverHooks;
import co.empresa.qa.automation.models.Credentials;
import co.empresa.qa.automation.steps.AllureSteps;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de autenticación en Savia: valida login exitoso con credenciales correctas
 * y rechazo con credenciales inválidas, verificando redirección al home o mensaje de error.
 * Integrada con Allure y gestionada mediante hooks del driver.
 *
 * @author David
 */

@Epic("Autenticación")
@Feature("Acceso a Savia")
@ExtendWith(DriverHooks.class)
public class LoginTest {

    private final LoginFlow loginFlow = new LoginFlow();
    private final AllureSteps steps = new AllureSteps();

    @Test
    @Story("Login exitoso con credenciales válidas")
    @DisplayName("⎆ Login válido en Savia debe redirigir al home (/home.faces)")
    @Description("Verifica que el usuario pueda ingresar al sistema Savia con credenciales correctas")
    @Severity(SeverityLevel.CRITICAL)
    void shouldLoginWithValidCredentials() {
        steps.openLoginPage(); // ← paso manual para inicio

        Credentials validCreds = new Credentials(
                ConfigManager.getUsername(),
                ConfigManager.getPassword()
        );

        loginFlow.login(validCreds);

        steps.validateLoginSuccess(); // ← paso en Allure
        assertTrue(
                loginFlow.isLoginSuccessful(),
                "El login debe ser exitoso: redirigir a /home.faces"
        );
    }

    @Test
    @Story("Login fallido con credenciales inválidas")
    @DisplayName("⎆ Login inválido en Savia debe mostrar mensaje de error")
    @Description("Verifica que Savia rechace credenciales incorrectas y muestre mensaje")
    @Severity(SeverityLevel.NORMAL)
    void shouldShowErrorWithInvalidCredentials() {
        steps.openLoginPage();

        Credentials invalidCreds = new Credentials("usuario_invalido", "pass123");

        loginFlow.login(invalidCreds);

        steps.validateLoginFailure(); // ← paso en Allure
        assertTrue(
                loginFlow.isLoginFailed(),
                "Debe mostrarse mensaje de error con credenciales inválidas"
        );
    }
}