package src;
import java.sql.*;
import java.util.*;

public class StudentManagement {

    static final String URL =
            "jdbc:mysql://localhost:3306/student_management";

    static final String USER = "root";

    static final String PASSWORD = "MyNewPassword@123";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    addStudent();
                    break;

                case 2:
                    viewStudents();
                    break;

                case 3:
                    searchStudent();
                    break;

                case 4:
                    updateStudent();
                    break;

                case 5:
                    deleteStudent();
                    break;

                case 6:
                    System.out.println("Thank you!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // 1. ADD STUDENT
    static void addStudent() {

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Age: ");
        int age = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter Course: ");
        String course = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        String sql =
                "INSERT INTO students VALUES (?, ?, ?, ?, ?)";

        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, age);
            ps.setString(4, course);
            ps.setString(5, email);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student added successfully!");
            }

        } catch (SQLException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }


    // 2. VIEW ALL STUDENTS
    static void viewStudents() {

        String sql = "SELECT * FROM students";

        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD);

             Statement st =
                     con.createStatement();

             ResultSet rs =
                     st.executeQuery(sql)) {

            System.out.println("\nID\tName\tAge\tCourse\tEmail");
            System.out.println("-----------------------------------------------");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("id") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getInt("age") + "\t" +
                        rs.getString("course") + "\t" +
                        rs.getString("email")
                );
            }

        } catch (SQLException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }


    // 3. SEARCH STUDENT
    static void searchStudent() {

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();

        String sql =
                "SELECT * FROM students WHERE id = ?";

        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("\nStudent Found!");
                System.out.println("ID     : " + rs.getInt("id"));
                System.out.println("Name   : " + rs.getString("name"));
                System.out.println("Age    : " + rs.getInt("age"));
                System.out.println("Course : " + rs.getString("course"));
                System.out.println("Email  : " + rs.getString("email"));

            } else {

                System.out.println("Student not found!");
            }

        } catch (SQLException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }


    // 4. UPDATE STUDENT
    static void updateStudent() {

        System.out.print("Enter Student ID to update: ");
        int id = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter New Name: ");
        String name = sc.nextLine();

        System.out.print("Enter New Age: ");
        int age = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter New Course: ");
        String course = sc.nextLine();

        System.out.print("Enter New Email: ");
        String email = sc.nextLine();

        String sql =
                "UPDATE students SET name=?, age=?, course=?, email=? WHERE id=?";

        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, course);
            ps.setString(4, email);
            ps.setInt(5, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                System.out.println("Student updated successfully!");

            } else {

                System.out.println("Student ID not found!");
            }

        } catch (SQLException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }


    // 5. DELETE STUDENT
    static void deleteStudent() {

        System.out.print("Enter Student ID to delete: ");
        int id = sc.nextInt();

        String sql =
                "DELETE FROM students WHERE id=?";

        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                System.out.println("Student deleted successfully!");

            } else {

                System.out.println("Student ID not found!");
            }

        } catch (SQLException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }
}