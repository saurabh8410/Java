/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.*;
import java.net.*;
import javafx.application.Platform;

//Message directing
class ServerThread extends Thread {

    public SocketServer server = null;
    public Socket socket = null;
    public int ID = -1;
    public String username = "";
    String key = "";
    public ObjectInputStream streamIn = null;
    public ObjectOutputStream streamOut = null;

    ServerThread(SocketServer server, Socket socket) {
        super();
        this.server = server;
        this.socket = socket;
        ID = socket.getPort();
    }

    public int getID() {
        return ID;
    }

    void open() throws IOException {
        streamOut = new ObjectOutputStream(socket.getOutputStream());
        streamOut.flush();
        streamIn = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        System.out.println("Server thread " + ID + " Running");

        while (true) {
            try {
                Message msg = (Message) streamIn.readObject();
                //System.out.println("before:" + msg);

                server.handle(ID, msg);//identify the message type
                //System.out.println("After");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(ID + " ERROR reading: " + e);
                if (e.toString().equals("java.net.SocketException: Connection reset")) {
                    server.handle(ID, new Message("logout", "Server", username, "All"));
                }
                stop();
            }
        }
    }

    public void send(Message msg) {
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
        } catch (IOException ex) {
            System.out.println("Exception [SocketClient : send(...)]");
        }
    }

    /*public static void main(String[] args) throws Exception {
     SocketServer server = new SocketServer();
     server.start();
     }*/
}

 //class to create connection
//to create thread for each client
class SocketServer extends Thread {

    ServerThread clients[];
    ServerSocket server = null;
    Socket socket = null;
    Thread thread = null;
    boolean running = true;
    int port = 13000, clientCount = 0;
    FXMLDocumentController userInterface;

    /*SocketServer() throws Exception {
     server = new ServerSocket(port);
     clients = new ServerThread[50];
     System.out.println("Server started address:" + InetAddress.getLocalHost() + " at port : " + server.getLocalPort());
     }*/
    SocketServer(FXMLDocumentController userInterface) throws Exception {
        port = Integer.parseInt(userInterface.txtPort.getText());
        server = new ServerSocket(port);
        this.userInterface = userInterface;
        clients = new ServerThread[50];
        userInterface.txtShow.appendText("\nServer started on address: " + InetAddress.getLocalHost().toString().split("/")[1] + " at port : " + server.getLocalPort());
        userInterface.txtPort.setDisable(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        userInterface.txtShow.appendText("\nWaiting for client");
                    }
                });
                addThread(server.accept());
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        for (int i = 0; i < clientCount; i++) {
            try {
                clients[i].socket.close();
                clients[i] = null;
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    userInterface.txtShow.appendText("\nClient accepted : " + socket);
                }
            });
            //System.out.println("Client accepted" + socket);
            clients[clientCount] = new ServerThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                System.out.println("\nError opening thread: " + ioe);
            }
        } else {
            //System.out.println("\nClient refused: maximum " + clients.length + " reached.");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    userInterface.txtShow.appendText("\nClient refused: maximum " + clients.length + " reached.");
                }
            });
        }

    }

    public synchronized void handle(int ID, Message msg) {
        if (msg.type.equals("login")) {
            String u = ""; //usernames that are logged in
            clients[findClient(ID)].key = msg.recipient;
            clients[findClient(ID)].username = msg.sender;

            if (clientCount >= 2) {
                if (!clients[findClient(ID)].key.equals(clients[0].key)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            userInterface.txtShow.appendText("\nClient with username '" + msg.sender + "' was trying to connect");
                        }
                    });
                    clients[findClient(ID)].send(new Message("error", "Server", "Unauthorized user", msg.sender));
                    clients[findClient(ID)] = null;
                    clientCount--;
                    return;
                }
            }

            for (int i = 0; i < clientCount - 1; i++) { //check user already exist or not
                if (msg.sender.equals(clients[i].username)) {
                    clients[findClient(ID)].send(new Message("error", "Server", "Username already exist", msg.sender));
                    clients[findClient(ID)] = null;
                    clientCount--;
                    return;
                }
            }

            if (clientCount >= 1) {
                for (int i = 0; i < clientCount; i++) { //notify all users about those user who are online
                    u = "Server,All,";
                    for (int j = 0; j < clientCount; j++) {
                        if (!clients[j].username.equals(clients[i].username)) {
                            u = u + clients[j].username + ",";
                        }
                    }
                    clients[i].send(new Message("login", "Server Notify", u, "all" + "accept" + msg.sender));
                }
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    userInterface.txtShow.appendText("\nClient with username '" + msg.sender + "' is conected with server");
                }
            });

        } else if (msg.type.equals("logout")) {
            String logoutUser = msg.content;
            if (clientCount > 1) {
                for (int i = 0; i < clientCount; i++) {
                    if (clients[i].username.equals(logoutUser)) {
                        clients[i].send(new Message("logout", "Server-" + logoutUser, "", "all"));
                        for (int j = i; j < clientCount - 1; j++) {
                            clients[j] = clients[j + 1];
                        }
                        clientCount--;
                        break;
                    }
                }
                String u = "";

                for (int i = 0; i < clientCount; i++) { //notify all users about that user who is logged out
                    u = "Server,All,";
                    for (int j = 0; j < clientCount; j++) {
                        if (!clients[j].username.equals(clients[i].username)) {
                            u = u + clients[j].username + ",";
                        }
                    }
                    System.out.println("\n" + u);
                    clients[i].send(new Message("logout", "Server-" + logoutUser, u, "all"));
                }

            } else {
                clients[0] = null;
                clientCount--;
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    userInterface.txtShow.appendText("\nClient with username '" + logoutUser + "' no longer exist");
                }
            });

        } else if (msg.type.equals("message") && msg.recipient.equals("Server")) { //message sent to server are handled here
            if (!msg.content.trim().equals("logout")) {
                clients[findClient(ID)].send(new Message(msg.type, "Server", "Wrong command \nLOGOUT - to logout", msg.sender));
            } else {
                if (msg.content.toLowerCase().trim().equals("logout")) {
                    //handle(ID, new Message("logout", "Server", msg.sender, "All"));
                    String logoutUser = msg.content;
                    clients[findClient(ID)].send(new Message("user quit", "Server", "", logoutUser));
                    if (clientCount > 1) {
                        for (int i = 0; i < clientCount; i++) {
                            if (clients[i].username.equals(logoutUser)) {
                                clients[i].send(new Message("logout", "Server-" + logoutUser, "", "all"));
                                for (int j = i; j < clientCount - 1; j++) {
                                    clients[j] = clients[j + 1];
                                }
                                clientCount--;
                                break;
                            }
                        }
                        String u = "";

                        for (int i = 0; i < clientCount; i++) { //notify all users about that user who is logged out
                            u = "Server,All,";
                            for (int j = 0; j < clientCount; j++) {
                                if (!clients[j].username.equals(clients[i].username)) {
                                    u = u + clients[j].username + ",";
                                }
                            }
                            System.out.println("\n" + u);
                            clients[i].send(new Message("logout", "Server-" + logoutUser, u, "all"));
                        }
                    } else {
                        clients[0] = null;
                        clientCount--;
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            userInterface.txtShow.appendText("\nClient with username '" + logoutUser + "' no longer exist");
                        }
                    });
                }
            }
        } else if (msg.type.equals("message") && msg.recipient.equals("All")) {
            findUserThread(msg.sender).send(new Message(msg.type, msg.sender, msg.content, msg.recipient));
            for (int i = 0; i < clientCount; i++) {
                if (!msg.sender.equals(clients[i].username)) {
                    clients[i].send(new Message(msg.type, msg.sender, msg.content, msg.recipient));
                }
            }
        } else {
            findUserThread(msg.recipient).send(new Message(msg.type, msg.sender, msg.content, msg.recipient)); //to send message to receiver
            clients[findClient(ID)].send(new Message(msg.type, msg.sender, msg.content, msg.recipient)); //to send message to sender
        }
    }

    public ServerThread findUserThread(String usr) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].username.equals(usr)) {
                return clients[i];
            }
        }
        return null;
    }

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getID() == ID) {
                return i;
            }
        }
        return -1;
    }

}
