package server;
import com.google.gson.Gson;
import service.UserService;
import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;
import service.UnauthorizedException;
import service.BadRequestException;
import service.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Context ctx) {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            
            ctx.status(200);
            return gson.toJson(result);

        } catch (BadRequestException e) {
            ctx.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        } catch (DataAccessException e) {
            ctx.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    public Object login(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

            LoginResult result = userService.login(request);

            ctx.status(200);
            return gson.toJson(result);

        } catch (UnauthorizedException e) {
            ctx.status(401); // Unauthorized
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (DataAccessException e) {
            ctx.status(500); // Unknown Errors
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    public Object logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);

            ctx.status(200);
            return "{}";
        } catch (UnauthorizedException e) {
            ctx.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (DataAccessException e) {
            ctx.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }
}