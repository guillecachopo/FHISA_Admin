package com.example.guill.fhisa_admin.Mail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by guill on 22/01/2018.
 */

public class EnviarEmailPassword extends AsyncTask<String, String, String> {

    public Context context;
    public ProgressBar progressBar;
    public SharedPreferences preferences;

    Session session = null;
    String from = "fhisaautomatico@gmail.com";

    public EnviarEmailPassword(Context context, SharedPreferences preferences, ProgressBar progressBar) {
        this.context = context;
        this.preferences = preferences;
        this.progressBar = progressBar;
    }

    private boolean valido(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        boolean valido = pattern.matcher(email).matches();
        return valido;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... password) {
        // Recipient's email ID needs to be mentioned.
        String to = preferences.getString("etEmail", "paco@gmail.com");

        //Asunto del mensaje
        String asunto = "Contraseña de la aplicación FHISA Servicio";

        //Conectamos con los servicios de gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class"
                , "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "fhisahormigones");
            }
        });

        try {

            if (valido(from)) {
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Mensaje generado automáticamente. " +
                        "La contraseña para finalizar la ejecución de la aplicación FHISA Servicio " +
                        "es: " + password[0] + ". Si desea modificarla, hágalo desde las " +
                        "Opciones de la aplicación FHISA Gestor.");

                MimeMultipart multiParte = new MimeMultipart();

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from
                        , "FHISA Auto Message"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                message.setSubject(asunto);
                multiParte.addBodyPart(messageBodyPart);
                message.setContent(multiParte);
                Transport.send(message);
            } else {
                Toast.makeText(context, "La dirección de correo electrónico no es válida", Toast.LENGTH_LONG).show();
            }

        } catch(MessagingException e) {
            e.printStackTrace();
        } catch(Exception e) {
            Toast.makeText(context,
                    "Error de autenticacion o fallo de conexión", Toast.LENGTH_LONG).show();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "Mensaje enviado",
                Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

}
