package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final ServerSocket server;
    private final Socket socket;

    Connection () throws IOException, InterruptedException {
        String address = "127.0.0.1";
        int port = 6666;
        server = new ServerSocket(/*port, 50, InetAddress.getByName(address*/);
        server.setReuseAddress(true);
        server.bind(new InetSocketAddress(InetAddress.getByName(address), port));
        socket = server.accept();
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        checkForConnection();
    }

    private void checkForConnection() throws InterruptedException, IOException {
        while (true) {
            String str = inputStream.readUTF();
            if(str.equals("Client started!"))
                break;
            Thread.sleep(50);
        }

        outputStream.writeUTF("Server started!");
    }

    Connection (String address, int port) throws IOException {
        server = new ServerSocket(port, 50, InetAddress.getByName(address));
        socket = server.accept();
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void shutdown() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
        server.close();
    }
}
