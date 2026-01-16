package co.empresa.qa.automation.pages.login;

import co.empresa.qa.automation.steps.AllureSteps;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.url;
import static java.time.Duration.ofSeconds;

/**
 * Página de inicio de sesión para la aplicación Savia.
 * Gestiona la entrada de credenciales, el clic en el botón de login y la validación
 * del resultado mediante presencia de elementos característicos (mensaje de error o fondo del home).
 * Integra pasos de Allure para trazabilidad.
 *
 * @author David
 */

public class LoginPage {

    private final SelenideElement usernameField = $("#login\\:usuario");
    private final SelenideElement passwordField = $("#login\\:contrasena");
    private final SelenideElement loginButton = $x("//span[text()='Ingresar']/ancestor::button[1]");

    // ✅ Selector basado en el HTML real que me diste
    private final SelenideElement errorGrowl = $x("//p[contains(text(), 'Usuario y/o Contraseña inválido')]");

    // Elemento del dashboard (fondo de home)
    private final SelenideElement homeBackground = $("div[style*='home.png']");

    private final AllureSteps steps = new AllureSteps();

    public LoginPage enterCredentials(String username, String password) {
        steps.enterCredentials(username); // ← paso en Allure
        usernameField.shouldBe(visible).setValue(username);
        passwordField.shouldBe(visible).setValue(password);
        return this;
    }

    public void clickLogin() {
        steps.clickLoginButton(); // ← paso en Allure
        loginButton.shouldBe(visible).click();
    }

    public boolean isLoginSuccessful() {
        // Espera activa hasta 20s por la URL deseada
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 20000) {
            if (url().contains("/home.faces")) {
                return homeBackground.is(visible, Duration.ofSeconds(5));
            }
            Selenide.sleep(500);
        }
        return false;
    }

    /**
     * Valida el mensaje de error real de Savia: "Usuario y/o Contraseña inválido"
     */
    public boolean hasErrorMessage() {
        return errorGrowl.is(visible, ofSeconds(10));
    }

    public String getErrorMessageText() {
        return errorGrowl.text(); // devuelve "Usuario y/o Contraseña inválido"
    }


}