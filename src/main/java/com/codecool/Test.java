package com.codecool;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    public static Map<String, Method> routes = new HashMap<>();

    public static void main(String[] args) throws Exception {
        readRoutes();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new ResponseHandler());
        server.setExecutor(null);
        server.start();
    }

    private static void readRoutes() {
        for (Method m : Routes.class.getMethods()) {
            if (m.isAnnotationPresent(WebRoute.class)) {
                WebRoute annotation = m.getAnnotation(WebRoute.class);
                routes.put(annotation.route(), m);
            }
        }
    }

    static class ResponseHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestPath = httpExchange.getRequestURI().getPath();
            Method responseMethod = routes.get(requestPath);
            if (responseMethod == null) {
                sendResponse(httpExchange, 404, "Not found");
                return;
            }
            try {
                sendResponse(httpExchange, 200, (String) responseMethod.invoke(null));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        private void sendResponse(HttpExchange httpExchange, int statusCode, String response) throws IOException {
            httpExchange.sendResponseHeaders(statusCode, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


}