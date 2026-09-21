import java.sql.*;
import java.util.Scanner;

public class ECommerce {

    static Scanner sc = new Scanner(System.in);

   static final String URL =
    "jdbc:mysql://localhost:3306/ecommerce?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASSWORD = "MyNewPassword@123"; // Change this

    // Database connection
    static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n==============================");
            System.out.println("     SIMPLE E-COMMERCE");
            System.out.println("==============================");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Search Product");
            System.out.println("4. Add to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Remove from Cart");
            System.out.println("7. Place Order");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1:
                    addProduct();
                    break;

                case 2:
                    viewProducts();
                    break;

                case 3:
                    searchProduct();
                    break;

                case 4:
                    addToCart();
                    break;

                case 5:
                    viewCart();
                    break;

                case 6:
                    removeFromCart();
                    break;

                case 7:
                    placeOrder();
                    break;

                case 8:
                    System.out.println("Thank you!");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // Add Product
    static void addProduct() {

        System.out.print("Enter product name: ");
        String name = sc.nextLine();

        System.out.print("Enter price: ");
        double price = Double.parseDouble(sc.nextLine());

        System.out.print("Enter stock: ");
        int stock = Integer.parseInt(sc.nextLine());

        String sql =
                "INSERT INTO products(name, price, stock) VALUES(?,?,?)";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);

            ps.executeUpdate();

            System.out.println("Product added successfully.");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View Products
    static void viewProducts() {

        String sql = "SELECT * FROM products";

        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\nID | Product | Price | Stock");
            System.out.println("--------------------------------");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("id") + " | " +
                        rs.getString("name") + " | " +
                        rs.getDouble("price") + " | " +
                        rs.getInt("stock")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Search Product
    static void searchProduct() {

        System.out.print("Enter product name: ");
        String name = sc.nextLine();

        String sql =
                "SELECT * FROM products WHERE name LIKE ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println(
                        "ID: " + rs.getInt("id") +
                        " | Product: " + rs.getString("name") +
                        " | Price: " + rs.getDouble("price") +
                        " | Stock: " + rs.getInt("stock")
                );
            }

            if (!found) {
                System.out.println("Product not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Add to Cart
    static void addToCart() {

        viewProducts();

        System.out.print("Enter product ID: ");
        int productId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter quantity: ");
        int quantity = Integer.parseInt(sc.nextLine());

        String sql =
                "INSERT INTO cart(product_id, quantity) " +
                "VALUES(?,?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, quantity);
            ps.setInt(3, quantity);

            ps.executeUpdate();

            System.out.println("Product added to cart.");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View Cart
    static void viewCart() {

        String sql =
                "SELECT p.id, p.name, p.price, c.quantity " +
                "FROM cart c " +
                "JOIN products p ON c.product_id = p.id";

        double total = 0;

        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n========== CART ==========");

            boolean empty = true;

            while (rs.next()) {

                empty = false;

                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                double itemTotal = price * quantity;

                total += itemTotal;

                System.out.println(
                        rs.getInt("id") + " | " +
                        rs.getString("name") +
                        " | Quantity: " + quantity +
                        " | Total: " + itemTotal
                );
            }

            if (empty) {
                System.out.println("Cart is empty.");
            } else {
                System.out.println("--------------------------");
                System.out.println("Total Amount: " + total);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Remove from Cart
    static void removeFromCart() {

        viewCart();

        System.out.print("Enter product ID to remove: ");
        int productId = Integer.parseInt(sc.nextLine());

        String sql =
                "DELETE FROM cart WHERE product_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);

            if (ps.executeUpdate() > 0) {
                System.out.println("Product removed from cart.");
            } else {
                System.out.println("Product not found in cart.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Place Order
    static void placeOrder() {

        String checkCart =
                "SELECT p.id, p.name, p.price, p.stock, c.quantity " +
                "FROM cart c " +
                "JOIN products p ON c.product_id = p.id";

        double total = 0;

        try (Connection con = connect()) {

            PreparedStatement ps =
                    con.prepareStatement(checkCart);

            ResultSet rs = ps.executeQuery();

            boolean empty = true;

            while (rs.next()) {

                empty = false;

                int stock = rs.getInt("stock");
                int quantity = rs.getInt("quantity");

                if (quantity > stock) {

                    System.out.println(
                            "Not enough stock for: " +
                            rs.getString("name"));

                    return;
                }

                total +=
                        rs.getDouble("price") * quantity;
            }

            if (empty) {
                System.out.println("Cart is empty.");
                return;
            }

            // Reduce product stock
            String updateStock =
                    "UPDATE products p " +
                    "JOIN cart c ON p.id=c.product_id " +
                    "SET p.stock=p.stock-c.quantity";

            PreparedStatement update =
                    con.prepareStatement(updateStock);

            update.executeUpdate();

            // Clear cart
            String clearCart =
                    "DELETE FROM cart";

            PreparedStatement delete =
                    con.prepareStatement(clearCart);

            delete.executeUpdate();

            System.out.println("\nOrder placed successfully!");
            System.out.println("Total Amount: " + total);

        } catch (SQLException e) {
            System.out.println("Order failed: " + e.getMessage());
        }
    }
}