package events;

import logger.Logger;

public class CodeEvent extends Event {

    private String name;
    private String filename;

    public CodeEvent(String name, String message, String filename) {
        super();
        this.setEventType(EventType.CODE);
        this.setMessage(message);
        this.name = name;
        this.filename = filename;
        Logger.log("Code Event created", "CodeEvent");
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }
}

