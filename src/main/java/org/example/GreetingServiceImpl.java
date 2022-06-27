package org.example;


import com.example.grpc.client.*;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    String url = "jdbc:postgresql://abul.db.elephantsql.com:5432/nspkcjrg", user = "nspkcjrg", password = "HvwCWSw9piCjZdNVL7MpHoBMSgCLDRFY";
    Connection connection = null;
    Statement statement = null;

    GreetingServiceImpl() {
        System.out.println("INIT");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println("Driver error");
            System.exit(1);
        }
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful!\n");
        } catch (SQLException e) {
            System.out.println("Connection error\n" + e.getMessage());
            System.exit(1);
        }
        System.out.println("FINISH INIT");
    }

    @Override
    public void addUser(addUserRequest request, StreamObserver<addUserResponse> responseStreamObserver) {
        System.out.println("ADD USER");
        addUserResponse.Builder response = addUserResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        try {
            statement.execute("INSERT INTO users VALUES ('" + request.getFirstName() + "', '" + request.getSecondName() + "', '" + request.getEmail() + "', '" + request.getPassword() + "');");
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        responseStreamObserver.onNext(response.setCode(true).build());
        responseStreamObserver.onCompleted();
        System.out.println("FINISH ADD USER");
    }

    @Override
    public void containsUser(containsUserRequest request, StreamObserver<containsUserResponse> responseStreamObserver) {
        System.out.println("CONTAINS USER");
        containsUserResponse.Builder response = containsUserResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery("SELECT count(*) FROM users WHERE email='" + request.getEmail() + "';");
            resultSet.next();
            if (resultSet.getInt("count") != 1) {
                responseStreamObserver.onNext(response.setCode(false).build());
                responseStreamObserver.onCompleted();
            } else {
                responseStreamObserver.onNext(response.setCode(true).build());
                responseStreamObserver.onCompleted();
            }
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        System.out.println("FINISH CONTAINS USER");
    }

    @Override
    public void containsPassword(containsPasswordRequest request, StreamObserver<containsPasswordResponse> responseStreamObserver) {
        System.out.println("CONTAINS PASSWORD");
        containsPasswordResponse.Builder response = containsPasswordResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery("SELECT count(*) FROM users WHERE email = '" + request.getEmail() + "' AND password = '" + request.getPassword() + "';");
            if (resultSet == null) {
                throw new SQLException("Empty result set");
            }
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        try {
            resultSet.next();
            if (resultSet.getInt("count") != 1) {
                responseStreamObserver.onNext(response.setCode(false).build());
            } else {
                responseStreamObserver.onNext(response.setCode(true).build());
                responseStreamObserver.onCompleted();
            }
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        System.out.println("FINISH CONTAINS PASSWORD");
    }

    @Override
    public void getUser(getUserRequest request, StreamObserver<getUserResponse> responseStreamObserver) {
        System.out.println("GET USER");
        getUserResponse.Builder response = getUserResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .setEmail("")
                    .setPassword("")
                    .setFirstName("")
                    .setSecondName("")
                    .build());
            responseStreamObserver.onCompleted();
            return;
        }
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery("SELECT * FROM users WHERE email = '" + request.getLogin() + "';");
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .setEmail("")
                    .setPassword("")
                    .setFirstName("")
                    .setSecondName("")
                    .build());
            responseStreamObserver.onCompleted();
            return;
        }
        try {
            resultSet.next();
            responseStreamObserver.onNext(response
                    .setFirstName(resultSet.getString("firstName"))
                    .setSecondName(resultSet.getString("secondName"))
                    .setEmail(resultSet.getString("email"))
                    .setPassword(resultSet.getString("password"))
                    .build());
            responseStreamObserver.onCompleted();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .setEmail("")
                    .setPassword("")
                    .setFirstName("")
                    .setSecondName("")
                    .build());
            responseStreamObserver.onCompleted();
        }
        System.out.println("FINISH GET USER");
    }

    @Override
    public void getUserTracks(getUserTracksRequest request, StreamObserver<getUserTracksResponse> responseStreamObserver) {
        System.out.println("GET USER TRACKS");
        getUserTracksResponse.Builder response = getUserTracksResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .addAllAuthor(new ArrayList<>())
                    .addAllId(new ArrayList<>())
                    .addAllUrl(new ArrayList<>())
                    .addAllName(new ArrayList<>())
                    .build());
            responseStreamObserver.onCompleted();
            return;
        }
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery("SELECT * from mixed_tracks WHERE author = '" + request.getLogin() + "';");
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .addAllAuthor(new ArrayList<>())
                    .addAllId(new ArrayList<>())
                    .addAllUrl(new ArrayList<>())
                    .addAllName(new ArrayList<>())
                    .build());
            responseStreamObserver.onCompleted();
            return;
        }
        List<String> names = new ArrayList<>();
        List<String> author = new ArrayList<>();
        List<String> url = new ArrayList<>();
        List<Integer> id = new ArrayList<>();
        try {
            assert resultSet != null;
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
                author.add(resultSet.getString("author"));
                url.add(resultSet.getString("url"));
                id.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .addAllAuthor(new ArrayList<>())
                    .addAllId(new ArrayList<>())
                    .addAllUrl(new ArrayList<>())
                    .addAllName(new ArrayList<>())
                    .build());
            responseStreamObserver.onCompleted();
            return;
        }
        responseStreamObserver.onNext(response
                .addAllName(names)
                .addAllAuthor(author)
                .addAllUrl(url)
                .addAllId(id)
                .build());
        responseStreamObserver.onCompleted();
        System.out.println("FINISH GET USER TRACKS");
    }

    @Override
    public void addTrackToUser(addTrackToUserRequest request, StreamObserver<addTrackToUserResponse> responseStreamObserver) {
        System.out.println("ADD TRACK TO USER");
        addTrackToUserResponse.Builder response = addTrackToUserResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        try {
            statement.execute("INSERT INTO mixed_tracks VALUES ('" + request.getName() + "', '" + request.getAuthor() + "', '" + request.getUrl() + "', " + request.getId() + ");");
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        responseStreamObserver.onNext(response.setCode(true).build());
        responseStreamObserver.onCompleted();
        System.out.println("FINISH ADD TRACK TO USER");
    }

    @Override
    public void getDefaultTracks(com.google.protobuf.Empty empty, StreamObserver<getDefaultTracksResponse> responseStreamObserver) {
        System.out.println("GET DEFAULT TRACKS");
        getDefaultTracksResponse.Builder response = getDefaultTracksResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .addAllAuthor(new ArrayList<>())
                    .addAllId(new ArrayList<>())
                    .addAllUrl(new ArrayList<>())
                    .addAllName(new ArrayList<>())
                    .build());
            responseStreamObserver.onCompleted();
            return;
        }
        ResultSet resultSet;
        List<String> text_link = new ArrayList<>();
        List<Integer> id = new ArrayList<>();
        List<String> track_link = new ArrayList<>();
        List<String> name = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM default_tracks;");
        } catch (SQLException e) {
            response.addAllAuthor(text_link);
            response.addAllId(id);
            response.addAllUrl(track_link);
            response.addAllName(name);
            responseStreamObserver.onNext(response.build());
            responseStreamObserver.onCompleted();
            return;
        }
        try {
            while (resultSet.next()) {
                name.add(resultSet.getString("name"));
                text_link.add(resultSet.getString("text_link"));
                track_link.add(resultSet.getString("track_link"));
                id.add(Integer.parseInt(resultSet.getString("id")));
            }
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .addAllAuthor(new ArrayList<>())
                    .addAllId(new ArrayList<>())
                    .addAllUrl(new ArrayList<>())
                    .addAllName(new ArrayList<>())
                    .build());
            responseStreamObserver.onCompleted();
            return;
        }
        response.addAllAuthor(text_link);
        response.addAllId(id);
        response.addAllUrl(track_link);
        response.addAllName(name);
        responseStreamObserver.onNext(response.build());
        responseStreamObserver.onCompleted();
        System.out.println("FINISH GET DEFAULT TRACKS");
    }

    public void getAllUserEmails(com.google.protobuf.Empty empty, StreamObserver<getAllUserEmailsResponse> responseStreamObserver) {
        System.out.println("GET ALL USER EMAILS");
        getAllUserEmailsResponse.Builder response = getAllUserEmailsResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.addAllName(new ArrayList<>()).build());
            responseStreamObserver.onCompleted();
            return;
        }
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery("SELECT * FROM users;");
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.addAllName(new ArrayList<>()).build());
            responseStreamObserver.onCompleted();
            return;
        }
        ArrayList<String> emails = new ArrayList<>();
        try {
            while (resultSet.next()) {
                emails.add(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.addAllName(new ArrayList<>()).build());
            responseStreamObserver.onCompleted();
            return;
        }
        responseStreamObserver.onNext(response.addAllName(emails).build());
        responseStreamObserver.onCompleted();
        System.out.println("FINISH GET ALL USER EMAILS");
    }

    @Override
    public void addLike(addLikeRequest request, StreamObserver<addLikeResponse> responseStreamObserver) {
        System.out.println("ADD LIKE");
        addLikeResponse.Builder response = addLikeResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        try {
            statement.execute("INSERT INTO likes VALUES ('" + request.getLogin() + "', " + request.getId() + ");");
        } catch (SQLException e) {
            e.printStackTrace();
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        responseStreamObserver.onNext(response.setCode(true).build());
        responseStreamObserver.onCompleted();
        System.out.println("FINISH ADD LIKE");
    }

    @Override
    public void removeLike(removeLikeRequest request, StreamObserver<removeLikeResponse> responseStreamObserver) {
        System.out.println("REMOVE LIKE");
        removeLikeResponse.Builder response = removeLikeResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        try {
            statement.execute("DELETE FROM likes WHERE login='" + request.getLogin() + "' AND id=" + request.getId() + ';');
        } catch (SQLException e) {
            e.printStackTrace();
            responseStreamObserver.onNext(response.setCode(false).build());
            responseStreamObserver.onCompleted();
            return;
        }
        responseStreamObserver.onNext(response.setCode(true).build());
        responseStreamObserver.onCompleted();
        System.out.println("FINISH REMOVE LIKE");
    }

    @Override
    public void getLikedTracks(getLikedTracksRequest request, StreamObserver<getLikedTracksResponse> responseStreamObserver) {
        System.out.println("GET LIKED TRACKS");
        getLikedTracksResponse.Builder response = getLikedTracksResponse.newBuilder();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .addAllAuthor(new ArrayList<>())
                    .addAllName(new ArrayList<>())
                    .addAllUrl(new ArrayList<>())
                    .addAllId(new ArrayList<>()).build());
            responseStreamObserver.onCompleted();
            return;
        }
        ResultSet resultSet;
        ArrayList<Integer> id = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM likes WHERE login='" + request.getLogin() + "';");
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .addAllAuthor(new ArrayList<>())
                    .addAllName(new ArrayList<>())
                    .addAllUrl(new ArrayList<>())
                    .addAllId(new ArrayList<>()).build());
            responseStreamObserver.onCompleted();
            return;
        }
        try {
            while (resultSet.next()) {
                id.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            responseStreamObserver.onNext(response
                    .addAllAuthor(new ArrayList<>())
                    .addAllName(new ArrayList<>())
                    .addAllUrl(new ArrayList<>())
                    .addAllId(new ArrayList<>()).build());
            responseStreamObserver.onCompleted();
            return;
        }
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> author = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();
        for (Integer integer : id) {
            try {
                resultSet = statement.executeQuery("SELECT * FROM default_tracks WHERE id=" + integer + ";");
                while (resultSet.next()) {
                    name.add(resultSet.getString("name"));
                    author.add(resultSet.getString("text_link"));
                    url.add(resultSet.getString("track_link"));
                }
            } catch (SQLException e) {
                responseStreamObserver.onNext(response
                        .addAllAuthor(new ArrayList<>())
                        .addAllName(new ArrayList<>())
                        .addAllUrl(new ArrayList<>())
                        .addAllId(new ArrayList<>()).build());
                responseStreamObserver.onCompleted();
                return;
            }
        }
        responseStreamObserver.onNext(response.addAllId(id).addAllUrl(url).addAllName(name).addAllAuthor(author).build());
        responseStreamObserver.onCompleted();
    }
}
