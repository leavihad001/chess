package client;
import ui.PreloginUI;

public class ClientMain {
    public static void main(String[] args) {
        var serverURL = "http://localhost:8080";

        new PreloginUI(serverURL).repl();
    }
}
