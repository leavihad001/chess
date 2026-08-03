package client.websocket;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint implements MessageHandler.Whole<String> {

    private Session session;
    private final NotificationHandler observer;
    private final Gson gson = new Gson();

    public WebSocketFacade(String url, NotificationHandler observer) throws DeploymentException, IOException, URISyntaxException {
        this.observer = observer;

        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(this);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
    }

    @Override
    public void onMessage(String message) {
        try {
            ServerMessage baseMessage = gson.fromJson(message, ServerMessage.class);
            switch (baseMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage loadMsg = gson.fromJson(message, LoadGameMessage.class);
                    observer.notify(loadMsg);
                }
                case NOTIFICATION -> {
                    NotificationMessage noticeMsg = gson.fromJson(message, NotificationMessage.class);
                    observer.notify(noticeMsg);
                }
                case ERROR -> {
                    ErrorMessage errorMsg = gson.fromJson(message, ErrorMessage.class);
                    observer.notify(errorMsg);
                }
            }
        } catch (Exception e) {
            System.err.println("Error deserializing message from server: " + e.getMessage());
        }
    }

    public void connect(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(gson.toJson(command));
    }
}