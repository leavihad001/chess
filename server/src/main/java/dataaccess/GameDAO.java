package dataaccess;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    Collection<GameData> listGames() throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(int gameID, String username, String playerColor) throws DataAccessException;

    void clearAllGameData() throws DataAccessException;
}