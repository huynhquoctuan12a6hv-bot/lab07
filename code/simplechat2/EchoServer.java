
package simplechat2;

import ocsf.server.*;
import java.util.Scanner;

public class EchoServer extends AbstractServer {

    public static final int DEFAULT_PORT = 5555;

    public EchoServer(int port) {
        super(port);
    }

    // Nhận tin nhắn từ Client
    @Override
    protected void handleMessageFromClient(
            Object message,
            ConnectionToClient client) {

        String text = String.valueOf(message);

        System.out.println("Received: " + text);

        sendToAllClients(text);
    }

    // Server bắt đầu lắng nghe
    @Override
    protected void serverStarted() {
        System.out.println(
            "Server listening for clients on port " + getPort()
        );
    }

    // Server dừng lắng nghe
    @Override
    protected void serverStopped() {
        System.out.println("Server stopped listening.");
    }

    // Server đóng
    @Override
    protected void serverClosed() {
        System.out.println("Server closed.");
    }

    // Kiểm tra Server đang lắng nghe
    private boolean isServerRunning() {
        return isListening();
    }

    // Bắt đầu Server
    private void startServer() {

        if (isServerRunning()) {
            System.out.println("Server is already running.");
            return;
        }

        try {
            listen();

            System.out.println(
                "Server started successfully on port " + getPort()
            );

        } catch (Exception e) {
            System.out.println(
                "Cannot start server: " + e.getMessage()
            );
        }
    }

    // Dừng Server nhưng giữ chương trình hoạt động
    private void stopServer() {

        if (!isServerRunning()) {
            System.out.println("Server is not running.");
            return;
        }

        try {
            stopListening();
            System.out.println("Server stopped successfully.");

        } catch (Exception e) {
            System.out.println(
                "Cannot stop server: " + e.getMessage()
            );
        }
    }

    // Đóng Server và các kết nối Client
    private void closeServer() {

        try {
            close();
            System.out.println("Server closed successfully.");

        } catch (Exception e) {
            System.out.println(
                "Cannot close server: " + e.getMessage()
            );
        }
    }

    // Thay đổi cổng Server
    private void setServerPort(String value) {

        if (value.isEmpty()) {
            System.out.println("Usage: #setport <port>");
            return;
        }

        try {
            int newPort = Integer.parseInt(value);

            if (newPort < 1 || newPort > 65535) {
                System.out.println(
                    "Port must be between 1 and 65535."
                );
                return;
            }

            if (isServerRunning()) {
                System.out.println(
                    "Please use #stop before changing port."
                );
                return;
            }

            setPort(newPort);

            System.out.println(
                "Server port changed to: " + getPort()
            );

        } catch (NumberFormatException e) {
            System.out.println("Invalid port number.");
        }
    }

    // Xử lý lệnh Server
    private boolean processCommand(String input) {

        if (input == null || input.trim().isEmpty()) {
            return true;
        }

        String[] parts = input.trim().split("\\s+", 2);

        String command = parts[0].toLowerCase();

        String argument = parts.length > 1
            ? parts[1].trim()
            : "";

        switch (command) {

            case "#quit":
                closeServer();
                return false;

            case "#stop":
                stopServer();
                break;

            case "#close":
                closeServer();
                break;

            case "#start":
                startServer();
                break;

            case "#setport":
                setServerPort(argument);
                break;

            default:

                if (isServerRunning()) {
                    sendToAllClients("[SERVER] " + input);

                    System.out.println(
                        "Server sent: " + input
                    );

                } else {
                    System.out.println(
                        "Server is not running. Cannot send message."
                    );
                }

                break;
        }

        return true;
    }

    public static void main(String[] args) {

        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port.");
                return;
            }
        }

        EchoServer server = new EchoServer(port);

        Scanner scanner = new Scanner(System.in);

        System.out.println("===== SIMPLECHAT2 SERVER =====");

        server.startServer();

        System.out.println("Commands:");
        System.out.println("#start");
        System.out.println("#stop");
        System.out.println("#close");
        System.out.println("#setport <port>");
        System.out.println("#quit");

        System.out.println("==============================");

        while (true) {

            String input = scanner.nextLine();

            if (!server.processCommand(input)) {
                break;
            }
        }

        System.out.println("Server program terminated.");
    }
}
