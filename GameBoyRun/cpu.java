package GameBoyRun;

import GameBoyJava.*;

public class cpu {

    public Emulator emu;

    public void start(Emulator emu){
        this.emu = emu;

        for (int i = 0; i < 10; i++) dispatch();
        
    }

    byte read( CByte address){
        int addr = address.combine_to_uint16();
        
        if (addr >= 0x00 && addr <= 0x7ffff) {
            return this.emu.cart.fileData[addr];
        } else System.out.println("Invalid read command");

        return (byte) 0xff;
    }

    private void write( CByte address, byte b){

        //System.out.printf("Writing into addr: 0x%04x, byte: 0x%02x\n", address.combine_to_uint16(), b);

        /* address = byte */

        int ROM_N0_16KB = 0x0000;
        int ROM_N0_16KB_END = 0x3FFF;
    
        int ROM_N1_NN_16KB = 0x4000;          // ROM Bank 01~NN
        int ROM_N1_NN_16KB_END = 0x7FFF;
    
        int VRAM_8KB = 0x8000;                // Video RAM
        int VRAM_8KB_END = 0x9FFF;
    
        int EXTERNAL_RAM_8KB = 0xA000;        // External RAM
        int EXTERNAL_RAM_8KB_END = 0xBFFF;
    
        int WRAM_4KB = 0xC000;                // Work RAM
        int WRAM_4KB_END = 0xCFFF;
    
        int WRAM_SWITCHABLE_4KB = 0xD000;     // Work RAM (Switchable Bank)
        int WRAM_SWITCHABLE_4KB_END = 0xDFFF;
    
        int ECHO_RAM = 0xE000;                // Mirror of C000~DDFF (ECHO RAM)
        int ECHO_RAM_END = 0xFDFF;
    
        int OAM = 0xFE00;                     // Sprite attribute table (OAM)
        int OAM_END = 0xFE9F;
    
        int NOT_USABLE = 0xFEA0;              // Not Usable
        int NOT_USABLE_END = 0xFEFF;
    
        int IO_REGISTERS = 0xFF00;            // I/O Registers
        int IO_REGISTERS_END = 0xFF7F;
    
        int HIGH_RAM = 0xFF80;                // High RAM (HRAM)
        int HIGH_RAM_END = 0xFFFE;
    
        int INTERRUPT_ENABLE = 0xFFFF;          // Interrupt Enable register (IE)

        int addr = address.combine_to_uint16();


        if ((addr >= ECHO_RAM && addr <= ECHO_RAM_END) || (addr >= NOT_USABLE && addr <= NOT_USABLE_END)) {System.out.printf("LOL\n"); return;}

        /*if (addr >= ROM_N0_16KB && addr <= ROM_N0_16KB_END) System.out.printf("ROM_NO 16kb\n");
        else*/ if (addr >= VRAM_8KB && addr <= VRAM_8KB_END) this.emu.vram[addr - VRAM_8KB] = b;
        else if (addr >= WRAM_4KB && addr <= WRAM_4KB_END) this.emu.wram1[addr - WRAM_4KB] = b;
        else if (addr >= WRAM_SWITCHABLE_4KB && addr <= WRAM_SWITCHABLE_4KB_END) this.emu.wram2[addr - WRAM_SWITCHABLE_4KB] = b;
        else if (addr >= HIGH_RAM && addr <= HIGH_RAM_END) this.emu.hram[addr - HIGH_RAM] = b;
        else if (addr >= IO_REGISTERS && addr <= IO_REGISTERS_END) { }
    }

    byte read_u8(){
        byte resp = read(this.emu.PC);

        /* Increment the instruction pointer */
        this.emu.PC.set_u16Byte(this.emu.PC.combine_to_uint16() + 1);

        return resp;
    }

    CByte read_u16(){
        
        CByte a1 = new CByte(this.emu.PC.MostSignificantByte, this.emu.PC.LeastSignificantByte);

        CByte a2 = new CByte(this.emu.PC.MostSignificantByte, this.emu.PC.LeastSignificantByte);
        a2.set_u16Byte(a2.combine_to_uint16() + 1);

        byte l = read(a1);
        byte h = read(a2);

        CByte f = new CByte(h, l);

        this.emu.PC.set_u16Byte(this.emu.PC.combine_to_uint16() + 2);

        return f;
    }

    /* Functions for LD operations */

    void LOAD_u16_REG16( CByte register) {
        register.set_u16Byte(read_u16().combine_to_uint16());
    }

    void LOAD_REG_TO_u16( CByte register){
        
    }

    void inc_reg8(byte oldval) {
        
    }

    private byte rotate_left( byte regVal, boolean zFlag, boolean carryFlag){

        byte lastBit = (byte) (regVal >> 7);

        regVal <<= 1;
        regVal |= carryFlag ? this.emu.getFlag(Flag.FLAG_C) : lastBit;

        /* Flag operations */


        return regVal;
    }

    private void dispatch(){

        /* Display details of last instruction */
        display displayer = new display();
        
        /* Display register info */
        displayer.display_registers(this.emu);
        
        /* Display instruction pointer , flags and instruction details */
        displayer.display_instruction(this.emu);

        /* Increment, then check address */
        byte addr = read_u8();

        switch (addr) {
            case (byte) 0x00: break;
            case (byte) 0x01: LOAD_u16_REG16(this.emu.BC); break;
            case (byte) 0x02: {
                /* Write the value of the given register to the given address */
                /* LD (BC),A
                * addr(BC) = A
                */

                //System.out.printf("Came across this instruction!! \n");

                byte addr2 = read(this.emu.BC);

                CByte copy = new CByte((byte) 0x00, addr2);

                //System.out.printf("addr2: 0x%02x, copy: 0x%04x, A: 0x%02x\n", addr2, copy.combine_to_uint16(), this.emu.AF.MostSignificantByte);
                write(copy, this.emu.AF.MostSignificantByte);

                break;
            }

            case (byte) 0x03: this.emu.BC.set_u16Byte(this.emu.BC.combine_to_uint16() + 1); break;
            case (byte) 0x04: this.emu.BC.MostSignificantByte ++; break;
            case (byte) 0x05: this.emu.BC.MostSignificantByte --; break;

            case (byte) 0x06: this.emu.BC.MostSignificantByte = read_u8(); break;

            case (byte) 0x11: LOAD_u16_REG16(this.emu.DE); break;
            case (byte) 0x21: LOAD_u16_REG16(this.emu.HL); break;
            case (byte) 0x31: LOAD_u16_REG16(this.emu.SP); break;

            case (byte) 0x3e: this.emu.AF.MostSignificantByte = read_u8(); break;

            case (byte) 0xc3: {
                /* a16 Jump instruction */
                
                CByte inst = read_u16();
                this.emu.PC.set_u16Byte(inst.combine_to_uint16());

                break;
            }

            default: break;
        }
    }
}
