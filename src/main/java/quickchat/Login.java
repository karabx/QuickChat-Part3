package quickchat;

import java.util.ArrayList;

public class Login {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    private static ArrayList<Login> users = new ArrayList<>();

    public Login(String username, String password, String firstName, String lastName, String cellPhoneNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public boolean checkUserName() {
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity() {
        return password.matches(".*[A-Z].*") && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*") && password.length() >= 8;
    }

    public boolean checkCellPhoneNumber() {
        return cellPhoneNumber.matches("\\+[0-9]{9,12}");
    }

    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters.";
        } else if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number and a special character.";
        } else if (!checkCellPhoneNumber()) {
            return "Cell number is incorrectly formatted or does not contain international code; please correct the number and try again.";
        } else {
            users.add(this);
            return "Account successfully created for " + firstName + " " + lastName + ".";
        }
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        for (Login user : users) {
            if (user.username.equals(enteredUsername) && user.password.equals(enteredPassword)) {
                return true;
            }
        }
        return false;
    }

    public static String getFirstName(String enteredUsername) {
        for (Login user : users) {
            if (user.username.equals(enteredUsername)) {
                return user.firstName;
            }
        }
        return "";
    }

    public static void resetUsers() {
        users.clear();
    }
}
