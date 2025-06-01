package de.emilschlampp.schecpuminecraft.screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.emilschlampp.scheCPU.emulator.ProcessorEmulator;
import de.emilschlampp.scheCPU.util.FolderIOUtil;

public class ScheCPUScreen {
    private int instructionPort = 130;
    private int colourArgPort = 131;
    private int xArgPort = 132;
    private int yArgPort = 133;
    private int widthArgPort = 134;
    private int heightArgPort = 135;
    private int triggerPort = 136;
    private int textBufferPort = 137;

    private final BufferedImage image;
    private final Graphics2D graphics2D;
    private String textBuffer = "";

    public ScheCPUScreen(int width, int height, int colour) {
        this.image = new BufferedImage(width, height, colour);
        this.graphics2D = this.image.createGraphics();
        this.graphics2D.setFont(this.graphics2D.getFont().deriveFont(12f));
    }

    public ScheCPUScreen(InputStream inputStream) throws IOException {
        image = new BufferedImage(
                FolderIOUtil.readInt(inputStream),
                FolderIOUtil.readInt(inputStream),
                FolderIOUtil.readInt(inputStream)
        );
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, FolderIOUtil.readInt(inputStream));
            }
        }
        textBuffer = FolderIOUtil.readString(inputStream);
        this.graphics2D = this.image.createGraphics();
        this.graphics2D.setFont(this.graphics2D.getFont().deriveFont(12f));
    }

    public void write(OutputStream outputStream) throws IOException {
        FolderIOUtil.writeInt(outputStream, image.getWidth());
        FolderIOUtil.writeInt(outputStream, image.getHeight());
        FolderIOUtil.writeInt(outputStream, image.getType());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                FolderIOUtil.writeInt(outputStream, image.getRGB(x, y));
            }
        }

        FolderIOUtil.writeString(outputStream, textBuffer);
    }

    public void tick(ProcessorEmulator emulator) {
        if(emulator.getIo()[textBufferPort] != 0) {
            if(textBuffer.length() < 255) {
                textBuffer += ((char) emulator.getIo()[textBufferPort]);
            }
            emulator.getIo()[textBufferPort] = 0;
        }
        if(emulator.getIo()[triggerPort] != 0) {
            emulator.getIo()[triggerPort] = 0;

            int[] io = emulator.getIo();

            if(io[instructionPort] == 0) { // Set colour raw
                graphics2D.setColor(new Color(io[colourArgPort]));
            }
            if(io[instructionPort] == 1) { // Set colour selected
                switch (io[colourArgPort]) {
                    case 0:
                        graphics2D.setColor(Color.BLACK);
                        break;
                    default:
                        graphics2D.setColor(Color.WHITE);
                        break;
                }
            }
            if(io[instructionPort] == 2) { // Fill rect
                graphics2D.fillRect(
                        io[xArgPort],
                        io[yArgPort],
                        io[widthArgPort],
                        io[heightArgPort]
                );
            }
            if(io[instructionPort] == 3) { // Draw rect
                graphics2D.drawRect(
                        io[xArgPort],
                        io[yArgPort],
                        io[widthArgPort],
                        io[heightArgPort]
                );
            }
            if(io[instructionPort] == 5) { // Text buffer clear
                textBuffer = "";
            }
            if(io[instructionPort] == 6) { // Text buffer draw
                graphics2D.drawString(textBuffer, io[xArgPort], io[yArgPort]);
            }
        }
    }

    public int getInstructionPort() {
        return instructionPort;
    }

    public int getxArgPort() {
        return xArgPort;
    }

    public int getyArgPort() {
        return yArgPort;
    }

    public int getWidthArgPort() {
        return widthArgPort;
    }

    public int getHeightArgPort() {
        return heightArgPort;
    }

    public int getTriggerPort() {
        return triggerPort;
    }

    public BufferedImage getImage() {
        return image;
    }

    public ScheCPUScreen setInstructionPort(int instructionPort) {
        this.instructionPort = instructionPort;
        return this;
    }

    public ScheCPUScreen setxArgPort(int xArgPort) {
        this.xArgPort = xArgPort;
        return this;
    }

    public ScheCPUScreen setyArgPort(int yArgPort) {
        this.yArgPort = yArgPort;
        return this;
    }

    public ScheCPUScreen setWidthArgPort(int widthArgPort) {
        this.widthArgPort = widthArgPort;
        return this;
    }

    public ScheCPUScreen setHeightArgPort(int heightArgPort) {
        this.heightArgPort = heightArgPort;
        return this;
    }

    public ScheCPUScreen setTriggerPort(int triggerPort) {
        this.triggerPort = triggerPort;
        return this;
    }

    public int getColourArgPort() {
        return colourArgPort;
    }

    public ScheCPUScreen setColourArgPort(int colourArgPort) {
        this.colourArgPort = colourArgPort;
        return this;
    }

    public Graphics2D getGraphics2D() {
        return graphics2D;
    }
}
