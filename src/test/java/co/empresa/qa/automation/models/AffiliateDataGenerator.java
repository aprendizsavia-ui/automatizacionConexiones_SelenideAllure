package co.empresa.qa.automation.models;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Random;

/**
 * Generador de datos aleatorios para afiliados, respetando reglas del contexto colombiano:
 * Edad coherente con tipo de documento (RC, TI, CC).
 * Fecha de expedición válida según edad.
 * Fechas de afiliación dentro del mes actual.
 * Números de documento con longitud adecuada por tipo.
 *
 * @author David
 */

public class AffiliateDataGenerator {

    private static final Faker FAKER = new Faker(new Locale("es", "CO"));
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static AffiliateData generateRandomAffiliate() {
        // 1. Fecha de nacimiento (0 a 80 años)
        LocalDate birthDate = FAKER.date().birthday(0, 80)
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
        String birthDateStr = birthDate.format(DATE_FORMAT);
        LocalDate today = LocalDate.now();
        int age = Period.between(birthDate, today).getYears();

        // 2. ✅ Fecha de expedición: respetando reglas por tipo de documento
        String expeditionDateStr;
        if (age >= 18) {
            // CC: debe ser al menos a los 18 años
            LocalDate minExpedition = birthDate.plusYears(18);
            if (minExpedition.isAfter(today)) {
                expeditionDateStr = today.format(DATE_FORMAT); // fallback seguro
            } else {
                long days = ChronoUnit.DAYS.between(minExpedition, today);
                LocalDate expeditionDate = minExpedition.plusDays(RANDOM.nextLong(days + 1));
                expeditionDateStr = expeditionDate.format(DATE_FORMAT);
            }
        } else {
            // TI o RC: puede ser reciente (usamos hoy)
            expeditionDateStr = today.format(DATE_FORMAT);
        }

        // 3. ✅ Fecha de afiliación: solo este mes (1° hasta hoy)
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        long daysInMonthSoFar = ChronoUnit.DAYS.between(firstDayOfMonth, today);
        LocalDate affiliationDate = firstDayOfMonth.plusDays(RANDOM.nextLong(daysInMonthSoFar + 1));
        String affiliationDateStr = affiliationDate.format(DATE_FORMAT);

        // 4. Número de documento según edad y normativa colombiana
        String documentNumber;
        if (age < 7) {
            // RC: 10 a 11 dígitos
            if (RANDOM.nextBoolean()) {
                documentNumber = String.valueOf(FAKER.number().numberBetween(1_000_000_000L, 9_999_999_999L)); // 10 dígitos
            } else {
                documentNumber = String.valueOf(FAKER.number().numberBetween(10_000_000_000L, 99_999_999_999L)); // 11 dígitos
            }
        } else if (age < 18) {
            // TI: 9 a 10 dígitos (común: 10)
            documentNumber = String.valueOf(FAKER.number().numberBetween(100_000_000L, 9_999_999_999L));
        } else {
            // CC: 10 dígitos (estándar actual)
            documentNumber = String.valueOf(FAKER.number().numberBetween(1_000_000_000L, 9_999_999_999L));
        }

        // 5. Crear afiliado
        return AffiliateData.of(
                birthDateStr,
                documentNumber,
                expeditionDateStr,
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                "COLOMBIA",
                FAKER.internet().emailAddress(),
                FAKER.phoneNumber().cellPhone(),
                FAKER.address().city(),
                FAKER.number().digits(8), // SerialBDUA
                "IPS " + FAKER.company().name(),
                RANDOM.nextBoolean(), // hasDisability
                affiliationDateStr,   // SGSSSAfiliationDate
                affiliationDateStr,   // EPSSAfiliationDate
                RANDOM.nextBoolean(), // hasBDUA
                RANDOM.nextBoolean(), // lgbtiq
                FAKER.number().digits(2), // dirNumber
                FAKER.address().streetName(), // barrio
                FAKER.number().digits(7) // celular
        );
    }
}