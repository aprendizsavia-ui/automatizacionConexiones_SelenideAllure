package co.empresa.qa.automation.models;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Modelo inmutable de datos de afiliado, validado en construcción.
 * Calcula automáticamente la edad a partir de la fecha de nacimiento y deriva el tipo de documento.
 * Incluye método fábrica {@code of()} para evitar errores al pasar la edad manualmente.
 *
 * @author David
 */
public record AffiliateData(
        String birthDate,               // yyyy-MM-dd
        int age,
        String documentNumber,
        String expeditionDate,
        String firstName,
        String lastName,
        String nationality,
        String email,
        String phone,
        String municipality,
        String serialBDUA,
        String ipsPrimary,
        boolean hasDisability,
        String SGSSSAfiliationDate,
        String EPSSAfiliationDate,
        boolean hasBDUA,
        boolean lgbtiq,
        String dirNumber,
        String barrio,
        String celular,
        String disabilityStartDate,
        String disabilityEndDate
) {
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AffiliateData {
        if (birthDate == null || birthDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha de nacimiento obligatoria");
        }
        if (!birthDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Formato de fecha inválido: debe ser yyyy-MM-dd");
        }
        int calculatedAge = calculateAge(birthDate);
        if (age != calculatedAge) {
            throw new IllegalArgumentException(
                    "Edad inconsistente: esperada=" + calculatedAge + ", dada=" + age
            );
        }

        // Validar fechas de discapacidad si aplica
        if (hasDisability) {
            if (disabilityStartDate == null || disabilityEndDate == null) {
                throw new IllegalArgumentException("Fechas de discapacidad requeridas si hasDisability=true");
            }
            if (!disabilityStartDate.matches("\\d{4}-\\d{2}-\\d{2}") ||
                    !disabilityEndDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new IllegalArgumentException("Fechas de discapacidad deben ser yyyy-MM-dd");
            }
        }
    }

    public static AffiliateData of(
            String birthDate,
            String documentNumber,
            String expeditionDate,
            String firstName,
            String lastName,
            String nationality,
            String email,
            String phone,
            String municipality,
            String serialBDUA,
            String ipsPrimary,
            boolean hasDisability,
            String SGSSSAfiliationDate,
            String EPSSAfiliationDate,
            boolean hasBDUA,
            boolean lgbtiq,
            String dirNumber,
            String barrio,
            String celular
    ) {
        int age = calculateAge(birthDate);

        String discStart = null;
        String discEnd = null;
        if (hasDisability) {
            LocalDate today = LocalDate.now();
            LocalDate start = today.minusYears(1 + RANDOM.nextInt(10)); // 1-10 años atrás
            LocalDate end = today.plusYears(1 + RANDOM.nextInt(5));     // 1-5 años adelante
            discStart = start.format(DATE_FORMAT);
            discEnd = end.format(DATE_FORMAT);
        }

        return new AffiliateData(
                birthDate,
                age,
                documentNumber,
                expeditionDate,
                firstName,
                lastName,
                nationality,
                email,
                phone,
                municipality,
                serialBDUA,
                ipsPrimary,
                hasDisability,
                SGSSSAfiliationDate,
                EPSSAfiliationDate,
                hasBDUA,
                lgbtiq,
                dirNumber,
                barrio,
                celular,
                discStart,
                discEnd
        );
    }

    public static int calculateAge(String birthDateStr) {
        LocalDate birthDate = LocalDate.parse(birthDateStr, DATE_FORMAT);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String documentType() {
        if (age < 7) return "RC";
        if (age < 18) return "TI";
        return "CC";
    }

    public String documentTypeDisplay() {
        return switch (documentType()) {
            case "CC" -> "Cedula Ciudadania";
            case "TI" -> "Tarjeta Identidad";
            case "CE" -> "Cédula Extranjería";
            case "PA" -> "Pasaporte";
            case "PEP" -> "Permiso Especial Permanencia";
            case "RC" -> "Registro Civil";
            default -> documentType();
        };
    }

    public boolean hasValidDisabilityDates() {
        return hasDisability && disabilityStartDate != null && disabilityEndDate != null;
    }
}