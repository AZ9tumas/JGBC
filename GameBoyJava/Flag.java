package GameBoyJava;

public enum Flag {
    FLAG_Z(7),
    FLAG_N(6),
    FLAG_H(5),
    FLAG_C(4);

    private final int value;

    Flag (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
