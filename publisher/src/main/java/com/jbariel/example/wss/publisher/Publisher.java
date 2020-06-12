package com.jbariel.example.wss.publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Publisher {

    private final static Logger log = LoggerFactory.getLogger(Publisher.class);

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private static int WSS_PORT = NumberUtils.toInt(System.getenv("WSS_PORT"), 9001);;

    public static void main(final String[] args) {
        log.info("Starting Publisher...");

        final WsServer server = new WsServer();
        final Thread sThread = new Thread(server::run);
        sThread.start();
        final ScheduledFuture<?> future = executor.scheduleAtFixedRate(server::tick, 2, 1, TimeUnit.SECONDS);

        log.debug("Reading console...");
        log.info("\tType 'exit' to quit");
        log.info("\tOther typed messages will broadcast");
        log.info("What would you like to say?");
        try (BufferedReader in = new BufferedReader(new InputStreamReader((System.in)))) {
            while (true) {
                String msg = StringUtils.trimToNull(in.readLine());
                if (null != msg) {
                    server.broadcast(msg);
                    if ("exit".equalsIgnoreCase(msg)) {
                        future.cancel(false);
                        server.stop(1000);
                        break;
                    }
                }
            }
        } catch (IOException | InterruptedException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }

        System.exit(0);
    }

    static class WsServer extends WebSocketServer {

        private final Logger log = LoggerFactory.getLogger(WsServer.class);

        public WsServer() {
            super(new InetSocketAddress(Publisher.WSS_PORT));
        }

        ZonedDateTime currTime = ZonedDateTime.now();

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            log.info(String.format("Connected [%s]", conn));
            conn.send(asString(currTime));
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            log.info(String.format("CLOSED CONNECTION [%s] [%d]: %s", conn, code, reason));
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            log.info("[RECIEVED MESSAGE] " + message);
            broadcast(message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }

        @Override
        public void onStart() {
            log.info("Started WSS on port " + WSS_PORT);
            setConnectionLostTimeout(5);
        }

        @Override
        public void broadcast(String text) {
            log.info("[BROADCASTING] " + text);
            super.broadcast(text);
        }

        public void tick() {
            currTime = ZonedDateTime.now();
            broadcast(currTime);
        }

        public void broadcast(ZonedDateTime time) {
            broadcast(asString(time));
        }

        protected String asString(ZonedDateTime time) {
            return time.toString();
        }

    }
}