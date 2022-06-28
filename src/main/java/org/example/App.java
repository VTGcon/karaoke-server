package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    static void createServer() throws Exception {
        Server server = ServerBuilder.forPort(50051).addService(new GreetingServiceImpl()).build();
        server.start();
        System.out.println("ABOBA!");
        server.awaitTermination();
    }

    static void run() {
        try {
            createServer();
        } catch (Exception e) {
            System.out.println("ABOBA ended(");
            e.printStackTrace();
            run();
        }
    }

    public static void main(String[] args) {
        run();
    }
}
