package io.github.bananalang.vm;

import io.github.bananalang.bytecode.ByteCodeFile;
import io.github.bananalang.bytecode.ByteCodes;
import io.github.bananalang.bytecode.constants.BBCConstant;
import io.github.bananalang.bytecode.constants.DoubleConstant;
import io.github.bananalang.bytecode.constants.IntegerConstant;
import io.github.bananalang.vm.objects.BananaDecimal;
import io.github.bananalang.vm.objects.BananaInt;
import io.github.bananalang.vm.objects.BananaObject;

public final class BananaVM {
    private final static int VALUE_STACK_SIZE = 8192; // Subject to change

    private final ByteCodeFile bbc;

    BananaVM(ByteCodeFile bbc) {
        this.bbc = bbc;
    }

    public static void execute(ByteCodeFile bbc) {
        BananaVM vm = new BananaVM(bbc);
        vm.execute();
    }

    public void execute() {
        BananaObject[] constants = new BananaObject[bbc.getConstantTable().size()];
        for (int i = 0; i < constants.length; i++) {
            BBCConstant constant = bbc.getConstantTable().get(i);
            if (constant instanceof IntegerConstant) {
                constants[i] = BananaInt.valueOf(((IntegerConstant)constant).value);
            } else if (constant instanceof DoubleConstant) {
                constants[i] = BananaDecimal.valueOf(((DoubleConstant)constant).value);
            }
        }

        BananaObject[] valueStack = new BananaObject[VALUE_STACK_SIZE];
        byte[] bytecode = bbc.getBytecode();

        int stackPointer = 0, pc = 0;
        mainloop:
        while (pc < bytecode.length) {
            int code = (bytecode[pc++] & 0xff) | ((bytecode[pc++] & 0xff) << 8);
            switch (code) {
                case ByteCodes.EOF:
                    break mainloop;
                case ByteCodes.DEBUG_PRINT: {
                    System.err.print("[ ");
                    for (int i = 0; i < stackPointer; i++) {
                        System.err.print(valueStack[i] + " ");
                    }
                    System.err.println("]");
                    break;
                }
                case ByteCodes.LOAD_CONSTANT:
                    valueStack[stackPointer++] = constants[(bytecode[pc++] & 0xff) | ((bytecode[pc++] & 0xff) << 8) |
                                                           (bytecode[pc++] & 0xff << 16) | ((bytecode[pc++] & 0xff) << 24)];
                    break;
                case ByteCodes.LOAD_BYTE: {
                    byte b = bytecode[pc++];
                    valueStack[stackPointer++] = BananaInt.valueOf(b & 0xff);
                    break;
                }
                case ByteCodes.LOAD_SBYTE:
                    valueStack[stackPointer++] = BananaInt.valueOf(bytecode[pc++]);
                    break;
                case ByteCodes.POP:
                    valueStack[stackPointer--] = null; // Allow garbage collection
                    break;
                case ByteCodes.LOAD_0:
                    valueStack[stackPointer++] = BananaInt.ZERO;
                    break;
                case ByteCodes.LOAD_1:
                    valueStack[stackPointer++] = BananaInt.ONE;
                    break;
                case ByteCodes.LOAD_2:
                    valueStack[stackPointer++] = BananaInt.TWO;
                    break;
                case ByteCodes.ADD: {
                    BananaObject right = valueStack[--stackPointer];
                    valueStack[stackPointer] = null;
                    BananaObject left = valueStack[--stackPointer];
                    valueStack[stackPointer] = null;
                    if (left instanceof BananaDecimal) {
                        if (right instanceof BananaDecimal) {
                            valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaDecimal)left).value + ((BananaDecimal)right).value);
                        } else {
                            valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaDecimal)left).value + ((BananaInt)right).value.doubleValue());
                        }
                    } else if (right instanceof BananaDecimal) {
                        valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaInt)left).value.doubleValue() + ((BananaDecimal)right).value);
                    } else {
                        valueStack[stackPointer++] = BananaInt.valueOf(((BananaInt)left).value.add(((BananaInt)right).value));
                    }
                    break;
                }
                case ByteCodes.SUB: {
                    BananaObject right = valueStack[--stackPointer];
                    valueStack[stackPointer] = null;
                    BananaObject left = valueStack[--stackPointer];
                    valueStack[stackPointer] = null;
                    if (left instanceof BananaDecimal) {
                        if (right instanceof BananaDecimal) {
                            valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaDecimal)left).value - ((BananaDecimal)right).value);
                        } else {
                            valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaDecimal)left).value - ((BananaInt)right).value.doubleValue());
                        }
                    } else if (right instanceof BananaDecimal) {
                        valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaInt)left).value.doubleValue() - ((BananaDecimal)right).value);
                    } else {
                        valueStack[stackPointer++] = BananaInt.valueOf(((BananaInt)left).value.subtract(((BananaInt)right).value));
                    }
                    break;
                }
                case ByteCodes.MUL: {
                    BananaObject right = valueStack[--stackPointer];
                    valueStack[stackPointer] = null;
                    BananaObject left = valueStack[--stackPointer];
                    valueStack[stackPointer] = null;
                    if (left instanceof BananaDecimal) {
                        if (right instanceof BananaDecimal) {
                            valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaDecimal)left).value * ((BananaDecimal)right).value);
                        } else {
                            valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaDecimal)left).value * ((BananaInt)right).value.doubleValue());
                        }
                    } else if (right instanceof BananaDecimal) {
                        valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaInt)left).value.doubleValue() * ((BananaDecimal)right).value);
                    } else {
                        valueStack[stackPointer++] = BananaInt.valueOf(((BananaInt)left).value.multiply(((BananaInt)right).value));
                    }
                    break;
                }
                case ByteCodes.DIV: {
                    BananaObject right = valueStack[--stackPointer];
                    valueStack[stackPointer] = null;
                    BananaObject left = valueStack[--stackPointer];
                    valueStack[stackPointer] = null;
                    if (left instanceof BananaDecimal) {
                        if (right instanceof BananaDecimal) {
                            valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaDecimal)left).value / ((BananaDecimal)right).value);
                        } else {
                            valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaDecimal)left).value / ((BananaInt)right).value.doubleValue());
                        }
                    } else if (right instanceof BananaDecimal) {
                        valueStack[stackPointer++] = BananaDecimal.valueOf(((BananaInt)left).value.doubleValue() / ((BananaDecimal)right).value);
                    } else {
                        valueStack[stackPointer++] = BananaInt.valueOf(((BananaInt)left).value.divide(((BananaInt)right).value));
                    }
                    break;
                }
            }
        }
    }
}
