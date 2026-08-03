package server.websocket;
import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
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
        //need to finish this
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