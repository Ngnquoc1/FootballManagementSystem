package Controller;

import Test.test;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class FxmlLoader {
    private VBox view;

    public VBox getPane(String fileName) {
        try {
            URL fileUrl = test.class.getResource("/View/"+fileName+".fxml");
            if(fileUrl == null) {
                throw new IOException("File not found: " + "/View/"+fileName+".fxml");
            }
            view= FXMLLoader.load(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }
}
