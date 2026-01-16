package co.empresa.qa.automation.tests;

import co.empresa.qa.automation.config.ConfigManager;
import co.empresa.qa.automation.flows.AffiliateFlow;
import co.empresa.qa.automation.flows.LoginFlow;
import co.empresa.qa.automation.infrastructure.hooks.DriverHooks;
import co.empresa.qa.automation.models.AffiliateData;
import co.empresa.qa.automation.models.AffiliateDataGenerator;
import co.empresa.qa.automation.models.Credentials;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de creación de afiliado: realiza login, genera datos aleatorios válidos,
 * ejecuta el flujo completo de alta y valida que el afiliado aparezca en la lista.
 * Usa hooks para gestión del driver y se integra con Allure para trazabilidad.
 *
 * @author David
 */

@Epic("Gestión de Afiliados")
@Feature("Alta Individual")
@ExtendWith(DriverHooks.class)
public class AffiliateCreationTest {

    private final LoginFlow loginFlow = new LoginFlow();
    private final AffiliateFlow affiliateFlow = new AffiliateFlow();

    @Test
    @Story("Alta de afiliado con datos válidos")
    @DisplayName("✅ Crear afiliado individual con datos aleatorios")
    @Severity(SeverityLevel.CRITICAL)
    void shouldCreateAffiliateWithValidData() {
        // 1. Login
        loginFlow.login(new Credentials(
                ConfigManager.getUsername(),
                ConfigManager.getPassword()
        ));
        assertTrue(loginFlow.isLoginSuccessful(), "Login debe ser exitoso");

        // 2. Crear afiliado
        AffiliateData affiliate = AffiliateDataGenerator.generateRandomAffiliate();
        affiliateFlow.createAffiliate(affiliate);

        // 3. Validar creación REAL (mensaje + lista)
        assertTrue(
                affiliateFlow.isAffiliateCreated(affiliate),
                "El afiliado debe aparecer en la lista tras creación"
        );
    }
}