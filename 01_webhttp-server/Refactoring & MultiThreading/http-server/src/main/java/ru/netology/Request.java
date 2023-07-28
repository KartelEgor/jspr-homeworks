package ru.netology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private final static String DELIMITER = "\r\n\r\n";
    private final static String NEW_LINE = "\r\n";
    private final static String HEADER_DELIMITER = ":";
    private final String message;
    private HttpMethods method;
    private final String url;
    private final Map<String, String> headers;
    private final String body;

    public Request(String message) {
        this.message = message;

        String[] parts = message.split(DELIMITER);

        String head = parts[0];

        String[] headers = head.split(NEW_LINE);

        String[] firstLine = headers[0].split(" ");

        method = HttpMethods.valueOf(firstLine[0]);

        url = firstLine[1];

        //обрабатываем тело сообщения и сохраняем в мап
        this.headers = Collections.unmodifiableMap(
                new HashMap<>() {{
                    for (int i = 1; i < headers.length; i++) {
                        String[] headerPart = headers[i].split(HEADER_DELIMITER, 2);
                        put(headerPart[0].trim(), headerPart[1].trim());
                    }
                }}
        );

        //Определение длины контента в теле
        String bodyLength = this.headers.get("Content-Length");
        int length = bodyLength != null ? Integer.parseInt(bodyLength) : 0;
        this.body = parts.length > 1 ? parts[1].trim().substring(0, length) : " ";
    }

    public String getMessage() {
        return message;
    }

    public HttpMethods getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Path getFilePath() {
        return Path.of("." + "public" + getUrl());
    }

    public String getMimeType() {
        try {
            return Files.probeContentType(getFilePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
