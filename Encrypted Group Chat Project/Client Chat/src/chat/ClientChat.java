/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Saurabh
 */
public class ClientChat extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setMinHeight(450);
        stage.setMinWidth(620);

        stage.setMaxHeight(550);
        stage.setMaxWidth(680);

        stage.setTitle("Encrypted Group Chat");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
