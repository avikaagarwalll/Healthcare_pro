package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    // Add a new patient to the database
    public void addPatient() {
        String name;
        do {
            System.out.print("Enter Patient Name: ");
            name = scanner.next().trim();
            if (name.isEmpty() || !name.matches("[a-zA-Z ]+")) {
                System.out.println("Invalid name! Please enter a valid name (letters only).");
                name = "";
            }
        } while (name.isEmpty());

        // Validate age input
        int age = -1;
        while (true) {
            System.out.print("Enter Patient Age: ");
            if (scanner.hasNextInt()) {
                age = scanner.nextInt();
                if (age > 0 && age < 120) { // Validate age range
                    break;
                } else {
                    System.out.println("Please enter a valid age between 1 and 120.");
                }
            } else {
                System.out.println("Invalid input! Please enter a valid number for age.");
                scanner.next(); // Clear invalid input
            }
        }

        // Validate gender input
        String gender = "";
        while (true) {
            System.out.print("Enter Patient Gender (Male/Female/Other): ");
            gender = scanner.next().trim();
            if (gender.equalsIgnoreCase("Male") ||
                    gender.equalsIgnoreCase("Female") ||
                    gender.equalsIgnoreCase("Other")) {
                break;
            } else {
                System.out.println("Invalid gender! Please enter 'Male', 'Female', or 'Other'.");
            }
        }

        // Insert patient into database
        String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, gender);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient added successfully!");
            } else {
                System.out.println("Failed to add patient.");
            }
        } catch (SQLException e) {
            System.err.println("Error while adding patient: " + e.getMessage());
        }
    }

    // View all patients in the database
    public void viewPatients() {
        String query = "SELECT * FROM patients";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            System.out.println("+------------+--------------------+----------+------------+");
            System.out.println("| Patient Id | Name               | Age      | Gender     |");
            System.out.println("+------------+--------------------+----------+------------+");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", id, name, age, gender);
            }

            System.out.println("+------------+--------------------+----------+------------+");
        } catch (SQLException e) {
            System.err.println("Error while retrieving patients: " + e.getMessage());
        }
    }

    // Check if a patient exists by ID
    public boolean getPatientById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error while retrieving patient by ID: " + e.getMessage());
        }
        return false;
    }
}
