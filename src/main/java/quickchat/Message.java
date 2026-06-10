package quickchat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String status;

    private static ArrayList<Message> messageList = new ArrayList<>();
    private static int totalMessagesSent = 0;

    // Part 3 parallel arrays
    private static ArrayList<String> sentMessages        = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<String> storedMessages      = new ArrayList<>();
    private static ArrayList<String> messageHashes       = new ArrayList<>();
    private static ArrayList<String> messageIDs          = new ArrayList<>();

    public Message(String recipient, String messageText) {
        this.messageID   = generateMessageID();
        this.recipient   = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
        this.status      = "";
    }

    public String getMessageID()   { return messageID;   }
    public String getRecipient()   { return recipient;   }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public String getStatus()      { return status;      }

    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipient.startsWith("+")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public static String validateMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = text.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    public String createMessageHash() {
        String firstTwo  = messageID.substring(0, 2);
        int msgNumber    = messageList.size();
        String[] words   = messageText.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord  = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");
        return (firstTwo + ":" + msgNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                status = "Sent";
                messageList.add(this);
                totalMessagesSent++;
                sentMessages.add(this.messageText);
                messageHashes.add(this.messageHash);
                messageIDs.add(this.messageID);
                return "Message successfully sent.";
            case 2:
                status = "Disregarded";
                disregardedMessages.add(this.messageText);
                return "Press 0 to delete the message.";
            case 3:
                status = "Stored";
                messageList.add(this);
                storedMessages.add(this.messageText);
                messageHashes.add(this.messageHash);
                messageIDs.add(this.messageID);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    public static String printMessages() {
        if (messageList.isEmpty()) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : messageList) {
            if ("Sent".equals(m.getStatus())) {
                sb.append("Message ID   : ").append(m.getMessageID()).append("\n");
                sb.append("Message Hash : ").append(m.getMessageHash()).append("\n");
                sb.append("Recipient    : ").append(m.getRecipient()).append("\n");
                sb.append("Message      : ").append(m.getMessageText()).append("\n");
                sb.append("------------------------------------------\n");
            }
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public void storeMessage() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Message m : messageList) {
                JSONObject obj = new JSONObject();
                obj.put("messageID",   m.getMessageID());
                obj.put("messageHash", m.getMessageHash());
                obj.put("recipient",   m.getRecipient());
                obj.put("message",     m.getMessageText());
                obj.put("status",      m.getStatus());
                jsonArray.put(obj);
            }
            FileWriter file = new FileWriter("messages.json");
            file.write(jsonArray.toString(4));
            file.flush();
            file.close();
        } catch (IOException e) {
            System.out.println("Error saving messages: " + e.getMessage());
        }
    }

    // Load stored messages from JSON file into storedMessages array
    // Source: https://stleary.github.io/JSON-java/index.html (org.json library)
    public static void loadStoredMessages() {
        loadStoredMessagesFromFile("messages.json");
    }

    public static void loadStoredMessagesFromFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj   = jsonArray.getJSONObject(i);
                String msgText   = obj.optString("message", "");
                String msgHash   = obj.optString("messageHash", "");
                String msgID     = obj.optString("messageID", "");
                String recipient = obj.optString("recipient", "");
                String status    = obj.optString("status", "");

                if ("Stored".equalsIgnoreCase(status)) {
                    if (!storedMessages.contains(msgText))  storedMessages.add(msgText);
                    if (!messageHashes.contains(msgHash))   messageHashes.add(msgHash);
                    if (!messageIDs.contains(msgID))        messageIDs.add(msgID);

                    boolean exists = false;
                    for (Message m : messageList) {
                        if (m.getMessageID().equals(msgID)) { exists = true; break; }
                    }
                    if (!exists) {
                        Message loaded     = new Message(recipient, msgText);
                        loaded.messageID   = msgID;
                        loaded.messageHash = msgHash;
                        loaded.status      = "Stored";
                        messageList.add(loaded);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No stored messages file found or error reading file: " + e.getMessage());
        }
    }

    // (a) Display sender and recipient of all stored messages
    public static String displayStoredSendersRecipients() {
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (Message m : messageList) {
            if ("Stored".equalsIgnoreCase(m.getStatus())) {
                sb.append("Sender    : You\n");
                sb.append("Recipient : ").append(m.getRecipient()).append("\n");
                sb.append("------------------------------------------\n");
                found = true;
            }
        }
        return found ? sb.toString() : "No stored messages found.";
    }

    // (b) Display the longest message
    public static String longestMessage() {
        if (messageList.isEmpty()) return "No messages available.";
        Message longest = null;
        for (Message m : messageList) {
            if (longest == null || m.getMessageText().length() > longest.getMessageText().length()) {
                longest = m;
            }
        }
        return longest != null ? longest.getMessageText() : "No messages available.";
    }

    // (c) Search by message ID
    public static String searchByMessageID(String id) {
        for (Message m : messageList) {
            if (m.getMessageID().equals(id)) {
                return "Recipient : " + m.getRecipient() + "\nMessage   : " + m.getMessageText();
            }
        }
        return "Message ID not found.";
    }

    // (d) Search all messages for a particular recipient
    public static String searchByRecipient(String recipient) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (Message m : messageList) {
            if (m.getRecipient().equals(recipient)) {
                sb.append(m.getMessageText()).append("\n");
                found = true;
            }
        }
        return found ? sb.toString().trim() : "No messages found for recipient: " + recipient;
    }

    // (e) Delete a message using its message hash
    public static String deleteByHash(String hash) {
        for (int i = 0; i < messageList.size(); i++) {
            Message m = messageList.get(i);
            if (m.getMessageHash().equalsIgnoreCase(hash)) {
                String deletedText = m.getMessageText();
                messageList.remove(i);
                messageHashes.remove(hash.toUpperCase());
                messageIDs.remove(m.getMessageID());
                sentMessages.remove(deletedText);
                storedMessages.remove(deletedText);
                return "Message: \"" + deletedText + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    // (f) Display full report of all messages
    public static String displayReport() {
        if (messageList.isEmpty()) return "No messages to report.";
        StringBuilder sb = new StringBuilder();
        sb.append("========== MESSAGE REPORT ==========\n");
        for (Message m : messageList) {
            sb.append("Message Hash : ").append(m.getMessageHash()).append("\n");
            sb.append("Recipient    : ").append(m.getRecipient()).append("\n");
            sb.append("Message      : ").append(m.getMessageText()).append("\n");
            sb.append("Status       : ").append(m.getStatus()).append("\n");
            sb.append("------------------------------------\n");
        }
        return sb.toString();
    }

    public static ArrayList<String> getSentMessages()        { return sentMessages;        }
    public static ArrayList<String> getDisregardedMessages() { return disregardedMessages; }
    public static ArrayList<String> getStoredMessages()      { return storedMessages;      }
    public static ArrayList<String> getMessageHashes()       { return messageHashes;       }
    public static ArrayList<String> getMessageIDs()          { return messageIDs;          }

    private static String generateMessageID() {
        Random rand = new Random();
        long id = (long)(rand.nextDouble() * 9000000000L) + 1000000000L;
        return String.valueOf(id);
    }

    public static void resetState() {
        messageList.clear();
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }
}
