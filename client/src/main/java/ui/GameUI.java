package ui;
import com.google.gson.Gson;
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
    private final Gson gson = new Gson();
    private chess.ChessGame currentGame;

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
                LoadGameMessage loadMsg = (LoadGameMessage) message;
                System.out.println();
                this.currentGame = loadMsg.getGame();

                String drawColor = playerColor.equals("BLACK") ? "BLACK" : "WHITE";
                ChessBoardDraw.draw(currentGame, drawColor);

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
                case "redraw" -> redraw();
                case "move" -> makeMove(params);
                //need to add others (resign, highlight)
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String help() {
        return """
            - redraw - redraws the chess board
            - leave - removes you from the game and returns to dashboard
            - move <START> <END> - make a move (e.g. move e2 e4)
            - resign - forfeit the game
            - highlight <SQUARE> - highlight legal moves for a piece (e.g. highlight e2)
            - help - with possible commands
            """;
    }

    private String redraw() {
        if (currentGame != null) {
            String drawColor = playerColor.equals("BLACK") ? "BLACK" : "WHITE";
            ChessBoardDraw.draw(currentGame, drawColor);
        } else {
            System.out.println("No board state available yet.");
        }
        return null;
    }

    private String leave() {
        try {
            wsFacade.leaveGame(authToken, gameID);
            return "leave";
        } catch (Exception e) {
            return "Error leaving game: " + e.getMessage();
        }
    }

    private String makeMove(String[] params) {
        if (params.length < 2) {
            return "Expected: move <START> <END> [PROMOTION_PIECE] (e.g., move e2 e4 or move e7 e8 Q)";
        }

        try {
            chess.ChessPosition start = parsePosition(params[0]);
            chess.ChessPosition end = parsePosition(params[1]);

            chess.ChessPiece.PieceType promotion = null;
            if (params.length >= 3) {
                promotion = parsePromotion(params[2]);
            }

            chess.ChessMove move = new chess.ChessMove(start, end, promotion);
            wsFacade.makeMove(authToken, gameID, move);

            return null;
        } catch (Exception e) {
            return "Invalid move format: " + e.getMessage();
        }
    }

    private chess.ChessPosition parsePosition(String pos) throws IllegalArgumentException {
        if (pos.length() != 2) {
            throw new IllegalArgumentException("Positions must be 2 characters (e.g., e2)");
        }
        int col = pos.toLowerCase().charAt(0) - 'a' + 1;
        int row = pos.charAt(1) - '0';

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Position out of board bounds: " + pos);
        }
        return new chess.ChessPosition(row, col);
    }

    private chess.ChessPiece.PieceType parsePromotion(String promo) {
        return switch (promo.toUpperCase()) {
            case "Q", "QUEEN" -> chess.ChessPiece.PieceType.QUEEN;
            case "R", "ROOK" -> chess.ChessPiece.PieceType.ROOK;
            case "B", "BISHOP" -> chess.ChessPiece.PieceType.BISHOP;
            case "N", "KNIGHT" -> chess.ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }
}