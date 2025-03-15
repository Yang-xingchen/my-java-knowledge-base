package other;

import java.io.Serializable;

public class Entry implements Serializable, Cloneable {


    private static int useConstructorCount = 0;

    private long aLong;

    private int integer = 1;

    private double aDouble;

    private String string;
    public String pubString;

    public static int getUseConstructorCount() {
        return useConstructorCount;
    }

    public Entry() {
        useConstructorCount++;
    }

    public long getaLong() {
        return aLong;
    }

    public int getInteger() {
        return integer;
    }

    public double getaDouble() {
        return aDouble;
    }

    public String getString() {
        return string;
    }

    public String getPubString() {
        return pubString;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        return clone;
    }

}
