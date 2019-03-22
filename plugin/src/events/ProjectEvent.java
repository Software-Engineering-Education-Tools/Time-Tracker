package events;

public class ProjectEvent extends Event {
    public static final String MESSAGE_OPENED = "opened";
    public static final String MESSAGE_CLOSED = "closed";

    private String name;
    private long time;

    public ProjectEvent(String name, String message) {
        super();
        this.setEventType(EventType.PROJECT);
        this.setMessage(message);
        this.name = name;
    }

    public ProjectEvent(String name, String message, long time) {
        super();
        this.setEventType(EventType.PROJECT);
        this.setMessage(message);
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }
}
