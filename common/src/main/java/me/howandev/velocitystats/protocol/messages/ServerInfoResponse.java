package me.howandev.velocitystats.protocol.messages;

import lombok.Getter;
import me.howandev.velocitystats.protocol.PluginMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Getter
public class ServerInfoResponse implements PluginMessage {
    private String serverName;
    private boolean online;
    private int ping;
    private int playerCount;
    private int maxPlayerCount;

    public ServerInfoResponse() {}

    public ServerInfoResponse(String serverName, boolean online, int ping, int playerCount, int maxPlayerCount) {
        this.serverName = serverName;
        this.online = online;
        this.ping = ping;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
    }

    @Override
    public String getSubchannel() {
        return "ServerInfoResponse";
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(serverName);
        out.writeBoolean(online);
        out.writeInt(ping);
        out.writeInt(playerCount);
        out.writeInt(maxPlayerCount);
    }

    @Override
    public void read(DataInput in) throws IOException {
        serverName = in.readUTF();
        online = in.readBoolean();
        ping = in.readInt();
        playerCount = in.readInt();
        maxPlayerCount = in.readInt();
    }
}
