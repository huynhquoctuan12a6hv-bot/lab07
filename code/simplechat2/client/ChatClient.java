package simplechat2.client;

import ocsf.client.AbstractClient;
import simplechat2.common.ChatIF;

public class ChatClient extends AbstractClient {

    private final ChatIF clientUI;
    private final String loginID;

    public ChatClient(
            String host,
            int port,
            String loginID,
            ChatIF clientUI) {

        super(host, port);
        this.loginID = loginID;
        this.clientUI = clientUI;
    }

    public String getLoginID() {
        return loginID;
    }

    @Override
    protected void handleMessageFromServer(Object message) {
        clientUI.display(String.valueOf(message));
    }

    public void handleMessageFromClientUI(String message) {
        try {
            sendToServer(loginID + ": " + message);
        } catch (Exception e) {
            clientUI.display(
                "ERROR - Could not send message to server."
            );
        }
    }

    @Override
    protected void connectionEstablished() {
        clientUI.display("Connected to server.");
    }

    @Override
    protected void connectionException(Exception exception) {
        clientUI.display(
            "Connection error: " + exception.getMessage()
        );
    }
}
