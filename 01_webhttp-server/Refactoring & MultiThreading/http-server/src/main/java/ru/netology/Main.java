package ru.netology;

import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;

public class Main {
  public static void main(String[] args) {
//    new Server((req, resp) -> {
//    }).start();

    final var server = new Server();
    // код инициализации сервера (из вашего предыдущего ДЗ)

    // добавление хендлеров (обработчиков)
    server.addHandler("GET", "/index.html", (request, responseStream) -> {
      File file = new File(".public/index.html");
      final var length = file.length();
      responseStream.write((
              "HTTP/1.1 200 OK\r\n" +
                      "Content-Type: " + "mimeType" + "\r\n" +
                      "Content-Length: " + length + "\r\n" +
                      "Connection: close\r\n" +
                      "\r\n"
      ).getBytes());
      Files.copy(file.toPath(), responseStream);
      responseStream.flush();
    });
//    server.addHandler("POST", "/messages", new Handler() {
//      public void handle(Request request, BufferedOutputStream responseStream) {
//        // TODO: handlers code
//      }
//    });

//    server.listen(9999);
    server.start();



  }
}


