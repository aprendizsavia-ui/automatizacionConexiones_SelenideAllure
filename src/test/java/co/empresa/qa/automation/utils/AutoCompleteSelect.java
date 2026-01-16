package co.empresa.qa.automation.utils;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.Random;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.sleep;

/**
 * Utilidad para interactuar con componentes {@code p:autoComplete} de PrimeFaces.
 * Permite seleccionar la primera opción, una opción aleatoria o recuperar el texto de la opción elegida.
 * Maneja visibilidad del panel de sugerencias y evita selecciones en listas vacías.
 *
 * @author David
 */

public class AutoCompleteSelect {

    private static final Random RANDOM = new Random();

    /**
     * Selecciona una opción aleatoria en un p:autoComplete de PrimeFaces.
     *
     * @param inputId     ID del input (ej: "frmCrear:municipioAfiliacion")
     * @param triggerText Texto a escribir para activar el autocomplete (ej: "Bog")
     */

    public static void selectRandomOption(String inputId, String triggerText) {
        // 1. Encontrar el input (usualmente tiene name o id con _input)
        SelenideElement input = $x("//input[contains(@id, '" + inputId + "') or contains(@name, '" + inputId + "')]");
        input.shouldBe(visible).click();
        input.setValue(triggerText);

        // 2. Esperar lista (clase típica: ui-autocomplete-items)
        SelenideElement panel = $x("//ul[contains(@class, 'ui-autocomplete-items') and not(contains(@style, 'display: none'))]");
        panel.shouldBe(visible, Duration.ofSeconds(5));

        // 3. Obtener opciones visibles
        ElementsCollection options = panel.$$("li").filterBy(visible);
        if (options.isEmpty()) {
            throw new IllegalStateException("No se encontraron sugerencias para '" + triggerText + "'");
        }

        // 4. Seleccionar aleatoriamente
        SelenideElement selected = options.get(RANDOM.nextInt(options.size()));
        selected.scrollIntoView(true);
        sleep(200);
        selected.click();
    }

    /**
     * Versión simplificada: escribe y elige la PRIMERA opción (más rápida y estable).
     */
    public static void selectFirstOption(String inputId, String triggerText) {
        SelenideElement input = $x("//input[contains(@id, '" + inputId + "') or contains(@name, '" + inputId + "')]");
        input.shouldBe(visible).click();
        input.setValue(triggerText);

        $x("//ul[contains(@class, 'ui-autocomplete-items')]//li[1]")
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
    }

    /**
     * Guarda la opción seleccionada.
     */
    public static String selectRandomOptionAndGetLabel(String inputId, String triggerText) {
        SelenideElement input = $x("//input[contains(@id, '" + inputId + "') or contains(@name, '" + inputId + "')]");
        input.shouldBe(visible).click();
        input.setValue(triggerText);

        SelenideElement panel = $x("//ul[contains(@class, 'ui-autocomplete-items') and not(contains(@style, 'display: none'))]");
        panel.shouldBe(visible, Duration.ofSeconds(5));

        ElementsCollection options = panel.$$("li").filterBy(visible);
        if (options.isEmpty()) {
            throw new IllegalStateException("No hay sugerencias para '" + triggerText + "'");
        }

        SelenideElement selected = options.get(RANDOM.nextInt(options.size()));
        String selectedLabel = selected.text().trim();
        selected.click();

        return selectedLabel; // ✅ Devuelve el texto seleccionado
    }
}