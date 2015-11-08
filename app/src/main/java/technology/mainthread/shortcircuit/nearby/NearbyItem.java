package technology.mainthread.shortcircuit.nearby;

public class NearbyItem {

    private String id;
    private String device;
    private long timestamp;

    public NearbyItem(String id, String device, long timestamp) {
        this.id = id;
        this.device = device;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getDevice() {
        return device;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
