package GameBoyJava;

public class Emulator {

    /* Registers */
    public CByte AF = new CByte((byte)0x11, (byte)0x00);
    public CByte BC = new CByte((byte)0x00, (byte)0x00);
    public CByte DE = new CByte((byte)0xff, (byte)0x56);

    public CByte HL = new CByte((byte)0x00, (byte)0x0d);
    
    public CByte PC = new CByte((byte)0x01, (byte)0x00);
    public CByte SP = new CByte((byte)0xff, (byte)0xfe);

    /* Memory */
    public byte[] vram = new byte[0x400]; /* 8 KB */
    
    /* wram1 + wram2 = 8 KB */
    public byte[] wram1 = new byte[0x200];
    public byte[] wram2 = new byte[0x200];

    public byte[] hram = new byte[16];
    public byte[] io = new byte[16];

    public Cartridge cart;

    public void modify_flag(Flag given_flag, int value) {
        int flag = given_flag.getValue();
        
        this.AF.LeastSignificantByte &= ~(1 << flag);
        this.AF.LeastSignificantByte |= value << flag;

    }

    public int getFlag(Flag given_flag) {
        int flag = given_flag.getValue();
        return (this.AF.LeastSignificantByte >> (flag + 4) & 1);
    }
}
