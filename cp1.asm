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

```

JMPs
      1  JMP  0029
      1  JMP  0076
     14  JMP  0086
      2  JMP  0098
      2  JMP  00c9
      1  JMP  00d9
      2  JMP  0104
      1  JMP  010c
      3  JMP  0146
      1  JMP  018a
      1  JMP  01bf
      1  JMP  01c4
      2  JMP  01f1
      1  JMP  01ff
      1  JMP  021a
      1  JMP  0221
      1  JMP  023a
      1  JMP  025c
      4  JMP  02a6
      1  JMP  02b0
      1  JMP  02b4
      1  JMP  02bc
      1  JMP  02dc
      1  JMP  02eb
      1  JMP  0316
      1  JMP  0340
      1  JMP  0349
      1  JMP  0352
      1  JMP  0379
      1  JMP  03af
      1  JMP  03d7
      1  JMP  03db
      1  JMP  042f
      1  JMP  044f
      1  JMP  045f
      2  JMP  048f
      2  JMP  04bd
      1  JMP  04f3
      1  JMP  0505
      1  JMP  050b
      1  JMP  050f
      1  JMP  052b
      3  JMP  0537
      1  JMP  053e
      1  JMP  0552
      1  JMP  056a
      1  JMP  0574
      1  JMP  058d
      1  JMP  0597
      1  JMP  05a4
      2  JMP  05ac
      1  JMP  05b3
      1  JMP  05c9
      1  JMP  05d3
      1  JMP  05ef
      1  JMP  05f9
      1  JMP  0603
      1  JMP  061d
      3  JMP  0622
     20  JMP  062f
      1  JMP  0639
      1  JMP  0659
      2  JMP  065d
      1  JMP  0673
      1  JMP  067c
      1  JMP  0690
      1  JMP  06c1
      7  JMP  06fd
      1  JMP  070e
      2  JMP  0764
      1  JMP  0784
      1  JMP  0787
      1  JMP  079c
      1  JMP  07d3
      2  JMPP @A


$0000: [ 04 29 ] JMP  $0029   ; Reset entry point: Execution starts at 0.
$0002: [ 00    ] NOP
$0003: [ 93    ] RETR         ; Interrupt entry point
$0004: [ 00    ] NOP
$0005: [ 00    ] NOP
$0006: [ 00    ] NOP
$0007: [ 44 5c ] JMP  $025c   ; Timer/Counter entry point
$0009: [ 00    ] NOP
$000a: [ 86 d1 ] JNI  $00d1
$000c: [ d1    ] XRL  A, @R1
$000d: [ d1    ] XRL  A, @R1
$000e: [ d1    ] XRL  A, @R1
$000f: [ d1    ] XRL  A, @R1
$0010: [ d1    ] XRL  A, @R1
$0011: [ d1    ] XRL  A, @R1
$0012: [ d1    ] XRL  A, @R1
$0013: [ d1    ] XRL  A, @R1
$0014: [ d1    ] XRL  A, @R1
$0015: [ 21    ] XCH  A, @R1
$0016: [ eb 23 ] DJNZ R3, $0023
$0018: [ 25    ] EN   TCNTI
$0019: [ 86 1f ] JNI  $001f
$001b: [ 27    ] CLR  A
$001c: [ c3    ] .DB  $c3
$001d: [ 98 bf ] ANL  BUS, #$bf
$001f: [ c4 03 ] JMP  $0603
$0021: [ 24 ff ] JMP  $01ff
$0023: [ e4 0e ] JMP  $070e
$0025: [ c4 22 ] JMP  $0622
$0027: [ 24 c4 ] JMP  $01c4
```

### Initialization code
```
$0029: [ b8 1e ] MOV  R0, #$1e
$002b: [ b0 00 ] MOV  @R0, #$00  ; Set R6' to 0
$002d: [ b8 20 ] MOV  R0, #$20   ; Clear 0x28 ...
$002f: [ bb 28 ] MOV  R3, #$28   ;
$0031: [ b0 00 ] MOV  @R0, #$00  ;
$0033: [ 18    ] INC  R0         ;
$0034: [ eb 31 ] DJNZ R3, $0031  ; ... bytes of RAM
$0036: [ be 00 ] MOV  R6, #$00
$0038: [ bf 00 ] MOV  R7, #$00
$003a: [ b8 1a ] MOV  R0, #$1a
$003c: [ b0 01 ] MOV  @R0, #$01  ; Set R1' to 1
$003e: [ d4 b4 ] CALL $06b4
$0040: [ 9a cf ] ANL  P2, #$cf
$0042: [ 9a bf ] ANL  P2, #$bf
$0044: [ 9a ef ] ANL  P2, #$ef
$0046: [ 8a 20 ] ORL  P2, #$20
$0048: [ b8 00 ] MOV  R0, #$00
$004a: [ 23 0f ] MOV  A, #$0f
$004c: [ 90    ] MOVX @R0, A
$004d: [ b8 02 ] MOV  R0, #$02
$004f: [ 23 ff ] MOV  A, #$ff
$0051: [ 90    ] MOVX @R0, A
$0052: [ 9a df ] ANL  P2, #$df
$0054: [ 8a 10 ] ORL  P2, #$10
$0056: [ b8 00 ] MOV  R0, #$00
$0058: [ 23 0d ] MOV  A, #$0d
$005a: [ 90    ] MOVX @R0, A
$005b: [ b8 01 ] MOV  R0, #$01
$005d: [ 23 ff ] MOV  A, #$ff
$005f: [ 90    ] MOVX @R0, A
$0060: [ b8 03 ] MOV  R0, #$03
$0062: [ 90    ] MOVX @R0, A
$0063: [ 9a 5f ] ANL  P2, #$5f
$0065: [ 8a 10 ] ORL  P2, #$10
$0067: [ b8 13 ] MOV  R0, #$13
$0069: [ 23 5a ] MOV  A, #$5a
$006b: [ 90    ] MOVX @R0, A
$006c: [ 27    ] CLR  A
$006d: [ 80    ] MOVX A, @R0
$006e: [ d3 5a ] XRL  A, #$5a
$0070: [ b8 3b ] MOV  R0, #$3b
$0072: [ c6 94 ] JZ   $0094
$0074: [ b0 7f ] MOV  @R0, #$7f
$0076: [ bb 04 ] MOV  R3, #$04
$0078: [ b8 3f ] MOV  R0, #$3f
$007a: [ 23 ff ] MOV  A, #$ff
$007c: [ a0    ] MOV  @R0, A
$007d: [ 18    ] INC  R0
$007e: [ eb 7c ] DJNZ R3, $007c
$0080: [ 9a 4f ] ANL  P2, #$4f
$0082: [ 74 28 ] CALL $0328
$0084: [ 25    ] EN   TCNTI
$0085: [ 55    ] STRT T
$0086: [ b8 1e ] MOV  R0, #$1e    ; Wait for key press: 0x1e is R6', used in interrupt
$0088: [ f0    ] MOV  A, @R0      ; check for key
$0089: [ c6 86 ] JZ   $0086       ; no key pressed, continue waiting
$008b: [ b9 44 ] MOV  R1, #$44    ;
$008d: [ a1    ] MOV  @R1, A      ; Store pressed key in 0x44
$008e: [ 23 0a ] MOV  A, #$0a
$0090: [ 60    ] ADD  A, @R0
$0091: [ b0 00 ] MOV  @R0, #$00
$0093: [ b3    ] JMPP @A
$0094: [ b0 ff ] MOV  @R0, #$ff
$0096: [ 04 76 ] JMP  $0076
$0098: [ fe    ] MOV  A, R6
$0099: [ c6 bb ] JZ   $00bb
$009b: [ d3 03 ] XRL  A, #$03
$009d: [ 96 c9 ] JNZ  $00c9
$009f: [ b8 27 ] MOV  R0, #$27
$00a1: [ 74 38 ] CALL $0338
$00a3: [ b8 3a ] MOV  R0, #$3a
$00a5: [ f0    ] MOV  A, @R0
$00a6: [ 52 b9 ] JB2  $00b9
$00a8: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$00aa: [ b0 73 ] MOV  @R0, #$73   ; ... to 'P'
$00ac: [ b8 27 ] MOV  R0, #$27
$00ae: [ ba 02 ] MOV  R2, #$02
$00b0: [ 34 4e ] CALL $014e
$00b2: [ b8 38 ] MOV  R0, #$38
$00b4: [ a0    ] MOV  @R0, A
$00b5: [ be 00 ] MOV  R6, #$00
$00b7: [ 04 86 ] JMP  $0086
$00b9: [ 24 46 ] JMP  $0146
$00bb: [ d4 b4 ] CALL $06b4
$00bd: [ 04 86 ] JMP  $0086
$00bf: [ d4 ea ] CALL $06ea
$00c1: [ 04 86 ] JMP  $0086
$00c3: [ 34 79 ] CALL clear_display
$00c5: [ be 00 ] MOV  R6, #$00
$00c7: [ 04 86 ] JMP  $0086
$00c9: [ bc 01 ] MOV  R4, #$01
$00cb: [ c4 fd ] JMP  $06fd
$00cd: [ 34 79 ] CALL clear_display
$00cf: [ 04 d9 ] JMP  $00d9
$00d1: [ fe    ] MOV  A, R6
$00d2: [ c6 cd ] JZ   $00cd
$00d4: [ 97    ] CLR  C
$00d5: [ 03 fb ] ADD  A, #$fb
$00d7: [ f6 c9 ] JC   $00c9
$00d9: [ fe    ] MOV  A, R6
$00da: [ 03 27 ] ADD  A, #$27
$00dc: [ a8    ] MOV  R0, A
$00dd: [ b9 44 ] MOV  R1, #$44
$00df: [ f1    ] MOV  A, @R1
$00e0: [ 07    ] DEC  A
$00e1: [ a0    ] MOV  @R0, A
$00e2: [ aa    ] MOV  R2, A
$00e3: [ 34 aa ] CALL $01aa
$00e5: [ 04 86 ] JMP  $0086
$00e7: [ 24 46 ] JMP  $0146
$00e9: [ 24 0c ] JMP  $010c
$00eb: [ fe    ] MOV  A, R6
$00ec: [ d3 03 ] XRL  A, #$03
$00ee: [ 96 e9 ] JNZ  $00e9
$00f0: [ 23 f7 ] MOV  A, #$f7
$00f2: [ 74 33 ] CALL $0333
$00f4: [ b8 27 ] MOV  R0, #$27
$00f6: [ 74 38 ] CALL $0338
$00f8: [ b8 3a ] MOV  R0, #$3a
$00fa: [ f0    ] MOV  A, @R0
$00fb: [ 52 e7 ] JB2  $00e7
$00fd: [ b8 27 ] MOV  R0, #$27
$00ff: [ ba 02 ] MOV  R2, #$02
$0101: [ 34 4e ] CALL $014e
$0103: [ af    ] MOV  R7, A
$0104: [ be 00 ] MOV  R6, #$00
$0106: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$0108: [ b0 79 ] MOV  @R0, #$79   ; ... to 'E'
$010a: [ 04 86 ] JMP  $0086
$010c: [ fe    ] MOV  A, R6
$010d: [ d3 05 ] XRL  A, #$05
$010f: [ 96 44 ] JNZ  $0144
$0111: [ b8 3a ] MOV  R0, #$3a
$0113: [ f0    ] MOV  A, @R0
$0114: [ 72 4a ] JB3  $014a
$0116: [ b8 27 ] MOV  R0, #$27
$0118: [ ba 01 ] MOV  R2, #$01
$011a: [ 34 4e ] CALL $014e
$011c: [ 97    ] CLR  C
$011d: [ a9    ] MOV  R1, A
$011e: [ 03 e7 ] ADD  A, #$e7
$0120: [ f6 4a ] JC   $014a
$0122: [ b8 29 ] MOV  R0, #$29
$0124: [ ba 02 ] MOV  R2, #$02
$0126: [ 34 4e ] CALL $014e
$0128: [ b6 4a ] JF0  $014a
$012a: [ ac    ] MOV  R4, A
$012b: [ f9    ] MOV  A, R1
$012c: [ aa    ] MOV  R2, A
$012d: [ ff    ] MOV  A, R7
$012e: [ 74 0a ] CALL $030a
$0130: [ fa    ] MOV  A, R2
$0131: [ 91    ] MOVX @R1, A
$0132: [ 19    ] INC  R1
$0133: [ fc    ] MOV  A, R4
$0134: [ 91    ] MOVX @R1, A
$0135: [ b8 3b ] MOV  R0, #$3b
$0137: [ ff    ] MOV  A, R7
$0138: [ d0    ] XRL  A, @R0
$0139: [ c6 3e ] JZ   $013e
$013b: [ 1f    ] INC  R7
$013c: [ 24 04 ] JMP  $0104
$013e: [ 23 08 ] MOV  A, #$08
$0140: [ 74 2e ] CALL $032e
$0142: [ 24 04 ] JMP  $0104
$0144: [ 04 c9 ] JMP  $00c9
$0146: [ 23 fb ] MOV  A, #$fb
$0148: [ 74 33 ] CALL $0333
$014a: [ bc 04 ] MOV  R4, #$04
$014c: [ c4 fd ] JMP  $06fd


?????????
Input
- R0: Source Address
- R2: Iterations???
Output:
- F0:
- A:
=========
$014e: [ f9    ] MOV  A, R1     ; save R1 in...
$014f: [ ab    ] MOV  R3, A     ; ... R3
$0150: [ b9 3c ] MOV  R1, #$3c  ; Destination Adresse: 0x3c == 60
$0152: [ bc 03 ] MOV  R4, #$03  ; Size: 3
$0154: [ f0    ] MOV  A, @R0    ; Copy from (R0)
$0155: [ a1    ] MOV  @R1, A    ; to (R1)
$0156: [ 18    ] INC  R0        ;
$0157: [ 19    ] INC  R1        ;
$0158: [ ec 54 ] DJNZ R4, $0154 ; Continue while not 3 bytes copied
$015a: [ fb    ] MOV  A, R3     ;
$015b: [ a9    ] MOV  R1, A     ; Restore R1
$015c: [ b8 3c ] MOV  R0, #$3c  ; Load address 0x3c == 60
$015e: [ f0    ] MOV  A, @R0    ; Load content of 0x3c
$015f: [ 97    ] CLR  C         ; Clear carry...
$0160: [ 85    ] CLR  F0        ; ... and F0
$0161: [ f7    ] RLC  A         ; check bit 7
$0162: [ f6 76 ] JC   $0176     ; jump if set
$0164: [ f7    ] RLC  A         ; check bit 6
$0165: [ f6 76 ] JC   $0176     ; jump if set
$0167: [ 60    ] ADD  A, @R0    ; add contents of 0x3c again ?????
$0168: [ f6 76 ] JC   $0176     ; jump if carry ist set?
$016a: [ f7    ] RLC  A         ; check bit 7
$016b: [ f6 76 ] JC   $0176     ; jump if set
$016d: [ 18    ] INC  R0        ;
$016e: [ 60    ] ADD  A, @R0    ; add next byte from address 0x3d == 61
$016f: [ f6 76 ] JC   $0176     ; jump if carry
$0171: [ a0    ] MOV  @R0, A    ; store result in 0x3d
$0172: [ ea 61 ] DJNZ R2, $0161 ; continue loop
$0174: [ f0    ] MOV  A, @R0    ; No-op? A contains already what was written to @R0?
$0175: [ 83    ] RET            ; Return
$0176: [ 95    ] CPL  F0        ; Set F0
$0177: [ 97    ] CLR  C         ; Clear carry
$0178: [ 83    ] RET            ; Return


clear_display:
$0179: [ ba 06 ] MOV  R2, #$06
$017b: [ b8 20 ] MOV  R0, #$20  ; 0x20 == 32: Begin of "Video RAM"
$017d: [ b0 00 ] MOV  @R0, #$00
$017f: [ 18    ] INC  R0
$0180: [ ea 7d ] DJNZ R2, $017d
$0182: [ 83    ] RET



?????????
=========
- Input:
  - R0: ???
  - R2: ???
```
$0183: [ f0    ] MOV  A, @R0
$0184: [ 54 d9 ] CALL $02d9
$0186: [ b6 93 ] JF0  $0193
$0188: [ f4 eb ] CALL $07eb
$018a: [ fc    ] MOV  A, R4
$018b: [ f4 eb ] CALL $07eb
$018d: [ fa    ] MOV  A, R2
$018e: [ f4 eb ] CALL $07eb
$0190: [ be 00 ] MOV  R6, #$00
$0192: [ 83    ] RET
$0193: [ 85    ] CLR  F0
$0194: [ 24 8a ] JMP  $018a
```

?????????
=========
$0196: [ 81    ] MOVX A, @R1
$0197: [ b8 39 ] MOV  R0, #$39
$0199: [ a0    ] MOV  @R0, A
$019a: [ be 00 ] MOV  R6, #$00
$019c: [ 85    ] CLR  F0
$019d: [ 95    ] CPL  F0
$019e: [ 34 83 ] CALL $0183
$01a0: [ 19    ] INC  R1
$01a1: [ 81    ] MOVX A, @R1
$01a2: [ b8 39 ] MOV  R0, #$39
$01a4: [ a0    ] MOV  @R0, A
$01a5: [ be 02 ] MOV  R6, #$02
$01a7: [ 34 83 ] CALL $0183
$01a9: [ 83    ] RET
```

?????????
=========
$01aa: [ b8 21 ] MOV  R0, #$21
$01ac: [ b9 22 ] MOV  R1, #$22
$01ae: [ bb 04 ] MOV  R3, #$04
$01b0: [ f1    ] MOV  A, @R1
$01b1: [ a0    ] MOV  @R0, A
$01b2: [ 18    ] INC  R0
$01b3: [ 19    ] INC  R1
$01b4: [ eb b0 ] DJNZ R3, $01b0
$01b6: [ 23 00 ] MOV  A, #$00
$01b8: [ 6a    ] ADD  A, R2
$01b9: [ e3    ] MOVP3 A, @A
$01ba: [ b8 25 ] MOV  R0, #$25
$01bc: [ a0    ] MOV  @R0, A
$01bd: [ 1e    ] INC  R6
$01be: [ 83    ] RET


NON-TARGET ?????????
====================
$01bf: [ 85    ] CLR  F0
$01c0: [ 74 f3 ] CALL $03f3
$01c2: [ 24 f1 ] JMP  $01f1
$01c4: [ 34 79 ] CALL clear_display
$01c6: [ 74 f3 ] CALL $03f3
$01c8: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$01ca: [ b0 23 ] MOV  @R0, #$23   ; .. to 'CAL' symbol
$01cc: [ 89 c0 ] ORL  P1, #$c0
$01ce: [ b8 3a ] MOV  R0, #$3a
$01d0: [ bc 3c ] MOV  R4, #$3c
$01d2: [ bb fa ] MOV  R3, #$fa      ; Delay for...
$01d4: [ 74 b6 ] CALL delay_millis  ; ... 250 millis
$01d6: [ f0    ] MOV  A, @R0
$01d7: [ 12 bf ] JB0  $01bf
$01d9: [ ec d2 ] DJNZ R4, $01d2
$01db: [ 27    ] CLR  A
$01dc: [ 74 0a ] CALL $030a
$01de: [ 74 bd ] CALL $03bd
$01e0: [ b6 bf ] JF0  $01bf
$01e2: [ b8 3b ] MOV  R0, #$3b
$01e4: [ f0    ] MOV  A, @R0
$01e5: [ f2 e9 ] JB7  $01e9
$01e7: [ 24 f1 ] JMP  $01f1
$01e9: [ 23 ff ] MOV  A, #$ff

// FALLTHROUGH!

?????????
=========

$01eb: [ 74 0a ] CALL $030a
$01ed: [ 74 bd ] CALL $03bd
$01ef: [ b6 fd ] JF0  $01fd
$01f1: [ 99 7f ] ANL  P1, #$7f
$01f3: [ 34 79 ] CALL clear_display
$01f5: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$01f7: [ b0 63 ] MOV  @R0, #$63   ; ... to 'ᵒ'     TODO: where is 'ᵒ' used?
$01f9: [ be 00 ] MOV  R6, #$00
$01fb: [ 04 86 ] JMP  $0086
$01fd: [ 24 bf ] JMP  $01bf
$01ff: [ 23 f7 ] MOV  A, #$f7
$0201: [ 74 33 ] CALL $0333
$0203: [ fe    ] MOV  A, R6
$0204: [ c6 27 ] JZ   $0227
$0206: [ d3 03 ] XRL  A, #$03
$0208: [ 96 40 ] JNZ  $0240
$020a: [ b8 27 ] MOV  R0, #$27
$020c: [ 74 38 ] CALL $0338
$020e: [ b8 3a ] MOV  R0, #$3a
$0210: [ f0    ] MOV  A, @R0
$0211: [ 52 3a ] JB2  $023a
$0213: [ b8 27 ] MOV  R0, #$27
$0215: [ ba 02 ] MOV  R2, #$02
$0217: [ 34 4e ] CALL $014e
$0219: [ af    ] MOV  R7, A
$021a: [ 34 79 ] CALL clear_display
$021c: [ ff    ] MOV  A, R7
$021d: [ 74 0a ] CALL $030a
$021f: [ 34 96 ] CALL $0196
$0221: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$0223: [ b0 39 ] MOV  @R0, #$39   ; ... to 'C'
$0225: [ 04 86 ] JMP  $0086
$0227: [ 97    ] CLR  C
$0228: [ ff    ] MOV  A, R7
$0229: [ 03 01 ] ADD  A, #$01
$022b: [ af    ] MOV  R7, A
$022c: [ f6 38 ] JC   $0238
$022e: [ b8 3b ] MOV  R0, #$3b
$0230: [ f0    ] MOV  A, @R0
$0231: [ f2 1a ] JB7  $021a
$0233: [ ff    ] MOV  A, R7
$0234: [ f2 3c ] JB7  $023c
$0236: [ 44 1a ] JMP  $021a
$0238: [ bf ff ] MOV  R7, #$ff
$023a: [ 24 46 ] JMP  $0146
$023c: [ bf 7f ] MOV  R7, #$7f
$023e: [ 44 3a ] JMP  $023a
$0240: [ fe    ] MOV  A, R6
$0241: [ d3 01 ] XRL  A, #$01
$0243: [ c6 47 ] JZ   $0247
$0245: [ 04 c9 ] JMP  $00c9
$0247: [ b8 27 ] MOV  R0, #$27
$0249: [ f0    ] MOV  A, @R0
$024a: [ d3 09 ] XRL  A, #$09
$024c: [ 96 45 ] JNZ  $0245
$024e: [ 34 79 ] CALL clear_display
$0250: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$0252: [ b0 39 ] MOV  @R0, #$39
$0254: [ b8 07 ] MOV  R0, #$07
$0256: [ be 02 ] MOV  R6, #$02
$0258: [ 34 83 ] CALL $0183
$025a: [ 04 86 ] JMP  $0086
```

### Timer/Counter interrupt
- R5: Mask used for keyboard reading and display addressing
- R3: Internal scratch register: Loop vars, computations
```
$025c: [ d5    ] SEL  RB1        ; Switch to register bank 1
$025d: [ 2f    ] XCH  A, R7      ; Exchange A and R7
$025e: [ 23 e0 ] MOV  A, #$e0    ; Reset timer...
$0260: [ 62    ] MOV  T, A       ; ... to 0xe0 (== 224) --> 2560 micros per timer interrupt
$0261: [ ea 6b ] DJNZ R2, $026b  ;
$0263: [ b9 25 ] MOV  R1, #$25
$0265: [ bd fe ] MOV  R5, #$fe
$0267: [ ba 06 ] MOV  R2, #$06
$0269: [ bc 35 ] MOV  R4, #$35
$026b: [ 8a a0 ] ORL  P2, #$a0   ; P2 |= 1010 0000 --> /CE == 1, IO == 1
$026d: [ 9a ef ] ANL  P2, #$ef   ; P2 &= 1110 1111 --> 8155 /CE == 0
$026f: [ 27    ] CLR  A          ; Clear Accumulator
$0270: [ b8 01 ] MOV  R0, #$01   ; Address Port A
$0272: [ 90    ] MOVX @R0, A     ; Write 0 -> Port A
$0273: [ fd    ] MOV  A, R5
$0274: [ b8 03 ] MOV  R0, #$03   ; Address Port C
$0276: [ 90    ] MOVX @R0, A     ; Write row selection -> Port C
$0277: [ f1    ] MOV  A, @R1
$0278: [ b8 01 ] MOV  R0, #$01
$027a: [ 90    ] MOVX @R0, A     ; (0x25) -> Port A; Write one character to display
$027b: [ 8a 0f ] ORL  P2, #$0f   ; P2 |= 0000 1111: Prepare to read from bit 0 - 4
$027d: [ 0c    ] MOVD A, P4      ; ?????write to lower nibble of port 2, then read from it? why not just read from it?
$027e: [ 97    ] CLR  C
$027f: [ bb 04 ] MOV  R3, #$04   ; Test (keypad) bits read: 4 bits in total
$0281: [ 67    ] RRC  A          ; shift lowest bit to carry
$0282: [ f6 88 ] JC   $0288      ; bit set -> 288
$0284: [ eb 81 ] DJNZ R3, $0281  ; not set, carry on with loop.
$0286: [ 44 bc ] JMP  $02bc      ; TODO: Carry on with what?
$0288: [ fa    ] MOV  A, R2      ;
$0289: [ 07    ] DEC  A
$028a: [ e7    ] RL   A
$028b: [ e7    ] RL   A
$028c: [ 17    ] INC  A          ; A = 4 * (R2 - 1) + 1; [R2 starts with 6]
$028d: [ 2b    ] XCH  A, R3      ; R3 = 4 * (R2 - 1) + 1; A = bit_num
$028e: [ 37    ] CPL  A          ; Compute two's complement...
$028f: [ 17    ] INC  A          ; of A --> A == -bit_num
$0290: [ 6b    ] ADD  A, R3      ; A = 4 * (R2 - 1) + 1 - bit_num
$0291: [ ae    ] MOV  R6, A      ; Store A in R6
$0292: [ f2 bc ] JB7  $02bc      ; Bit 7 Set? -> 2bc
$0294: [ fc    ] MOV  A, R4
$0295: [ a8    ] MOV  R0, A
$0296: [ fe    ] MOV  A, R6
$0297: [ d0    ] XRL  A, @R0
$0298: [ c6 b8 ] JZ   $02b8
$029a: [ fe    ] MOV  A, R6
$029b: [ a0    ] MOV  @R0, A
$029c: [ fe    ] MOV  A, R6
$029d: [ d3 0f ] XRL  A, #$0f
$029f: [ c6 c2 ] JZ   $02c2
$02a1: [ fe    ] MOV  A, R6
$02a2: [ d3 0e ] XRL  A, #$0e
$02a4: [ c6 c8 ] JZ   $02c8
$02a6: [ fd    ] MOV  A, R5
$02a7: [ e7    ] RL   A
$02a8: [ ad    ] MOV  R5, A
$02a9: [ c9    ] DEC  R1
$02aa: [ cc    ] DEC  R4
$02ab: [ b8 3a ] MOV  R0, #$3a
$02ad: [ f0    ] MOV  A, @R0
$02ae: [ 92 ce ] JB4  $02ce
$02b0: [ d2 d4 ] JB6  $02d4
$02b2: [ 9a 7f ] ANL  P2, #$7f
$02b4: [ 2f    ] XCH  A, R7
$02b5: [ c5    ] SEL  RB0
$02b6: [ 25    ] EN   TCNTI
$02b7: [ 93    ] RETR
$02b8: [ be 00 ] MOV  R6, #$00
$02ba: [ 44 a6 ] JMP  $02a6


$02bc: [ fc    ] MOV  A, R4
$02bd: [ a8    ] MOV  R0, A
$02be: [ b0 00 ] MOV  @R0, #$00
$02c0: [ 44 a6 ] JMP  $02a6
$02c2: [ 23 01 ] MOV  A, #$01
$02c4: [ 74 2e ] CALL $032e
$02c6: [ 44 a6 ] JMP  $02a6
$02c8: [ 23 02 ] MOV  A, #$02
$02ca: [ 74 2e ] CALL $032e
$02cc: [ 44 a6 ] JMP  $02a6
$02ce: [ 9a df ] ANL  P2, #$df
$02d0: [ 8a 10 ] ORL  P2, #$10
$02d2: [ 44 b0 ] JMP  $02b0
$02d4: [ 8a 80 ] ORL  P2, #$80
$02d6: [ 44 b4 ] JMP  $02b4
$02d8: [ 83    ] RET
$02d9: [ 97    ] CLR  C
$02da: [ bc 00 ] MOV  R4, #$00
$02dc: [ 03 f6 ] ADD  A, #$f6
$02de: [ e6 e4 ] JNC  $02e4
$02e0: [ 97    ] CLR  C
$02e1: [ 1c    ] INC  R4
$02e2: [ 44 dc ] JMP  $02dc
$02e4: [ 03 0a ] ADD  A, #$0a
$02e6: [ aa    ] MOV  R2, A
$02e7: [ 97    ] CLR  C
$02e8: [ fc    ] MOV  A, R4
$02e9: [ bc 00 ] MOV  R4, #$00
$02eb: [ 03 f6 ] ADD  A, #$f6
$02ed: [ e6 f3 ] JNC  $02f3
$02ef: [ 97    ] CLR  C
$02f0: [ 1c    ] INC  R4
$02f1: [ 44 eb ] JMP  $02eb
$02f3: [ 03 0a ] ADD  A, #$0a
$02f5: [ 2c    ] XCH  A, R4
$02f6: [ 97    ] CLR  C
$02f7: [ 83    ] RET
$02f8: [ 00    ] NOP
$02f9: [ 88 c4 ] ORL  BUS, #$c4
$02fb: [ 00    ] NOP
$02fc: [ 00    ] NOP
$02fd: [ 00    ] NOP
$02fe: [ 00    ] NOP
$02ff: [ 1e    ] INC  R6
$0300: [ 3f    ] MOVD P7, A
$0301: [ 06    ] .DB  $06
$0302: [ 5b    ] ANL  A, R3
$0303: [ 4f    ] ORL  A, R7
$0304: [ 66    ] .DB  $66
$0305: [ 6d    ] ADD  A, R5
$0306: [ 7d    ] ADDC A, R5
$0307: [ 27    ] CLR  A
$0308: [ 7f    ] ADDC A, R7
$0309: [ 6f    ] ADD  A, R7

// FALLTHROUGH??

??????????????????
------------------
$030a: [ f2 19 ] JB7  $0319
$030c: [ ab    ] MOV  R3, A
$030d: [ 23 ef ] MOV  A, #$ef
$030f: [ 74 33 ] CALL $0333
$0311: [ 8a 20 ] ORL  P2, #$20
$0313: [ 9a ef ] ANL  P2, #$ef
$0315: [ fb    ] MOV  A, R3
$0316: [ e7    ] RL   A
$0317: [ a9    ] MOV  R1, A
$0318: [ 83    ] RET
$0319: [ 03 80 ] ADD  A, #$80
$031b: [ 97    ] CLR  C
$031c: [ ab    ] MOV  R3, A
$031d: [ 23 10 ] MOV  A, #$10
$031f: [ 74 2e ] CALL $032e
$0321: [ 8a 10 ] ORL  P2, #$10
$0323: [ 9a df ] ANL  P2, #$df
$0325: [ fb    ] MOV  A, R3
$0326: [ 64 16 ] JMP  $0316

?????????????????????????????
-----------------------------
$0328: [ 27    ] CLR  A
$0329: [ a9    ] MOV  R1, A
$032a: [ 91    ] MOVX @R1, A
$032b: [ e9 2a ] DJNZ R1, $032a
$032d: [ 83    ] RET

?????????????????????????????
-----------------------------
$032e: [ b8 3a ] MOV  R0, #$3a
$0330: [ 40    ] ORL  A, @R0
$0331: [ a0    ] MOV  @R0, A
$0332: [ 83    ] RET

?????????????????????????????
-----------------------------
$0333: [ b8 3a ] MOV  R0, #$3a
$0335: [ 50    ] ANL  A, @R0
$0336: [ a0    ] MOV  @R0, A
$0337: [ 83    ] RET

?????????????????????????????
-----------------------------
$0338: [ ba 02 ] MOV  R2, #$02
$033a: [ 34 4e ] CALL $014e
$033c: [ b6 48 ] JF0  $0348
$033e: [ f2 41 ] JB7  $0341
$0340: [ 83    ] RET
$0341: [ b8 3b ] MOV  R0, #$3b
$0343: [ f0    ] MOV  A, @R0
$0344: [ f2 40 ] JB7  $0340
$0346: [ 64 49 ] JMP  $0349
$0348: [ 85    ] CLR  F0
$0349: [ 23 04 ] MOV  A, #$04
$034b: [ 74 2e ] CALL $032e
$034d: [ 64 40 ] JMP  $0340

?????????????????????????????
-----------------------------
$034f: [ 85    ] CLR  F0
$0350: [ f2 53 ] JB7  $0353
$0352: [ 83    ] RET
$0353: [ b8 3b ] MOV  R0, #$3b
$0355: [ f0    ] MOV  A, @R0
$0356: [ f2 52 ] JB7  $0352
$0358: [ 95    ] CPL  F0
$0359: [ 64 52 ] JMP  $0352

?????????????????????????????
-----------------------------
$035b: [ 81    ] MOVX A, @R1
$035c: [ ab    ] MOV  R3, A
$035d: [ 74 4f ] CALL $034f
$035f: [ b6 64 ] JF0  $0364
$0361: [ fb    ] MOV  A, R3
$0362: [ 74 0a ] CALL $030a
$0364: [ 83    ] RET

?????????????????????????????
-----------------------------
$0365: [ 81    ] MOVX A, @R1
$0366: [ ab    ] MOV  R3, A
$0367: [ 74 4f ] CALL $034f
$0369: [ b6 78 ] JF0  $0378
$036b: [ fb    ] MOV  A, R3
$036c: [ 74 0a ] CALL $030a
$036e: [ 81    ] MOVX A, @R1
$036f: [ 96 78 ] JNZ  $0378
$0371: [ 19    ] INC  R1
$0372: [ b8 36 ] MOV  R0, #$36
$0374: [ f0    ] MOV  A, @R0
$0375: [ 96 78 ] JNZ  $0378
$0377: [ 18    ] INC  R0
$0378: [ 83    ] RET

?????????????????????????????
-----------------------------
$0379: [ ec 82 ] DJNZ R4, $0382
$037b: [ 12 7f ] JB0  $037f
$037d: [ 27    ] CLR  A
$037e: [ 83    ] RET
$037f: [ 23 01 ] MOV  A, #$01
$0381: [ 83    ] RET
$0382: [ 77    ] RR   A
$0383: [ 64 79 ] JMP  $0379

?????????????????????????????
-----------------------------
$0385: [ 23 40 ] MOV  A, #$40
$0387: [ 74 2e ] CALL $032e
$0389: [ 23 ef ] MOV  A, #$ef
$038b: [ 74 33 ] CALL $0333
$038d: [ 8a a0 ] ORL  P2, #$a0
$038f: [ 9a ef ] ANL  P2, #$ef
$0391: [ 83    ] RET

?????????????????????????????
-----------------------------
$0392: [ 23 50 ] MOV  A, #$50
$0394: [ 74 2e ] CALL $032e
$0396: [ 9a df ] ANL  P2, #$df
$0398: [ 8a 90 ] ORL  P2, #$90
$039a: [ 83    ] RET

?????????????????????????????
-----------------------------
$039b: [ b8 36 ] MOV  R0, #$36
$039d: [ b0 00 ] MOV  @R0, #$00
$039f: [ 18    ] INC  R0
$03a0: [ a0    ] MOV  @R0, A
$03a1: [ 83    ] RET

?????????????????????????????
-----------------------------
$03a2: [ aa    ] MOV  R2, A
$03a3: [ 97    ] CLR  C
$03a4: [ a7    ] CPL  C
$03a5: [ 27    ] CLR  A
$03a6: [ f7    ] RLC  A
$03a7: [ ea a6 ] DJNZ R2, $03a6
$03a9: [ aa    ] MOV  R2, A
$03aa: [ f0    ] MOV  A, @R0
$03ab: [ c6 b1 ] JZ   $03b1
$03ad: [ fa    ] MOV  A, R2
$03ae: [ 41    ] ORL  A, @R1
$03af: [ a1    ] MOV  @R1, A
$03b0: [ 83    ] RET
$03b1: [ fa    ] MOV  A, R2
$03b2: [ 37    ] CPL  A
$03b3: [ 51    ] ANL  A, @R1
$03b4: [ 64 af ] JMP  $03af
```

### Delay for n millis
Inputs:
- R3: number of millis to delay
```
delay_millis:
$03b6: [ ba c8 ] MOV  R2, #$c8   ; cycles: 2
$03b8: [ ea b8 ] DJNZ R2, $03b8  ; cycles: + 200 * 2
$03ba: [ eb b6 ] DJNZ R3, $03b6  ; cycles: R3 * 201 * 2
$03bc: [ 83    ] RET             ; cycles: R3 * 201 * 2 + 1



?????????????????  ; Probably "Save to tape" subroutine...
-----------------
$03bd: [ b9 00 ] MOV  R1, #$00
$03bf: [ b8 3a ] MOV  R0, #$3a
$03c1: [ f0    ] MOV  A, @R0
$03c2: [ 12 eb ] JB0  $03eb
$03c4: [ 97    ] CLR  C
$03c5: [ b8 08 ] MOV  R0, #$08
$03c7: [ 81    ] MOVX A, @R1
$03c8: [ 67    ] RRC  A
$03c9: [ e6 dd ] JNC  $03dd
$03cb: [ 99 7f ] ANL  P1, #$7f
$03cd: [ bb 1e ] MOV  R3, #$1e     ; 30 millis
$03cf: [ 74 b6 ] CALL delay_millis
$03d1: [ 89 80 ] ORL  P1, #$80
$03d3: [ bb 3c ] MOV  R3, #$3c     ; 60 millis
$03d5: [ 74 b6 ] CALL delay_millis
$03d7: [ e8 c8 ] DJNZ R0, $03c8
$03d9: [ e9 bf ] DJNZ R1, $03bf
$03db: [ 97    ] CLR  C
$03dc: [ 83    ] RET
$03dd: [ 99 7f ] ANL  P1, #$7f
$03df: [ bb 3c ] MOV  R3, #$3c     ; 60 millis
$03e1: [ 74 b6 ] CALL delay_millis
$03e3: [ 89 80 ] ORL  P1, #$80
$03e5: [ bb 1e ] MOV  R3, #$1e     ; 30 millis
$03e7: [ 74 b6 ] CALL delay_millis
$03e9: [ 64 d7 ] JMP  $03d7
$03eb: [ 85    ] CLR  F0
$03ec: [ 95    ] CPL  F0
$03ed: [ 23 fe ] MOV  A, #$fe
$03ef: [ 74 33 ] CALL $0333
$03f1: [ 64 db ] JMP  $03db



$03f3: [ 23 fe ] MOV  A, #$fe
$03f5: [ 74 33 ] CALL $0333
$03f7: [ 83    ] RET
$03f8: [ 00    ] NOP
$03f9: [ ef 54 ] DJNZ R7, $0354
$03fb: [ 28    ] XCH  A, R0
$03fc: [ d2 36 ] JB6  $0336
$03fe: [ 00    ] NOP
$03ff: [ 05    ] EN   I
$0400: [ 19    ] INC  R1
$0401: [ 34 eb ] CALL $01eb
$0403: [ 7b    ] ADDC A, R3
$0404: [ 46 5f ] JNT1 $045f
$0406: [ 4f    ] ORL  A, R7
$0407: [ f3    ] .DB  $f3
$0408: [ 1b    ] INC  R3
$0409: [ 8f    ] ORLD P7, A
$040a: [ 1d    ] INC  R5
$040b: [ 96 1f ] JNZ  $041f
$040d: [ 21    ] XCH  A, @R1
$040e: [ a6    ] .DB  $a6
$040f: [ c1    ] .DB  $c1
$0410: [ 23 25 ] MOV  A, #$25
$0412: [ 27    ] CLR  A
$0413: [ 6d    ] ADD  A, R5
$0414: [ 74 9f ] CALL $039f
$0416: [ 29    ] XCH  A, R1
$0417: [ 2b    ] XCH  A, R3
$0418: [ 2d    ] XCH  A, R5
$0419: [ c4 59 ] JMP  $0659
$041b: [ a4 0b ] JMP  $050b
$041d: [ a4 2b ] JMP  $052b
$041f: [ a4 52 ] JMP  $0552
$0421: [ a4 3e ] JMP  $053e
$0423: [ a4 6a ] JMP  $056a
$0425: [ a4 8d ] JMP  $058d
$0427: [ a4 a4 ] JMP  $05a4
$0429: [ a4 c9 ] JMP  $05c9
$042b: [ a4 ef ] JMP  $05ef
$042d: [ a4 f9 ] JMP  $05f9
$042f: [ 81    ] MOVX A, @R1
$0430: [ 03 00 ] ADD  A, #$00
$0432: [ 19    ] INC  R1
$0433: [ b3    ] JMPP @A
$0434: [ 23 01 ] MOV  A, #$01
$0436: [ 74 33 ] CALL $0333
$0438: [ b8 38 ] MOV  R0, #$38
$043a: [ f0    ] MOV  A, @R0
$043b: [ b8 3b ] MOV  R0, #$3b
$043d: [ d0    ] XRL  A, @R0
$043e: [ c6 44 ] JZ   $0444
$0440: [ 85    ] CLR  F0
$0441: [ 95    ] CPL  F0
$0442: [ c4 2f ] JMP  $062f
$0444: [ c4 73 ] JMP  $0673
$0446: [ b8 36 ] MOV  R0, #$36
$0448: [ b0 00 ] MOV  @R0, #$00
$044a: [ 18    ] INC  R0
$044b: [ 81    ] MOVX A, @R1
$044c: [ a0    ] MOV  @R0, A
$044d: [ c4 2f ] JMP  $062f
$044f: [ 74 5b ] CALL $035b
$0451: [ b6 5d ] JF0  $045d
$0453: [ b8 36 ] MOV  R0, #$36
$0455: [ f0    ] MOV  A, @R0
$0456: [ 91    ] MOVX @R1, A
$0457: [ 18    ] INC  R0
$0458: [ 19    ] INC  R1
$0459: [ f0    ] MOV  A, @R0
$045a: [ 91    ] MOVX @R1, A
$045b: [ c4 2f ] JMP  $062f
$045d: [ c4 5d ] JMP  $065d
$045f: [ 74 5b ] CALL $035b
$0461: [ b6 5d ] JF0  $045d
$0463: [ b8 36 ] MOV  R0, #$36
$0465: [ 81    ] MOVX A, @R1
$0466: [ a0    ] MOV  @R0, A
$0467: [ 18    ] INC  R0
$0468: [ 19    ] INC  R1
$0469: [ 81    ] MOVX A, @R1
$046a: [ a0    ] MOV  @R0, A
$046b: [ c4 2f ] JMP  $062f
$046d: [ 74 5b ] CALL $035b
$046f: [ b6 5d ] JF0  $045d
$0471: [ 19    ] INC  R1
$0472: [ 84 5f ] JMP  $045f
$0474: [ 74 5b ] CALL $035b
$0476: [ b6 5d ] JF0  $045d
$0478: [ 19    ] INC  R1
$0479: [ 84 4f ] JMP  $044f
$047b: [ 81    ] MOVX A, @R1
$047c: [ c6 8d ] JZ   $048d
$047e: [ aa    ] MOV  R2, A
$047f: [ bb 01 ] MOV  R3, #$01
$0481: [ bc 01 ] MOV  R4, #$01
$0483: [ bd c8 ] MOV  R5, #$c8
$0485: [ ed 85 ] DJNZ R5, $0485
$0487: [ ec 83 ] DJNZ R4, $0483
$0489: [ eb 81 ] DJNZ R3, $0481
$048b: [ ea 7f ] DJNZ R2, $047f
$048d: [ c4 2f ] JMP  $062f
$048f: [ 81    ] MOVX A, @R1
$0490: [ b8 38 ] MOV  R0, #$38
$0492: [ ab    ] MOV  R3, A
$0493: [ a0    ] MOV  @R0, A
$0494: [ c4 39 ] JMP  $0639
$0496: [ b8 3a ] MOV  R0, #$3a
$0498: [ f0    ] MOV  A, @R0
$0499: [ b2 9d ] JB5  $049d
$049b: [ c4 2f ] JMP  $062f
$049d: [ 84 8f ] JMP  $048f
$049f: [ 74 5b ] CALL $035b
$04a1: [ b6 5d ] JF0  $045d
$04a3: [ 19    ] INC  R1
$04a4: [ 84 8f ] JMP  $048f
$04a6: [ b8 36 ] MOV  R0, #$36
$04a8: [ f0    ] MOV  A, @R0
$04a9: [ 96 bd ] JNZ  $04bd
$04ab: [ 97    ] CLR  C
$04ac: [ 18    ] INC  R0
$04ad: [ f0    ] MOV  A, @R0
$04ae: [ 03 fe ] ADD  A, #$fe
$04b0: [ f6 bd ] JC   $04bd
$04b2: [ f0    ] MOV  A, @R0
$04b3: [ c6 b9 ] JZ   $04b9
$04b5: [ b0 00 ] MOV  @R0, #$00
$04b7: [ c4 2f ] JMP  $062f
$04b9: [ b0 01 ] MOV  @R0, #$01
$04bb: [ c4 2f ] JMP  $062f
$04bd: [ bc 05 ] MOV  R4, #$05
$04bf: [ c4 fd ] JMP  $06fd
$04c1: [ b8 36 ] MOV  R0, #$36
$04c3: [ f0    ] MOV  A, @R0
$04c4: [ 96 bd ] JNZ  $04bd
$04c6: [ 97    ] CLR  C
$04c7: [ 18    ] INC  R0
$04c8: [ f0    ] MOV  A, @R0
$04c9: [ 03 fe ] ADD  A, #$fe
$04cb: [ f6 bd ] JC   $04bd
$04cd: [ 74 5b ] CALL $035b
$04cf: [ b6 5d ] JF0  $045d
$04d1: [ 81    ] MOVX A, @R1
$04d2: [ 96 bd ] JNZ  $04bd
$04d4: [ 97    ] CLR  C
$04d5: [ 19    ] INC  R1
$04d6: [ 81    ] MOVX A, @R1
$04d7: [ 03 fe ] ADD  A, #$fe
$04d9: [ f6 bd ] JC   $04bd
$04db: [ b8 37 ] MOV  R0, #$37
$04dd: [ f0    ] MOV  A, @R0
$04de: [ c6 bb ] JZ   $04bb
$04e0: [ 81    ] MOVX A, @R1
$04e1: [ c6 e7 ] JZ   $04e7
$04e3: [ b0 01 ] MOV  @R0, #$01
$04e5: [ c4 2f ] JMP  $062f
$04e7: [ b0 00 ] MOV  @R0, #$00
$04e9: [ c4 2f ] JMP  $062f
$04eb: [ d4 ea ] CALL $06ea
$04ed: [ c4 2f ] JMP  $062f
$04ef: [ a4 0f ] JMP  $050f
$04f1: [ a4 05 ] JMP  $0505
$04f3: [ 74 65 ] CALL $0365
$04f5: [ b6 5d ] JF0  $045d
$04f7: [ 96 bd ] JNZ  $04bd
$04f9: [ 76 ef ] JF1  $04ef
$04fb: [ 97    ] CLR  C
$04fc: [ 81    ] MOVX A, @R1
$04fd: [ 60    ] ADD  A, @R0
$04fe: [ f6 f1 ] JC   $05f1
$0500: [ a0    ] MOV  @R0, A
$0501: [ c4 2f ] JMP  $062f
$0503: [ c4 5d ] JMP  $065d
$0505: [ bc 06 ] MOV  R4, #$06
$0507: [ c4 fd ] JMP  $06fd
$0509: [ 84 bd ] JMP  $04bd
$050b: [ a5    ] CLR  F1
$050c: [ b5    ] CPL  F1
$050d: [ 84 f3 ] JMP  $04f3
$050f: [ a5    ] CLR  F1
$0510: [ 81    ] MOVX A, @R1
$0511: [ ab    ] MOV  R3, A
$0512: [ d0    ] XRL  A, @R0
$0513: [ c6 27 ] JZ   $0527
$0515: [ fb    ] MOV  A, R3
$0516: [ c6 25 ] JZ   $0525
$0518: [ 37    ] CPL  A
$0519: [ 97    ] CLR  C
$051a: [ 03 01 ] ADD  A, #$01
$051c: [ 60    ] ADD  A, @R0
$051d: [ e6 05 ] JNC  $0505
$051f: [ 97    ] CLR  C
$0520: [ f0    ] MOV  A, @R0
$0521: [ 07    ] DEC  A
$0522: [ eb 21 ] DJNZ R3, $0521
$0524: [ a0    ] MOV  @R0, A
$0525: [ c4 2f ] JMP  $062f
$0527: [ b0 00 ] MOV  @R0, #$00
$0529: [ c4 2f ] JMP  $062f
$052b: [ f4 e1 ] CALL $07e1
$052d: [ 74 65 ] CALL $0365
$052f: [ b6 03 ] JF0  $0503
$0531: [ 96 09 ] JNZ  $0509
$0533: [ 81    ] MOVX A, @R1
$0534: [ d0    ] XRL  A, @R0
$0535: [ c6 3a ] JZ   $053a
$0537: [ 97    ] CLR  C
$0538: [ c4 2f ] JMP  $062f
$053a: [ f4 e6 ] CALL $07e6
$053c: [ a4 37 ] JMP  $0537
$053e: [ f4 e1 ] CALL $07e1
$0540: [ 74 65 ] CALL $0365
$0542: [ b6 03 ] JF0  $0503
$0544: [ 96 09 ] JNZ  $0509
$0546: [ 81    ] MOVX A, @R1
$0547: [ c6 37 ] JZ   $0537
$0549: [ 37    ] CPL  A
$054a: [ 97    ] CLR  C
$054b: [ 03 01 ] ADD  A, #$01
$054d: [ 60    ] ADD  A, @R0
$054e: [ e6 3a ] JNC  $053a
$0550: [ a4 37 ] JMP  $0537
$0552: [ f4 e1 ] CALL $07e1
$0554: [ 74 65 ] CALL $0365
$0556: [ b6 03 ] JF0  $0503
$0558: [ 96 09 ] JNZ  $0509
$055a: [ f0    ] MOV  A, @R0
$055b: [ c6 37 ] JZ   $0537
$055d: [ 37    ] CPL  A
$055e: [ 97    ] CLR  C
$055f: [ 03 01 ] ADD  A, #$01
$0561: [ ab    ] MOV  R3, A
$0562: [ 81    ] MOVX A, @R1
$0563: [ 6b    ] ADD  A, R3
$0564: [ e6 3a ] JNC  $053a
$0566: [ a4 37 ] JMP  $0537
$0568: [ a4 74 ] JMP  $0574
$056a: [ 81    ] MOVX A, @R1
$056b: [ 96 68 ] JNZ  $0568
$056d: [ 89 ff ] ORL  P1, #$ff
$056f: [ 09    ] IN   A, P1
$0570: [ 74 9b ] CALL $039b
$0572: [ c4 2f ] JMP  $062f
$0574: [ ac    ] MOV  R4, A
$0575: [ 97    ] CLR  C
$0576: [ 03 f7 ] ADD  A, #$f7
$0578: [ f6 09 ] JC   $0509
$057a: [ fc    ] MOV  A, R4
$057b: [ aa    ] MOV  R2, A
$057c: [ 97    ] CLR  C
$057d: [ a7    ] CPL  C
$057e: [ 27    ] CLR  A
$057f: [ f7    ] RLC  A
$0580: [ ea 7f ] DJNZ R2, $057f
$0582: [ b9 3f ] MOV  R1, #$3f
$0584: [ 41    ] ORL  A, @R1
$0585: [ 39    ] OUTL P1, A
$0586: [ 09    ] IN   A, P1
$0587: [ 74 79 ] CALL $0379
$0589: [ 74 9b ] CALL $039b
$058b: [ c4 2f ] JMP  $062f
$058d: [ b8 37 ] MOV  R0, #$37
$058f: [ 81    ] MOVX A, @R1
$0590: [ aa    ] MOV  R2, A
$0591: [ b9 3f ] MOV  R1, #$3f
$0593: [ 96 9a ] JNZ  $059a
$0595: [ f0    ] MOV  A, @R0
$0596: [ a1    ] MOV  @R1, A
$0597: [ 39    ] OUTL P1, A
$0598: [ c4 2f ] JMP  $062f
$059a: [ 97    ] CLR  C
$059b: [ 03 f7 ] ADD  A, #$f7
$059d: [ f6 09 ] JC   $0509
$059f: [ fa    ] MOV  A, R2
$05a0: [ 74 a2 ] CALL $03a2
$05a2: [ a4 97 ] JMP  $0597
$05a4: [ 81    ] MOVX A, @R1
$05a5: [ aa    ] MOV  R2, A
$05a6: [ 74 85 ] CALL $0385
$05a8: [ b9 40 ] MOV  R1, #$40
$05aa: [ bc 02 ] MOV  R4, #$02
$05ac: [ b8 37 ] MOV  R0, #$37
$05ae: [ fa    ] MOV  A, R2
$05af: [ 96 bf ] JNZ  $05bf
$05b1: [ f0    ] MOV  A, @R0
$05b2: [ a1    ] MOV  @R1, A
$05b3: [ 2c    ] XCH  A, R4
$05b4: [ a8    ] MOV  R0, A
$05b5: [ 2c    ] XCH  A, R4
$05b6: [ 90    ] MOVX @R0, A
$05b7: [ 23 bf ] MOV  A, #$bf
$05b9: [ 74 33 ] CALL $0333
$05bb: [ 9a 7f ] ANL  P2, #$7f
$05bd: [ c4 2f ] JMP  $062f
$05bf: [ 97    ] CLR  C
$05c0: [ 03 f7 ] ADD  A, #$f7
$05c2: [ f6 e7 ] JC   $05e7
$05c4: [ fa    ] MOV  A, R2
$05c5: [ 74 a2 ] CALL $03a2
$05c7: [ a4 b3 ] JMP  $05b3
$05c9: [ 81    ] MOVX A, @R1
$05ca: [ ac    ] MOV  R4, A
$05cb: [ 74 92 ] CALL $0392
$05cd: [ b8 02 ] MOV  R0, #$02
$05cf: [ fc    ] MOV  A, R4
$05d0: [ 96 dd ] JNZ  $05dd
$05d2: [ 80    ] MOVX A, @R0
$05d3: [ 74 9b ] CALL $039b
$05d5: [ 23 bf ] MOV  A, #$bf
$05d7: [ 74 33 ] CALL $0333
$05d9: [ 9a 7f ] ANL  P2, #$7f
$05db: [ c4 2f ] JMP  $062f
$05dd: [ 97    ] CLR  C
$05de: [ 03 f7 ] ADD  A, #$f7
$05e0: [ f6 e7 ] JC   $05e7
$05e2: [ 80    ] MOVX A, @R0
$05e3: [ 74 79 ] CALL $0379
$05e5: [ a4 d3 ] JMP  $05d3
$05e7: [ 23 bf ] MOV  A, #$bf
$05e9: [ 74 33 ] CALL $0333
$05eb: [ 9a 7f ] ANL  P2, #$7f
$05ed: [ 84 bd ] JMP  $04bd
$05ef: [ 81    ] MOVX A, @R1
$05f0: [ aa    ] MOV  R2, A
$05f1: [ 74 92 ] CALL $0392
$05f3: [ b9 41 ] MOV  R1, #$41
$05f5: [ bc 01 ] MOV  R4, #$01
$05f7: [ a4 ac ] JMP  $05ac
$05f9: [ 81    ] MOVX A, @R1
$05fa: [ aa    ] MOV  R2, A
$05fb: [ 74 92 ] CALL $0392
$05fd: [ b9 42 ] MOV  R1, #$42
$05ff: [ bc 03 ] MOV  R4, #$03
$0601: [ a4 ac ] JMP  $05ac
$0603: [ fe    ] MOV  A, R6
$0604: [ d3 01 ] XRL  A, #$01
$0606: [ 96 14 ] JNZ  $0614
$0608: [ b8 27 ] MOV  R0, #$27
$060a: [ f0    ] MOV  A, @R0
$060b: [ d3 09 ] XRL  A, #$09
$060d: [ c6 78 ] JZ   $0678
$060f: [ f0    ] MOV  A, @R0
$0610: [ d3 08 ] XRL  A, #$08
$0612: [ c6 7a ] JZ   $067a
$0614: [ 34 79 ] CALL clear_display
$0616: [ be 00 ] MOV  R6, #$00
$0618: [ b8 3a ] MOV  R0, #$3a
$061a: [ f0    ] MOV  A, @R0
$061b: [ 12 4f ] JB0  $064f
$061d: [ b8 3a ] MOV  R0, #$3a
$061f: [ f0    ] MOV  A, @R0
$0620: [ 32 53 ] JB1  $0653
$0622: [ b8 38 ] MOV  R0, #$38
$0624: [ f0    ] MOV  A, @R0
$0625: [ 74 0a ] CALL $030a
$0627: [ 97    ] CLR  C
$0628: [ 81    ] MOVX A, @R1
$0629: [ 03 e7 ] ADD  A, #$e7
$062b: [ f6 59 ] JC   $0659
$062d: [ 84 2f ] JMP  $042f
$062f: [ 97    ] CLR  C
$0630: [ b8 38 ] MOV  R0, #$38
$0632: [ f0    ] MOV  A, @R0
$0633: [ 03 01 ] ADD  A, #$01
$0635: [ a0    ] MOV  @R0, A
$0636: [ ab    ] MOV  R3, A
$0637: [ f6 5d ] JC   $065d
$0639: [ b8 3b ] MOV  R0, #$3b
$063b: [ f0    ] MOV  A, @R0
$063c: [ f2 41 ] JB7  $0641
$063e: [ fb    ] MOV  A, R3
$063f: [ f2 5d ] JB7  $065d
$0641: [ b6 73 ] JF0  $0673
$0643: [ b8 3a ] MOV  R0, #$3a
$0645: [ f0    ] MOV  A, @R0
$0646: [ 32 6f ] JB1  $066f
$0648: [ b8 3a ] MOV  R0, #$3a
$064a: [ f0    ] MOV  A, @R0
$064b: [ 12 61 ] JB0  $0661
$064d: [ c4 22 ] JMP  $0622
$064f: [ 74 f3 ] CALL $03f3
$0651: [ c4 1d ] JMP  $061d
$0653: [ 23 fd ] MOV  A, #$fd
$0655: [ 74 33 ] CALL $0333
$0657: [ c4 22 ] JMP  $0622
$0659: [ bc 02 ] MOV  R4, #$02
$065b: [ c4 fd ] JMP  $06fd
$065d: [ bc 03 ] MOV  R4, #$03
$065f: [ c4 fd ] JMP  $06fd
$0661: [ 23 ef ] MOV  A, #$ef
$0663: [ 74 33 ] CALL $0333
$0665: [ 8a 20 ] ORL  P2, #$20
$0667: [ 9a ef ] ANL  P2, #$ef
$0669: [ b9 00 ] MOV  R1, #$00
$066b: [ 34 96 ] CALL $0196
$066d: [ 44 21 ] JMP  $0221
$066f: [ 04 98 ] JMP  $0098
$0671: [ 04 86 ] JMP  $0086
$0673: [ 85    ] CLR  F0
$0674: [ be 00 ] MOV  R6, #$00
$0676: [ 04 98 ] JMP  $0098
$0678: [ c4 c1 ] JMP  $06c1
$067a: [ c4 7c ] JMP  $067c
$067c: [ 34 79 ] CALL clear_display
$067e: [ 74 f3 ] CALL $03f3
$0680: [ b8 43 ] MOV  R0, #$43
$0682: [ 10    ] INC  @R0
$0683: [ f0    ] MOV  A, @R0        ; @R0 contains # of secs to delay
$0684: [ bc 04 ] MOV  R4, #$04      ; loop 4 times...
$0686: [ bb fa ] MOV  R3, #$fa      ; 250 millis each time
$0688: [ 74 b6 ] CALL delay_millis
$068a: [ ec 86 ] DJNZ R4, $0686
$068c: [ 07    ] DEC  A
$068d: [ 96 84 ] JNZ  $0684         ; Continue delaying until 0 secs left.
$068f: [ 97    ] CLR  C
$0690: [ b8 05 ] MOV  R0, #$05
$0692: [ ad    ] MOV  R5, A
$0693: [ be 02 ] MOV  R6, #$02
$0695: [ 34 83 ] CALL $0183
$0697: [ b9 3a ] MOV  R1, #$3a
$0699: [ f1    ] MOV  A, @R1
$069a: [ 12 a7 ] JB0  $06a7
$069c: [ fd    ] MOV  A, R5
$069d: [ 03 01 ] ADD  A, #$01
$069f: [ f6 a9 ] JC   $06a9
$06a1: [ bb 01 ] MOV  R3, #$01      ; 1 milli
$06a3: [ 74 b6 ] CALL delay_millis
$06a5: [ c4 90 ] JMP  $0690
$06a7: [ 74 f3 ] CALL $03f3
$06a9: [ be 00 ] MOV  R6, #$00
$06ab: [ b8 43 ] MOV  R0, #$43
$06ad: [ fd    ] MOV  A, R5
$06ae: [ 53 07 ] ANL  A, #$07
$06b0: [ a0    ] MOV  @R0, A
$06b1: [ 97    ] CLR  C
$06b2: [ 04 86 ] JMP  $0086
```

???????????????????????
-----------------------
```
$06b4: [ 34 79 ] CALL clear_display
$06b6: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$06b8: [ b0 73 ] MOV  @R0, #$73   ; ... to 'P'
$06ba: [ b8 38 ] MOV  R0, #$38
$06bc: [ be 02 ] MOV  R6, #$02
$06be: [ 34 83 ] CALL $0183
$06c0: [ 83    ] RET
$06c1: [ 34 79 ] CALL clear_display
$06c3: [ bb ff ] MOV  R3, #$ff    ; 255 millis
$06c5: [ 74 b6 ] CALL delay_millis
$06c7: [ bb ff ] MOV  R3, #$ff    ; 255 millis
$06c9: [ 74 b6 ] CALL delay_millis
$06cb: [ b9 09 ] MOV  R1, #$09
$06cd: [ bc 0a ] MOV  R4, #$0a
$06cf: [ 23 00 ] MOV  A, #$00
$06d1: [ 69    ] ADD  A, R1
$06d2: [ e3    ] MOVP3 A, @A
$06d3: [ bb 06 ] MOV  R3, #$06
$06d5: [ b8 25 ] MOV  R0, #$25
$06d7: [ a0    ] MOV  @R0, A
$06d8: [ c8    ] DEC  R0
$06d9: [ eb d7 ] DJNZ R3, $06d7
$06db: [ bb ff ] MOV  R3, #$ff     ; 255 millis
$06dd: [ 74 b6 ] CALL delay_millis
$06df: [ bb ff ] MOV  R3, #$ff     ; 255 millis
$06e1: [ 74 b6 ] CALL delay_millis
$06e3: [ c9    ] DEC  R1
$06e4: [ ec cf ] DJNZ R4, $06cf
$06e6: [ be 00 ] MOV  R6, #$00
$06e8: [ 04 86 ] JMP  $0086


$06ea: [ be 00 ] MOV  R6, #$00
$06ec: [ 85    ] CLR  F0
$06ed: [ 95    ] CPL  F0
$06ee: [ b8 36 ] MOV  R0, #$36
$06f0: [ 34 83 ] CALL $0183
$06f2: [ b8 37 ] MOV  R0, #$37
$06f4: [ be 02 ] MOV  R6, #$02
$06f6: [ 34 83 ] CALL $0183
$06f8: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$06fa: [ b0 77 ] MOV  @R0, #$77   ; ... to 'A'
$06fc: [ 83    ] RET


$06fd: [ 34 79 ] CALL clear_display
$06ff: [ 97    ] CLR  C
$0700: [ 85    ] CLR  F0
$0701: [ a5    ] CLR  F1
$0702: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$0704: [ b0 71 ] MOV  @R0, #$71   ; ... to 'F'
$0706: [ b8 04 ] MOV  R0, #$04
$0708: [ be 02 ] MOV  R6, #$02
$070a: [ 34 83 ] CALL $0183
$070c: [ 04 86 ] JMP  $0086


$070e: [ 34 79 ] CALL clear_display
$0710: [ 74 f3 ] CALL $03f3
$0712: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$0714: [ b0 1c ] MOV  @R0, #$1c   ; .. to 'CAL' symbol
$0716: [ 27    ] CLR  A
$0717: [ 74 0a ] CALL $030a
$0719: [ 99 bf ] ANL  P1, #$bf
$071b: [ 89 80 ] ORL  P1, #$80
$071d: [ b8 3a ] MOV  R0, #$3a
$071f: [ f0    ] MOV  A, @R0
$0720: [ 12 78 ] JB0  $0778
$0722: [ 09    ] IN   A, P1
$0723: [ f2 1d ] JB7  $071d
$0725: [ 27    ] CLR  A
$0726: [ a9    ] MOV  R1, A
$0727: [ ad    ] MOV  R5, A
$0728: [ b8 08 ] MOV  R0, #$08
$072a: [ f4 7c ] CALL $077c
$072c: [ 76 71 ] JF1  $0771
$072e: [ f4 d0 ] CALL $07d0
$0730: [ f8    ] MOV  A, R0
$0731: [ d3 08 ] XRL  A, #$08
$0733: [ 96 2a ] JNZ  $072a
$0735: [ b8 3a ] MOV  R0, #$3a
$0737: [ f0    ] MOV  A, @R0
$0738: [ 12 78 ] JB0  $0778
$073a: [ b8 08 ] MOV  R0, #$08
$073c: [ f9    ] MOV  A, R1
$073d: [ 96 2a ] JNZ  $072a
$073f: [ b8 3b ] MOV  R0, #$3b
$0741: [ f0    ] MOV  A, @R0
$0742: [ f2 46 ] JB7  $0746
$0744: [ e4 64 ] JMP  $0764
$0746: [ 23 80 ] MOV  A, #$80
$0748: [ 74 0a ] CALL $030a
$074a: [ 27    ] CLR  A
$074b: [ a9    ] MOV  R1, A
$074c: [ ad    ] MOV  R5, A
$074d: [ b8 08 ] MOV  R0, #$08
$074f: [ f4 7c ] CALL $077c
$0751: [ 76 74 ] JF1  $0774
$0753: [ f4 d0 ] CALL $07d0
$0755: [ f8    ] MOV  A, R0
$0756: [ d3 08 ] XRL  A, #$08
$0758: [ 96 4f ] JNZ  $074f
$075a: [ b8 3a ] MOV  R0, #$3a
$075c: [ f0    ] MOV  A, @R0
$075d: [ 12 78 ] JB0  $0778
$075f: [ b8 08 ] MOV  R0, #$08
$0761: [ f9    ] MOV  A, R1
$0762: [ 96 4f ] JNZ  $074f
$0764: [ 34 79 ] CALL clear_display
$0766: [ b8 20 ] MOV  R0, #$20    ; Set left-most digit...
$0768: [ b0 5c ] MOV  @R0, #$5c   ; ... to 'o'              TODO: where is 'o' used?
$076a: [ 97    ] CLR  C
$076b: [ 85    ] CLR  F0
$076c: [ a5    ] CLR  F1
$076d: [ be 00 ] MOV  R6, #$00
$076f: [ 04 86 ] JMP  $0086
$0771: [ f9    ] MOV  A, R1
$0772: [ c6 1d ] JZ   $071d
$0774: [ bc 07 ] MOV  R4, #$07
$0776: [ c4 fd ] JMP  $06fd
$0778: [ 74 f3 ] CALL $03f3
$077a: [ e4 64 ] JMP  $0764


???????????????????????
-----------------------
$077c: [ 27    ] CLR  A
$077d: [ aa    ] MOV  R2, A
$077e: [ ab    ] MOV  R3, A
$077f: [ 97    ] CLR  C
$0780: [ 85    ] CLR  F0
$0781: [ a5    ] CLR  F1
$0782: [ 89 80 ] ORL  P1, #$80
$0784: [ 09    ] IN   A, P1
$0785: [ f2 93 ] JB7  $0793
$0787: [ fa    ] MOV  A, R2
$0788: [ 03 01 ] ADD  A, #$01
$078a: [ f6 c5 ] JC   $07c5
$078c: [ aa    ] MOV  R2, A
$078d: [ bc 64 ] MOV  R4, #$64 ; Delay for 100*20 micros == 2 millis
$078f: [ f4 c7 ] CALL delay_20micros
$0791: [ e4 84 ] JMP  $0784
$0793: [ bc c8 ] MOV  R4, #$c8; Delay for 200*20 micros == 4 millis
$0795: [ f4 c7 ] CALL delay_20micros
$0797: [ 09    ] IN   A, P1
$0798: [ f2 9c ] JB7  $079c
$079a: [ e4 87 ] JMP  $0787
$079c: [ bc 64 ] MOV  R4, #$64 ; Delay for 100*20 micros == 2 millis
$079e: [ f4 c7 ] CALL delay_20micros
$07a0: [ 09    ] IN   A, P1
$07a1: [ f2 bd ] JB7  $07bd
$07a3: [ bc c8 ] MOV  R4, #$c8 ; Delay for 200*20 micros == 4 millis
$07a5: [ f4 c7 ] CALL delay_20micros
$07a7: [ 09    ] IN   A, P1
$07a8: [ f2 bd ] JB7  $07bd
$07aa: [ fa    ] MOV  A, R2
$07ab: [ 03 fd ] ADD  A, #$fd
$07ad: [ e6 c5 ] JNC  $07c5
$07af: [ 97    ] CLR  C
$07b0: [ fb    ] MOV  A, R3
$07b1: [ 03 fd ] ADD  A, #$fd
$07b3: [ e6 c5 ] JNC  $07c5
$07b5: [ 97    ] CLR  C
$07b6: [ fa    ] MOV  A, R2
$07b7: [ 37    ] CPL  A
$07b8: [ 6b    ] ADD  A, R3
$07b9: [ e6 bc ] JNC  $07bc
$07bb: [ 95    ] CPL  F0
$07bc: [ 83    ] RET
$07bd: [ fb    ] MOV  A, R3
$07be: [ 03 01 ] ADD  A, #$01
$07c0: [ f6 c5 ] JC   $07c5
$07c2: [ ab    ] MOV  R3, A
$07c3: [ e4 9c ] JMP  $079c
$07c5: [ b5    ] CPL  F1
$07c6: [ 83    ] RET

### Delay for a multiple of 20 micros
Inputs:
- R4: number of 20 micros delays
```
delay_20micros:
$07c7: [ 00    ] NOP
$07c8: [ 00    ] NOP
$07c9: [ 00    ] NOP
$07ca: [ 00    ] NOP
$07cb: [ 00    ] NOP
$07cc: [ 00    ] NOP
$07cd: [ ec c7 ] DJNZ R4, delay_20micros
$07cf: [ 83    ] RET

???????????????????????
-----------------------
$07d0: [ 97    ] CLR  C
$07d1: [ b6 de ] JF0  $07de
$07d3: [ fd    ] MOV  A, R5
$07d4: [ 67    ] RRC  A
$07d5: [ ad    ] MOV  R5, A
$07d6: [ e8 dd ] DJNZ R0, $07dd
$07d8: [ fd    ] MOV  A, R5
$07d9: [ 91    ] MOVX @R1, A
$07da: [ c9    ] DEC  R1
$07db: [ b8 08 ] MOV  R0, #$08
$07dd: [ 83    ] RET
$07de: [ a7    ] CPL  C
$07df: [ e4 d3 ] JMP  $07d3


???????????????????????
-----------------------
$07e1: [ 23 df ] MOV  A, #$df
$07e3: [ 74 33 ] CALL $0333
$07e5: [ 83    ] RET


???????????????????????
-----------------------
$07e6: [ 23 20 ] MOV  A, #$20
$07e8: [ 74 2e ] CALL $032e
$07ea: [ 83    ] RET


???????????????????????
-----------------------
$07eb: [ ab    ] MOV  R3, A
$07ec: [ 23 00 ] MOV  A, #$00
$07ee: [ 6b    ] ADD  A, R3
$07ef: [ e3    ] MOVP3 A, @A
$07f0: [ ab    ] MOV  R3, A
$07f1: [ fe    ] MOV  A, R6
$07f2: [ 03 20 ] ADD  A, #$20
$07f4: [ 17    ] INC  A
$07f5: [ a8    ] MOV  R0, A
$07f6: [ fb    ] MOV  A, R3
$07f7: [ a0    ] MOV  @R0, A
$07f8: [ 1e    ] INC  R6
$07f9: [ 83    ] RET


; Never executed?
$07fa: [ 6d    ] ADD  A, R5
$07fb: [ d6    ] .DB  $d6
$07fc: [ 78    ] ADDC A, R0
$07fd: [ eb 00 ] DJNZ R3, $0700
$07ff: [ 02    ] OUTL BUS, A