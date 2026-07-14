package dataaccess;
import model.UserData;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUserData(String username) throws DataAccessException;

    void clearUserData() throws DataAccessException;
}