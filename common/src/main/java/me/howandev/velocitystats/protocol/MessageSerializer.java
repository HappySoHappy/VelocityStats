package me.howandev.velocitystats.protocol;

import me.howandev.velocitystats.protocol.messages.ServerInfoRequest;
import me.howandev.velocitystats.protocol.messages.ServerInfoResponse;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MessageSerializer {
    private static final Map<String, Supplier<PluginMessage>> messageTypes = new HashMap<>();

    static {
        register("RequestServerInfo", ServerInfoRequest::new);
        register("ServerInfoResponse", ServerInfoResponse::new);
    }

    public static void register(String subchannel, Supplier<PluginMessage> supplier) {
        messageTypes.put(subchannel, supplier);
    }

    public static PluginMessage deserialize(byte[] data) throws IOException {
        DataInput in = new DataInputStream(new ByteArrayInputStream(data));
        String subchannel = in.readUTF();

        Supplier<PluginMessage> supplier = messageTypes.get(subchannel);
        if (supplier == null) throw new IOException("Unknown subchannel: " + subchannel);

        PluginMessage message = supplier.get();
        message.read(in);
        return message;
    }

    public static byte[] serialize(PluginMessage message) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(byteOut);

        out.writeUTF(message.getSubchannel());
        message.write(out);
        return byteOut.toByteArray();
    }
}
