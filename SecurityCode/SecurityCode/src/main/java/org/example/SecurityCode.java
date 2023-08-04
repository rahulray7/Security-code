package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class SecurityCode {
    static User user = null;

    public static long rand(){
        return ThreadLocalRandom.current().nextInt(100000,999999);
    }
    
    public static void connect_db() throws SQLException{
            System.out.println("Database Connected");
            String url = "jdbc:mysql://localhost:3306/user";
            String sqlUser = "root";
            String password = "jabir";
            Connection connection;
            String sql = "INSERT INTO user (email, security_code, iv, password) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt;
            connection = DriverManager.getConnection(url, sqlUser, password);
            stmt = connection.prepareStatement(sql);
            stmt.setObject(1, user.getEmail());
            stmt.setObject(2, rand());
            stmt.setObject(3, Client.encodedIV);
            stmt.setObject(4, Client.encodedPassword);
            stmt.executeUpdate();
            System.out.println("User Added Successfully");
    }

    public static void main(String[] args) {
        System.out.println("Started...");
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        Route postHandler = router.post("/addUser")
                .handler(BodyHandler.create())
                .handler(routingContext -> {
                    System.out.println("Hello");
                    HttpServerResponse serverResponse = routingContext.response();
                    serverResponse.setChunked(true);
                    serverResponse.end();
                });

        server
                .requestHandler(router::accept)
                .listen(9090);
    }
}


