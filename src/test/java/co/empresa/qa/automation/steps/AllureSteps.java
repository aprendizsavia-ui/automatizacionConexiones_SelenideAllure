package co.empresa.qa.automation.steps;

import io.qameta.allure.Step;

/**
 * Agrupa pasos reutilizables anotados con {@code @Step} para integración en reportes de Allure.
 * Cada método representa una acción clara y trazable durante la ejecución de pruebas.
 *
 * @author David
 */

public class AllureSteps {

    @Step("Abrir página de login")
    public void openLoginPage() {}

    @Step("Ingresar credenciales: usuario = {0}")
    public void enterCredentials(String username) {}

    @Step("Hacer clic en 'Ingresar'")
    public void clickLoginButton() {}

    @Step("Validar login exitoso: redirección a /home.faces")
    public void validateLoginSuccess() {}

    @Step("Validar mensaje de error: 'Usuario y/o Contraseña inválido'")
    public void validateLoginFailure() {}
}