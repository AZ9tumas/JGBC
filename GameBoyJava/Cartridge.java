package GameBoyJava;

import java.nio.charset.StandardCharsets;

public class Cartridge {
    public byte[] fileData;

    public String title;
    public String manufacturerCode;

    private void readTitle(byte[] fileData){
        this.title = new String(this.fileData, 0x134, 11, StandardCharsets.US_ASCII);
    }

    public void initCartridge (byte[] fileData){
        this.fileData = fileData;
        readTitle(this.fileData);
    }
}
