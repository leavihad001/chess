package server.websocket;
import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.Session;
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
        //need to finish this
    }

    private void leaveGame(UserGameCommand command, Session session) throws IOException {
        //need to finish this
    }

    private void resignGame(UserGameCommand command, Session session) throws IOException {
        //need to finish this
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