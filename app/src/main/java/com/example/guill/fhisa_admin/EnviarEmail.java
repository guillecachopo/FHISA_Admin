package com.example.guill.fhisa_admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
 * Created by guill on 21/12/2017.
 */

public class EnviarEmail extends AsyncTask<String, String, String> {

    public Context context;
    public SharedPreferences preferences;
    public String rutaFichero;
    public ProgressBar progressBar;

    Session session = null;
    String from = "fhisaautomatico@gmail.com";

    public EnviarEmail(Context context, SharedPreferences preferences, String rutaFichero, ProgressBar progressBar) {
        this.context = context;
        this.preferences = preferences;
        this.rutaFichero = rutaFichero;
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
    protected String doInBackground(String... strings) {
        //Getting hour to file name
        final Date currentTime = Calendar.getInstance().getTime();
        final String day = (String) android.text.format.DateFormat.format("dd",   currentTime); // 31
        final String monthNumber  = (String) android.text.format.DateFormat.format("MM",   currentTime); // 10
        final String year         = (String) android.text.format.DateFormat.format("yy", currentTime); // 2017
        final String hour = (String) android.text.format.DateFormat.format("HHmmss", currentTime); //1326

        // Recipient's email ID needs to be mentioned.
        String to = preferences.getString("etEmail", "paco@gmail.com");
        Log.i("EMAIL", to);
        //Asunto del mensaje
        String asunto = "Copia de seguridad " + day+"/"+monthNumber+"/"+year+" " + hour;

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
                messageBodyPart.setText("Mensaje generado automáticamente.");

                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(rutaFichero);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(rutaFichero);

                MimeMultipart multiParte = new MimeMultipart();

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from
                        , "FHISA Auto Message"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                message.setSubject(asunto);
                multiParte.addBodyPart(messageBodyPart);
                multiParte.addBodyPart(attachmentBodyPart);
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
