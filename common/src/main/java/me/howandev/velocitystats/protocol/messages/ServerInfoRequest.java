package me.howandev.velocitystats.protocol.messages;

import lombok.Getter;
import me.howandev.velocitystats.protocol.PluginMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Getter
public class ServerInfoRequest implements PluginMessage {
    public String serverName;

    public ServerInfoRequest() {}

    public ServerInfoRequest(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public String getSubchannel() {
        return "RequestServerInfo";
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(serverName);
    }

    @Override
    public void read(DataInput in) throws IOException {
        serverName = in.readUTF();
    }
}
