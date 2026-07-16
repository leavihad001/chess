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

    public void register(Context ctx) {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result("{ \"message\": \"Error: bad request\" }");
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result("{ \"message\": \"Error: already taken\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

            LoginResult result = userService.login(request);

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (BadRequestException e) { // Catch the new 400 error (see Fix 2)
            ctx.status(400);
            ctx.result("{ \"message\": \"Error: bad request\" }");
        } catch (UnauthorizedException e) {
            ctx.status(401); // Unauthorized
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
        } catch (DataAccessException e) {
            ctx.status(500); // Unknown Errors
            ctx.result("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);

            ctx.status(200);
            ctx.result("{}");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }
}