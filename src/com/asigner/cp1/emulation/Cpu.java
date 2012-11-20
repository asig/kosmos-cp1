package com.asigner.cp1.emulation;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Cpu {

    public static final int CY_MASK = 0x80;
    public static final int AC_MASK = 0x40;
    public static final int F0_MASK = 0x20;
    public static final int BS_MASK = 0x10;
    public static final int SP_MASK = 0x07;

    private int A;
    private int PC;
    private byte PSW;
    private byte[] rom = new byte[2048];
    private byte[] ram;

    public Cpu(int ramSize) {
        ram = new byte[ramSize];
        reset();
    }

    public void reset() {
        A = 0;
        PC = 0;
        PSW = 0xf; // bit 3 is always readRom as "1"
        Arrays.fill(ram, (byte)0);
    }

    void run(int cycles) {
        while (cycles > 0) {
            int op = readRom(PC++) & 0xff;
            cycles--;
            switch(op) {
                case 0x00:
                    // NOP
                    break;

                case 0x02:
                    //emit(curPos, "OUTL", "BUS", "A");
                    break;

                case 0x03:
                    int data = readRom(PC++);
                    cycles--;
                    A += data;
                    break;

                case 0x04: case 0x24: case 0x44: case 0x64: case 0x84: case 0xa4: case 0xc4: case 0xe4:
                    int addr = (op & 0xe0) << 3 | (readRom(PC++) & 0xff);
                    cycles--;
                    PC = addr;
                    break;

                case 0x05:
                    //emit(curPos, "EN", "I");
                    break;

                case 0x07:
                    A = (A - 1) & 0xff;
                    break;

                case 0x08:
                    //emit(curPos, "INS", "A", "BUS");
                    break;

//                case 0x09: case 0x0a:
//                    p = op & 0x1;
//                    emit(curPos, "IN", "A", "P" + p);
//                    break;
//
//                case 0x0c: case 0x0d: case 0x0e: case 0x0f:
//                    p = op & 0x3;
//                    emit(curPos, "MOVD", "A", "P" + (4 + p));
//                    break;
//
//                case 0x10: case 0x11:
//                    r = op & 0x1;
//                    emit(curPos, "INC", "@R" + r);
//                    break;
//
//                case 0x12: case 0x32: case 0x52: case 0x72: case 0x92: case 0xb2: case 0xd2: case 0xf2:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    b = op & 0x7;
//                    emit(curPos, "JB" + b, String.format("%04x", addr));
//                    break;
//
//                case 0x13:
//                    data = readRom(pos++);
//                    emit(curPos, "ADDC", "A", "#" + data);
//                    break;
//
//                case 0x14: case 0x34: case 0x54: case 0x74: case 0x94: case 0xb4: case 0xd4: case 0xf4:
//                    addr = (op & 0xe0) << 3 | (readRom(pos++) & 0xff);
//                    emit(curPos, "CALL", String.format("%04x", addr));
//                    break;
//
//                case 0x15:
//                    emit(curPos, "DIS", "I");
//                    break;
//
//                case 0x16:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JTF", String.format("%04x", addr));
//                    break;
//
//                case 0x17:
//                    emit(curPos, "INC", "A");
//                    break;
//
//                case 0x18: case 0x19: case 0x1a: case 0x1b: case 0x1c: case 0x1d: case 0x1e: case 0x1f:
//                    r = op & 0x7;
//                    emit(curPos, "INC", "R" + r);
//                    break;
//
//                case 0x20: case 0x21:
//                    r = op & 0x1;
//                    emit(curPos, "XCH", "A", "@R" + r);
//                    break;
//
//                case 0x23:
//                    data = readRom(pos++);
//                    emit(curPos, "MOV", "A", "#" + data);
//                    break;
//
//                case 0x25:
//                    emit(curPos, "EN", "TCNTI");
//                    break;
//
//                case 0x26:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JNT0", String.format("%04x", addr));
//                    break;
//
//                case 0x27:
//                    emit(curPos, "CLR", "A");
//                    break;
//
//                case 0x28: case 0x29: case 0x2a: case 0x2b: case 0x2c: case 0x2d: case 0x2e: case 0x2f:
//                    r = op & 0x7;
//                    emit(curPos, "XCH", "A", "R" + r);
//                    break;
//
//                case 0x30: case 0x31:
//                    r = op & 0x1;
//                    emit(curPos, "XCHD", "A", "@R" + r);
//                    break;
//
//                case 0x35:
//                    emit(curPos, "DIS", "TCNTI");
//                    break;
//
//                case 0x36:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JT0", String.format("%04x", addr));
//                    break;
//
//                case 0x37:
//                    emit(curPos, "CPL", "A");
//                    break;
//
//                case 0x39: case 0x3a:
//                    p = op & 0x1;
//                    emit(curPos, "OUTL", "P" + p, "A");
//
//                case 0x3c: case 0x3d: case 0x3e: case 0x3f:
//                    p = op & 0x3;
//                    emit(curPos, "MOVD", "P" + (4 + p), "A");
//                    break;
//
//                case 0x40: case 0x41:
//                    r = op & 0x1;
//                    emit(curPos, "ORL", "A", "@R" + r);
//                    break;
//
//                case 0x42:
//                    emit(curPos, "MOV", "A", "T");
//                    break;
//
//                case 0x43:
//                    data = readRom(pos++);
//                    emit(curPos, "ORL", "A", "#" + data);
//                    break;
//
//                case 0x45:
//                    emit(curPos, "STRT", "TCNT");
//                    break;
//
//                case 0x46:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JNT1", String.format("%04x", addr));
//                    break;
//
//                case 0x47:
//                    emit(curPos, "SWAP", "A");
//                    break;
//
//                case 0x48: case 0x49: case 0x4a: case 0x4b: case 0x4c: case 0x4d: case 0x4e: case 0x4f:
//                    r = op & 0x7;
//                    emit(curPos, "ORL", "A", "R" + r);
//                    break;
//
//                case 0x50: case 0x51:
//                    r = op & 0x1;
//                    emit(curPos, "ANL", "A", "@R" + r);
//                    break;
//
//                case 0x53:
//                    data = readRom(pos++);
//                    emit(curPos, "ANL", "A", "#" + data);
//                    break;
//
//                case 0x55:
//                    emit(curPos, "STRT", "T");
//                    break;
//
//                case 0x56:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JT1", String.format("%04x", addr));
//                    break;
//
//                case 0x57:
//                    emit(curPos, "DA", "A");
//                    break;
//
//                case 0x58: case 0x59: case 0x5a: case 0x5b: case 0x5c: case 0x5d: case 0x5e: case 0x5f:
//                    r = op & 0x7;
//                    emit(curPos, "ANL", "A", "R" + r);
//                    break;
//
//                case 0x60: case 0x61:
//                    r = op & 0x1;
//                    emit(curPos, "ADD", "A", "@R" + r);
//                    break;
//
//                case 0x62:
//                    emit(curPos, "MOV", "T", "A");
//                    break;
//
//                case 0x65:
//                    emit(curPos, "STOP", "TCNT");
//                    break;
//
//                case 0x67:
//                    emit(curPos, "RRC", "A");
//                    break;
//
//                case 0x68: case 0x69: case 0x6a: case 0x6b: case 0x6c: case 0x6d: case 0x6e: case 0x6f:
//                    r = op & 0x7;
//                    emit(curPos, "ADD", "A", "R" + r);
//                    break;
//
//                case 0x70: case 0x71:
//                    r = op & 0x1;
//                    emit(curPos, "ADDC", "A", "@R" + r);
//                    break;
//
//                case 0x75:
//                    emit(curPos, "ENT0", "CLK");
//                    break;
//
//                case 0x76:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JF1", String.format("%04x", addr));
//                    break;
//
//                case 0x77:
//                    emit(curPos, "RR", "A");
//                    break;
//
//                case 0x78: case 0x79: case 0x7a: case 0x7b: case 0x7c: case 0x7d: case 0x7e: case 0x7f:
//                    r = op & 0x7;
//                    emit(curPos, "ADDC", "A", "R" + r);
//                    break;
//
//                case 0x80: case 0x81:
//                    r = op & 0x2;
//                    emit(curPos, "MOVX", "A", "@R" + r);
//                    break;
//
//                case 0x83:
//                    emit(curPos, "RET");
//                    break;
//
//                case 0x85:
//                    emit(curPos, "CLR", "F0");
//                    break;
//
//                case 0x86:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JNI", String.format("%04x", addr));
//                    break;
//
//                case 0x88:
//                    data = readRom(pos++);
//                    emit(curPos, "ORL", "BUS", "#" + data);
//                    break;
//
//                case 0x89: case 0x8a:
//                    data = readRom(pos++);
//                    p = op & 0x1;
//                    emit(curPos, "ORL", "P" + p, "#" + data);
//                    break;
//
//                case 0x8c: case 0x8d: case 0x8e: case 0x8f:
//                    p = op & 0x3;
//                    emit(curPos, "ORLD", "P" + (4 + p), "A");
//                    break;
//
//                case 0x90: case 0x91:
//                    r = op & 0x2;
//                    emit(curPos, "MOVX", "@R" + r, "A");
//                    break;
//
//                case 0x93:
//                    emit(curPos, "RETR");
//                    break;
//
//                case 0x95:
//                    emit(curPos, "CPL", "F0");
//                    break;
//
//                case 0x96:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JNZ", String.format("%04x", addr));
//                    break;
//
//                case 0x97:
//                    emit(curPos, "CLR", "C");
//                    break;
//
//                case 0x98:
//                    data = readRom(pos++);
//                    emit(curPos, "ANL", "BUS", "#" + data);
//                    break;
//
//                case 0x99: case 0x9a:
//                    data = readRom(pos++);
//                    p = op & 0x1;
//                    emit(curPos, "ANL", "P" + p, "#" + data);
//                    break;
//
//                case 0x9b:
//                    emit(curPos, "ILLEGAL");
//                    break;
//
//                case 0x9c: case 0x9d: case 0x9e: case 0x9f:
//                    p = op & 0x3;
//                    emit(curPos, "ANLD", "P" + (4 + p), "A");
//                    break;
//
//                case 0xa0: case 0xa1:
//                    r = op & 0x1;
//                    emit(curPos, "MOV", "@R" + r, "A");
//                    break;
//
//                case 0xa3:
//                    emit(curPos, "MOVP", "A", "@A");
//                    break;
//
//                case 0xa5:
//                    emit(curPos, "CLR", "F1");
//                    break;
//
//                case 0xa7:
//                    emit(curPos, "CPL", "C");
//                    break;
//
//                case 0xa8: case 0xa9: case 0xaa: case 0xab: case 0xac: case 0xad: case 0xae: case 0xaf:
//                    r = op & 0x7;
//                    emit(curPos, "MOV", "R" + r, "A");
//                    break;
//
//                case 0xb0: case 0xb1:
//                    data = readRom(pos++);
//                    r = op & 0x1;
//                    emit(curPos, "MOV", "@R" + r, "#" + data);
//                    break;
//
//                case 0xb3:
//                    emit(curPos, "JMPP", "@A");
//                    break;
//
//                case 0xb5:
//                    emit(curPos, "CPL", "F1");
//                    break;
//
//                case 0xb6:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JF0", String.format("%04x", addr));
//                    break;
//
//                case 0xb8: case 0xb9: case 0xba: case 0xbb: case 0xbc: case 0xbd: case 0xbe: case 0xbf:
//                    data = readRom(pos++);
//                    r = op & 0x7;
//                    emit(curPos, "MOV", "R" + r, "#" + data);
//                    break;
//
//                case 0xc5:
//                    emit(curPos, "SEL", "RB0");
//                    break;
//
//                case 0xc6:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JZ", String.format("%04x", addr));
//                    break;
//
//                case 0xc7:
//                    emit(curPos, "MOV", "A", "PSW");
//                    break;
//
//                case 0xc8: case 0xc9: case 0xca: case 0xcb: case 0xcc: case 0xcd: case 0xce: case 0xcf:
//                    r = op & 0x7;
//                    emit(curPos, "DEC", "R" + r);
//                    break;
//
//                case 0xd0: case 0xd1:
//                    r = op & 0x1;
//                    emit(curPos, "XRL", "A", "@R" + r);
//                    break;
//
//                case 0xd3:
//                    data = readRom(pos++);
//                    emit(curPos, "XRL", "A", "#" + data);
//                    break;
//
//                case 0xd5:
//                    emit(curPos, "SEL", "RB1");
//                    break;
//
//                case 0xd7:
//                    emit(curPos, "MOV", "PSW", "A");
//                    break;
//
//                case 0xd8: case 0xd9: case 0xda: case 0xdb: case 0xdc: case 0xdd: case 0xde: case 0xdf:
//                    r = op & 0x7;
//                    emit(curPos, "XRL", "A", "R" + r);
//                    break;
//
//                case 0xe3:
//                    emit(curPos, "MOVP3", "A", "@A");
//                    break;
//
//                case 0xe5:
//                    emit(curPos, "SEL", "MB0");
//                    break;
//
//                case 0xe6:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JNC", String.format("%04x", addr));
//                    break;
//
//                case 0xe7:
//                    emit(curPos, "RL", "A");
//                    break;
//
//                case 0xe8: case 0xe9: case 0xea: case 0xeb: case 0xec: case 0xed: case 0xee: case 0xef:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    r = op & 0x7;
//                    emit(curPos, "DJNZ", "R" + r, String.format("%04x", addr));
//                    break;
//
//                case 0xf0: case 0xf1:
//                    r = op & 0x1;
//                    emit(curPos, "MOV", "A", "@R" + r);
//                    break;
//
//                case 0xf5:
//                    emit(curPos, "SEL", "MB1");
//                    break;
//
//                case 0xf6:
//                    addr = readRom(pos++);
//                    addr = (pos & 0xf00) | (addr & 0xff);
//                    emit(curPos, "JC", String.format("%04x", addr));
//                    break;
//
//                case 0xf7:
//                    emit(curPos, "RLC", "A");
//                    break;
//
//                case 0xf8: case 0xf9: case 0xfa: case 0xfb: case 0xfc: case 0xfd: case 0xfe: case 0xff:
//                    r = op & 0x7;
//                    emit(curPos, "MOV", "A", "R" + r);
//                    break;
//
//                case 0x01:
//                case 0x06:
//                case 0x0b:
//                case 0x22:
//                case 0x33:
//                case 0x38:
//                case 0x3b:
//                case 0x63:
//                case 0x66:
//                case 0x73:
//                case 0x82:
//                case 0x87:
//                case 0x8b:
//                case 0xa2:
//                case 0xa6:
//                case 0xb7:
//                case 0xc0:
//                case 0xc1:
//                case 0xc2:
//                case 0xc3:
//                case 0xd6:
//                case 0xe0:
//                case 0xe1:
//                case 0xe2:
//                case 0xf3:
//                    emit(curPos, ".DB", "" + op);
//                    break;
            }
        }
    }

    //
    // Memory related stuff
    //

    public byte readRom(int addr) {
        return rom[addr];
    }

    public byte readRam(int addr) {
        return ram[addr];
    }

    public void writeRam(int addr, byte val) {
        ram[addr] = val;
    }

    public void loadMemory(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int read;
        while ( (read = is.read(buf)) > 0) {
            os.write(buf, 0, read);
        }
        rom = os.toByteArray();
    }

    //
    // Utilities
    //
    public void dissassemble(int from, int to) {
        int pos = from;
        while (pos < to) {
            int curPos = pos;
            int op = readRom(pos++) & 0xff;
            int r, p, b, addr;
            int data;

            switch(op) {
                case 0x00:
                    emit(curPos, 1, "NOP");
                    break;

                case 0x02:
                    emit(curPos, 1, "OUTL", "BUS", "A");
                    break;

                case 0x03:
                    data = readRom(pos++);
                    emit(curPos, 2, "ADD", "A", "#" + data);
                    break;

                case 0x04: case 0x24: case 0x44: case 0x64: case 0x84: case 0xa4: case 0xc4: case 0xe4:
                    addr = (op & 0xe0) << 3 | (readRom(pos++) & 0xff);
                    emit(curPos, 2, "JMP", String.format("%04x", addr));
                    break;

                case 0x05:
                    emit(curPos, 1, "EN", "I");
                    break;

                case 0x07:
                    emit(curPos, 1, "DEC", "A");
                    break;

                case 0x08:
                    emit(curPos, 1, "INS", "A", "BUS");
                    break;

                case 0x09: case 0x0a:
                    p = op & 0x1;
                    emit(curPos, 1, "IN", "A", "P" + p);
                    break;

                case 0x0c: case 0x0d: case 0x0e: case 0x0f:
                    p = op & 0x3;
                    emit(curPos, 1, "MOVD", "A", "P" + (4 + p));
                    break;

                case 0x10: case 0x11:
                    r = op & 0x1;
                    emit(curPos, 1, "INC", "@R" + r);
                    break;

                case 0x12: case 0x32: case 0x52: case 0x72: case 0x92: case 0xb2: case 0xd2: case 0xf2:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    b = op & 0x7;
                    emit(curPos, 2, "JB" + b, String.format("%04x", addr));
                    break;

                case 0x13:
                    data = readRom(pos++) & 0xff;
                    emit(curPos, 2, "ADDC", "A", "#" + data);
                    break;

                case 0x14: case 0x34: case 0x54: case 0x74: case 0x94: case 0xb4: case 0xd4: case 0xf4:
                    addr = (op & 0xe0) << 3 | (readRom(pos++) & 0xff);
                    emit(curPos, 1, "CALL", String.format("%04x", addr));
                    break;

                case 0x15:
                    emit(curPos, 1, "DIS", "I");
                    break;

                case 0x16:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JTF", String.format("%04x", addr));
                    break;

                case 0x17:
                    emit(curPos, 1, "INC", "A");
                    break;

                case 0x18: case 0x19: case 0x1a: case 0x1b: case 0x1c: case 0x1d: case 0x1e: case 0x1f:
                    r = op & 0x7;
                    emit(curPos, 1, "INC", "R" + r);
                    break;

                case 0x20: case 0x21:
                    r = op & 0x1;
                    emit(curPos, 1, "XCH", "A", "@R" + r);
                    break;

                case 0x23:
                    data = readRom(pos++) & 0xff;
                    emit(curPos, 2, "MOV", "A", "#" + data);
                    break;

                case 0x25:
                    emit(curPos, 1, "EN", "TCNTI");
                    break;

                case 0x26:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNT0", String.format("%04x", addr));
                    break;

                case 0x27:
                    emit(curPos, 1, "CLR", "A");
                    break;

                case 0x28: case 0x29: case 0x2a: case 0x2b: case 0x2c: case 0x2d: case 0x2e: case 0x2f:
                    r = op & 0x7;
                    emit(curPos, 1, "XCH", "A", "R" + r);
                    break;

                case 0x30: case 0x31:
                    r = op & 0x1;
                    emit(curPos, 1, "XCHD", "A", "@R" + r);
                    break;

                case 0x35:
                    emit(curPos, 1, "DIS", "TCNTI");
                    break;

                case 0x36:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JT0", String.format("%04x", addr));
                    break;

                case 0x37:
                    emit(curPos, 1, "CPL", "A");
                    break;

                case 0x39: case 0x3a:
                    p = op & 0x1;
                    emit(curPos, 1, "OUTL", "P" + p, "A");

                case 0x3c: case 0x3d: case 0x3e: case 0x3f:
                    p = op & 0x3;
                    emit(curPos, 1, "MOVD", "P" + (4 + p), "A");
                    break;

                case 0x40: case 0x41:
                    r = op & 0x1;
                    emit(curPos, 1, "ORL", "A", "@R" + r);
                    break;

                case 0x42:
                    emit(curPos, 1, "MOV", "A", "T");
                    break;

                case 0x43:
                    data = readRom(pos++) & 0xff;
                    emit(curPos, 2, "ORL", "A", "#" + data);
                    break;

                case 0x45:
                    emit(curPos, 1, "STRT", "TCNT");
                    break;

                case 0x46:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNT1", String.format("%04x", addr));
                    break;

                case 0x47:
                    emit(curPos, 1, "SWAP", "A");
                    break;

                case 0x48: case 0x49: case 0x4a: case 0x4b: case 0x4c: case 0x4d: case 0x4e: case 0x4f:
                    r = op & 0x7;
                    emit(curPos, 1, "ORL", "A", "R" + r);
                    break;

                case 0x50: case 0x51:
                    r = op & 0x1;
                    emit(curPos, 1, "ANL", "A", "@R" + r);
                    break;

                case 0x53:
                    data = readRom(pos++);
                    emit(curPos, 2, "ANL", "A", "#" + data);
                    break;

                case 0x55:
                    emit(curPos, 1, "STRT", "T");
                    break;

                case 0x56:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JT1", String.format("%04x", addr));
                    break;

                case 0x57:
                    emit(curPos, 1, "DA", "A");
                    break;

                case 0x58: case 0x59: case 0x5a: case 0x5b: case 0x5c: case 0x5d: case 0x5e: case 0x5f:
                    r = op & 0x7;
                    emit(curPos, 1, "ANL", "A", "R" + r);
                    break;

                case 0x60: case 0x61:
                    r = op & 0x1;
                    emit(curPos, 1, "ADD", "A", "@R" + r);
                    break;

                case 0x62:
                    emit(curPos, 1, "MOV", "T", "A");
                    break;

                case 0x65:
                    emit(curPos, 1, "STOP", "TCNT");
                    break;

                case 0x67:
                    emit(curPos, 1, "RRC", "A");
                    break;

                case 0x68: case 0x69: case 0x6a: case 0x6b: case 0x6c: case 0x6d: case 0x6e: case 0x6f:
                    r = op & 0x7;
                    emit(curPos, 1, "ADD", "A", "R" + r);
                    break;

                case 0x70: case 0x71:
                    r = op & 0x1;
                    emit(curPos, 1, "ADDC", "A", "@R" + r);
                    break;

                case 0x75:
                    emit(curPos, 1, "ENT0", "CLK");
                    break;

                case 0x76:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JF1", String.format("%04x", addr));
                    break;

                case 0x77:
                    emit(curPos, 1, "RR", "A");
                    break;

                case 0x78: case 0x79: case 0x7a: case 0x7b: case 0x7c: case 0x7d: case 0x7e: case 0x7f:
                    r = op & 0x7;
                    emit(curPos, 1, "ADDC", "A", "R" + r);
                    break;

                case 0x80: case 0x81:
                    r = op & 0x2;
                    emit(curPos, 1, "MOVX", "A", "@R" + r);
                    break;

                case 0x83:
                    emit(curPos, 1, "RET");
                    break;

                case 0x85:
                    emit(curPos, 1, "CLR", "F0");
                    break;

                case 0x86:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNI", String.format("%04x", addr));
                    break;

                case 0x88:
                    data = readRom(pos++);
                    emit(curPos, 2, "ORL", "BUS", "#" + data);
                    break;

                case 0x89: case 0x8a:
                    data = readRom(pos++);
                    p = op & 0x1;
                    emit(curPos, 2, "ORL", "P" + p, "#" + data);
                    break;

                case 0x8c: case 0x8d: case 0x8e: case 0x8f:
                    p = op & 0x3;
                    emit(curPos, 1, "ORLD", "P" + (4 + p), "A");
                    break;

                case 0x90: case 0x91:
                    r = op & 0x2;
                    emit(curPos, 1, "MOVX", "@R" + r, "A");
                    break;

                case 0x93:
                    emit(curPos, 1, "RETR");
                    break;

                case 0x95:
                    emit(curPos, 1, "CPL", "F0");
                    break;

                case 0x96:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNZ", String.format("%04x", addr));
                    break;

                case 0x97:
                    emit(curPos, 1, "CLR", "C");
                    break;

                case 0x98:
                    data = readRom(pos++);
                    emit(curPos, 2, "ANL", "BUS", "#" + data);
                    break;

                case 0x99: case 0x9a:
                    data = readRom(pos++);
                    p = op & 0x1;
                    emit(curPos, 2, "ANL", "P" + p, "#" + data);
                    break;

                case 0x9b:
                    emit(curPos, 1, "ILLEGAL");
                    break;

                case 0x9c: case 0x9d: case 0x9e: case 0x9f:
                    p = op & 0x3;
                    emit(curPos, 1, "ANLD", "P" + (4 + p), "A");
                    break;

                case 0xa0: case 0xa1:
                    r = op & 0x1;
                    emit(curPos, 1, "MOV", "@R" + r, "A");
                    break;

                case 0xa3:
                    emit(curPos, 1, "MOVP", "A", "@A");
                    break;

                case 0xa5:
                    emit(curPos, 1, "CLR", "F1");
                    break;

                case 0xa7:
                    emit(curPos, 1, "CPL", "C");
                    break;

                case 0xa8: case 0xa9: case 0xaa: case 0xab: case 0xac: case 0xad: case 0xae: case 0xaf:
                    r = op & 0x7;
                    emit(curPos, 1, "MOV", "R" + r, "A");
                    break;

                case 0xb0: case 0xb1:
                    data = readRom(pos++);
                    r = op & 0x1;
                    emit(curPos, 2, "MOV", "@R" + r, "#" + data);
                    break;

                case 0xb3:
                    emit(curPos, 1, "JMPP", "@A");
                    break;

                case 0xb5:
                    emit(curPos, 1, "CPL", "F1");
                    break;

                case 0xb6:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JF0", String.format("%04x", addr));
                    break;

                case 0xb8: case 0xb9: case 0xba: case 0xbb: case 0xbc: case 0xbd: case 0xbe: case 0xbf:
                    data = readRom(pos++);
                    r = op & 0x7;
                    emit(curPos, 2, "MOV", "R" + r, "#" + data);
                    break;

                case 0xc5:
                    emit(curPos, 1, "SEL", "RB0");
                    break;

                case 0xc6:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JZ", String.format("%04x", addr));
                    break;

                case 0xc7:
                    emit(curPos, 1, "MOV", "A", "PSW");
                    break;

                case 0xc8: case 0xc9: case 0xca: case 0xcb: case 0xcc: case 0xcd: case 0xce: case 0xcf:
                    r = op & 0x7;
                    emit(curPos, 1, "DEC", "R" + r);
                    break;

                case 0xd0: case 0xd1:
                    r = op & 0x1;
                    emit(curPos, 1, "XRL", "A", "@R" + r);
                    break;

                case 0xd3:
                    data = readRom(pos++);
                    emit(curPos, 2, "XRL", "A", "#" + data);
                    break;

                case 0xd5:
                    emit(curPos, 1, "SEL", "RB1");
                    break;

                case 0xd7:
                    emit(curPos, 1, "MOV", "PSW", "A");
                    break;

                case 0xd8: case 0xd9: case 0xda: case 0xdb: case 0xdc: case 0xdd: case 0xde: case 0xdf:
                    r = op & 0x7;
                    emit(curPos, 1, "XRL", "A", "R" + r);
                    break;

                case 0xe3:
                    emit(curPos, 1, "MOVP3", "A", "@A");
                    break;

                case 0xe5:
                    emit(curPos, 1, "SEL", "MB0");
                    break;

                case 0xe6:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNC", String.format("%04x", addr));
                    break;

                case 0xe7:
                    emit(curPos, 1, "RL", "A");
                    break;

                case 0xe8: case 0xe9: case 0xea: case 0xeb: case 0xec: case 0xed: case 0xee: case 0xef:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    r = op & 0x7;
                    emit(curPos, 2, "DJNZ", "R" + r, String.format("%04x", addr));
                    break;

                case 0xf0: case 0xf1:
                    r = op & 0x1;
                    emit(curPos, 1, "MOV", "A", "@R" + r);
                    break;

                case 0xf5:
                    emit(curPos, 1, "SEL", "MB1");
                    break;

                case 0xf6:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JC", String.format("%04x", addr));
                    break;

                case 0xf7:
                    emit(curPos, 1, "RLC", "A");
                    break;

                case 0xf8: case 0xf9: case 0xfa: case 0xfb: case 0xfc: case 0xfd: case 0xfe: case 0xff:
                    r = op & 0x7;
                    emit(curPos, 1, "MOV", "A", "R" + r);
                    break;

                case 0x01:
                case 0x06:
                case 0x0b:
                case 0x22:
                case 0x33:
                case 0x38:
                case 0x3b:
                case 0x63:
                case 0x66:
                case 0x73:
                case 0x82:
                case 0x87:
                case 0x8b:
                case 0xa2:
                case 0xa6:
                case 0xb7:
                case 0xc0:
                case 0xc1:
                case 0xc2:
                case 0xc3:
                case 0xd6:
                case 0xe0:
                case 0xe1:
                case 0xe2:
                case 0xf3:
                    emit(curPos, 1, ".DB", "" + op);
                    break;
            }
        }
    }

    private void emit(int curPos, int bytes, String op, String ... args) {
        String hexDump = "";
        for (int i = 0; i < 2; i++) {
            if (hexDump.length() > 0) hexDump += " ";
            hexDump += i < bytes ? String.format("%02x", readRom(curPos + i)) : "  ";
        }
        System.out.println(String.format("%04x: %s %-4s %s", curPos, hexDump, op, join(args)));
    }

    private String join(String ... args) {
        StringBuffer buf = new StringBuffer();
        boolean first = true;
        for (String arg : args) {
            if (!first) {
                buf.append(", ");
            }
            buf.append(arg);
            first = false;
        }
        return buf.toString();
    }


    public static void main(String ... args) {
        try {
            Cpu cpu = new Cpu(128);
            cpu.loadMemory(new FileInputStream("CP1.bin"));
            cpu.dissassemble(0, 0x800);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
