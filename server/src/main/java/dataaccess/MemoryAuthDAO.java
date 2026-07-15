package dataaccess;
import model.AuthData;
import java.util.HashMap;

public class  MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    public void createAuth(AuthData authData) throws DataAccessException{
        authTokens.put(authData.authToken(), authData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        return authTokens.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        authTokens.remove(authToken);
    }

    public void clearAllAuthData() throws DataAccessException{
        authTokens.clear();
    }
}