import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.System;

import GameBoyJava.Cartridge;
import GameBoyJava.Emulator;
import GameBoyRun.cpu;

public class GameBoyRomReader {
    public static void main(String[] args) {
        if (args.length > 0) {
            String filePath = args[0];
            File file = new File(filePath);

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                long fileSize = file.length();
                byte[] romData = new byte[(int) fileSize];

                int bytesRead = fileInputStream.read(romData);

                if (bytesRead != fileSize) {
                    System.err.println("Error reading file.");
                    return;
                }

                Cartridge cart = new Cartridge();
                cart.initCartridge(romData);

                Emulator emu = new Emulator();
                emu.cart = cart;

                cpu c = new cpu();

                c.start(emu);


            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            System.out.println("No input file has been provided.");
        }
    }
}
