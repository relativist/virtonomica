package help;


import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class Mailme
{

    @Test
    public void testName() throws Exception {
        String host = "imap.gmail.com";
        String Password = "gbfcnhsgbfcnhs";
        String from = "a.p.sitnikov@gmail.com";
        String toAddress = "asitnikov@merchantry.com";
        String filename = "/home/rest/tmp_src/prooo";
        // Get system properties
        Properties props = System.getProperties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", 465);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, null);


        props.put("mail.debug", "true");


        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));

        message.setRecipients(Message.RecipientType.TO, toAddress);

        message.setSubject("JavaMail Attachment");
        message.setContent("<h1>This is actual message</h1>",
                "text/html");



        try {
            Transport tr = session.getTransport("smtps");
            tr.connect(host, from, Password);             tr.send(message);
            //tr.send(message);
            tr.sendMessage(message, message.getAllRecipients());
            System.out.println("Mail Sent Successfully");
            tr.close();

        } catch (SendFailedException sfe) {

            System.out.println(sfe);
        }




        //--------------------


    }

    public static void main()
    {
        sendEmail();
    }

    /**
     * Send the email via SMTP using StartTLS and SSL
     */
    private static void sendEmail() {

        // Create all the needed properties
        Properties connectionProperties = new Properties();
        // SMTP host
        connectionProperties.put("mail.smtp.host", "smtp.itcuties.com");
        // Is authentication enabled
        connectionProperties.put("mail.smtp.auth", "true");
        // Is StartTLS enabled
        connectionProperties.put("mail.smtp.starttls.enable", "true");
        // SSL Port
        connectionProperties.put("mail.smtp.socketFactory.port", "465");
        // SSL Socket Factory class
        connectionProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // SMTP port, the same as SSL port :)
        connectionProperties.put("mail.smtp.port", "465");

        System.out.print("Creating the session...");

        // Create the session
        Session session = Session.getDefaultInstance(connectionProperties,
                new javax.mail.Authenticator() {    // Define the authenticator
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("coding@itcuties.com","P@ssw0rd");
                    }
                });

        System.out.println("done!");

        // Create and send the message
        try {
            // Create the message
            Message message = new MimeMessage(session);
            // Set sender
            message.setFrom(new InternetAddress("coding@itcuties.com"));
            // Set the recipients
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("a.p.sitnikov@gmail.com"));
            // Set message subject
            message.setSubject("Hello from Team ITCuties");
            // Set message text
            message.setText("Java is easy when you watch our tutorials ;)");

            System.out.print("Sending message...");
            // Send the message
            Transport.send(message);

            System.out.println("done!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}





