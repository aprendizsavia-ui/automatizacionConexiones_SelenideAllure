package co.empresa.qa.automation.utils;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.sleep;

/**
 * Utilidad para interactuar con componentes {@code p:selectOneMenu} de PrimeFaces.
 * Permite seleccionar opciones por texto exacto, aleatoriamente o excluyendo valores específicos.
 * Maneja apertura del dropdown, filtrado de opciones inválidas y selección segura con scroll y espera.
 *
 * @author David
 */

public class PrimeFacesSelect {

    private static final Random RANDOM = new Random();
    private static final Duration DROPDOWN_OPEN_TIMEOUT = Duration.ofSeconds(3);

    // ✅ Nuevo método: selecciona aleatoriamente EXCLUYENDO opciones específicas
    public static String selectRandomOptionExcluding(String widgetVar, String... excludedLabels) {
        // 1. Abrir dropdown
        SelenideElement trigger = $x("//div[contains(@id, '" + widgetVar + "')]//div[contains(@class, 'ui-selectonemenu-trigger')]");
        trigger.shouldBe(visible).click();

        // 2. Buscar <ul> de opciones por ID (XPath, seguro con :)
        String itemsId = widgetVar + "_items";
        SelenideElement itemsList = $x("//ul[@id='" + itemsId + "']")
                .shouldBe(visible, DROPDOWN_OPEN_TIMEOUT);

        // 3. Filtrar opciones: visibles, no placeholder, y no excluidas
        List<SelenideElement> validOptions = itemsList.$$("li")
                .stream()
                .filter(option -> {
                    String txt = option.text().trim();
                    // Excluir vacíos, placeholders y listas negras
                    if (txt.isEmpty() ||
                            txt.equals("--") ||
                            option.has(com.codeborne.selenide.Condition.cssClass("ui-noselection-option"))) {
                        return false;
                    }
                    // Excluir por texto (case-insensitive)
                    for (String excluded : excludedLabels) {
                        if (txt.equalsIgnoreCase(excluded)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        if (validOptions.isEmpty()) {
            throw new IllegalStateException(
                    "No hay opciones válidas en '" + widgetVar + "' tras excluir: " +
                            Arrays.toString(excludedLabels)
            );
        }

        // 4. Seleccionar y clic
        SelenideElement selected = validOptions.get(RANDOM.nextInt(validOptions.size()));
        selected.scrollIntoView(true);
        String selectedLabel = selected.text().trim();
        sleep(300);
        selected.click();

        return selectedLabel; // ✅ Devuelve el texto seleccionado
    }

    // ✅ Método original (sin exclusión) para retrocompatibilidad
    public static void selectRandomOption(String widgetVar) {
        selectRandomOptionExcluding(widgetVar); // sin exclusiones
    }

    /**
     * Selecciona una opción específica por su texto visible en un p:selectOneMenu de PrimeFaces.
     * @param widgetVar ID del componente (ej: "frmCrear:tipoDocumento")
     * @param labelText Texto exacto de la opción a seleccionar (ej: "Cedula Ciudadania")
     */
    public static void selectOption(String widgetVar, String labelText) {
        // 1. Abrir el dropdown
        SelenideElement trigger = $x("//div[contains(@id, '" + widgetVar + "')]//div[contains(@class, 'ui-selectonemenu-trigger')]");
        trigger.shouldBe(visible).click();

        // 2. Buscar la lista de opciones
        String itemsId = widgetVar + "_items";
        SelenideElement itemsList = $x("//ul[@id='" + itemsId + "']")
                .shouldBe(visible, DROPDOWN_OPEN_TIMEOUT);

        // 3. Encontrar la opción por texto EXACTO (case-sensitive para evitar ambigüedad)
        SelenideElement targetOption = itemsList.$$("li")
                .filterBy(text(labelText))
                .first();

        if (targetOption == null) {
            throw new IllegalStateException(
                    "Opción no encontrada: '" + labelText + "' en dropdown '" + widgetVar + "'. " +
                            "Opciones disponibles: " +
                            itemsList.$$("li").stream()
                                    .map(SelenideElement::text)
                                    .collect(Collectors.toList())
            );
        }

        // 4. Hacer clic
        targetOption.scrollIntoView(true);
        sleep(300);
        targetOption.click();
    }
}