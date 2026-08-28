package com.Matricula2027.matricula2027.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarComprobante(String destinatario, String numeroMatricula, String fechaRegistro) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject("Comprobante de Pre-Matrícula N° " + numeroMatricula);

        String contenidoHtml = "<h2>¡Pre-Matrícula Recibida con Éxito!</h2>"
                + "<p>Estimado(a) apoderado(a),</p>"
                + "<p>Confirmamos que la pre-matrícula ha sido registrada correctamente en nuestro sistema.</p>"
                + "<ul>"
                + "  <li><strong>N° de Matrícula:</strong> " + numeroMatricula + "</li>"
                + "  <li><strong>Fecha de Registro:</strong> " + fechaRegistro + "</li>"
                + "</ul>"
                + "<p>Conserve este correo como respaldo de su trámite.</p>"
                + "<br><p>Atentamente,<br><strong>Administración Colegio</strong></p>";

        helper.setText(contenidoHtml, true);
        mailSender.send(message);
    }
}