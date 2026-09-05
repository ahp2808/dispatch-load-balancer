package com.freightfox.dispatch.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> homeHtml() {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Dispatch Load Balancer API</title>
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; line-height: 1.6; color: #333; }
                        h1 { color: #1e293b; margin-bottom: 0.5rem; }
                        p { color: #64748b; margin-top: 0; }
                        ul { list-style-type: none; padding: 0; }
                        li { margin: 10px 0; padding: 12px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; }
                        code { background: #e2e8f0; padding: 2px 6px; border-radius: 4px; font-family: monospace; }
                        a { color: #2563eb; text-decoration: none; font-weight: 600; }
                        a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <h1>Dispatch Load Balancer API</h1>
                    <p>Spring Boot service is running.</p>
                    <h3>Available Endpoints</h3>
                    <ul>
                        <li><a href="/api/dispatch/plan">GET /api/dispatch/plan</a> &mdash; Calculate & view optimized dispatch plan</li>
                        <li><code>POST /api/dispatch/vehicles</code> &mdash; Submit vehicle batch</li>
                        <li><code>POST /api/dispatch/orders</code> &mdash; Submit order batch</li>
                        <li><a href="/api/dispatch/vehicles">GET /api/dispatch/vehicles</a> &mdash; List current vehicles</li>
                        <li><a href="/api/dispatch/orders">GET /api/dispatch/orders</a> &mdash; List current orders</li>
                    </ul>
                </body>
                </html>
                """;
        return ResponseEntity.ok(html);
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> homeJson() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "Dispatch Load Balancer REST API");
        response.put("status", "UP");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("GET /api/dispatch/plan", "Retrieve optimized dispatch plan");
        endpoints.put("POST /api/dispatch/orders", "Submit delivery orders batch");
        endpoints.put("GET /api/dispatch/orders", "List all delivery orders");
        endpoints.put("POST /api/dispatch/vehicles", "Submit fleet vehicles batch");
        endpoints.put("GET /api/dispatch/vehicles", "List all fleet vehicles");
        response.put("endpoints", endpoints);

        return ResponseEntity.ok(response);
    }
}
