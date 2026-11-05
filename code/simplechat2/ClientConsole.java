package simplechat2;

import simplechat2.common.ChatIF;
import simplechat2.client.ChatClient;

import java.util.Scanner;

public class ClientConsole implements ChatIF {

    private ChatClient client;
    private String host;
    private int port = 5555;
    private final String loginID;

    public ClientConsole(String host, String loginID) {
        this.host = host;
        this.loginID = loginID;

        client = new ChatClient(
            this.host,
            this.port,
            this.loginID,
            this
        );

        login();
    }

    @Override
    public void display(String message) {
        System.out.println(message);
    }

    // Kết nối Server
    private void login() {
        try {
            if (client.isConnected()) {
                display("Already connected to server.");
                return;
            }

            client.openConnection();

        } catch (Exception e) {
            display("Cannot connect to server: " + e.getMessage());
        }
    }

    // Ngắt kết nối
    private void logoff() {
        try {
            if (client != null && client.isConnected()) {
                client.closeConnection();
                display("Disconnected from server.");
            } else {
                display("You are not connected.");
            }
        } catch (Exception e) {
            display("Error disconnecting: " + e.getMessage());
        }
    }

    // Đổi địa chỉ Server
    private void setHost(String newHost) {
        if (newHost == null || newHost.trim().isEmpty()) {
            display("Usage: #sethost <hostname>");
            return;
        }

        try {
            if (client.isConnected()) {
                client.closeConnection();
            }

            host = newHost.trim();
            client.setHost(host);

            display("Host changed to: " + host);
            display("Use #login to reconnect.");

        } catch (Exception e) {
            display("Cannot change host: " + e.getMessage());
        }
    }

    // Đổi cổng Server
    private void setPort(String newPort) {
        try {
            int value = Integer.parseInt(newPort);

            if (value < 1 || value > 65535) {
                display("Port must be between 1 and 65535.");
                return;
            }

            if (client.isConnected()) {
                client.closeConnection();
            }

            port = value;
            client.setPort(port);

            display("Port changed to: " + port);
            display("Use #login to reconnect.");

        } catch (NumberFormatException e) {
            display("Usage: #setport <port number>");
        } catch (Exception e) {
            display("Cannot change port: " + e.getMessage());
        }
    }

    // Hiển thị địa chỉ Server
    private void getHost() {
        display("Current host: " + client.getHost());
    }

    // Hiển thị cổng Server
    private void getPort() {
        display("Current port: " + client.getPort());
    }

    // Xử lý các lệnh Client
    private boolean processCommand(String input) {

        String[] parts = input.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();

        String argument = parts.length > 1
            ? parts[1].trim()
            : "";

        switch (command) {

            case "#quit":
                logoff();
                return false;

            case "#logoff":
                logoff();
                break;

            case "#login":
                login();
                break;

            case "#sethost":
                setHost(argument);
                break;

            case "#setport":
                setPort(argument);
                break;

            case "#gethost":
                getHost();
                break;

            case "#getport":
                getPort();
                break;

            default:
                if (client != null && client.isConnected()) {
                    client.handleMessageFromClientUI(input);
                } else {
                    display("Not connected. Use #login first.");
                }
                break;
        }

        return true;
    }

    // Nhận lệnh từ bàn phím
    public void accept() {

        Scanner scanner = new Scanner(System.in);

        display("===== SIMPLECHAT2 CLIENT =====");
        display("Commands:");
        display("#login");
        display("#logoff");
        display("#sethost <hostname>");
        display("#setport <port>");
        display("#gethost");
        display("#getport");
        display("#quit");
        display("==============================");

        while (true) {

            String input = scanner.nextLine();

            if (!processCommand(input)) {
                break;
            }
        }

        display("Client terminated.");
    }

    public static void main(String[] args) {

        if (args.length < 1 || args[0].trim().isEmpty()) {
            System.out.println(
                "ERROR - No login ID specified. Connection aborted."
            );
            return;
        }

        String host = args.length >= 2
            ? args[1]
            : "localhost";

        ClientConsole console =
            new ClientConsole(host, args[0]);

        console.accept();
    }
}
