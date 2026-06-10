package quickchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @BeforeEach
    void setUp() {
        Message.resetState();
    }

    // =========================================================================
    // Part 2 tests
    // =========================================================================

    @Test
    void testMessageLengthSuccess() {
        String message = "Hi Mike, can you join us for dinner tonight?";
        String result = Message.validateMessageLength(message);
        assertEquals("Message ready to send.", result);
    }

    @Test
    void testMessageLengthFailure() {
        String message = "a".repeat(260);
        String result = Message.validateMessageLength(message);
        assertTrue(result.contains("Message exceeds 250 characters by"));
    }

    @Test
    void testCheckRecipientCellSuccess() {
        Message msg = new Message("+2771869300", "Hello there");
        String result = msg.checkRecipientCell();
        assertEquals("Cell phone number successfully captured.", result);
    }

    @Test
    void testCheckRecipientCellFailure() {
        Message msg = new Message("08575975889", "Hi Keegan, did you receive the payment?");
        String result = msg.checkRecipientCell();
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", result);
    }

    @Test
    void testMessageHashCorrect() {
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        String hash = msg.getMessageHash();
        assertTrue(hash.endsWith(":HITONIGHT"), "Hash should end with :HITONIGHT but was: " + hash);
        assertEquals(hash, hash.toUpperCase());
    }

    @Test
    void testMessageHashLoop() {
        String[] messages  = {"Hi Mike, can you join us for dinner tonight?", "Hi Keegan, did you receive the payment?"};
        String[] recipients = {"+27718693002", "+2785759588"};
        for (int i = 0; i < messages.length; i++) {
            Message msg = new Message(recipients[i], messages[i]);
            String hash = msg.getMessageHash();
            assertNotNull(hash);
            assertFalse(hash.isEmpty());
            assertEquals(hash, hash.toUpperCase());
        }
    }

    @Test
    void testMessageIDCreated() {
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        String id = msg.getMessageID();
        assertNotNull(id);
        assertFalse(id.isEmpty());
        assertTrue(msg.checkMessageID());
        assertTrue(id.length() == 10, "Message ID should be exactly 10 digits");
    }

    @Test
    void testSentMessageSend() {
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", msg.SentMessage(1));
    }

    @Test
    void testSentMessageDisregard() {
        Message msg = new Message("+2785759588", "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", msg.SentMessage(2));
    }

    @Test
    void testSentMessageStore() {
        Message msg = new Message("+27718693002", "Test store message");
        assertEquals("Message successfully stored.", msg.SentMessage(3));
    }

    @Test
    void testReturnTotalMessages() {
        new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?").SentMessage(1);
        new Message("+2785759588",  "Hi Keegan, did you receive the payment?").SentMessage(2);
        assertEquals(1, Message.returnTotalMessages());
    }

    // =========================================================================
    // Part 3 tests
    // =========================================================================

    @Test
    void testSentMessagesArrayPopulated() {
        new Message("+27834557896", "Did you get the cake?").SentMessage(1);
        new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.").SentMessage(3);
        new Message("+27834484567", "Yohoooo, I am at your gate.").SentMessage(2);
        new Message("0838884567",   "It is dinner time !").SentMessage(1);

        assertTrue(Message.getSentMessages().contains("Did you get the cake?"),    "Sent array should contain message 1");
        assertTrue(Message.getSentMessages().contains("It is dinner time !"),      "Sent array should contain message 4");
    }

    @Test
    void testLongestMessage() {
        new Message("+27834557896", "Did you get the cake?").SentMessage(1);
        new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.").SentMessage(3);
        new Message("+27834484567", "Yohoooo, I am at your gate.").SentMessage(2);
        new Message("0838884567",   "It is dinner time !").SentMessage(1);

        assertEquals("Where are you? You are late! I have asked you to be on time.", Message.longestMessage());
    }

    @Test
    void testSearchByMessageID() {
        Message msg4 = new Message("0838884567", "It is dinner time !");
        msg4.SentMessage(1);

        String result = Message.searchByMessageID(msg4.getMessageID());
        assertTrue(result.contains("0838884567"),      "Result should contain the recipient");
        assertTrue(result.contains("It is dinner time !"), "Result should contain the message text");
    }

    @Test
    void testSearchByRecipient() {
        new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.").SentMessage(3);
        new Message("+27838884567", "Ok, I am leaving without you.").SentMessage(3);

        String result = Message.searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."), "Should contain message 2");
        assertTrue(result.contains("Ok, I am leaving without you."),                                "Should contain message 5");
    }

    @Test
    void testDeleteByHash() {
        Message msg2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.");
        msg2.SentMessage(3);

        String result = Message.deleteByHash(msg2.getMessageHash());
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."), "Should contain deleted message text");
        assertTrue(result.contains("successfully deleted"), "Should confirm deletion");
    }

    @Test
    void testDisplayReport() {
        new Message("+27834557896", "Did you get the cake?").SentMessage(1);
        new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.").SentMessage(3);

        String report = Message.displayReport();
        assertTrue(report.contains("Message Hash"),  "Report should contain Message Hash label");
        assertTrue(report.contains("Recipient"),     "Report should contain Recipient label");
        assertTrue(report.contains("Did you get the cake?"), "Report should include message 1");
        assertTrue(report.contains("Where are you? You are late! I have asked you to be on time."), "Report should include message 2");
    }
}
