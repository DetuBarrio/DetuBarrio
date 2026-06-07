package detubarrio.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    @NotBlank
    @Email
    private String email;

    // Getter y Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}