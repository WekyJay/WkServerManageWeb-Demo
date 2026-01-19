package cn.wekyjay.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/server")
public class ServerBasicController {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Object tryParseJson(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        char c = t.charAt(0);
        if (c == '{' || c == '[') {
            try {
                return MAPPER.readValue(t, Object.class);
            } catch (JsonProcessingException ignored) {
            }
        }
        return null;
    }

    @GetMapping("/info")
    public ResponseEntity<Result<Object>> info(
            @RequestParam(value = "host", required = false, defaultValue = "127.0.0.1") String host,
            @RequestParam(value = "port", required = false, defaultValue = "5380") int port,
            @RequestParam(value = "connTimeoutMs", required = false, defaultValue = "2000") int connTimeoutMs,
            @RequestParam(value = "readTimeoutMs", required = false, defaultValue = "5000") int readTimeoutMs
    ) {
        try {
            String response = sendCommand(host, port, "INFO", connTimeoutMs, readTimeoutMs);
            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(Result.fail(HttpStatus.BAD_GATEWAY.value(), "server closed connection without response"));
            }
            Object json = tryParseJson(response);
            return ResponseEntity.ok(Result.ok(json != null ? json : response));
        } catch (SocketTimeoutException e) {
            return ResponseEntity
                    .status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(Result.fail(HttpStatus.GATEWAY_TIMEOUT.value(), "read-timeout: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(Result.fail(HttpStatus.BAD_GATEWAY.value(), "connect/send failed: " + e.getMessage()));
        }
    }

    @PostMapping("/exec")
    public ResponseEntity<Result<Object>> exec(
            @RequestParam(value = "payload", required = false) String payload,
            @RequestParam(value = "host", required = false, defaultValue = "127.0.0.1") String host,
            @RequestParam(value = "port", required = false, defaultValue = "5380") int port,
            @RequestParam(value = "connTimeoutMs", required = false, defaultValue = "2000") int connTimeoutMs,
            @RequestParam(value = "readTimeoutMs", required = false, defaultValue = "5000") int readTimeoutMs,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        if (payload == null && body != null) {
            Object p = body.get("payload");
            if (p != null) payload = String.valueOf(p);
        }
        if (payload == null || payload.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Result.fail(HttpStatus.BAD_REQUEST.value(), "exec payload is empty; provide 'payload' as query or JSON body"));
        }

        try {
            String response = sendCommand(host, port, "EXEC " + payload, connTimeoutMs, readTimeoutMs);
            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(Result.fail(HttpStatus.BAD_GATEWAY.value(), "server closed connection without response"));
            }
            Object json = tryParseJson(response);
            return ResponseEntity.ok(Result.ok(json != null ? json : response));
        } catch (SocketTimeoutException e) {
            return ResponseEntity
                    .status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(Result.fail(HttpStatus.GATEWAY_TIMEOUT.value(), "read-timeout: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(Result.fail(HttpStatus.BAD_GATEWAY.value(), "connect/send failed: " + e.getMessage()));
        }
    }

    private String sendCommand(String host, int port, String line, int connectTimeoutMs, int readTimeoutMs)
            throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), connectTimeoutMs);
            socket.setSoTimeout(readTimeoutMs);
            try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                writer.println(line);
                return reader.readLine();
            }
        }
    }
}