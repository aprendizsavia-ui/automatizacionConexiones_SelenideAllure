package co.empresa.qa.automation.flows;

import co.empresa.qa.automation.models.Credentials;
import co.empresa.qa.automation.pages.login.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flujo de inicio de sesión para la aplicación Conexiones.
 * Coordina la entrada de credenciales y delega las validaciones (éxito o fallo) a la página de login.
 *
 * @author David
 */
public class LoginFlow {
    private static final Logger log = LoggerFactory.getLogger(LoginFlow.class);
    private final LoginPage loginPage = new LoginPage();

    public void login(Credentials credentials) {
        log.info("🔐 Iniciando sesión en Savia con usuario: {}", credentials.username());
        loginPage
                .enterCredentials(credentials.username(), credentials.password())
                .clickLogin();
    }

    public boolean isLoginSuccessful() {
        return loginPage.isLoginSuccessful();
    }

    public boolean isLoginFailed() {
        return loginPage.hasErrorMessage();
    }
}