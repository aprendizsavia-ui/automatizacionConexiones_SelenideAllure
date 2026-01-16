package co.empresa.qa.automation.pages.aseg_afiliados;

import co.empresa.qa.automation.models.AffiliateData;
import co.empresa.qa.automation.utils.AutoCompleteSelect;
import co.empresa.qa.automation.utils.PrimeFacesSelect;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

/**
 * Página del formulario de afiliado. Llena todos los campos del formulario utilizando datos de un modelo {@code AffiliateData},
 * incluyendo fechas, selecciones en dropdowns (con soporte para PrimeFaces), autocompletes, direcciones, contactos y más.
 * Gestiona lógica condicional (como dependencias entre campos) y finaliza con la acción de guardar.
 * Proporciona métodos para enviar el formulario y verificar si la operación fue exitosa.
 *
 * @author David
 */

public class AffiliateFormPage {

    private static final DateTimeFormatter MODEL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AffiliateFormPage fillForm(AffiliateData affiliate) {
        // === DATOS PERSONALES ===
        setFormattedDate("#frmCrear\\:fechaNacimiento_input", affiliate.birthDate());
        $("[id='frmCrear\\:edad']").shouldBe(visible, Duration.ofSeconds(5));

        PrimeFacesSelect.selectOption("frmCrear:tipoDocumento", affiliate.documentTypeDisplay());
        PrimeFacesSelect.selectRandomOption("frmCrear:paisNacimiento");
        PrimeFacesSelect.selectRandomOption("frmCrear:paisNacionalidad");
        PrimeFacesSelect.selectRandomOption("frmCrear:genero");
        PrimeFacesSelect.selectRandomOption("frmCrear:generoIdentif");
        PrimeFacesSelect.selectRandomOption("frmCrear:estadoCivil");

        String tipoAfi = PrimeFacesSelect.selectRandomOptionExcluding("frmCrear:tipoAfiliado", "Beneficiario", "Adicional");
        if (tipoAfi != null && !tipoAfi.equalsIgnoreCase("Cabeza de Hogar")) {
            PrimeFacesSelect.selectRandomOption("frmCrear:parentesco");
        }

        PrimeFacesSelect.selectRandomOption("frmCrear:origenAfiliado");

        $("#frmCrear\\:numeroDocumento").setValue(affiliate.documentNumber());
        $("#frmCrear\\:primerNombre").setValue(affiliate.firstName());
        $("#frmCrear\\:segundoNombre").clear();
        $("#frmCrear\\:primerApellido").setValue(affiliate.lastName());
        $("#frmCrear\\:segundoApellido").clear();
        $("#frmCrear\\:email").setValue(affiliate.email());

        if ("CC".equals(affiliate.documentType())) {
            setFormattedDate("#frmCrear\\:fechaExpDoc_input", affiliate.expeditionDate());
        }

        // === BDUA ===
        SelenideElement bduaButton = $x("//div[@id='frmCrear:registraBDUA']").shouldBe(visible, Duration.ofSeconds(10));
        String bduaText = $x("//div[@id='frmCrear:registraBDUA']//span[contains(@class, 'ui-button-text')]")
                .shouldBe(visible, Duration.ofSeconds(10))
                .text()
                .trim();

        if ("Si".equals(bduaText)) {
            PrimeFacesSelect.selectRandomOption("frmCrear:tipoDocumentoBDUA");
            $x("//input[@id='frmCrear:serialBDUA']").setValue(affiliate.serialBDUA());
            $x("//input[@id='frmCrear:numeroDocumentoBDUA']").setValue(affiliate.documentNumber());
            $x("//input[@id='frmCrear:primerApellidoBDUA']").setValue(affiliate.lastName());
            $x("//input[@id='frmCrear:primerNombreBDUA']").setValue(affiliate.firstName());
            setFormattedDate("input[id='frmCrear:fechaNacimientoBDUA_input']", affiliate.birthDate());
        }

        AutoCompleteSelect.selectRandomOption("frmCrear:epsBDUA", "a");

        // === DATOS AFILIACION ===
        setFormattedDate("#frmCrear\\:fechaAfilEPS_input", affiliate.EPSSAfiliationDate());
        setFormattedDate("#frmCrear\\:fechaAfilSGSSS_input", affiliate.SGSSSAfiliationDate());

        if (affiliate.lgbtiq()) {
            $("#frmCrear\\:poblacionLgtbiq").shouldBe(visible).click();
        }

        // === MUNICIPIO AFILIACION ===
        String municipio = AutoCompleteSelect.selectRandomOptionAndGetLabel("frmCrear:municipioAfiliacion", "a");

        $("#frmCrear\\:autorizoEmail").shouldBe(visible).click();
        $("#frmCrear\\:autorizoSMS").shouldBe(visible).click();

        // === DIRECCIÓN ===
        $x("//form[@id='frmCrear']//button[@type='submit' and .//span[text()='Dirección']]")
                .shouldBe(visible).click();

        PrimeFacesSelect.selectRandomOptionExcluding("frmDireccion:j_idt864", "Sin Dirección");
        PrimeFacesSelect.selectRandomOption("frmDireccion:j_idt874");
        PrimeFacesSelect.selectRandomOption("frmDireccion:j_idt905");
        PrimeFacesSelect.selectRandomOption("frmDireccion:j_idt913");
        PrimeFacesSelect.selectRandomOption("frmDireccion:j_idt944");

        $("#frmDireccion\\:numeroDirecion").setValue(affiliate.dirNumber());
        $("#frmDireccion\\:placa").setValue(affiliate.dirNumber());
        $("#frmDireccion\\:placa2").setValue(affiliate.dirNumber());
        $("#frmDireccion\\:j_idt953").setValue(affiliate.dirNumber());

        $x("//*[@id=\"frmDireccion:j_idt965\"]").shouldBe(visible).click();

        // === BARRIO ===
        $x("//button[@type='submit' and .//span[text()='Barrio']]")
                .shouldBe(visible).click();

        if (municipio.equalsIgnoreCase("MEDELLÍN - ANTIOQUIA")) {
            PrimeFacesSelect.selectRandomOption("frmBarrio:selectBarrio");
        } else {
            $("#frmBarrio\\:textBarrio").setValue(affiliate.barrio());
        }

        $x("//*[@id=\"frmBarrio:j_idt978\"]").shouldBe(visible).click();

        // === OTROS DATOS ===
        PrimeFacesSelect.selectRandomOption("frmCrear:zona");
        $("#frmCrear\\:email").setValue(affiliate.email());

        // === CONTACTO ===
        $x("//button[@type='submit' and .//span[text()='Agregar Contacto']]")
                .shouldBe(visible).click();

        PrimeFacesSelect.selectRandomOptionExcluding("frmCrearContacto:tipoContacto", "Telefono");
        String celular = affiliate.celular();
        String telefono = celular.startsWith("300") ? celular : "300" + celular;
        $("#frmCrearContacto\\:numeroContacto").setValue(telefono);

        $x("//*[@id=\"frmCrearContacto:j_idt1063\"]").shouldBe(visible).click();

        // === IPS ATENCIÓN PRIMARIA ===
        PrimeFacesSelect.selectRandomOption("frmCrear:sedeIpsPrimaria");

        // === DATOS SOCIOECONÓMICOS ===
        String grupoPobla = PrimeFacesSelect.selectRandomOptionExcluding("frmCrear:grupoPoblacional");
        PrimeFacesSelect.selectRandomOption("frmCrear:grupoEtnico");
        if (grupoPobla != null &&
                (grupoPobla.equalsIgnoreCase("Comunidades indígenas") ||
                        grupoPobla.equalsIgnoreCase("Rrom (Gitano)"))) {
            PrimeFacesSelect.selectRandomOption("frmCrear:comunidadEtnica");
        }
        PrimeFacesSelect.selectRandomOption("frmCrear:metodGrupoPoblacional");
        PrimeFacesSelect.selectRandomOption("frmCrear:nivelSisben");

        // === DISCAPACIDAD ===
        if (affiliate.hasDisability()) {
            $("#frmCrear\\:discapacidad").shouldBe(visible).click();
            PrimeFacesSelect.selectRandomOption("frmCrear:tipoDiscapacidad");
            PrimeFacesSelect.selectRandomOption("frmCrear:condicionDiscapacidad");

            // ✅ Asignar fechas desde el modelo
            setFormattedDate("#frmCrear\\:fechaInicioDiscapacidad_input", affiliate.disabilityStartDate());
            setFormattedDate("#frmCrear\\:fechaFinDiscapacidad_input", affiliate.disabilityEndDate());
        }

        // === ESTADO AFILIACIÓN ===
        PrimeFacesSelect.selectRandomOption("frmCrear:causaNovedad");

        return this;
    }

    private void setFormattedDate(String selector, String dateValue) {
        if (dateValue == null || dateValue.trim().isEmpty()) {
            return;
        }
        LocalDate date = LocalDate.parse(dateValue, MODEL_DATE_FORMAT);
        String uiDate = date.format(UI_DATE_FORMAT);
        $(selector).shouldBe(visible).setValue(uiDate);
    }

    public void clickSave() {
        $x("//button[.//span[normalize-space()='Guardar']]")
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
    }

    public boolean isSuccessMessageVisible() {
        SelenideElement success = $x("//div[contains(@class, 'ui-messages-success')]//li");
        SelenideElement error = $x("//div[contains(@class, 'ui-messages-error')]");

        boolean successVisible = success.is(visible, Duration.ofSeconds(10));
        boolean errorVisible = error.is(visible, Duration.ofSeconds(2));

        return successVisible && !errorVisible;
    }
}