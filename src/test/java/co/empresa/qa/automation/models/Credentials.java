package co.empresa.qa.automation.models;

/**
 * Modelo inmutable y validado para credenciales de acceso.
 * Garantiza que usuario y contraseña no sean nulos ni vacíos.
 *
 * @author David
 */

public record Credentials(String username, String password) {
    public Credentials {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username no puede ser nulo o vacío");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password no puede ser nulo o vacío");
        }
    }
}