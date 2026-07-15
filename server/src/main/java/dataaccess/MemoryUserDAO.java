package dataaccess;
import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    public void clearUserData() throws DataAccessException {
        users.clear();
    }
}