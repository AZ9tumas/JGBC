package GameBoyJava;

public class CByte {
    public byte MostSignificantByte;
    public byte LeastSignificantByte;

    public CByte (byte MostSignificantByte, byte LeastSignificantByte) {
        this.MostSignificantByte = MostSignificantByte;
        this.LeastSignificantByte = LeastSignificantByte;
    }

    public int combine_to_uint16(){
        int finalValue = ((this.MostSignificantByte & 0xff) << 8) | (this.LeastSignificantByte & 0xff);
        return finalValue;
    }

    public void set_u16Byte(int value){
        this.MostSignificantByte = (byte) ((value >> 8) & 0xff);
        this.LeastSignificantByte = (byte) (value & 0xff);
    }
}
