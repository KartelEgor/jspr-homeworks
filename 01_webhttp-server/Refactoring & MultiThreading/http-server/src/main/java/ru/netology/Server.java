package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int LIMIT = 4096;

    private static final String BAD_REQUEST_MESSAGE =
            "HTTP/1.1 400 Bad Request\r\n" +
            "Content-Length: 0\r\n" +
            "Connection: close\r\n" +
            "\r\n";

    private static final String NOT_FOUND_MESSAGE =
            "HTTP/1.1 404 Not Found\r\n" +
            "Content-Length: 0\r\n" +
            "Connection: close\r\n" +
            "\r\n";

    private final List<String> validPaths = new ArrayList<>(List.of("/index.html", "/spring.svg", "/spring.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html",
            "/classic.html", "/events.html", "/events.js"));

    private final int PORT = 9999;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public void start() {
        System.out.println("server started");
        try(var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    threadPool.execute(() -> {
                        processRequest(socket);
                    });
                } catch (IOException e) {e.printStackTrace();}
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void processRequest(Socket socket) {
        try (final var in = new BufferedInputStream(socket.getInputStream());
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            in.mark(LIMIT);
            final var buffer = new byte[LIMIT];
            final int read = in.read(buffer);
            final var requestMessage = new String(buffer, 0, read);
            Request request = new Request(requestMessage);
            clientRequestProcessing(request, out);
        } catch (IOException e) {e.printStackTrace();}
    }

    private void clientRequestProcessing (Request request, BufferedOutputStream out) {
        try {
            if (correctRequestLength(request.getFirstLine())) {
                if (correctPath(request.getUrl())) {
                    writeFileToStream(request.getFilePath(), request.getMimeType(), out);
                } else {
                    out.write((NOT_FOUND_MESSAGE).getBytes());
                    }
            } else {
                out.write((BAD_REQUEST_MESSAGE).getBytes());
            }
            out.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private boolean correctRequestLength (String[] parts) {
        return (parts.length == 3);
    }

    private boolean correctPath (String path) {
        return (validPaths.contains(path));
    }

    private void writeFileToStream(Path filePath, String mimeType, BufferedOutputStream out) {
        if (filePath.toString().equals("public\\classic.html")) writeFileClassicHTML(filePath, mimeType, out);
        try {
            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFileClassicHTML(Path filePath, String mimeType, BufferedOutputStream out) {
        try {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
        } catch (IOException e) {e.printStackTrace();}
    }
}
