import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoApp extends Application {
    private Connection conn;
    private int userId;
    private List<Task> tasks;

    @Override
    public void start(Stage primaryStage) throws Exception {
        String url = "jdbc:mysql://localhost:3306/todoapp";
        String user = "root";
        String password = "password";
        conn = DriverManager.getConnection(url, user, password);
        createTables();

        // Login screen
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Label loginErrorLabel = new Label();
        VBox loginBox = new VBox(usernameLabel, usernameField, passwordLabel, passwordField,
                                 loginButton, loginErrorLabel);
        loginBox.setSpacing(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(10));
        Scene loginScene = new Scene(loginBox, 300, 200);

        // Main screen
        Label taskLabel = new Label("Tasks:");
        ListView<Task> taskListView = new ListView<>();
        taskListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showTaskDetails(newValue));
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();
        Label dueDateLabel = new Label("Due date:");
        DatePicker dueDateField = new DatePicker();
        Button addButton = new Button("Add");
        Button completeButton = new Button("Complete");
        Button deleteButton = new Button("Delete");
        Button logoutButton = new Button("Logout");
        Label taskErrorLabel = new Label();
        VBox taskBox = new VBox(taskLabel, taskListView, nameLabel, nameField,
                                descriptionLabel, descriptionField, dueDateLabel, dueDateField,
                                addButton, completeButton, deleteButton, logoutButton,
                                taskErrorLabel);
        taskBox.setSpacing(10);
        taskBox.setAlignment(Pos.CENTER);
        taskBox.setPadding(new Insets(10));
        Scene taskScene = new Scene(taskBox, 600, 400);

        loginButton.setOnAction(event -> {
            try {
                userId = getUserId(usernameField.getText(), passwordField.getText());
                if (userId == -1) {
                    loginErrorLabel.setText("Invalid username or password");
                } else {
                    tasks = getTasks(userId);
                    taskListView.getItems().addAll(tasks);
                    primaryStage.setScene(taskScene);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                loginErrorLabel.setText("An error occurred");
            }
        });

        addButton.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String description = descriptionField.getText();
                LocalDate dueDate = dueDateField.getValue();
                if (name.isEmpty() || dueDate == null) {
                    taskErrorLabel.setText("Name and due date are required");
                } else {
                    Date sqlDueDate = Date.valueOf(dueDate);
                    addTask(userId, name, description, sqlDueDate);
                    Task task = new Task(-1,
