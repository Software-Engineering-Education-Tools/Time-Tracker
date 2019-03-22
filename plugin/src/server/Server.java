package server;

import logger.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import data.DataProvider;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Server extends WebSocketServer {

    private WebSocket connection;
    private DataProvider dataProvider;


    public Server(int port, DataProvider dataProvider) {
        super(new InetSocketAddress(port));
        this.dataProvider = dataProvider;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        Logger.log("closed " + conn.getRemoteSocketAddress() + "connected.", "Server");
        this.connection = conn;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Logger.log("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason, "Server");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        message = message.trim();
        ServerMessage serverMessage = new ServerMessage(message);

        switch (serverMessage.type) {
            case "test": {
                conn.send(serverMessage.payload);
                break;
            }
            case "dashboardData": {
                String data = dataProvider.getDashboardData();
                Logger.log(data, "Server");
                conn.send(data);
                break;
            }
            case "projectData": {
                String data = dataProvider.getProjectData(serverMessage.payload);
                conn.send(data);
                break;
            }
            case "graphData": {
                String data = dataProvider.getGraphData();
                conn.send(data);
                break;
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Logger.log("an error occured on connection ", "Server");//+ conn.getRemoteSocketAddress());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        Logger.log("server started successfully", "Server");
    }

    public void shutdown() {
        try {
            stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}