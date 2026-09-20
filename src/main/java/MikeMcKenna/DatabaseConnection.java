package MikeMcKenna;

import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Omaha_26";

    public static void main(String[] args) {
        System.out.println("Connecting to postgreSQL...");

        try
                ( Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            Scanner scanner = new Scanner(System.in)){
                System.out.println("Connected successfully");
                boolean running = true;
                while (running) {

                    System.out.println("\n=== MUSIC TRACK MANAGER === ");
                    System.out.println("1. View All Songs");
                    System.out.println("2. Add a New Song");
                    System.out.println("3. Delete a Song by ID");
                    System.out.println("4. Search Songs (Title or Artist)");
                    System.out.println("5. Update Song's Details");
                    System.out.println("6. Exit Application");
                    System.out.print("Chose an option(1-6): ");

                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            printMusicList(connection);
                            break;
                        case 2:
                            addNewSong(connection, scanner);
                            break;
                        case 3:
                            deleteSong(connection, scanner);
                            break;
                        case 4:
                            searchSongs(connection, scanner);
                            break;
                        case 5:
                            updateSong(connection, scanner);
                            break;
                        case 6:
                            running = false;
                            System.out.println(" Closing application. Goodbye!");
                            break;
                        default:
                            System.out.println("Invalid choice! Please choose between 1 and 6 ");

                    }

                }
            } catch(SQLException e){
                System.err.println("Database.Error code: " + e.getErrorCode());
                System.err.println("SQLState: " + e.getSQLState());
                System.out.println("Database operation failed:" + e.getMessage());
            }
        }

        public static void printMusicList(Connection connection) throws SQLException {
        String sql = "SELECT id, title, artist FROM music_list ORDER By id ASC";


         try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()){
              System.out.println("\n---CURRENT MUSIC LIST---");
              boolean hasData = false;
              while (resultSet.next()) {
                  hasData = true;
                  int id = resultSet.getInt("id");
                  String title = resultSet.getString("title");
                  String artist = resultSet.getString("artist");
                  System.out.println("ID: " + id + "| Title: " + title + "| Artist: " + artist);
              }
                if (!hasData) {
                    System.out.println("(The track list is currently empty)");
                }
            }

        }
        public static void addNewSong(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter song title: ");
        String title = scanner.nextLine();
        System.out.println("Enter artist name: ");
        String artist = scanner.nextLine();
        String sql = "INSERT INTO music_list (title, artist) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, artist);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Success! '" + title + "' by " + artist + " added to database. ");
            }else{
                System.out.println("Noticed! That exact song and artist combo already exists! Skipped duplicate. ");
            }
        }
        }
        public static void deleteSong(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID number of the song to delete: ");
        int idToDelete = scanner.nextInt();
        scanner.nextLine();
        String sql = "DELETE FROM music_list WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idToDelete);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Success! TRACKID:  + idToDelete + was permanently deleted. ");

            }else{
                System.out.println("Error: no song found with ID "  + idToDelete);
            }
        }

        }
        public static void searchSongs(Connection connection, Scanner scanner) throws SQLException {
         scanner.nextLine();
        System.out.println("Enter a keyword to search (title or artist): ");
        String keyword = scanner.nextLine();
        String searchPattern = "%" + keyword + "%";

        String sql = "SELECT id, title, artist  FROM music_list WHERE title ILIKE ? OR artist ILIKE ? ORDER BY ID ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet resultSet = stmt.executeQuery()) {
                System.out.println("\n---SEARCH RESULTS---");
                boolean hasData = false;

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String artist = resultSet.getString("artist");
                    System.out.println("ID: " + id + "| Title: " + title + "| Artist: " + artist);
                }
                if (!hasData) {
                    System.out.println("No matching tracks found for keyword: '" + keyword + "'");
                }
            }
        }
        }
         public static void updateSong(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID number of the song to update: ");
         int idToUpdate = scanner.nextInt();
         scanner.nextLine();

        System.out.println("Enter the new artist name: ");
         String newArtist = scanner.nextLine();
         String sql = "UPDATE music_list SET artist = ? WHERE id = ?";

         try (PreparedStatement stmt = connection.prepareStatement(sql)) {
             stmt.setString(1, newArtist);
             stmt.setInt(2, idToUpdate);

             int rowsAffected = stmt.executeUpdate();
             if (rowsAffected > 0) {
                 System.out.println("Success! TRACK ID "  + idToUpdate + " has been updated. ");
             } else{
                 System.out.println("Error: No song found with ID " + idToUpdate);
             }
         }
         }

}