package io.github.bananalang.vm.objects;

public class BananaDecimal extends BananaObject {
    public final double value;

    BananaDecimal(double value) {
        this.value = value;
    }

    public static BananaDecimal valueOf(double value) {
        return new BananaDecimal(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
