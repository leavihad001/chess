package server.websocket;
import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import model.AuthData;
import model.GameData;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import java.io.IOException;

public class WebSocketHandler {

    private final ConnectionManager sessions = new ConnectionManager();
    private final GameService gameService;
    private final Gson gson = new Gson();

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handleMessage(WsMessageContext ctx) {
        try {
            String json = ctx.message();

            System.out.println("SERVER RECEIVED RAW JSON: " + json);

            UserGameCommand baseCommand = gson.fromJson(json, UserGameCommand.class);

            switch (baseCommand.getCommandType()) {
                case CONNECT -> connect(baseCommand, ctx.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = gson.fromJson(json, MakeMoveCommand.class);
                    makeMove(moveCommand, ctx.session);
                }
                case LEAVE -> leaveGame(baseCommand, ctx.session);
                case RESIGN -> resignGame(baseCommand, ctx.session);
            }
        } catch (Exception e) {
            sendError(ctx.session, "Error: Invalid command format or server error.");
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData auth = gameService.verifyAuth(command.getAuthToken());
            String username = auth.username();

            GameData gameData = gameService.getGame(command.getGameID());
            int gameID = gameData.gameID();

            sessions.addSession(gameID, session);

            LoadGameMessage loadMessage = new LoadGameMessage(gameData.game());
            sessions.sendMessage(session, loadMessage);

            String role = "an observer";
            if (username.equals(gameData.whiteUsername())) {
                role = "white";
            } else if (username.equals(gameData.blackUsername())) {
                role = "black";
            }

            String noticeText = String.format("%s joined the game as %s.", username, role);
            NotificationMessage noticeMessage = new NotificationMessage(noticeText);
            sessions.broadcast(gameID, noticeMessage, session);

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }

    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        try {
            AuthData auth = gameService.verifyAuth(command.getAuthToken());
            String username = auth.username();

            GameData gameData = gameService.getGame(command.getGameID());
            chess.ChessGame game = gameData.game();

            if (game.isGameOver()) {
                throw new Exception("You cannot make a move because the game is already over.");
            }

            ChessGame.TeamColor playerColor = getTeamColor(username, gameData, game);

            chess.ChessMove move = command.getMove();
            var validMoves = game.validMoves(move.getStartPosition());
            if (validMoves == null || !validMoves.contains(move)) {
                throw new Exception("Illegal move: " + move);
            }

            game.makeMove(move);

            gameService.updateGameState(gameData.gameID(), game);

            LoadGameMessage loadMessage = new LoadGameMessage(game);

            sessions.broadcast(gameData.gameID(), loadMessage, null);

            String moveDesc = String.format("%s moved from %s to %s.",
                    username, formatPosition(move.getStartPosition()), formatPosition(move.getEndPosition()));
            NotificationMessage noticeMessage = new NotificationMessage(moveDesc);
            sessions.broadcast(gameData.gameID(), noticeMessage, session);

            chess.ChessGame.TeamColor opponentColor = (playerColor == chess.ChessGame.TeamColor.WHITE)
                    ? chess.ChessGame.TeamColor.BLACK : chess.ChessGame.TeamColor.WHITE;
            String opponentName = (opponentColor == chess.ChessGame.TeamColor.WHITE)
                    ? gameData.whiteUsername() : gameData.blackUsername();

            if (game.isInCheckmate(opponentColor)) {
                sessions.broadcast(gameData.gameID(),
                        new NotificationMessage(opponentName + " is in checkmate!"), null);
            } else if (game.isInCheck(opponentColor)) {
                sessions.broadcast(gameData.gameID(),
                        new NotificationMessage(opponentName + " is in check!"), null);
            } else if (game.isInStalemate(opponentColor)) {
                sessions.broadcast(gameData.gameID(),
                        new NotificationMessage("Game ended in stalemate!"), null);
            }

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    @NotNull
    private static ChessGame.TeamColor getTeamColor(String username, GameData gameData, ChessGame game) throws Exception {
        ChessGame.TeamColor playerColor = null;
        if (username.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        }

        if (playerColor == null) {
            throw new Exception("Observers cannot make moves.");
        }
        if (game.getTeamTurn() != playerColor) {
            throw new Exception("It is not your turn.");
        }
        return playerColor;
    }

    private String formatPosition(chess.ChessPosition pos) {
        char colChar = (char) ('a' + pos.getColumn() - 1);
        return "" + colChar + pos.getRow();
    }

    private void leaveGame(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData auth = gameService.verifyAuth(command.getAuthToken());
            String username = auth.username();

            GameData gameData = gameService.getGame(command.getGameID());

            sessions.removeSession(gameData.gameID(), session);

            if (username.equals(gameData.whiteUsername())) {
                gameService.removePlayer(gameData.gameID(), "WHITE");
            } else if (username.equals(gameData.blackUsername())) {
                gameService.removePlayer(gameData.gameID(), "BLACK");
            }

            String noticeText = String.format("%s left the game.", username);
            NotificationMessage noticeMessage = new NotificationMessage(noticeText);
            sessions.broadcast(gameData.gameID(), noticeMessage, null);

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void resignGame(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData auth = gameService.verifyAuth(command.getAuthToken());
            String username = auth.username();

            GameData gameData = gameService.getGame(command.getGameID());
            chess.ChessGame game = gameData.game();

            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                throw new Exception("Observers cannot resign from the game.");
            }

            if (game.isGameOver()) {
                throw new Exception("The game is already over.");
            }

            game.setGameOver(true);
            gameService.updateGameState(gameData.gameID(), game);

            String noticeText = String.format("%s resigned from the game.", username);
            NotificationMessage noticeMessage = new NotificationMessage(noticeText);
            sessions.broadcast(gameData.gameID(), noticeMessage, null);

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void sendError(Session session, String errorMessage) {
        try {
            ErrorMessage error = new ErrorMessage(errorMessage);
            sessions.sendMessage(session, error);
        } catch (IOException e) {
            System.err.println("Failed to send error message: " + e.getMessage());
        }
    }
}