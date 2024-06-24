package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.nio.file.Paths;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AccessFBView {

    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField ageField;
    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;
    @FXML
    private TextArea outputField;
    @FXML
    private TableView<Person> tableView;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> majorColumn;
    @FXML
    private TableColumn<Person, Integer> ageColumn;
    @FXML
    private Button uploadButton;

    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    public void initialize() {
        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        tableView.setItems(listOfUsers);
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
    }

    @FXML
    private void readRecord(ActionEvent event) {
        readFirebase();
    }

    @FXML
    private void regRecord(ActionEvent event) {
        registerUser();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("/files/WebContainer.fxml");
    }

    @FXML
    private void showRegistrationForm() throws IOException {
        App.setRoot("/files/RegistrationForm.fxml");
    }

    @FXML
    private void showLoginForm() throws IOException {
        App.setRoot("/files/LoginForm.fxml");
    }
    @FXML
    private void closeApplication() {
        Stage stage = (Stage) writeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Check firebase.com for help.");
        alert.showAndWait();
    }
    public void addData() {
        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        // asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    @FXML
    private void uploadProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) uploadButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                uploadFileToFirebase(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadFileToFirebase(File file) throws IOException {
        FileInputStream serviceAccount = new FileInputStream("C:\\Users\\sbman\\Downloads\\week6csc325sm-firebase-adminsdk-ficft-0306158de6.json");

        StorageOptions options = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        Storage storage = options.getService();

        String bucketName = "week6csc325sm.appspot.com";
        String blobName = "profile_pictures/" + file.getName();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getPath())));

        System.out.println("Profile picture uploaded successfully.");
    }


    public boolean readFirebase() {
        // Clear existing data
        listOfUsers.clear();

        // asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            if (documents.size() > 0) {
                for (QueryDocumentSnapshot document : documents) {
                    Person person = new Person(
                            String.valueOf(document.getData().get("Name")),
                            document.getData().get("Major").toString(),
                            Integer.parseInt(document.getData().get("Age").toString())
                    );
                    listOfUsers.add(person);
                }
            } else {
                System.out.println("No data");
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public void sendVerificationEmail() {
        try {
            UserRecord user = App.fauth.getUser("name");
            // String url = user.getPassword();

        } catch (Exception e) {
        }
    }

    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail("user@example.com")
                .setEmailVerified(false)
                .setPassword("secretPassword")
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = App.fauth.createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
