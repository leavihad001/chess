package server.websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionManager {

    private final ConcurrentMap<Integer, Set<Session>> sessionMap = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void addSession(int gameID, Session session) {
        sessionMap.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public Set<Session> getSessions(int gameID) {
        return sessionMap.getOrDefault(gameID, Collections.emptySet());
    }

    public void removeSession(int gameID, Session session) {
        Set<Session> sessions = sessionMap.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessionMap.remove(gameID);
            }
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(gson.toJson(message));
        }
    }

    public void broadcast(int gameID, ServerMessage message, Session excludeSession) throws IOException {
        Set<Session> sessions = getSessions(gameID);
        String jsonMessage = gson.toJson(message);

        List<Session> closedSessions = new ArrayList<>();

        for (Session session : sessions) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(jsonMessage);
                }
            } else {
                closedSessions.add(session);
            }
        }

        for (Session closedSession : closedSessions) {
            removeSession(gameID, closedSession);
        }
    }
}