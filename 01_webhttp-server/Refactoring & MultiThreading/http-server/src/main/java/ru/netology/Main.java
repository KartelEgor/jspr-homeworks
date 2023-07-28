package ru.netology;

import java.io.BufferedOutputStream;

public class Main {
  public static void main(String[] args) {
//    new Server((req, resp) -> {
//    }).start();

    final var server = new Server();
    // код инициализации сервера (из вашего предыдущего ДЗ)

    // добавление хендлеров (обработчиков)
    server.addHandler("GET", "/messages", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        // TODO: handlers code
      }
    });
    server.addHandler("POST", "/messages", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        // TODO: handlers code
      }
    });

//    server.listen(9999);
    server.start();



  }
}


