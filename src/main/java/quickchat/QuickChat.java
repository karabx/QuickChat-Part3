package quickchat;

import java.util.Scanner;

public class QuickChat {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== QuickChat Registration ===");
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter cell phone number (e.g. +27718693002): ");
        String cellPhone = scanner.nextLine();
        System.out.print("Choose a username (must contain '_', max 5 chars): ");
        String username = scanner.nextLine();
        System.out.print("Choose a password (min 8 chars, 1 uppercase, 1 digit, 1 special char): ");
        String password = scanner.nextLine();

        Login newUser = new Login(username, password, firstName, lastName, cellPhone);
        String registrationResult = newUser.registerUser();
        System.out.println(registrationResult);

        if (!registrationResult.startsWith("Account successfully created")) {
            scanner.close();
            return;
        }

        System.out.println("\n=== QuickChat Login ===");
        System.out.print("Username: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Password: ");
        String loginPassword = scanner.nextLine();

        boolean loggedIn = newUser.loginUser(loginUsername, loginPassword);

        if (!loggedIn) {
            System.out.println("Login failed. Username or password incorrect.");
            scanner.close();
            return;
        }

        System.out.println("Welcome back, " + Login.getFirstName(loginUsername) + "!");
        System.out.println("\nWelcome to QuickChat.");

        Message.loadStoredMessages();

        boolean running = true;

        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");
            String menuChoice = scanner.nextLine().trim();

            switch (menuChoice) {
                case "1":
                    sendMessagesFlow(scanner);
                    break;
                case "2":
                    System.out.println(Message.printMessages());
                    break;
                case "3":
                    storedMessagesMenu(scanner);
                    break;
                case "4":
                    System.out.println("Thank you for using QuickChat!");
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }

        scanner.close();
    }

    private static void sendMessagesFlow(Scanner scanner) {
        System.out.print("How many messages would you like to send? ");
        int numMessages;
        try {
            numMessages = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered.");
            return;
        }

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

            System.out.print("Enter recipient cell number: ");
            String recipient = scanner.nextLine().trim();

            String messageText;
            while (true) {
                System.out.print("Enter your message (max 250 characters): ");
                messageText = scanner.nextLine();
                String validation = Message.validateMessageLength(messageText);
                if (validation.equals("Message ready to send.")) {
                    break;
                } else {
                    System.out.println("Please enter a message of less than 250 characters.");
                }
            }

            Message msg = new Message(recipient, messageText);

            System.out.println(msg.checkRecipientCell());
            System.out.println("\nMessage ID   : " + msg.getMessageID());
            System.out.println("Message Hash : " + msg.getMessageHash());
            System.out.println("Recipient    : " + msg.getRecipient());
            System.out.println("Message      : " + msg.getMessageText());

            System.out.println("\n1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose: ");

            int sendChoice;
            try {
                sendChoice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                sendChoice = 2;
            }

            System.out.println(msg.SentMessage(sendChoice));
        }

        System.out.println("=== All Sent Messages ===");
        System.out.println(Message.printMessages());
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
    }

    private static void storedMessagesMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Stored Messages ===");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message by ID");
            System.out.println("d) Search all messages for a particular recipient");
            System.out.println("e) Delete a message using message hash");
            System.out.println("f) Display full message report");
            System.out.println("0) Back to main menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    System.out.println(Message.displayStoredSendersRecipients());
                    break;
                case "b":
                    System.out.println("Longest message: " + Message.longestMessage());
                    break;
                case "c":
                    System.out.print("Enter Message ID to search: ");
                    String id = scanner.nextLine().trim();
                    System.out.println(Message.searchByMessageID(id));
                    break;
                case "d":
                    System.out.print("Enter recipient number to search: ");
                    String recipient = scanner.nextLine().trim();
                    System.out.println(Message.searchByRecipient(recipient));
                    break;
                case "e":
                    System.out.print("Enter message hash to delete: ");
                    String hash = scanner.nextLine().trim();
                    System.out.println(Message.deleteByHash(hash));
                    break;
                case "f":
                    System.out.println(Message.displayReport());
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please choose a, b, c, d, e, f or 0.");
            }
        }
    }
}
