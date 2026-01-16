package co.empresa.qa.automation.flows;

import co.empresa.qa.automation.config.ConfigManager;
import co.empresa.qa.automation.models.AffiliateData;
import co.empresa.qa.automation.pages.aseg_afiliados.AffiliateFormPage;
import co.empresa.qa.automation.pages.aseg_afiliados.AffiliatesListPage;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.open;

/**
 * Orquesta el flujo completo de creación de un afiliado: navega a la lista, abre el formulario,
 * llena los datos, guarda y valida la creación exitosa mediante mensaje de éxito y búsqueda en tabla.
 * Usa Allure Steps para trazabilidad y sigue el patrón Page Object.
 *
 * @author David
 */

public class AffiliateFlow {
    private static final Logger log = LoggerFactory.getLogger(AffiliateFlow.class);
    private static final String AFFILIATES_LIST_PATH = "/aseguramiento/afiliados.faces";
    private final String affiliatesListUrl = ConfigManager.getBaseUrl() + AFFILIATES_LIST_PATH;

    private final AffiliatesListPage listPage = new AffiliatesListPage();
    private final AffiliateFormPage formPage = new AffiliateFormPage();

    @Step("🌐 Navegar a lista de afiliados")
    public void navigateToList() {
        log.info("Abriendo: {}", affiliatesListUrl);
        open(affiliatesListUrl);
    }

    @Step("🖱️ Acceder al formulario de creación")
    public void clickCreate() {
        listPage.clickCreate();
    }

    @Step("📝 Llenar formulario para {affiliate.firstName} {affiliate.lastName} (Doc: {affiliate.documentNumber})")
    public void fillForm(AffiliateData affiliate) {
        formPage.fillForm(affiliate);
    }

    @Step("💾 Guardar afiliado")
    public void save() {
        formPage.clickSave();
    }

    /**
     * Flujo completo: navegar → crear → llenar → guardar.
     */
    @Step("🔄 Ejecutar flujo completo de creación de afiliado")
    public void createAffiliate(AffiliateData affiliate) {
        navigateToList();
        clickCreate();
        fillForm(affiliate);
        save();
    }

    /**
     * Valida que:
     * 1. Aparezca mensaje de éxito
     * 2. El afiliado esté en la lista al buscar por documento
     */
    @Step("🔍 Verificar que afiliado con documento {documentNumber} fue creado exitosamente")
    public boolean isAffiliateCreated(String documentNumber) {
        boolean successMsg = formPage.isSuccessMessageVisible();
        if (!successMsg) {
            log.warn("❌ Mensaje de éxito no detectado");
            return false;
        }

        log.info("Buscando documento {} en lista...", documentNumber);
        return listPage.searchByDocumentAndVerifyExists(documentNumber);
    }

    // Sobrecarga para usar directamente con AffiliateData
    public boolean isAffiliateCreated(AffiliateData affiliate) {
        return isAffiliateCreated(affiliate.documentNumber());
    }
}