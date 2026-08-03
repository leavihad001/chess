package ui;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class GameUI implements NotificationHandler {

    private final String serverURL;
    private final String authToken;
    private final int gameID;
    private final String playerColor;
    private WebSocketFacade wsFacade;

    public GameUI(String serverURL, String authToken, int gameID, String playerColor) {
        this.serverURL = serverURL;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public void repl() {
        try {
            wsFacade = new WebSocketFacade(serverURL, this);
            wsFacade.connect(authToken, gameID);

        } catch (Exception e) {
            System.out.println("Failed to connect to gameplay WebSocket: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!Objects.equals(result, "leave")) {
            System.out.print("[IN_GAME] >>> ");
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
                if (result != null && !Objects.equals(result, "leave")) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                System.out.print(e.getMessage() + "\n");
            }
        }
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                System.out.println();

                String drawColor = playerColor.equals("BLACK") ? "BLACK" : "WHITE";
                ChessBoardDraw.draw(drawColor);

                printUIHead();
            }
            case NOTIFICATION -> {
                NotificationMessage noticeMsg = (NotificationMessage) message;
                System.out.println("\n[NOTIFICATION]: " + noticeMsg.getMessage());
                printUIHead();
            }
            case ERROR -> {
                ErrorMessage errorMsg = (ErrorMessage) message;
                System.out.println("\n[ERROR]: " + errorMsg.getErrorMessage());
                printUIHead();
            }
        }
    }

    private void printUIHead() {
        System.out.print("\n[IN_GAME] >>> ");
    }

    private String evaluate(String in) {
        try {
            var tokens = in.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "leave" -> leave();
                //need to add others (redraw, move, resign, highlight)
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String help() {
        return null;
    }

    private String leave() {
        //need to finish this notification part
        return "leave";
    }
}