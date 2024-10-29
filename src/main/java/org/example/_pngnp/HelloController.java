package org.example._pngnp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    private ImageModel imageModel;

    public void setImage(ImageModel image) {
        this.imageModel = image;
        //firstNameField.textProperty().bindBidirectional(image.firstNameProperty());
        //lastNameField.textProperty().bindBidirectional(image.lastNameProperty());
    }
}