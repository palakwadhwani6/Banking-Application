import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.JPasswordField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class bank {
    
    public static void main(String[] args) {
        firstWind firstWind = new firstWind();
        firstWind.setVisible(true);
    }
    
    
}

class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found");
            e.printStackTrace();
            throw new SQLException("JDBC driver not found", e);
        }

        
        String url = "jdbc:mysql://localhost:3306/bank";
        String username = "[Enter your username]";
        String password = "[Enter your password]";
        return DriverManager.getConnection(url, username, password);
    }
}

class Account {
    private String account_no;
    private String name;
    private double balance;
    private String pin;
    
    public Account(String account_no, String name, double balance, String pin) {
        this.account_no = account_no;
        this.name = name;
        this.balance = balance;
        this.pin = pin;
    }
    
    public String getAccountNo() {
        return account_no;
    }
    
    public String getName() {
        return name;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public String getPin() {
        return pin;
    }
    
    public void deposit(double amount) {
        balance += amount;
        updateBalanceInDatabase();
    }

    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            updateBalanceInDatabase();
            return true;
        } else {
            return false;
        }
    }

    private void updateBalanceInDatabase() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE details SET balance = ? WHERE account_no = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDouble(1, balance);
                preparedStatement.setString(2, account_no);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Balance updated successfully in the database.");
                } else {
                    System.out.println("Failed to update balance in the database.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while updating balance in the database.");
        }
    }

    
    public void changePin(String newPin) {
        pin = newPin;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE details SET pin = ? WHERE account_no = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newPin);
                preparedStatement.setString(2, account_no);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("PIN updated successfully in the database.");
                } else {
                    System.out.println("Failed to update PIN in the database.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while updating PIN in the database.");
        }
    }

}

class firstWind extends JFrame {
    
    private JButton createAccountButton, loginButton;
    
    public firstWind() {
        setTitle("SVKM BANK");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        createAccountButton = new JButton("Create Account");
        loginButton = new JButton("Login");
        
        add(createAccountButton);
        add(loginButton);
        
        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AccountWind AccountWind = new AccountWind();
                AccountWind.setVisible(true);
            }
        });
        
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginWind loginWindow = new loginWind();
                loginWindow.setVisible(true);
            }
        });
    }
}



class AccountWind extends JFrame implements ActionListener {

    private JTextField accField, nameField, balanceField, pinField;
    private JButton createAcc, backButton; // Added backButton

    public AccountWind() {
        setTitle("Account Creation");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());

        add(new JLabel("Account Number (8 digits):"));
        accField = new JTextField(10);
        add(accField);

        add(new JLabel("Name:"));
        nameField = new JTextField(10);
        add(nameField);

        add(new JLabel("PIN (4 digits):"));
        pinField = new JTextField(5);
        add(pinField);

        add(new JLabel("Balance:"));
        balanceField = new JTextField(10);
        add(balanceField);

        createAcc = new JButton("Create Account");
        backButton = new JButton("Home"); // Added backButton
        add(createAcc);
        add(backButton); // Added backButton

        createAcc.addActionListener(this);
        backButton.addActionListener(this); // Added backButton action listener
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createAcc) {
            String account_no = accField.getText();
            String name = nameField.getText();
            String pin = pinField.getText();
            double balance;

            try {
                balance = Double.parseDouble(balanceField.getText());
                if (balance < 0) {
                    JOptionPane.showMessageDialog(this, "Balance must be a positive number or zero.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid balance amount.");
                return;
            }

            // Validate account number
            try {
                int accNoInt = Integer.parseInt(account_no);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Account number must be an integer value.");
                return;
            }

            if (account_no.length() != 8 && pin.length() != 4) {
                JOptionPane.showMessageDialog(this, "Account number must be 8 digits and PIN must be 4 digits.");
                return;
            } else if (account_no.length() != 8) {
                JOptionPane.showMessageDialog(this, "Account number must be 8 digits.");
                return;
            } else if (pin.length() != 4) {
                JOptionPane.showMessageDialog(this, "PIN must be 4 digits.");
                return;
            }

            try {
                // Check if account number already exists
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String checkQuery = "SELECT * FROM details WHERE account_no = ?";
                    try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                        checkStatement.setString(1, account_no);
                        ResultSet resultSet = checkStatement.executeQuery();
                        if (resultSet.next()) {
                            JOptionPane.showMessageDialog(this, "Account number already exists. Please choose a different one.");
                            return;
                        }
                    }
                }

                // Create new account
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String query = "INSERT INTO details (account_no, name, balance, pin) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, account_no);
                        preparedStatement.setString(2, name);
                        preparedStatement.setDouble(3, balance);
                        preparedStatement.setString(4, pin);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Account Created Successfully");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to create account");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to create account");
            }
        } else if (e.getSource() == backButton) {
            dispose(); // Close the current window
            firstWind firstWindow = new firstWind(); // Create an instance of the main window
            firstWindow.setVisible(true); // Show the main window
        }
    }
}


class loginWind extends JFrame implements ActionListener {

    JTextField accField;
    JPasswordField pinField; // Use JPasswordField for PIN input
    JButton loginButton, backButton; // Added backButton
    Account account;

    public loginWind() {
        setTitle("Login");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        add(new JLabel("Account Number:"));
        accField = new JTextField(10);
        add(accField);

        add(new JLabel("Pin:"));
        pinField = new JPasswordField(5); // Use JPasswordField for PIN input
        add(pinField);

        loginButton = new JButton("Login");
        backButton = new JButton("Home"); // Added backButton
        add(loginButton);
        add(backButton); // Added backButton

        loginButton.addActionListener(this);
        backButton.addActionListener(this); // Added backButton action listener
    }

    public void actionPerformed(ActionEvent e) {
        String account_no = accField.getText();
        String pin = new String(pinField.getPassword()); // Retrieve PIN as String from JPasswordField

        if (e.getSource() == loginButton) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM details WHERE account_no = ? AND pin = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, account_no);
                statement.setString(2, pin);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {

                    String name = resultSet.getString("name");

                    account = new Account(account_no, name, resultSet.getDouble("balance"), pin);

                    dispose();
                    bankingWind bankingWind = new bankingWind(account);
                    bankingWind.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect Account Number or PIN! Please enter again");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while logging in. Please try again later.");
            }
        } else if (e.getSource() == backButton) { // Handle backButton click
            dispose(); // Close the current window
            firstWind firstWindow = new firstWind(); // Create an instance of the main window
            firstWindow.setVisible(true); // Show the main window
        }
    }
}


class bankingWind extends JFrame implements ActionListener {
    
    private JButton depositButton, withdrawButton, balanceButton, changepinButton, logoutButton;
    private Account account;
    private JLabel nameLabel;
    
    public bankingWind(Account account) {
        setTitle("Banking Operations");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        this.account = account;
        
        nameLabel = new JLabel("Welcome, " + account.getName()); 
        add(nameLabel); 
        
        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");
        balanceButton = new JButton("View Balance");
        changepinButton = new JButton("Change PIN");
        logoutButton = new JButton("Logout");
        
        add(depositButton);
        add(withdrawButton);
        add(balanceButton);
        add(changepinButton);
        add(logoutButton);
        
        depositButton.addActionListener(this);
        withdrawButton.addActionListener(this);
        balanceButton.addActionListener(this);
        changepinButton.addActionListener(this);
        logoutButton.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == depositButton) {
            String depositAmountString = JOptionPane.showInputDialog("Enter amount:");
            double depositAmount = Double.parseDouble(depositAmountString);
            if (depositAmount < 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount for deposit.");
                return; 
            }
            account.deposit(depositAmount);
            JOptionPane.showMessageDialog(this, "Amount deposited successfully");
        } else if (e.getSource() == withdrawButton) {
            String withdrawAmountString = JOptionPane.showInputDialog("Enter amount");
            double withdrawAmount = Double.parseDouble(withdrawAmountString);
            if (withdrawAmount < 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount for withdrawal.");
                return; 
            }
            if (account.withdraw(withdrawAmount)) {
                JOptionPane.showMessageDialog(this, "Amount withdrawal successful");
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient Balance");
            }
        } else if (e.getSource() == balanceButton) {
            JOptionPane.showMessageDialog(this, "Account Balance: " + account.getBalance());
        } else if (e.getSource() == changepinButton) {
           
            String currentPinString = JOptionPane.showInputDialog("Enter current PIN:");
            if (currentPinString == null || currentPinString.isEmpty()) {
                return; // User canceled or didn't input anything
            }

            // Verify the entered current PIN
            if (!currentPinString.equals(account.getPin())) {
                JOptionPane.showMessageDialog(this, "Incorrect current PIN. Please try again.");
                return;
            }

            String newPinString = JOptionPane.showInputDialog("Enter new PIN (4 digits):");
            if (newPinString == null || newPinString.isEmpty()) {
                return; // User canceled or didn't input anything
            }

            // Validate new PIN
            if (newPinString.length() != 4) {
                JOptionPane.showMessageDialog(this, "PIN must be 4 digits.");
                return;
            }

            String confirmNewPinString = JOptionPane.showInputDialog("Confirm new PIN:");
            if (confirmNewPinString == null || confirmNewPinString.isEmpty()) {
                return; // User canceled or didn't input anything
            }

            // Check if the new PIN matches the confirmation
            if (!newPinString.equals(confirmNewPinString)) {
                JOptionPane.showMessageDialog(this, "PIN confirmation does not match. Please try again.");
                return;
            }

            try {
                // Change PIN only if it's an integer
                account.changePin(newPinString);
                JOptionPane.showMessageDialog(this, "PIN changed successfully");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid PIN format. Please enter digits only.");
            }
        }


        
         else if (e.getSource() == logoutButton) {
            JOptionPane.showMessageDialog(this, "Successfully logged out!!");
            dispose();
            firstWind firstWindow = new firstWind();
            firstWindow.setVisible(true);
        }
    }
}

