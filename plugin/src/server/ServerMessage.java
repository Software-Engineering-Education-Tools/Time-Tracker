package server;

import logger.Logger;
import org.json.JSONObject;

public class ServerMessage {
    JSONObject messageObject;
    String type;
    String payload;

    ServerMessage(String json) {
        Logger.log(json, "ServerMessage");
        this.messageObject = new JSONObject(json);

        this.type = messageObject.getString("type");
        this.payload = messageObject.getString("payload");
    }

    public ServerMessage(String type, String payload) {
        Logger.log("Type:" + type + ", Payload:" + payload, "ServerMessage");

        this.type = type;
        this.payload = payload;
    }

    public String getMessageAsJSONString() {
        String jsonString = "{\"type\":" + "\"" + type + "\"," + "\"payload\":" + payload + "}";
        Logger.log(jsonString, "ServerMessage");
        return jsonString;
    }
}
