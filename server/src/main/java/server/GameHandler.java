package server;

import com.google.gson.Gson;
import service.*;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            ListGamesResult result = gameService.listGames(authToken);

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);

            CreateGameResult result = gameService.createGame(authToken, request);

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result("{ \"message\": \"Error: bad request\" }");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);

            gameService.joinGame(authToken, request);

            ctx.status(200);
            ctx.result("{}");

        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result("{ \"message\": \"Error: bad request\" }");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result("{ \"message\": \"Error: already taken\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }
}