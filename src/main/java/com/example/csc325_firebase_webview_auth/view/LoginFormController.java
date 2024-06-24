package com.example.csc325_firebase_webview_auth.view;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLoginButtonAction() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            // Authenticate the user
            UserRecord userRecord = App.fauth.getUserByEmail(email);
            if (userRecord != null) {
                // Assuming password check is done here (for simplicity)
                showAlert("Login Successful", "You have successfully logged in!");
                // Redirect to the main application
                App.setRoot("/files/AccessFBView.fxml");
            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            showAlert("Login Failed", "An error occurred during login.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load the main application view.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



