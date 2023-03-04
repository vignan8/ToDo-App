import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TodoApp {
    private Connection conn;

    public TodoApp(String url, String user, String password) throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
        createTables();
    }

    public void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, " +
                     "username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL)";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS tasks (id INT PRIMARY KEY AUTO_INCREMENT, " +
              "user_id INT NOT NULL, name VARCHAR(255) NOT NULL, " +
              "description VARCHAR(255), due_date DATE, " +
              "completed BOOLEAN DEFAULT false, " +
              "FOREIGN KEY (user_id) REFERENCES users(id))";
        stmt.executeUpdate(sql);
    }

    public void addUser(String username, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (username, password) VALUES (?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.executeUpdate();
    }

    public int getUserId(String username, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM users WHERE username = ? AND password = ?");
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            return -1;
        }
    }

    public void addTask(int userId, String name, String description,
                        Date dueDate) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO tasks (user_id, name, description, due_date) " +
                "VALUES (?, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, name);
        stmt.setString(3, description);
        stmt.setDate(4, dueDate);
        stmt.executeUpdate();
    }

    public void completeTask(int taskId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE tasks SET completed = true WHERE id = ?");
        stmt.setInt(1, taskId);
        stmt.executeUpdate();
    }

    public void deleteTask(int taskId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM tasks WHERE id = ?");
        stmt.setInt(1, taskId);
        stmt.executeUpdate();
    }

    public List<Task> getTasks(int userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name, description, due_date, completed FROM tasks " +
                "WHERE user_id = ?");
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Task task = new Task(rs.getInt("id"), rs.getString("name"),
                                 rs.getString("description"), rs.getDate("due_date"),
                                 rs.getBoolean("completed"));
            tasks.add(task);
        }
        return tasks;
    }

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/todoapp";
        String user = "root";
        String password = "password";
        try {
            TodoApp app = new TodoApp(url, user, password);
            app.addUser("john", "password");
            int userId = app.getUserId("john", "password");
            app.addTask(userId, "Do laundry", "Wash clothes and fold them", Date.valueOf("2023-03
