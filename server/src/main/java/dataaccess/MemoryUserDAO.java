package dataaccess;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    void createUser(UserData userData) throws DataAccessException {
        //Do the stuff here
    }

    UserData getUserData(String username) throws DataAccessException {
        //Do the stuff here
        return users.get();
    }

    void clearUserData() throws DataAccessException {
        //Do the stuff here
    }
}