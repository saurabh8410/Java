/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class Client implements Runnable {

    public int port = 13000;
    public Socket socket = null;
    public ObjectInputStream In = null;
    public ObjectOutputStream Out = null;
    public FXMLDocumentController userInterface = null;
    String user = "";
    static String onlineUsers[] = new String[50];
    Play p = new Play();

    public Client(FXMLDocumentController userInterface) throws IOException, Exception {
        port = Integer.parseInt(userInterface.txtPort.getText());
        String ip[] = userInterface.txtIP.getText().split("\\.");
        byte b[] = new byte[4];
        for (int i = 0; i < ip.length; i++) {
            b[i] = new Integer(Integer.parseInt(ip[i])).byteValue();
        }
        socket = new Socket(InetAddress.getByAddress(b), port);
        this.userInterface = userInterface;
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            try {
                System.out.println("in");
                Message msg = (Message) In.readObject();
                System.out.println("in");
                if (!msg.sender.contains("Server") && !msg.content.equals("")) {
                    p.sp = msg.content.split("@@")[1];
                    msg.content = msg.content.split("@@")[0];
                    msg.content = AutoKey.decryption(msg.content);
                    msg.content = p.decryption(msg.content);
                    System.out.println("msg = " + msg.content);
                }
                System.out.println("Incoming : " + msg.toString());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (msg.type.equals("login")) {
                            String u[] = msg.content.split(",");
                            ObservableList<String> items = FXCollections.observableArrayList(msg.content.split(","));
                            userInterface.onlineUsers.setItems(items);
                            userInterface.onlineUsers.getSelectionModel().selectFirst();
                            if (!msg.recipient.split("accept")[1].equals(user)) {
                                userInterface.showChat.appendText("\nServer <" + (new Date()).toString().split(" ")[3] + "> >" + u[u.length - 1] + " has logged in");
                            } else {
                                userInterface.showChat.appendText("\nServer <" + (new Date()).toString().split(" ")[3] + "> >" + " You have logged in successfully.");
                                userInterface.txtIP.setDisable(true);
                                userInterface.txtKey.setDisable(true);
                                userInterface.txtName.setDisable(true);
                                userInterface.txtPort.setDisable(true);
                                userInterface.btnConnect.setDisable(true);
                                userInterface.txtmsg.setDisable(false);
                                userInterface.onlineUsers.setDisable(false);
                            }
                        } else if (msg.type.equals("user quit")) {
                            userInterface.showChat.appendText("\nServer <" + (new Date()).toString().split(" ")[3] + "> >" + " You have logged out successfully.");
                            userInterface.txtIP.setDisable(false);
                            userInterface.txtKey.setDisable(false);
                            userInterface.txtName.setDisable(false);
                            userInterface.txtPort.setDisable(false);
                            userInterface.btnConnect.setDisable(false);
                            userInterface.txtmsg.setDisable(true);
                            userInterface.onlineUsers.setDisable(true);
                            userInterface.btnSend.setDisable(true);
                            userInterface.onlineUsers.setItems(null);
                        } else if (msg.type.equals("error")) {
                            userInterface.showChat.appendText("\nServer <" + (new Date()).toString().split(" ")[3] + "> >" + msg.content);
                        } else if (msg.type.equals("logout")) {
                            if (user.equals(msg.sender.split("-")[1])) {
                                msg.type = "stop";
                            } else {
                                String u[] = msg.content.split(",");
                                ObservableList<String> items = FXCollections.observableArrayList(msg.content.split(","));
                                userInterface.onlineUsers.setItems(items);
                                userInterface.showChat.appendText("\nServer <" + (new Date()).toString().split(" ")[3] + "> >" + msg.sender.split("-")[1] + " has logged out");
                            }
                        } else if (msg.sender.equals(user)) {
                            userInterface.showChat.appendText("\nMe --> " + msg.recipient + " <" + (new Date()).toString().split(" ")[3] + "> >" + msg.content);
                        } else {
                            userInterface.showChat.appendText("\n" + msg.sender + " <" + (new Date()).toString().split(" ")[3] + "> >" + msg.content);
                        }
                    }
                });
                if (msg.type.equals("error") || msg.type.equals("stop")) {
                    running = false;
                }
            } catch (Exception e) {
                System.out.println("Error:" + e);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        userInterface.showChat.clear();
                        userInterface.showChat.appendText("\nCan't reach Sever. Please make sure that Server is running.");
                        userInterface.onlineUsers.setItems(null);
                        userInterface.onlineUsers.setDisable(true);
                        userInterface.btnSend.setDisable(true);
                        userInterface.txtmsg.setDisable(true);
                        userInterface.txtIP.setDisable(false);
                        userInterface.txtKey.setDisable(false);
                        userInterface.txtName.setDisable(false);
                        userInterface.txtPort.setDisable(false);
                        userInterface.btnConnect.setDisable(false);

                    }
                });
                running = false;
            }
        }
    }

    public void send(Message msg) {
        if (msg.type.equals("login")) {
            user = msg.sender;
            p.setKey(userInterface.txtKey.getText().trim());
        }
        try {
            if (msg.type.equals("message") && !msg.recipient.equals("Server")) {
                p.en = "";
                p.sp = "";
                msg.content = p.encryption(msg.content);
                msg.content = AutoKey.encryption(msg.content);
                msg.content = msg.content + "@@" + p.sp; //to diiffrenciate between contents and space between them
                System.out.println("msg = " + msg.content);
            }
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : " + msg);
        } catch (IOException ex) {
            userInterface.showChat.appendText("\nDue to some reason message did not send, please try again.");
            System.out.println(ex);
        }
    }

    public void closeThread(Thread t) {
        t = null;
    }

    /*public static void main(String[] args) throws Exception {
     Client sc = new Client();
     Scanner s = new Scanner(System.in);
     System.out.println("Enter sender name");
     String sender = s.next();
     System.out.println("Enter receiver name");
     String receiver = s.next();
     sc.send(new Message("login", sender, "sdfsdfasd", receiver));
     sc.send(new Message("message", sender, "sdfsdfasd", receiver));
     Thread t = new Thread(sc);
     t.start();
     }*/
}
