package de.emilschlampp.schecpuminecraft.util;

import de.emilschlampp.scheCPU.util.FolderIOUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ChannelData {
    private static final int VERSION = 1;

    private int[] data;

    public ChannelData(InputStream in) throws IOException {
        int version = FolderIOUtil.readInt(in);

        if(version == 1) {
            int len = FolderIOUtil.readInt(in);
            data = new int[len];
            for (int i = 0; i < len; i++) {
                data[i] = FolderIOUtil.readInt(in);
            }
        }
    }

    public ChannelData() {
        data = new int[10];
    }

    public void write(OutputStream outputStream) throws IOException {
        FolderIOUtil.writeInt(outputStream, VERSION);

        FolderIOUtil.writeInt(outputStream, data.length);
        for (int datum : data) {
            FolderIOUtil.writeInt(outputStream, datum);
        }
    }
}
