package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import java.util.UUID;

public class RegisterService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws BadRequestException, AlreadyTakenException, DataAccessException {
        if (request.username() == null || request.username().isEmpty() || request.password() == null ||
                request.password().isEmpty() || request.email() == null || request.email().isEmpty()) {
            throw new BadRequestException("Error: bad request");
        }

        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);

        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, request.username());
        authDAO.createAuth(newAuth);

        return new RegisterResult(request.username(), authToken);
    }
}