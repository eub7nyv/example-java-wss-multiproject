package com.jbariel.example.wss.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {

    private final static Logger log = LoggerFactory.getLogger(Consumer.class);

    private static int INIT_DELAY = NumberUtils.toInt(System.getenv("INIT_DELAY"), 2);
    private static int WSS_PORT = NumberUtils.toInt(System.getenv("WSS_PORT"), 9001);
    private static String WSS_HOST = StringUtils.trimToNull(System.getenv("WSS_HOST"));
    protected static URI WSS_URI;

    static {
        if (null == WSS_HOST) {
            WSS_HOST = "localhost";
        }
        try {
            log.debug("Building URI for : '" + WSS_HOST + "' on port : '" + WSS_PORT + "'");
            WSS_URI = new URI("ws://" + WSS_HOST + ":" + WSS_PORT);
        } catch (URISyntaxException e) {
            log.error("Could not generate URI!", e);
        }
    }

    public static void main(final String[] args) {
        log.info("Starting Consumer...");
        log.debug("Waiting " + INIT_DELAY + "s...");
        try {
            Thread.sleep(INIT_DELAY * 1000);
        } catch (InterruptedException e1) {
            log.error(e1.getLocalizedMessage(), e1);
        }

        final WsClient client = new WsClient();
        final Thread cThread = new Thread(client::run);
        cThread.start();

        log.debug("Reading console...");
        log.info("\tType 'exit' to quit");
        try (BufferedReader in = new BufferedReader(new InputStreamReader((System.in)))) {
            while (true) {
                String i = in.readLine();
                if ("exit".equalsIgnoreCase(i)) {
                    break;
                }
            }
        } catch (IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }

        client.close();
        try {
            cThread.join();
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        System.exit(0);
    }

    static class WsClient extends WebSocketClient {

        private final Logger log = LoggerFactory.getLogger(WsClient.class);

        public WsClient() {
            super(Consumer.WSS_URI);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            log.info(String.format("CLOSED [%d]: %s", code, reason));
        }

        @Override
        public void onError(Exception ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }

        @Override
        public void onMessage(String msg) {
            log.info("MSG: " + msg);
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            log.info("Connected!");
        }

    }
}