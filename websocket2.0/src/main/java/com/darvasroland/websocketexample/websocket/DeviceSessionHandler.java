package com.darvasroland.websocketexample.websocket;


import com.darvasroland.websocketexample.model.Device;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author darvasroland
 */
@ApplicationScoped
public class DeviceSessionHandler {

    private long deviceId = 0;

    private final Set<Session> sessions = new HashSet<>();

    private final Set<Device> devices = new HashSet<>();

    private final Logger logger = Logger.getLogger(DeviceSessionHandler.class.getName());


    public void addSession(Session session) {
        sessions.add(session);

        for (Device device : devices) {
            JsonObject addMessage = createAddMessage(device);
            sendToSession(session, addMessage);
        }
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }

    public void addDevice(Device device) {
        device.setId(deviceId);
        devices.add(device);
        deviceId++;
        JsonObject addMessage = createAddMessage(device);
        sendToAllConnectedSessions(addMessage);
    }

    public void removeDevice(long id) {
        Device device = getDeviceById(id);
        if (device != null) {
            devices.remove(device);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder().add("action", "remove")
                    .add("id", id).build();
            sendToAllConnectedSessions(removeMessage);
        }
    }

    public void toggleDevice(long id) {
        JsonProvider provider = JsonProvider.provider();
        Device device = getDeviceById(id);
        if (device != null) {
            if ("on".equals(device.getStatus())) {
                device.setStatus("Off");
            } else {
                device.setStatus("On");
            }

            JsonObject updateDevMessage = provider.createObjectBuilder().add("action", "toggle")
                    .add("id", device.getId()).add("status", device.getStatus()).build();
            sendToAllConnectedSessions(updateDevMessage);
        }
    }

    private Device getDeviceById(long id) {
        for (Device device: devices) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

    private JsonObject createAddMessage(Device device) {
        JsonProvider provider = JsonProvider.provider();
        return provider.createObjectBuilder().add("action", "add")
                .add("id", device.getId()).add("name", device.getName()).add("type", device.getType())
                .add("status", device.getStatus()).add("description", device.getDescription()).build();
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        for (Session session: sessions) {
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException e) {
            sessions.remove(session);
            logger.log(Level.SEVERE, null, e);
        }
    }
}
