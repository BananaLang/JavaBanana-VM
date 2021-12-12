package io.github.bananalang;

import java.io.IOException;

import io.github.bananalang.bytecode.ByteCodeFile;
import io.github.bananalang.vm.BananaVM;

public class VMTest {
    public static void main(String[] args) throws IOException {
        ByteCodeFile bbc = ByteCodeFile.read("example.bbc");
        bbc.disassemble();
        BananaVM.execute(bbc);
    }
}
