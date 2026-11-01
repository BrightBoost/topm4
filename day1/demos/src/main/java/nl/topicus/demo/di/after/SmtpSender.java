package nl.topicus.demo.di.after;

public class SmtpSender implements MessageSender {

    @Override
    public void send(String to, String message) {
        System.out.println("[SMTP] to=" + to + " message=" + message);
    }
}
