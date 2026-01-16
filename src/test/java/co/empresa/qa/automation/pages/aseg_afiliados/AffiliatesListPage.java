package co.empresa.qa.automation.pages.aseg_afiliados;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class AffiliatesListPage {

    /**
     * Página de lista de afiliados. Permite crear nuevos afiliados y buscar por número de documento.
     * Los elementos se acceden mediante selectores fijos (XPath e ID) y se validan con condiciones de visibilidad.
     *
     * @author David
     */

    public void clickCreate() {
        $x("//*[@id=\"frmAfiliados:j_idt45\"]").shouldBe(visible).click();
    }

    /**
     * Busca por número de documento y verifica que aparezca en la tabla de resultados.
     * @return true si el documento está visible en la lista
     */
    public boolean searchByDocumentAndVerifyExists(String documentNumber) {
        // Limpiar y buscar
        $("#frmBuscar\\:numeroDocumento").clear();
        $("#frmBuscar\\:numeroDocumento").setValue(documentNumber);
        $x("//button[.//span[text()='Buscar']]").click();

        // Esperar resultados y verificar presencia
        SelenideElement resultRow = $x(
                "//tbody//td[contains(text(), '" + documentNumber + "')]"
        );

        return resultRow.is(visible, Duration.ofSeconds(8));
    }
}