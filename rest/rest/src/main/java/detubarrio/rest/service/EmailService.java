package detubarrio.rest.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async // 🌟 Esto evita que el cliente experimente lag en la web mientras se envía el email
    public void enviarEmailConPdf(String emailDestino, String nombreCliente, String nombreComercio, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // True indica que el mensaje va a ser multipart (lleva adjuntos)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestino);
            helper.setSubject("Confirmación de tu reserva en " + nombreComercio + " 📅");
            
            String cuerpoTexto = "¡Hola, " + nombreCliente + "!\n\n"
                    + "Tu cita se ha confirmado correctamente. Te adjuntamos a este correo el comprobante oficial en PDF "
                    + "con la dirección, hora y los detalles del comercio.\n\n"
                    + "¡Nos vemos en el barrio!";
            helper.setText(cuerpoTexto);

            // Adjuntamos el PDF directamente desde la memoria RAM
            helper.addAttachment("Comprobante_Reserva_" + nombreComercio.replace(" ", "_") + ".pdf", 
                    new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de la reserva a " + emailDestino + ": " + e.getMessage());
        }
    }
}