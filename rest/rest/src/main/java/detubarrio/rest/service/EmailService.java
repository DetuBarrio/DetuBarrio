package detubarrio.rest.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 🌟 MÉTODO CORREGIDO: Enviar email de recuperación
    @Async
    public void enviarEmailRecuperacion(String emailDestino, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDestino);
            message.setSubject("Recuperar Contraseña - DeTuBarrio 🔐");
            
            // 🔗 AQUÍ ESTÁ LA MAGIA: Añadido el /#/ para el router de Vue
            String urlRecuperacion = "http://localhost:5173/#/reset-password?token=" + token;
            
            String cuerpoTexto = "Hola:\n\n"
                    + "Has solicitado restablecer la contraseña de tu cuenta en DeTuBarrio.\n"
                    + "Para continuar, haz clic en el siguiente enlace:\n\n" 
                    + urlRecuperacion + "\n\n"
                    + "Este enlace expirará en 15 minutos por motivos de seguridad.\n"
                    + "Si tú no has solicitado este cambio, puedes ignorar este correo de forma segura.\n\n"
                    + "¡Un saludo!";
                    
            message.setText(cuerpoTexto);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de recuperación a " + emailDestino + ": " + e.getMessage());
        }
    }

    // 📄 Tu método actual (intacto)
    @Async 
    public void enviarEmailConPdf(String emailDestino, String nombreCliente, String nombreComercio, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestino);
            helper.setSubject("Confirmación de tu reserva en " + nombreComercio + " 📅");
            
            String cuerpoTexto = "¡Hola, " + nombreCliente + "!\n\n"
                    + "Tu cita se ha confirmado correctamente. Te adjuntamos a este correo el comprobante oficial en PDF "
                    + "con la dirección, hora y los detalles del comercio.\n\n"
                    + "¡Nos vemos en el barrio!";
            helper.setText(cuerpoTexto);

            helper.addAttachment("Comprobante_Reserva_" + nombreComercio.replace(" ", "_") + ".pdf", 
                    new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de la reserva a " + emailDestino + ": " + e.getMessage());
        }
    }
}