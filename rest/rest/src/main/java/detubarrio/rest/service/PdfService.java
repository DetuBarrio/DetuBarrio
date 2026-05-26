package detubarrio.rest.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import detubarrio.rest.model.Reserva;
import detubarrio.rest.model.Usuario;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;

@Service
public class PdfService {

    public byte[] generarPdfReserva(Reserva reserva, Usuario usuario, String nombreComercio) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Definición de fuentes y estilos vistosos
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.DARK_GRAY);
            Font subTituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.GRAY);
            Font cuerpoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font destacadoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 128, 128));

            // Encabezado
            Paragraph titulo = new Paragraph("CONFIRMACIÓN DE RESERVA", tituloFont);
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph intro = new Paragraph("¡Hola " + usuario.getNombre() + "! Tu reserva se ha procesado con éxito. Aquí tienes los detalles del servicio de tu barrio:", cuerpoFont);
            intro.setSpacingAfter(30);
            document.add(intro);

            // Bloque de información
            document.add(new Paragraph("Establecimiento: ", subTituloFont));
            Paragraph comercioP = new Paragraph(nombreComercio, destacadoFont);
            comercioP.setSpacingAfter(15);
            document.add(comercioP);

            if (reserva.getDisponibilidad() != null) {
                document.add(new Paragraph("Fecha de la cita: ", subTituloFont));
                Paragraph fechaP = new Paragraph(reserva.getDisponibilidad().getFecha().toString(), cuerpoFont);
                fechaP.setSpacingAfter(15);
                document.add(fechaP);

                document.add(new Paragraph("Hora asignada: ", subTituloFont));
                Paragraph horaP = new Paragraph(reserva.getDisponibilidad().getHoraInicio().toString() + " h.", cuerpoFont);
                horaP.setSpacingAfter(15);
                document.add(horaP);
            }

            document.add(new Paragraph("Número de localizador único: ", subTituloFont));
            Paragraph idP = new Paragraph("#RES-00" + reserva.getId(), destacadoFont);
            idP.setSpacingAfter(40);
            document.add(idP);

            // Pie de página de cortesía
            Paragraph pie = new Paragraph("Gracias por apoyar al comercio local de tu barrio.\nSi necesitas cancelar tu cita, recuerda hacerlo desde tu perfil con antelación.", cuerpoFont);
            pie.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(pie);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error fatal construyendo el comprobante PDF", e);
        }

        return out.toByteArray();
    }
}