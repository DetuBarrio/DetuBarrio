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

    // 📩 NUEVO MÉTODO: Notificar resultado de la solicitud de Comercio (Aprobado/Rechazado)
    @Async
    public void enviarEmailResultadoComercio(String emailDestino, String nombreComercio, boolean aprobado, String motivoRechazo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDestino);
            
            if (aprobado) {
                message.setSubject("¡Tu comercio ha sido aprobado! 🎉 - DeTuBarrio");
                message.setText("¡Hola!\n\nBuenas noticias, tu solicitud para el comercio \"" + nombreComercio + "\" ha sido aprobada por el equipo de administración y ya es visible en el directorio del barrio.\n\n¡Mucho éxito con tu negocio!\nUn saludo.");
            } else {
                message.setSubject("Actualización sobre tu solicitud de comercio - DeTuBarrio 🔔");
                message.setText("Hola:\n\nLamentamos informarte que tu solicitud de registro para el comercio \"" + nombreComercio + "\" ha sido rechazada.\n\nMotivo del rechazo:\n\"" + motivoRechazo + "\"\n\nPuedes corregir los datos necesarios y volver a intentarlo.\nUn saludo.");
            }

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de resolución del comercio a " + emailDestino + ": " + e.getMessage());
        }
    }

    // 📩 NUEVO MÉTODO: Notificar resultado de Solicitud de Colaboración (Aprobada/Rechazada)
    @Async
    public void enviarEmailResultadoColaboracion(String emailDestino, String nombreComercio, boolean aprobada, String motivoRechazo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDestino);
            
            if (aprobada) {
                message.setSubject("¡Solicitud de colaboración aprobada! 🤝 - DeTuBarrio");
                message.setText("¡Hola!\n\nTu solicitud de vinculación/colaboración con el comercio \"" + nombreComercio + "\" ha sido aprobada.\nYa cuentas con los accesos autorizados para gestionar el panel de control del comercio.\n\n¡Gracias por hacer crecer el barrio!");
            } else {
                message.setSubject("Actualización de tu solicitud de colaboración - DeTuBarrio 🔔");
                message.setText("Hola:\n\nTu solicitud de colaboración para gestionar el comercio \"" + nombreComercio + "\" ha sido rechazada.\n\nMotivo del rechazo:\n\"" + motivoRechazo + "\"\n\nSi crees que es un error, por favor ponte en contacto con el administrador del sitio.");
            }

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de resolución de colaboración a " + emailDestino + ": " + e.getMessage());
        }
    }

    // 🔐 Método de recuperación intacto
    @Async
    public void enviarEmailRecuperacion(String emailDestino, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDestino);
            message.setSubject("Recuperar Contraseña - DeTuBarrio 🔐");
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

    // 📅 Método de PDF de reservas intacto
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