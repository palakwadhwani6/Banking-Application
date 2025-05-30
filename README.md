Java-GUI-Banking-Application

This Java application is a simple banking system that allows users to create accounts, log in, perform banking operations such as deposit, withdrawal, view balance, change PIN, and log out. 
The application uses Swing for the graphical user interface (GUI) and JDBC for database connectivity with MySQL.

Features:

Account Creation: Users can create new bank accounts by providing account number, name, PIN, and initial balance.
Login: Existing users can log in using their account number and PIN.
Deposit: Users can deposit funds into their accounts.
Withdrawal: Users can withdraw funds from their accounts, provided they have sufficient balance.
View Balance: Users can view their account balance.
Change PIN: Users can change their PIN for added security.
Logout: Users can safely log out from their accounts.

Prerequisites:
JDK (Java Development Kit)
MySQL database

Setup:
Clone the repository or download the source code.
Set up the MySQL database by running the provided SQL script (bank.sql) to create the necessary tables and schema.
Configure the database connection parameters in the DatabaseConnection class.
Compile the Java files using javac.
Run the bank class to start the application.

Usage
Upon running the application, the initial window (firstWind) will be displayed.
Users can choose to create a new account or log in.
After logging in, users will be presented with the main banking window (bankingWind) where they can perform various banking operations.
Users can log out from the main window to return to the initial window.
