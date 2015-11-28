/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Saurabh
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;
    @FXML
    MenuItem aboutUs;
    @FXML
    AnchorPane anchor;
    @FXML
    ListView onlineUsers;
    @FXML
    TextField txtPort, txtName, txtKey, txtmsg, txtIP;
    @FXML
    TextArea showChat;
    @FXML
    Button btnConnect, btnSend;

    Client sc = null;

    @FXML
    private void handleButtonAction(ActionEvent event) { //send button action
        if (!txtmsg.getText().trim().equals("")) {
            sc.send(new Message("message", txtName.getText(), txtmsg.getText(), (String) onlineUsers.getSelectionModel().getSelectedItem()));
            txtmsg.setText("");
        }

    }

    @FXML
    private void aboutUsAction(ActionEvent event) { //About us menu clicked
        try {
            Desktop.getDesktop().browse(new URI(new File("help/about us.html").toURI().toString()));
        } catch (IOException | URISyntaxException e) {
            showChat.appendText("\nhelp contents not found");
        }

    }

    @FXML
    private void helpContentAction(ActionEvent event) { //Help Content menu clicked
        try {
            Desktop.getDesktop().browse(new URI(new File("help/client_help.html").toURI().toString()));
        } catch (IOException | URISyntaxException e) {
            showChat.appendText("\nhelp contents not found");
        }

    }
    
    @FXML
    private void handleConnectAction(ActionEvent event) {
        if (txtName.getText().trim().equals("") || txtPort.getText().trim().equals("") || txtIP.getText().trim().equals("") || txtKey.getText().trim().equals("")) {
            showChat.appendText("\nPlease fill all the information");
        } else if (Integer.parseInt(txtPort.getText()) <= 1000) {
            showChat.appendText("\nYou can not use port number below 1000");
        } else if (txtIP.getText().trim().split("\\.").length != 4) {
            showChat.appendText("\nPlease enter IP address in correct format, for eg '10.20.30.40'");
        } else if (txtKey.getText().length() > 6) {
            showChat.appendText("\nKey length should not more then 6");
        } else {
            int ip[] = new int[4];
            try {   //to check IP address is in numeric value or not
                ip[0] = Integer.parseInt(txtIP.getText().trim().split("\\.")[0]);
                ip[1] = Integer.parseInt(txtIP.getText().trim().split("\\.")[1]);
                ip[2] = Integer.parseInt(txtIP.getText().trim().split("\\.")[2]);
                ip[3] = Integer.parseInt(txtIP.getText().trim().split("\\.")[3]);
                if (!(ip[0] > 0 && ip[0] <= 255) || !(ip[1] >= 0 && ip[1] <= 255) || !(ip[2] >= 0 && ip[2] <= 255) || !(ip[3] > 0 && ip[3] < 255)) {
                    throw new Exception();
                }
            } catch (NumberFormatException e) {
                showChat.appendText("\nPlease enter IP address in correct format, for eg '10.20.30.40'");
                return;
            } catch (Exception e) {
                showChat.appendText("\nNot a valid IP address , address should be within the range of '1.0.0.1 and '255.255.255.254' both are included");
                return;
            }

            try {
                sc = new Client(this);
                Thread t = new Thread(sc);
                t.setDaemon(true);
                sc.send(new Message("login", txtName.getText(), "login message", txtKey.getText().trim()));
                t.start();
            } catch (Exception e) {
                if (e.toString().split(":")[1].trim().equals("1")) {
                    showChat.appendText("\nHost not found, Please ensure that IP address is correct, IP format is not valid, for eg '10.20.30.40'");
                } else if (e.toString().split(":")[1].trim().equals("Connection refused")) {
                    showChat.appendText("\nServer is not running");
                } else if (e.toString().split(":")[1].trim().equals("No route to host")) {
                    showChat.appendText("\nMake sure Server is running or IP address of server is correct");
                } else if (e.toString().split(":")[1].trim().equals("For input string")) {
                    showChat.appendText("\nNot a valid IP address, IP address should be in format like '10.20.30.40'");
                } else {
                    showChat.appendText("\n" + e.toString().split(":")[1].trim());
                }
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        // TODO
        onlineUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        btnSend.setDisable(true);
        onlineUsers.setDisable(true);
        txtmsg.setDisable(true);

        //send message on pressing enter while focus on msg text field
        txtmsg.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ENTER)) {
                    if (!txtmsg.getText().trim().equals("")) {
                        sc.send(new Message("message", txtName.getText(), txtmsg.getText(), (String) onlineUsers.getSelectionModel().getSelectedItem()));
                        txtmsg.setText("");
                    }
                }
                if (txtmsg.getText().length() > 0) {
                    btnSend.setDisable(false);
                } else {
                    btnSend.setDisable(true);
                }
            }
        });

        //Check the user has entered numeric value or not
        txtPort.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                try {
                    if (Character.isLetter(txtPort.getText().charAt(txtPort.getText().length() - 1))) {
                        txtPort.setText("");
                        showChat.appendText("\nPlease use number for specifying port number");
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println(e);
                }
            }
        });

        try {
            txtIP.setText(InetAddress.getLocalHost().toString().split("/")[1]);
        } catch (UnknownHostException e) {
            System.out.println(e);
        }
        showChat.setEditable(false);
    }

}
