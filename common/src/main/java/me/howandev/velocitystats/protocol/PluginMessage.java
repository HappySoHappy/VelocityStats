package me.howandev.velocitystats.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface PluginMessage {
    String getSubchannel();

    void write(DataOutput out) throws IOException;

    void read(DataInput in) throws IOException;
}
