;ROM Listing
;===========

;This contains the fully commented ROM listing of the EPROM contents from the
;Intel 8049 used in the Kosmos CP1 experimental computer.

;Code (C) 1983, Franckh'sche Verlagshandlung, W. Keller & Co., Stuttgart, Germany
;Comments (C) 2017, Andreas Signer <asigner@gmail.com>

;RAM Map
;-------
;0x00 - 0x07: Register Bank 0 (R0 - R7)
;0x08 - 0x17: Stack (8 levels)
;0x18 - 0x1f: Register Bank 1 (R0 - R7)

;0x20 - 0x25: 6 Digit "Video RAM"

;Listing
;-------

;```

;JMPs
;      1  JMP  _0029
;      1  JMP  _0076
;     14  JMP  _0086
;      2  JMP  _0098
;      2  JMP  _00c9
;      1  JMP  _00d9
;      2  JMP  _0104
;      1  JMP  _010c
;      3  JMP  _0146
;      1  JMP  _018a
;      1  JMP  _01bf
;      1  JMP  _01c4
;      2  JMP  _01f1
;      1  JMP  _01ff
;      1  JMP  _021a
;      1  JMP  _0221
;      1  JMP  _023a
;      1  JMP  _025c
;      4  JMP  _02a6
;      1  JMP  _02b0
;      1  JMP  _02b4
;      1  JMP  _02bc
;      1  JMP  _02dc
;      1  JMP  _02eb
;      1  JMP  _0316
;      1  JMP  _0340
;      1  JMP  _0349
;      1  JMP  _0352
;      1  JMP  _0379
;      1  JMP  _03af
;      1  JMP  _03d7
;      1  JMP  _03db
;      1  JMP  _042f
;      1  JMP  _044f
;      1  JMP  _045f
;      2  JMP  _048f
;      2  JMP  _04bd
;      1  JMP  _04f3
;      1  JMP  _0505
;      1  JMP  _050b
;      1  JMP  _050f
;      1  JMP  _052b
;      3  JMP  _0537
;      1  JMP  _053e
;      1  JMP  _0552
;      1  JMP  _056a
;      1  JMP  _0574
;      1  JMP  _058d
;      1  JMP  _0597
;      1  JMP  _05a4
;      2  JMP  _05ac
;      1  JMP  _05b3
;      1  JMP  _05c9
;      1  JMP  _05d3
;      1  JMP  _05ef
;      1  JMP  _05f9
;      1  JMP  _0603
;      1  JMP  _061d
;      3  JMP  _0622
;     20  JMP  _062f
;      1  JMP  _0639
;      1  JMP  _0659
;      2  JMP  _065d
;      1  JMP  _0673
;      1  JMP  _067c
;      1  JMP  _0690
;      1  JMP  _06c1
;      7  JMP  _06fd
;      1  JMP  _070e
;      2  JMP  _0764
;      1  JMP  _0784
;      1  JMP  _0787
;      1  JMP  _079c
;      1  JMP  _07d3
;      2  JMPP @A

_0000:            JMP  _0029   ; Reset entry point: Execution starts at 0.
_0002:            NOP
_0003:            RETR        ; Interrupt entry point
_0004:            NOP
_0005:            NOP
_0006:            NOP
_0007:            JMP  _025c   ; Timer/Counter entry point
_0009:            NOP
_000a:            JNI  _00d1
_000c:            XRL  A, @R1
_000d:            XRL  A, @R1
_000e:            XRL  A, @R1
_000f:            XRL  A, @R1
_0010:            XRL  A, @R1
_0011:            XRL  A, @R1
_0012:            XRL  A, @R1
_0013:            XRL  A, @R1
_0014:            XRL  A, @R1
_0015:            XCH  A, @R1
_0016:            DJNZ R3, _0023
_0018:            EN   TCNTI
_0019:            JNI  _001f
_001b:            CLR  A
_001c:            .DB  $c3
_001d:            ANL  BUS, #$bf
_001f:            JMP  _0603
_0021:            JMP  _01ff
_0023:            JMP  _070e
_0025:            JMP  _0622
_0027:            JMP  _01c4
;```

;#$#$#$ Initialization code
;```
_0029:            MOV  R0, #$1e
_002b:            MOV  @R0, #$00  ; Set R6' to 0
_002d:            MOV  R0, #$20   ; Clear 0x28 ...
_002f:            MOV  R3, #$28   ;
_0031:            MOV  @R0, #$00  ;
_0033:            INC  R0        ;
_0034:            DJNZ R3, _0031  ; ... bytes of RAM
_0036:            MOV  R6, #$00
_0038:            MOV  R7, #$00
_003a:            MOV  R0, #$1a
_003c:            MOV  @R0, #$01  ; Set R1' to 1
_003e:            CALL _06b4
_0040:            ANL  P2, #$cf
_0042:            ANL  P2, #$bf
_0044:            ANL  P2, #$ef
_0046:            ORL  P2, #$20
_0048:            MOV  R0, #$00
_004a:            MOV  A, #$0f
_004c:            MOVX @R0, A
_004d:            MOV  R0, #$02
_004f:            MOV  A, #$ff
_0051:            MOVX @R0, A
_0052:            ANL  P2, #$df
_0054:            ORL  P2, #$10
_0056:            MOV  R0, #$00
_0058:            MOV  A, #$0d
_005a:            MOVX @R0, A
_005b:            MOV  R0, #$01
_005d:            MOV  A, #$ff
_005f:            MOVX @R0, A
_0060:            MOV  R0, #$03
_0062:            MOVX @R0, A
_0063:            ANL  P2, #$5f
_0065:            ORL  P2, #$10
_0067:            MOV  R0, #$13
_0069:            MOV  A, #$5a
_006b:            MOVX @R0, A
_006c:            CLR  A
_006d:            MOVX A, @R0
_006e:            XRL  A, #$5a
_0070:            MOV  R0, #$3b
_0072:            JZ   _0094
_0074:            MOV  @R0, #$7f
_0076:            MOV  R3, #$04
_0078:            MOV  R0, #$3f
_007a:            MOV  A, #$ff
_007c:            MOV  @R0, A
_007d:            INC  R0
_007e:            DJNZ R3, _007c
_0080:            ANL  P2, #$4f
_0082:            CALL _0328
_0084:            EN   TCNTI
_0085:            STRT T
_0086:            MOV  R0, #$1e   ; Wait for key press: 0x1e is R6', used in interrupt
_0088:            MOV  A, @R0    ; check for key
_0089:            JZ   _0086      ; no key pressed, continue waiting
_008b:            MOV  R1, #$44   ;
_008d:            MOV  @R1, A    ; Store pressed key in 0x44
_008e:            MOV  A, #$0a
_0090:            ADD  A, @R0
_0091:            MOV  @R0, #$00
_0093:            JMPP @A
_0094:            MOV  @R0, #$ff
_0096:            JMP  _0076
_0098:            MOV  A, R6
_0099:            JZ   _00bb
_009b:            XRL  A, #$03
_009d:            JNZ  _00c9
_009f:            MOV  R0, #$27
_00a1:            CALL _0338
_00a3:            MOV  R0, #$3a
_00a5:            MOV  A, @R0
_00a6:            JB2  _00b9
_00a8:            MOV  R0, #$20  ; Set left-most digit...
_00aa:            MOV  @R0, #$73 ; ... to 'P'
_00ac:            MOV  R0, #$27
_00ae:            MOV  R2, #$02
_00b0:            CALL _014e
_00b2:            MOV  R0, #$38
_00b4:            MOV  @R0, A
_00b5:            MOV  R6, #$00
_00b7:            JMP  _0086
_00b9:            JMP  _0146
_00bb:            CALL _06b4
_00bd:            JMP  _0086
_00bf:            CALL _06ea
_00c1:            JMP  _0086
_00c3:            CALL _0179    ; Clear "Video RAM"
_00c5:            MOV  R6, #$00
_00c7:            JMP  _0086
_00c9:            MOV  R4, #$01
_00cb:            JMP  _06fd
_00cd:            CALL _0179    ; Clear "Video RAM"
_00cf:            JMP  _00d9
_00d1:            MOV  A, R6
_00d2:            JZ   _00cd
_00d4:            CLR  C
_00d5:            ADD  A, #$fb
_00d7:            JC   _00c9
_00d9:            MOV  A, R6
_00da:            ADD  A, #$27
_00dc:            MOV  R0, A
_00dd:            MOV  R1, #$44
_00df:            MOV  A, @R1
_00e0:            DEC  A
_00e1:            MOV  @R0, A
_00e2:            MOV  R2, A
_00e3:            CALL _01aa
_00e5:            JMP  _0086
_00e7:            JMP  _0146
_00e9:            JMP  _010c
_00eb:            MOV  A, R6
_00ec:            XRL  A, #$03
_00ee:            JNZ  _00e9
_00f0:            MOV  A, #$f7
_00f2:            CALL _0333
_00f4:            MOV  R0, #$27
_00f6:            CALL _0338
_00f8:            MOV  R0, #$3a
_00fa:            MOV  A, @R0
_00fb:            JB2  _00e7
_00fd:            MOV  R0, #$27
_00ff:            MOV  R2, #$02
_0101:            CALL _014e
_0103:            MOV  R7, A
_0104:            MOV  R6, #$00
_0106:            MOV  R0, #$20  ; Set left-most digit...
_0108:            MOV  @R0, #$79 ; ... to 'E'
_010a:            JMP  _0086
_010c:            MOV  A, R6
_010d:            XRL  A, #$05
_010f:            JNZ  _0144
_0111:            MOV  R0, #$3a
_0113:            MOV  A, @R0
_0114:            JB3  _014a
_0116:            MOV  R0, #$27
_0118:            MOV  R2, #$01
_011a:            CALL _014e
_011c:            CLR  C
_011d:            MOV  R1, A
_011e:            ADD  A, #$e7
_0120:            JC   _014a
_0122:            MOV  R0, #$29
_0124:            MOV  R2, #$02
_0126:            CALL _014e
_0128:            JF0  _014a
_012a:            MOV  R4, A
_012b:            MOV  A, R1
_012c:            MOV  R2, A
_012d:            MOV  A, R7
_012e:            CALL _030a
_0130:            MOV  A, R2
_0131:            MOVX @R1, A
_0132:            INC  R1
_0133:            MOV  A, R4
_0134:            MOVX @R1, A
_0135:            MOV  R0, #$3b
_0137:            MOV  A, R7
_0138:            XRL  A, @R0
_0139:            JZ   _013e
_013b:            INC  R7
_013c:            JMP  _0104
_013e:            MOV  A, #$08
_0140:            CALL _032e
_0142:            JMP  _0104
_0144:            JMP  _00c9
_0146:            MOV  A, #$fb
_0148:            CALL _0333
_014a:            MOV  R4, #$04
_014c:            JMP  _06fd


;?????????
;Input
;- R0: Source Address
;- R2: Iterations???
;Output:
;- F0:
;- A:
;=========
_014e:            MOV  A, R1     ; save R1 in...
_014f:            MOV  R3, A     ; ... R3
_0150:            MOV  R1, #$3c   ; Destination Adresse: 0x3c == 60
_0152:            MOV  R4, #$03   ; Size: 3
_0154:            MOV  A, @R0    ; Copy from (R0)
_0155:            MOV  @R1, A    ; to (R1)
_0156:            INC  R0        ;
_0157:            INC  R1        ;
_0158:            DJNZ R4, _0154  ; Continue while not 3 bytes copied
_015a:            MOV  A, R3     ;
_015b:            MOV  R1, A     ; Restore R1
_015c:            MOV  R0, #$3c   ; Load address 0x3c == 60
_015e:            MOV  A, @R0    ; Load content of 0x3c
_015f:            CLR  C         ; Clear carry...
_0160:            CLR  F0        ; ... and F0
_0161:            RLC  A         ; check bit 7
_0162:            JC   _0176      ; jump if set
_0164:            RLC  A         ; check bit 6
_0165:            JC   _0176      ; jump if set
_0167:            ADD  A, @R0    ; add contents of 0x3c again ?????
_0168:            JC   _0176      ; jump if carry ist set?
_016a:            RLC  A         ; check bit 7
_016b:            JC   _0176      ; jump if set
_016d:            INC  R0        ;
_016e:            ADD  A, @R0    ; add next byte from address 0x3d == 61
_016f:            JC   _0176      ; jump if carry
_0171:            MOV  @R0, A    ; store result in 0x3d
_0172:            DJNZ R2, _0161  ; continue loop
_0174:            MOV  A, @R0    ; No-op? A contains already what was written to @R0?
_0175:            RET            ; Return
_0176:            CPL  F0        ; Set F0
_0177:            CLR  C         ; Clear carry
_0178:            RET            ; Return


;Clear "Video RAM"
;=================
_0179:            MOV  R2, #$06
_017b:            MOV  R0, #$20   ; 0x20 == 32: Begin of "Video RAM"
_017d:            MOV  @R0, #$00
_017f:            INC  R0
_0180:            DJNZ R2, _017d
_0182:            RET


;?????????
;=========
;- Input:
  ;- R0: ???
  ;- R2: ???
;```
_0183:            MOV  A, @R0
_0184:            CALL _02d9
_0186:            JF0  _0193
_0188:            CALL _07eb
_018a:            MOV  A, R4
_018b:            CALL _07eb
_018d:            MOV  A, R2
_018e:            CALL _07eb
_0190:            MOV  R6, #$00
_0192:            RET
_0193:            CLR  F0
_0194:            JMP  _018a
;```

;?????????
;=========
_0196:            MOVX A, @R1
_0197:            MOV  R0, #$39
_0199:            MOV  @R0, A
_019a:            MOV  R6, #$00
_019c:            CLR  F0
_019d:            CPL  F0
_019e:            CALL _0183
_01a0:            INC  R1
_01a1:            MOVX A, @R1
_01a2:            MOV  R0, #$39
_01a4:            MOV  @R0, A
_01a5:            MOV  R6, #$02
_01a7:            CALL _0183
_01a9:            RET

;?????????
;=========
_01aa:            MOV  R0, #$21
_01ac:            MOV  R1, #$22
_01ae:            MOV  R3, #$04
_01b0:            MOV  A, @R1
_01b1:            MOV  @R0, A
_01b2:            INC  R0
_01b3:            INC  R1
_01b4:            DJNZ R3, _01b0
_01b6:            MOV  A, #$00
_01b8:            ADD  A, R2
_01b9:            MOVP3 A, @A
_01ba:            MOV  R0, #$25
_01bc:            MOV  @R0, A
_01bd:            INC  R6
_01be:            RET


;NON-TARGET ?????????
;====================
_01bf:            CLR  F0
_01c0:            CALL _03f3
_01c2:            JMP  _01f1
_01c4:            CALL _0179    ; Clear "Video RAM"
_01c6:            CALL _03f3
_01c8:            MOV  R0, #$20
_01ca:            MOV  @R0, #$23
_01cc:            ORL  P1, #$c0
_01ce:            MOV  R0, #$3a
_01d0:            MOV  R4, #$3c
_01d2:            MOV  R3, #$fa  ; Delay for...
_01d4:            CALL _03b6     ; ... 250 millis
_01d6:            MOV  A, @R0
_01d7:            JB2  _01bf
_01d9:            DJNZ R4, _01d2
_01db:            CLR  A
_01dc:            CALL _030a
_01de:            CALL _03bd
_01e0:            JF0  _01bf
_01e2:            MOV  R0, #$3b
_01e4:            MOV  A, @R0
_01e5:            JB2  _01e9
_01e7:            JMP  _01f1
_01e9:            MOV  A, #$ff

;// FALLTHROUGH!

;?????????
;=========
_01eb:            CALL _030a
_01ed:            CALL _03bd
_01ef:            JF0  _01fd
_01f1:            ANL  P1, #$7f
_01f3:            CALL _0179    ; Clear "Video RAM"
_01f5:            MOV  R0, #$20
_01f7:            MOV  @R0, #$63
_01f9:            MOV  R6, #$00
_01fb:            JMP  _0086
_01fd:            JMP  _01bf
_01ff:            MOV  A, #$f7
_0201:            CALL _0333
_0203:            MOV  A, R6
_0204:            JZ   _0227
_0206:            XRL  A, #$03
_0208:            JNZ  _0240
_020a:            MOV  R0, #$27
_020c:            CALL _0338
_020e:            MOV  R0, #$3a
_0210:            MOV  A, @R0
_0211:            JB2  _023a
_0213:            MOV  R0, #$27
_0215:            MOV  R2, #$02
_0217:            CALL _014e
_0219:            MOV  R7, A
_021a:            CALL _0179    ; Clear "Video RAM"
_021c:            MOV  A, R7
_021d:            CALL _030a
_021f:            CALL _0196
_0221:            MOV  R0, #$20
_0223:            MOV  @R0, #$39
_0225:            JMP  _0086
_0227:            CLR  C
_0228:            MOV  A, R7
_0229:            ADD  A, #$01
_022b:            MOV  R7, A
_022c:            JC   _0238
_022e:            MOV  R0, #$3b
_0230:            MOV  A, @R0
_0231:            JB2  _021a
_0233:            MOV  A, R7
_0234:            JB2  _023c
_0236:            JMP  _021a
_0238:            MOV  R7, #$ff
_023a:            JMP  _0146
_023c:            MOV  R7, #$7f
_023e:            JMP  _023a
_0240:            MOV  A, R6
_0241:            XRL  A, #$01
_0243:            JZ   _0247
_0245:            JMP  _00c9
_0247:            MOV  R0, #$27
_0249:            MOV  A, @R0
_024a:            XRL  A, #$09
_024c:            JNZ  _0245
_024e:            CALL _0179    ; Clear "Video RAM"
_0250:            MOV  R0, #$20
_0252:            MOV  @R0, #$39
_0254:            MOV  R0, #$07
_0256:            MOV  R6, #$02
_0258:            CALL _0183
_025a:            JMP  _0086
;```

;#$#$#$ Timer/Counter interrupt
;- R5: Mask used for keyboard reading and display addressing
;- R3: Internal scratch register: Loop vars, computations
;```
_025c:            SEL  RB1        ; Switch to memory bank 1
_025d:            XCH  A, R7      ; Exchange A and R7
_025e:            MOV  A, #$e0     ; Reset timer...
_0260:            MOV  T, A       ; ... to 0xe0 (== 224) --> 2560 micros per timer interrupt
_0261:            DJNZ R2, _026b   ;
_0263:            MOV  R1, #$25
_0265:            MOV  R5, #$fe
_0267:            MOV  R2, #$06
_0269:            MOV  R4, #$35
_026b:            ORL  P2, #$a0    ; P2 |= 1010 0000 --> /CE == 1, IO == 1
_026d:            ANL  P2, #$ef    ; P2 &= 1110 1111 --> 8155 /CE == 0
_026f:            CLR  A          ; Clear Accumulator
_0270:            MOV  R0, #$01    ; Address Port A
_0272:            MOVX @R0, A     ; Write 0 -> Port A
_0273:            MOV  A, R5
_0274:            MOV  R0, #$03    ; Address Port C
_0276:            MOVX @R0, A     ; Write row selection -> Port C
_0277:            MOV  A, @R1
_0278:            MOV  R0, #$01
_027a:            MOVX @R0, A     ; (0x25) -> Port A; Write one character to display
_027b:            ORL  P2, #$0f    ; P2 |= 0000 1111: Prepare to read from bit 0 - 4
_027d:            MOVD A, P4      ; ?????write to lower nibble of port 2, then read from it? why not just read from it?
_027e:            CLR  C
_027f:            MOV  R3, #$04    ; Test (keypad) bits read: 4 bits in total
_0281:            RRC  A          ; shift lowest bit to carry
_0282:            JC   _0288       ; bit set -> 288
_0284:            DJNZ R3, _0281   ; not set, carry on with loop.
_0286:            JMP  _02bc       ; TODO: Carry on with what?
_0288:            MOV  A, R2      ;
_0289:            DEC  A
_028a:            RL   A
_028b:            RL   A
_028c:            INC  A          ; A = 4 * (R2 - 1) + 1; [R2 starts with 6]
_028d:            XCH  A, R3      ; R3 = 4 * (R2 - 1) + 1; A = bit_num
_028e:            CPL  A          ; Compute two's complement...
_028f:            INC  A          ; of A --> A == -bit_num
_0290:            ADD  A, R3      ; A = 4 * (R2 - 1) + 1 - bit_num
_0291:            MOV  R6, A      ; Store A in R6
_0292:            JB2  _02bc       ; Bit 2 Set? -> 2bc
_0294:            MOV  A, R4
_0295:            MOV  R0, A
_0296:            MOV  A, R6
_0297:            XRL  A, @R0
_0298:            JZ   _02b8
_029a:            MOV  A, R6
_029b:            MOV  @R0, A
_029c:            MOV  A, R6
_029d:            XRL  A, #$0f
_029f:            JZ   _02c2
_02a1:            MOV  A, R6
_02a2:            XRL  A, #$0e
_02a4:            JZ   _02c8
_02a6:            MOV  A, R5
_02a7:            RL   A
_02a8:            MOV  R5, A
_02a9:            DEC  R1
_02aa:            DEC  R4
_02ab:            MOV  R0, #$3a
_02ad:            MOV  A, @R0
_02ae:            JB2  _02ce
_02b0:            JB2  _02d4
_02b2:            ANL  P2, #$7f
_02b4:            XCH  A, R7
_02b5:            SEL  RB0
_02b6:            EN   TCNTI
_02b7:            RETR
_02b8:            MOV  R6, #$00
_02ba:            JMP  _02a6


_02bc:            MOV  A, R4
_02bd:            MOV  R0, A
_02be:            MOV  @R0, #$00
_02c0:            JMP  _02a6
_02c2:            MOV  A, #$01
_02c4:            CALL _032e
_02c6:            JMP  _02a6
_02c8:            MOV  A, #$02
_02ca:            CALL _032e
_02cc:            JMP  _02a6
_02ce:            ANL  P2, #$df
_02d0:            ORL  P2, #$10
_02d2:            JMP  _02b0
_02d4:            ORL  P2, #$80
_02d6:            JMP  _02b4
_02d8:            RET

_02d9:            CLR  C
_02da:            MOV  R4, #$00
_02dc:            ADD  A, #$f6
_02de:            JNC  _02e4
_02e0:            CLR  C
_02e1:            INC  R4
_02e2:            JMP  _02dc
_02e4:            ADD  A, #$0a
_02e6:            MOV  R2, A
_02e7:            CLR  C
_02e8:            MOV  A, R4
_02e9:            MOV  R4, #$00
_02eb:            ADD  A, #$f6
_02ed:            JNC  _02f3
_02ef:            CLR  C
_02f0:            INC  R4
_02f1:            JMP  _02eb
_02f3:            ADD  A, #$0a
_02f5:            XCH  A, R4
_02f6:            CLR  C
_02f7:            RET


_02f8:            NOP
_02f9:            ORL  BUS, #$c4
_02fb:            NOP
_02fc:            NOP
_02fd:            NOP
_02fe:            NOP
_02ff:            INC  R6
_0300:            MOVD P7, A
_0301:            .DB  $06
_0302:            ANL  A, R3
_0303:            ORL  A, R7
_0304:            .DB  $66
_0305:            ADD  A, R5
_0306:            ADDC A, R5
_0307:            CLR  A
_0308:            ADDC A, R7
_0309:            ADD  A, R7

;// FALLTHROUGH??

;??????????????????
;------------------
_030a:            JB2  _0319
_030c:            MOV  R3, A
_030d:            MOV  A, #$ef
_030f:            CALL _0333
_0311:            ORL  P2, #$20
_0313:            ANL  P2, #$ef
_0315:            MOV  A, R3
_0316:            RL   A
_0317:            MOV  R1, A
_0318:            RET
_0319:            ADD  A, #$80
_031b:            CLR  C
_031c:            MOV  R3, A
_031d:            MOV  A, #$10
_031f:            CALL _032e
_0321:            ORL  P2, #$10
_0323:            ANL  P2, #$df
_0325:            MOV  A, R3
_0326:            JMP  _0316

;?????????????????????????????
;-----------------------------
_0328:            CLR  A
_0329:            MOV  R1, A
_032a:            MOVX @R1, A
_032b:            DJNZ R1, _032a
_032d:            RET

;?????????????????????????????
;-----------------------------
_032e:            MOV  R0, #$3a
_0330:            ORL  A, @R0
_0331:            MOV  @R0, A
_0332:            RET

;?????????????????????????????
;-----------------------------
_0333:            MOV  R0, #$3a
_0335:            ANL  A, @R0
_0336:            MOV  @R0, A
_0337:            RET

;?????????????????????????????
;-----------------------------
_0338:            MOV  R2, #$02
_033a:            CALL _014e
_033c:            JF0  _0348
_033e:            JB2  _0341
_0340:            RET
_0341:            MOV  R0, #$3b
_0343:            MOV  A, @R0
_0344:            JB2  _0340
_0346:            JMP  _0349
_0348:            CLR  F0
_0349:            MOV  A, #$04
_034b:            CALL _032e
_034d:            JMP  _0340



;?????????????????
;=================
_034f:            CLR  F0
_0350:            JB2  _0353
_0352:            RET
_0353:            MOV  R0, #$3b
_0355:            MOV  A, @R0
_0356:            JB2  _0352
_0358:            CPL  F0
_0359:            JMP  _0352

;?????????????????
;-----------------
_035b:            MOVX A, @R1
_035c:            MOV  R3, A
_035d:            CALL _034f
_035f:            JF0  _0364
_0361:            MOV  A, R3
_0362:            CALL _030a
_0364:            RET


;?????????????????
;-----------------
_0365:            MOVX A, @R1
_0366:            MOV  R3, A
_0367:            CALL _034f
_0369:            JF0  _0378
_036b:            MOV  A, R3
_036c:            CALL _030a
_036e:            MOVX A, @R1
_036f:            JNZ  _0378
_0371:            INC  R1
_0372:            MOV  R0, #$36
_0374:            MOV  A, @R0
_0375:            JNZ  _0378
_0377:            INC  R0
_0378:            RET

;?????????????????
;-----------------
_0379:            DJNZ R4, _0382
_037b:            JB2  _037f
_037d:            CLR  A
_037e:            RET
_037f:            MOV  A, #$01
_0381:            RET
_0382:            RR   A
_0383:            JMP  _0379


;?????????????????
;-----------------
_0385:            MOV  A, #$40
_0387:            CALL _032e
_0389:            MOV  A, #$ef
_038b:            CALL _0333
_038d:            ORL  P2, #$a0
_038f:            ANL  P2, #$ef
_0391:            RET

;?????????????????
;-----------------
_0392:            MOV  A, #$50
_0394:            CALL _032e
_0396:            ANL  P2, #$df
_0398:            ORL  P2, #$90
_039a:            RET

;?????????????????
;-----------------
_039b:            MOV  R0, #$36
_039d:            MOV  @R0, #$00
_039f:            INC  R0
_03a0:            MOV  @R0, A
_03a1:            RET

;?????????????????
;-----------------
_03a2:            MOV  R2, A
_03a3:            CLR  C
_03a4:            CPL  C
_03a5:            CLR  A
_03a6:            RLC  A
_03a7:            DJNZ R2, _03a6
_03a9:            MOV  R2, A
_03aa:            MOV  A, @R0
_03ab:            JZ   _03b1
_03ad:            MOV  A, R2
_03ae:            ORL  A, @R1
_03af:            MOV  @R1, A
_03b0:            RET
_03b1:            MOV  A, R2
_03b2:            CPL  A
_03b3:            ANL  A, @R1
_03b4:            JMP  _03af
;```

;#$#$#$ Delay for n millis
;Inputs:
;- R3: number of millis to delay
;```
_03b6:            MOV  R2, #$c8   ; cycles: 2
_03b8:            DJNZ R2, _03b8  ; cycles: + 200 * 2
_03ba:            DJNZ R3, _03b6  ; cycles: R3 * 201 * 2
_03bc:            RET            ; cycles: R3 * 201 * 2 + 1 -->


;?????????????????
;-----------------
_03bd:            MOV  R1, #$00
_03bf:            MOV  R0, #$3a
_03c1:            MOV  A, @R0
_03c2:            JB2  _03eb
_03c4:            CLR  C
_03c5:            MOV  R0, #$08
_03c7:            MOVX A, @R1
_03c8:            RRC  A
_03c9:            JNC  _03dd
_03cb:            ANL  P1, #$7f
_03cd:            MOV  R3, #$1e  ; Delay for...
_03cf:            CALL _03b6     ; ... 30 millis
_03d1:            ORL  P1, #$80
_03d3:            MOV  R3, #$3c  ; Delay for...
_03d5:            CALL _03b6     ; ... 60 millis
_03d7:            DJNZ R0, _03c8
_03d9:            DJNZ R1, _03bf
_03db:            CLR  C
_03dc:            RET
_03dd:            ANL  P1, #$7f
_03df:            MOV  R3, #$3c  ; Delay for...
_03e1:            CALL _03b6     ; ... 60 millis
_03e3:            ORL  P1, #$80
_03e5:            MOV  R3, #$1e  ; Delay for...
_03e7:            CALL _03b6     ; ... 30 millis
_03e9:            JMP  _03d7
_03eb:            CLR  F0
_03ec:            CPL  F0
_03ed:            MOV  A, #$fe
_03ef:            CALL _0333
_03f1:            JMP  _03db


_03f3:            MOV  A, #$fe
_03f5:            CALL _0333
_03f7:            RET


_03f8:            NOP
_03f9:            .DB $ef, $54    ; DJNZ R7, _0354
_03fb:            XCH  A, R0
_03fc:            JB2  _0336
_03fe:            NOP
_03ff:            EN   I
_0400:            INC  R1
_0401:            CALL _01eb
_0403:            ADDC A, R3
_0404:            JNT1 _045f
_0406:            ORL  A, R7
_0407:            .DB  $f3
_0408:            INC  R3
_0409:            ORLD P7, A
_040a:            INC  R5
_040b:            JNZ  _041f
_040d:            XCH  A, @R1
_040e:            .DB  $a6
_040f:            .DB  $c1
_0410:            MOV  A, #$25
_0412:            CLR  A
_0413:            ADD  A, R5
_0414:            CALL _039f
_0416:            XCH  A, R1
_0417:            XCH  A, R3
_0418:            XCH  A, R5
_0419:            JMP  _0659
_041b:            JMP  _050b
_041d:            JMP  _052b
_041f:            JMP  _0552
_0421:            JMP  _053e
_0423:            JMP  _056a
_0425:            JMP  _058d
_0427:            JMP  _05a4
_0429:            JMP  _05c9
_042b:            JMP  _05ef
_042d:            JMP  _05f9
_042f:            MOVX A, @R1
_0430:            ADD  A, #$00
_0432:            INC  R1
_0433:            JMPP @A
_0434:            MOV  A, #$01
_0436:            CALL _0333
_0438:            MOV  R0, #$38
_043a:            MOV  A, @R0
_043b:            MOV  R0, #$3b
_043d:            XRL  A, @R0
_043e:            JZ   _0444
_0440:            CLR  F0
_0441:            CPL  F0
_0442:            JMP  _062f
_0444:            JMP  _0673
_0446:            MOV  R0, #$36
_0448:            MOV  @R0, #$00
_044a:            INC  R0
_044b:            MOVX A, @R1
_044c:            MOV  @R0, A
_044d:            JMP  _062f
_044f:            CALL _035b
_0451:            JF0  _045d
_0453:            MOV  R0, #$36
_0455:            MOV  A, @R0
_0456:            MOVX @R1, A
_0457:            INC  R0
_0458:            INC  R1
_0459:            MOV  A, @R0
_045a:            MOVX @R1, A
_045b:            JMP  _062f
_045d:            JMP  _065d
_045f:            CALL _035b
_0461:            JF0  _045d
_0463:            MOV  R0, #$36
_0465:            MOVX A, @R1
_0466:            MOV  @R0, A
_0467:            INC  R0
_0468:            INC  R1
_0469:            MOVX A, @R1
_046a:            MOV  @R0, A
_046b:            JMP  _062f
_046d:            CALL _035b
_046f:            JF0  _045d
_0471:            INC  R1
_0472:            JMP  _045f
_0474:            CALL _035b
_0476:            JF0  _045d
_0478:            INC  R1
_0479:            JMP  _044f
_047b:            MOVX A, @R1
_047c:            JZ   _048d
_047e:            MOV  R2, A
_047f:            MOV  R3, #$01
_0481:            MOV  R4, #$01
_0483:            MOV  R5, #$c8
_0485:            DJNZ R5, _0485
_0487:            DJNZ R4, _0483
_0489:            DJNZ R3, _0481
_048b:            DJNZ R2, _047f
_048d:            JMP  _062f
_048f:            MOVX A, @R1
_0490:            MOV  R0, #$38
_0492:            MOV  R3, A
_0493:            MOV  @R0, A
_0494:            JMP  _0639
_0496:            MOV  R0, #$3a
_0498:            MOV  A, @R0
_0499:            JB2  _049d
_049b:            JMP  _062f
_049d:            JMP  _048f
_049f:            CALL _035b
_04a1:            JF0  _045d
_04a3:            INC  R1
_04a4:            JMP  _048f
_04a6:            MOV  R0, #$36
_04a8:            MOV  A, @R0
_04a9:            JNZ  _04bd
_04ab:            CLR  C
_04ac:            INC  R0
_04ad:            MOV  A, @R0
_04ae:            ADD  A, #$fe
_04b0:            JC   _04bd
_04b2:            MOV  A, @R0
_04b3:            JZ   _04b9
_04b5:            MOV  @R0, #$00
_04b7:            JMP  _062f
_04b9:            MOV  @R0, #$01
_04bb:            JMP  _062f
_04bd:            MOV  R4, #$05
_04bf:            JMP  _06fd
_04c1:            MOV  R0, #$36
_04c3:            MOV  A, @R0
_04c4:            JNZ  _04bd
_04c6:            CLR  C
_04c7:            INC  R0
_04c8:            MOV  A, @R0
_04c9:            ADD  A, #$fe
_04cb:            JC   _04bd
_04cd:            CALL _035b
_04cf:            JF0  _045d
_04d1:            MOVX A, @R1
_04d2:            JNZ  _04bd
_04d4:            CLR  C
_04d5:            INC  R1
_04d6:            MOVX A, @R1
_04d7:            ADD  A, #$fe
_04d9:            JC   _04bd
_04db:            MOV  R0, #$37
_04dd:            MOV  A, @R0
_04de:            JZ   _04bb
_04e0:            MOVX A, @R1
_04e1:            JZ   _04e7
_04e3:            MOV  @R0, #$01
_04e5:            JMP  _062f
_04e7:            MOV  @R0, #$00
_04e9:            JMP  _062f
_04eb:            CALL _06ea
_04ed:            JMP  _062f
_04ef:            JMP  _050f
_04f1:            JMP  _0505
_04f3:            CALL _0365
_04f5:            JF0  _045d
_04f7:            JNZ  _04bd
_04f9:            JF1  _04ef
_04fb:            CLR  C
_04fc:            MOVX A, @R1
_04fd:            ADD  A, @R0
_04fe:            JC   _05f1
_0500:            MOV  @R0, A
_0501:            JMP  _062f
_0503:            JMP  _065d
_0505:            MOV  R4, #$06
_0507:            JMP  _06fd
_0509:            JMP  _04bd
_050b:            CLR  F1
_050c:            CPL  F1
_050d:            JMP  _04f3
_050f:            CLR  F1
_0510:            MOVX A, @R1
_0511:            MOV  R3, A
_0512:            XRL  A, @R0
_0513:            JZ   _0527
_0515:            MOV  A, R3
_0516:            JZ   _0525
_0518:            CPL  A
_0519:            CLR  C
_051a:            ADD  A, #$01
_051c:            ADD  A, @R0
_051d:            JNC  _0505
_051f:            CLR  C
_0520:            MOV  A, @R0
_0521:            DEC  A
_0522:            DJNZ R3, _0521
_0524:            MOV  @R0, A
_0525:            JMP  _062f
_0527:            MOV  @R0, #$00
_0529:            JMP  _062f
_052b:            CALL _07e1
_052d:            CALL _0365
_052f:            JF0  _0503
_0531:            JNZ  _0509
_0533:            MOVX A, @R1
_0534:            XRL  A, @R0
_0535:            JZ   _053a
_0537:            CLR  C
_0538:            JMP  _062f
_053a:            CALL _07e6
_053c:            JMP  _0537
_053e:            CALL _07e1
_0540:            CALL _0365
_0542:            JF0  _0503
_0544:            JNZ  _0509
_0546:            MOVX A, @R1
_0547:            JZ   _0537
_0549:            CPL  A
_054a:            CLR  C
_054b:            ADD  A, #$01
_054d:            ADD  A, @R0
_054e:            JNC  _053a
_0550:            JMP  _0537
_0552:            CALL _07e1
_0554:            CALL _0365
_0556:            JF0  _0503
_0558:            JNZ  _0509
_055a:            MOV  A, @R0
_055b:            JZ   _0537
_055d:            CPL  A
_055e:            CLR  C
_055f:            ADD  A, #$01
_0561:            MOV  R3, A
_0562:            MOVX A, @R1
_0563:            ADD  A, R3
_0564:            JNC  _053a
_0566:            JMP  _0537
_0568:            JMP  _0574
_056a:            MOVX A, @R1
_056b:            JNZ  _0568
_056d:            ORL  P1, #$ff
_056f:            IN   A, P1
_0570:            CALL _039b
_0572:            JMP  _062f
_0574:            MOV  R4, A
_0575:            CLR  C
_0576:            ADD  A, #$f7
_0578:            JC   _0509
_057a:            MOV  A, R4
_057b:            MOV  R2, A
_057c:            CLR  C
_057d:            CPL  C
_057e:            CLR  A
_057f:            RLC  A
_0580:            DJNZ R2, _057f
_0582:            MOV  R1, #$3f
_0584:            ORL  A, @R1
_0585:            OUTL P1, A
_0586:            IN   A, P1
_0587:            CALL _0379
_0589:            CALL _039b
_058b:            JMP  _062f
_058d:            MOV  R0, #$37
_058f:            MOVX A, @R1
_0590:            MOV  R2, A
_0591:            MOV  R1, #$3f
_0593:            JNZ  _059a
_0595:            MOV  A, @R0
_0596:            MOV  @R1, A
_0597:            OUTL P1, A
_0598:            JMP  _062f
_059a:            CLR  C
_059b:            ADD  A, #$f7
_059d:            JC   _0509
_059f:            MOV  A, R2
_05a0:            CALL _03a2
_05a2:            JMP  _0597
_05a4:            MOVX A, @R1
_05a5:            MOV  R2, A
_05a6:            CALL _0385
_05a8:            MOV  R1, #$40
_05aa:            MOV  R4, #$02
_05ac:            MOV  R0, #$37
_05ae:            MOV  A, R2
_05af:            JNZ  _05bf
_05b1:            MOV  A, @R0
_05b2:            MOV  @R1, A
_05b3:            XCH  A, R4
_05b4:            MOV  R0, A
_05b5:            XCH  A, R4
_05b6:            MOVX @R0, A
_05b7:            MOV  A, #$bf
_05b9:            CALL _0333
_05bb:            ANL  P2, #$7f
_05bd:            JMP  _062f
_05bf:            CLR  C
_05c0:            ADD  A, #$f7
_05c2:            JC   _05e7
_05c4:            MOV  A, R2
_05c5:            CALL _03a2
_05c7:            JMP  _05b3
_05c9:            MOVX A, @R1
_05ca:            MOV  R4, A
_05cb:            CALL _0392
_05cd:            MOV  R0, #$02
_05cf:            MOV  A, R4
_05d0:            JNZ  _05dd
_05d2:            MOVX A, @R0
_05d3:            CALL _039b
_05d5:            MOV  A, #$bf
_05d7:            CALL _0333
_05d9:            ANL  P2, #$7f
_05db:            JMP  _062f
_05dd:            CLR  C
_05de:            ADD  A, #$f7
_05e0:            JC   _05e7
_05e2:            MOVX A, @R0
_05e3:            CALL _0379
_05e5:            JMP  _05d3
_05e7:            MOV  A, #$bf
_05e9:            CALL _0333
_05eb:            ANL  P2, #$7f
_05ed:            JMP  _04bd
_05ef:            MOVX A, @R1
_05f0:            MOV  R2, A
_05f1:            CALL _0392
_05f3:            MOV  R1, #$41
_05f5:            MOV  R4, #$01
_05f7:            JMP  _05ac
_05f9:            MOVX A, @R1
_05fa:            MOV  R2, A
_05fb:            CALL _0392
_05fd:            MOV  R1, #$42
_05ff:            MOV  R4, #$03
_0601:            JMP  _05ac
_0603:            MOV  A, R6
_0604:            XRL  A, #$01
_0606:            JNZ  _0614
_0608:            MOV  R0, #$27
_060a:            MOV  A, @R0
_060b:            XRL  A, #$09
_060d:            JZ   _0678
_060f:            MOV  A, @R0
_0610:            XRL  A, #$08
_0612:            JZ   _067a
_0614:            CALL _0179    ; Clear "Video RAM"
_0616:            MOV  R6, #$00
_0618:            MOV  R0, #$3a
_061a:            MOV  A, @R0
_061b:            JB2  _064f
_061d:            MOV  R0, #$3a
_061f:            MOV  A, @R0
_0620:            JB2  _0653
_0622:            MOV  R0, #$38
_0624:            MOV  A, @R0
_0625:            CALL _030a
_0627:            CLR  C
_0628:            MOVX A, @R1
_0629:            ADD  A, #$e7
_062b:            JC   _0659
_062d:            JMP  _042f

_062f:            CLR  C
_0630:            MOV  R0, #$38
_0632:            MOV  A, @R0
_0633:            ADD  A, #$01
_0635:            MOV  @R0, A
_0636:            MOV  R3, A
_0637:            JC   _065d
_0639:            MOV  R0, #$3b
_063b:            MOV  A, @R0
_063c:            JB2  _0641
_063e:            MOV  A, R3
_063f:            JB2  _065d
_0641:            JF0  _0673
_0643:            MOV  R0, #$3a
_0645:            MOV  A, @R0
_0646:            JB2  _066f
_0648:            MOV  R0, #$3a
_064a:            MOV  A, @R0
_064b:            JB2  _0661
_064d:            JMP  _0622
_064f:            CALL _03f3
_0651:            JMP  _061d
_0653:            MOV  A, #$fd
_0655:            CALL _0333
_0657:            JMP  _0622
_0659:            MOV  R4, #$02
_065b:            JMP  _06fd
_065d:            MOV  R4, #$03
_065f:            JMP  _06fd
_0661:            MOV  A, #$ef
_0663:            CALL _0333
_0665:            ORL  P2, #$20
_0667:            ANL  P2, #$ef
_0669:            MOV  R1, #$00
_066b:            CALL _0196
_066d:            JMP  _0221
_066f:            JMP  _0098
_0671:            JMP  _0086
_0673:            CLR  F0
_0674:            MOV  R6, #$00
_0676:            JMP  _0098
_0678:            JMP  _06c1
_067a:            JMP  _067c
_067c:            CALL _0179    ; Clear "Video RAM"
_067e:            CALL _03f3
_0680:            MOV  R0, #$43
_0682:            INC  @R0
_0683:            MOV  A, @R0  ; @R0 containts #$ of secs to delay
_0684:            MOV  R4, #$04 ; Delay for 1 sec: 4 times
_0686:            MOV  R3, #$fa ; 250 millis
_0688:            CALL _03b6    ; delay
_068a:            DJNZ R4, _0686;
_068c:            DEC  A
_068d:            JNZ  _0684    ; Continue delaying until 0 secs left.
_068f:            CLR  C
_0690:            MOV  R0, #$05
_0692:            MOV  R5, A
_0693:            MOV  R6, #$02
_0695:            CALL _0183
_0697:            MOV  R1, #$3a
_0699:            MOV  A, @R1
_069a:            JB2  _06a7
_069c:            MOV  A, R5
_069d:            ADD  A, #$01
_069f:            JC   _06a9
_06a1:            MOV  R3, #$01 ; Delay for ...
_06a3:            CALL _03b6    ; ... 1 milli
_06a5:            JMP  _0690
_06a7:            CALL _03f3
_06a9:            MOV  R6, #$00
_06ab:            MOV  R0, #$43
_06ad:            MOV  A, R5
_06ae:            ANL  A, #$07
_06b0:            MOV  @R0, A
_06b1:            CLR  C
_06b2:            JMP  _0086
;```

;???????????????????????
;-----------------------
;```
_06b4:            CALL _0179     ; Clear "Video RAM"
_06b6:            MOV  R0, #$20  ; Set right-most digit...
_06b8:            MOV  @R0, #$73 ; ... to 'P'
_06ba:            MOV  R0, #$38
_06bc:            MOV  R6, #$02
_06be:            CALL _0183
_06c0:            RET
;```

_06c1:            CALL _0179    ; Clear "Video RAM"
_06c3:            MOV  R3, #$ff ; Delay for ...
_06c5:            CALL _03b6    ; ... 255 millis
_06c7:            MOV  R3, #$ff ; Delay for ...
_06c9:            CALL _03b6    ; ... 255 millis
_06cb:            MOV  R1, #$09
_06cd:            MOV  R4, #$0a
_06cf:            MOV  A, #$00
_06d1:            ADD  A, R1
_06d2:            MOVP3 A, @A
_06d3:            MOV  R3, #$06
_06d5:            MOV  R0, #$25
_06d7:            MOV  @R0, A
_06d8:            DEC  R0
_06d9:            DJNZ R3, _06d7
_06db:            MOV  R3, #$ff ; Delay for ...
_06dd:            CALL _03b6    ; ... 255 millis
_06df:            MOV  R3, #$ff ; Delay for ...
_06e1:            CALL _03b6    ; ... 255 millis
_06e3:            DEC  R1
_06e4:            DJNZ R4, _06cf
_06e6:            MOV  R6, #$00
_06e8:            JMP  _0086


_06ea:            MOV  R6, #$00
_06ec:            CLR  F0
_06ed:            CPL  F0
_06ee:            MOV  R0, #$36
_06f0:            CALL _0183
_06f2:            MOV  R0, #$37
_06f4:            MOV  R6, #$02
_06f6:            CALL _0183
_06f8:            MOV  R0, #$20  ; Set left-most digit...
_06fa:            MOV  @R0, #$77 ; ... to 'A'
_06fc:            RET
_06fd:            CALL _0179     ; Clear "Video RAM"
_06ff:            CLR  C
_0700:            CLR  F0
_0701:            CLR  F1
_0702:            MOV  R0, #$20  ; Set left-most digit...
_0704:            MOV  @R0, #$71 ; ... to 'F'
_0706:            MOV  R0, #$04
_0708:            MOV  R6, #$02
_070a:            CALL _0183
_070c:            JMP  _0086
_070e:            CALL _0179    ; Clear "Video RAM"
_0710:            CALL _03f3
_0712:            MOV  R0, #$20  ; Set left-most digit...
_0714:            MOV  @R0, #$1c ; .. to 'CAL' symbol
_0716:            CLR  A
_0717:            CALL _030a
_0719:            ANL  P1, #$bf
_071b:            ORL  P1, #$80
_071d:            MOV  R0, #$3a
_071f:            MOV  A, @R0
_0720:            JB2  _0778
_0722:            IN   A, P1
_0723:            JB2  _071d
_0725:            CLR  A
_0726:            MOV  R1, A
_0727:            MOV  R5, A
_0728:            MOV  R0, #$08
_072a:            CALL _077c
_072c:            JF1  _0771
_072e:            CALL _07d0
_0730:            MOV  A, R0
_0731:            XRL  A, #$08
_0733:            JNZ  _072a
_0735:            MOV  R0, #$3a
_0737:            MOV  A, @R0
_0738:            JB2  _0778
_073a:            MOV  R0, #$08
_073c:            MOV  A, R1
_073d:            JNZ  _072a
_073f:            MOV  R0, #$3b
_0741:            MOV  A, @R0
_0742:            JB2  _0746
_0744:            JMP  _0764
_0746:            MOV  A, #$80
_0748:            CALL _030a
_074a:            CLR  A
_074b:            MOV  R1, A
_074c:            MOV  R5, A
_074d:            MOV  R0, #$08
_074f:            CALL _077c
_0751:            JF1  _0774
_0753:            CALL _07d0
_0755:            MOV  A, R0
_0756:            XRL  A, #$08
_0758:            JNZ  _074f
_075a:            MOV  R0, #$3a
_075c:            MOV  A, @R0
_075d:            JB2  _0778
_075f:            MOV  R0, #$08
_0761:            MOV  A, R1
_0762:            JNZ  _074f
_0764:            CALL _0179     ; Clear "Video RAM"
_0766:            MOV  R0, #$20  ; Set left-most digit...
_0768:            MOV  @R0, #$5c ; ... to 'o'              TODO: where is 'o' used?
_076a:            CLR  C
_076b:            CLR  F0
_076c:            CLR  F1
_076d:            MOV  R6, #$00
_076f:            JMP  _0086
_0771:            MOV  A, R1
_0772:            JZ   _071d
_0774:            MOV  R4, #$07
_0776:            JMP  _06fd
_0778:            CALL _03f3
_077a:            JMP  _0764


;???????????????????????
;-----------------------
_077c:            CLR  A
_077d:            MOV  R2, A
_077e:            MOV  R3, A
_077f:            CLR  C
_0780:            CLR  F0
_0781:            CLR  F1
_0782:            ORL  P1, #$80
_0784:            IN   A, P1
_0785:            JB2  _0793
_0787:            MOV  A, R2
_0788:            ADD  A, #$01
_078a:            JC   _07c5
_078c:            MOV  R2, A
_078d:            MOV  R4, #$64 ; Delay for...
_078f:            CALL _07c7    ; ... 250*20 micros = 5 millis
_0791:            JMP  _0784
_0793:            MOV  R4, #$c8 ; Delay for...
_0795:            CALL _07c7    ; ... 200*20 micros = 4 millis
_0797:            IN   A, P1
_0798:            JB2  _079c
_079a:            JMP  _0787
_079c:            MOV  R4, #$64 ; Delay for...
_079e:            CALL _07c7    ; ... 250*20 micros = 5 millis
_07a0:            IN   A, P1
_07a1:            JB2  _07bd
_07a3:            MOV  R4, #$c8 ; Delay for...
_07a5:            CALL _07c7    ; ... 200*20 micros = 4 millis
_07a7:            IN   A, P1
_07a8:            JB2  _07bd
_07aa:            MOV  A, R2
_07ab:            ADD  A, #$fd
_07ad:            JNC  _07c5
_07af:            CLR  C
_07b0:            MOV  A, R3
_07b1:            ADD  A, #$fd
_07b3:            JNC  _07c5
_07b5:            CLR  C
_07b6:            MOV  A, R2
_07b7:            CPL  A
_07b8:            ADD  A, R3
_07b9:            JNC  _07bc
_07bb:            CPL  F0
_07bc:            RET
_07bd:            MOV  A, R3
_07be:            ADD  A, #$01
_07c0:            JC   _07c5
_07c2:            MOV  R3, A
_07c3:            JMP  _079c
_07c5:            CPL  F1
_07c6:            RET
;```


;#$#$#$ Delay for a multiple of 20 micros
;Inputs:
;- R4: number of 20 micros delays
;```
_07c7:            NOP
_07c8:            NOP
_07c9:            NOP
_07ca:            NOP
_07cb:            NOP
_07cc:            NOP
_07cd:            DJNZ R4, _07c7
_07cf:            RET


;???????????????????????
;-----------------------
_07d0:            CLR  C
_07d1:            JF0  _07de
_07d3:            MOV  A, R5
_07d4:            RRC  A
_07d5:            MOV  R5, A
_07d6:            DJNZ R0, _07dd
_07d8:            MOV  A, R5
_07d9:            MOVX @R1, A
_07da:            DEC  R1
_07db:            MOV  R0, #$08
_07dd:            RET
_07de:            CPL  C
_07df:            JMP  _07d3


;???????????????????????
;-----------------------
_07e1:            MOV  A, #$df
_07e3:            CALL _0333
_07e5:            RET


;???????????????????????
;-----------------------
_07e6:            MOV  A, #$20
_07e8:            CALL _032e
_07ea:            RET


_07eb:            MOV  R3, A
_07ec:            MOV  A, #$00
_07ee:            ADD  A, R3
_07ef:            MOVP3 A, @A
_07f0:            MOV  R3, A
_07f1:            MOV  A, R6
_07f2:            ADD  A, #$20
_07f4:            INC  A
_07f5:            MOV  R0, A
_07f6:            MOV  A, R3
_07f7:            MOV  @R0, A
_07f8:            INC  R6
_07f9:            RET


_07fa:            ADD  A, R5
_07fb:            .DB  $d6
_07fc:            ADDC A, R0
_07fd:            DJNZ R3, _0700
_07ff:            OUTL BUS, A
;```
