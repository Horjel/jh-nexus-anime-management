package com.otakucenter.util;

import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class CsvUtils {

    private CsvUtils() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.replace("\"", "\"\"");
        return "\"" + normalized + "\"";
    }

    public static ResponseEntity<byte[]> buildResponse(String fileName, String csvContent) {
        String withBom = "\uFEFF" + csvContent;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(withBom.getBytes(StandardCharsets.UTF_8));
    }
}
