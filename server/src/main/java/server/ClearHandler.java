package server;
import service.ClearService;
import dataaccess.DataAccessException;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }
    
    public Object clear(io.javalin.http.Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            return "{}";
        } catch (DataAccessException e) {
            ctx.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }
}