package io.github.bananalang.vm.objects;

import java.math.BigInteger;

public class BananaInt extends BananaObject {
    private static final BananaInt[] CACHE;
    public static final BananaInt ZERO, ONE, TWO;

    static {
        CACHE = new BananaInt[256];
        for (int i = 0; i < 256; i++) {
            CACHE[i] = new BananaInt(BigInteger.valueOf(i - 128));
        }
        ZERO = CACHE[128];
        ONE = CACHE[129];
        TWO = CACHE[130];
    }

    public final BigInteger value;

    BananaInt(BigInteger value) {
        this.value = value;
    }

    public static BananaInt valueOf(BigInteger value) {
        return new BananaInt(value);
    }

    public static BananaInt valueOf(int value) {
        if (value > -129 & value < 128) {
            return CACHE[value + 128];
        }
        return new BananaInt(BigInteger.valueOf(value));
    }

    public static BananaInt valueOf(long value) {
        if (value > -129 & value < 128) {
            return CACHE[(int)(value + 128)];
        }
        return new BananaInt(BigInteger.valueOf(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
