package GameBoyRun;
import GameBoyJava.CByte;
import GameBoyJava.Emulator;

public class display {
    public void display_registers(Emulator emu) {
        System.err.printf("[A%02x|B%02x|C%02x|D%02x|E%02x|H%02x|L%02x|SP%04x]\n",
        
            emu.AF.MostSignificantByte & 0xFF, 
            emu.BC.MostSignificantByte & 0xFF, 
            emu.BC.LeastSignificantByte & 0xFF, 
            emu.DE.MostSignificantByte & 0xFF, 
            emu.DE.LeastSignificantByte & 0xFF, 
            emu.HL.MostSignificantByte & 0xFF, 
            emu.HL.LeastSignificantByte & 0xFF,
            emu.SP.combine_to_uint16()
        );
    }

    public void displayFlags(Emulator emu){
        int flagState = emu.AF.LeastSignificantByte;

        System.out.printf("[Z%d", flagState >> 7);
        System.out.printf(" N%d", (flagState >> 6) & 1);
        System.out.printf(" H%d", (flagState >> 5) & 1);
        System.out.printf(" C%d]", (flagState >> 7) & 1);
        
    }

    byte d_read(Emulator emu, CByte address){
        int addr = address.combine_to_uint16();
        
        if (addr >= 0x00 && addr <= 0x7ffff) {
            return emu.cart.fileData[addr];
        }

        return (byte)0xff;
    }

    int d_read_one_byte(Emulator emu){
        CByte a = new CByte(emu.PC.MostSignificantByte, emu.PC.LeastSignificantByte);
        a.set_u16Byte(a.combine_to_uint16() + 1);

        return d_read(emu, a);
    }

    int d_read_two_bytes(Emulator emu){

        CByte a1 = new CByte(emu.PC.MostSignificantByte, emu.PC.LeastSignificantByte);
        a1.set_u16Byte(a1.combine_to_uint16() + 1);

        CByte a2 = new CByte(emu.PC.MostSignificantByte, emu.PC.LeastSignificantByte);
        a2.set_u16Byte(a2.combine_to_uint16() + 2);

        byte l = d_read(emu, a1);
        byte h = d_read(emu, a2);

        CByte f = new CByte(h, l);

        return f.combine_to_uint16();
    }

    void simple_instruction(String inst) {

        System.out.printf("\t  %s\n", inst);
    }
    
    void a16(Emulator emu, String inst){
        System.out.printf("\t  %s (0x%04x)\n", inst, d_read_two_bytes(emu));
    }

    void d16(Emulator emu, String inst){
        a16(emu, inst);
    }

    void d8(Emulator emu, String inst){

        System.out.printf("\t %s (0x%02x)\n", inst, d_read_one_byte(emu));
    }

    public void display_instruction(Emulator emu) {

        byte addr = d_read(emu, emu.PC);
        System.out.printf("[0x%04x]", emu.PC.combine_to_uint16());

        displayFlags(emu);

        System.out.printf(" [[ 0x%02x ]]", addr);
        
        switch (addr) {
            case (byte) 0x00: simple_instruction("NOP"); break;

            case (byte) 0x01: d16(emu, "LD BC, d16"); break;
            case (byte) 0x02: simple_instruction("LD (BC), A"); break;
            case (byte) 0x03: simple_instruction("INC BC"); break;
            case (byte) 0x04: simple_instruction("INC B"); break;
            case (byte) 0x05: simple_instruction("DEC B"); break;
            case (byte) 0x06: d8(emu, "LD B, u8"); break;

            case (byte) 0x11: d16(emu, "LD DE, d16"); break;
            case (byte) 0x21: d16(emu, "LD HL, d16"); break;
            case (byte) 0x31: d16(emu, "LD SP, d16"); break;

            case (byte) 0xc3: a16(emu, "JP a16"); break;
            case (byte) 0x3e: d8(emu, "LD A, d8"); break;

            default: System.out.printf("\n"); break;
        }
    }
}
