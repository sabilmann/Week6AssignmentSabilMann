package com.example.csc325_firebase_webview_auth.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SplashScreenController {
    @FXML
    private Label splashLabel;

    public void initialize() {
        splashLabel.setText("Loading...");
    }
}

