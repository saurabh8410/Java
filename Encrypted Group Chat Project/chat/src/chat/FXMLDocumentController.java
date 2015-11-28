/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Saurabh
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    public TextArea txtShow;

    @FXML
    public TextField txtPort;

    @FXML
    public Button btnStart;

    private SocketServer server;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        txtShow.clear();
        if (btnStart.getText().equals("Start Server")) {
            if (txtPort.getText().trim().equals("")) {
                txtShow.appendText("\nPlease enter a port number");
            } else if (Integer.parseInt(txtPort.getText()) <= 1000) {
                txtShow.appendText("\nYou can not use port number below 1000");
            }
            try {
                server = new SocketServer(this);
                server.setDaemon(true);
                server.start();
                btnStart.setText("Stop Server");
                txtPort.setDisable(true);

            } catch (Exception e) {
                if (e.toString().split(":")[1].equals("Address already in use")) {
                    txtShow.appendText("\nEither Server is already running on the given port or the port is in use by any other application");
                    txtShow.appendText("\nHint : You can change port number if you want");
                } else {
                    System.out.println("Error : " + e);
                }
            }
        } else {
            txtShow.appendText("\nServer has stopped");           
            btnStart.setText("Start Server");
            txtPort.setDisable(false);
            server.running=false;
            try {
                server.server.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }
    
    @FXML
    private void aboutUsAction(ActionEvent event) { //About us menu clicked
        try {
            Desktop.getDesktop().browse(new URI(new File("help/about us.html").toURI().toString()));
        } catch (IOException | URISyntaxException e) {
            txtShow.appendText("\nhelp contents not found");
        }

    }
    
    @FXML
    private void helpContentAction(ActionEvent event) { //Help Content menu clicked
        try {
            Desktop.getDesktop().browse(new URI(new File("help/server_help.html").toURI().toString()));
        } catch (IOException | URISyntaxException e) {
            txtShow.appendText("\nhelp contents not found");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        txtShow.setEditable(false);

        txtPort.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                try {
                    if (Character.isLetter(txtPort.getText().charAt(txtPort.getText().length() - 1))) {
                        txtPort.setText("");
                        txtShow.appendText("\nPlease use number for specifying port number");
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println(e);
                }
            }
        });
    }

}
