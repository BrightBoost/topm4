package nl.topicus.demo.di.before;

public class EmailSender {

    public void send(String to, String message) {
        System.out.println("[REAL EMAIL] to=" + to + " message=" + message);
    }
}
