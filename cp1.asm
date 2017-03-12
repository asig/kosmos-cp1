; ROM Listing
; ===========
;
; This is the reverse engineered and fully commented ROM listing of the EPROM
; contents of the Intel 8049 used in the Kosmos CP1 experimental computer.
;
; To recreate the EEPROM content, you can use this 8048 cross assembler:
; https://sourceforge.net/projects/asm48/
;
; Code (C) 1983, Franckh'sche Verlagshandlung, W. Keller & Co., Stuttgart, Germany
; Comments (C) 2017, Andreas Signer <asigner@gmail.com>
;
; 8049 Memory Map
; ---------------
; 0x00 - 0x07: Register Bank 0 (R0 - R7)
; 0x08 - 0x17: Stack (8 levels)
; 0x18 - 0x1f: Register Bank 1 (R0 - R7)
;
; 0x20 - 0x25: 6 Digit "Video RAM"
; 0x26: ?
; 0x27 - 0x2b: decoded entered digits. most significant digit at 0x27
;
; 0x30 - 0x35: last interrupt's key presses per line
; 0x36: Accu MSB
; 0x37: Accu LSB
; 0x38: PC
; 0x39: Last byte read from external RAM
; 0x3a: status register
;      bit 7: ?
;      bit 6: IO on 8155s enabled
;      bit 5: result of last comparison
;      bit 4: accessed RAM extension
;      bit 3: Memory full
;      bit 2: Address overflow
;      bit 1: STEP pressed
;      bit 0: STP pressed
; 0x3b: memory size
; 0x3c - 0x3e: buffer for atoi
; 0x3f: Last byte written to port 1
; 0x40: Last byte written to port 2
; 0x41: Last byte written to port 4
; 0x42: Last byte written to port 5
; 0x43: Reaction test delay in secs
; 0x44: last key pressed
;
; Global Register usage:
; ----------------------
; R0: Address pointer
; R1: Address pointer
; R2: ????
; R3: ????
; R4: ????
; R5: ????
; R6: Number of digits entered
; R7: current input/output position

; Listing
; -------

                .equ VRAM,         $20
                .equ KBUF,         $27
                .equ ACCU,         $36
                .equ ACCU_LSB,     $37
                .equ PC,           $38
                .equ STATUS,       $3a
                .equ MEM_SIZE,     $3b
                .equ LAST_BYTE_P1, $3f
                .equ LAST_BYTE_P2, $40
                .equ LAST_BYTE_P4, $41
                .equ LAST_BYTE_P5, $42

                .org $0
                JMP  init    ; Reset entry point: Execution starts at 0.
                NOP
                RETR         ; Interrupt entry point
                NOP
                NOP
                NOP
                JMP  timer   ; Timer/Counter entry point
                NOP

; JMPP Jump-Table for key presses
keypress_jmpp_table:
                .db <wait_key                ; ??? this is pointing to the wait_key function
                .db <digit_handler           ; key '0'
                .db <digit_handler           ; key '1'
                .db <digit_handler           ; key '2'
                .db <digit_handler           ; key '3'
                .db <digit_handler           ; key '4'
                .db <digit_handler           ; key '5'
                .db <digit_handler           ; key '6'
                .db <digit_handler           ; key '7'
                .db <digit_handler           ; key '8'
                .db <digit_handler           ; key '9'
                .db <out_handler_trampoline  ; key 'OUT'
                .db <inp_handler             ; key 'INP'
                .db <cal_handler_trampoline  ; key 'CAL'
                .db <step_handler_trampoline ; key 'STEP'
                .db <wait_key                ; key 'STP'
                .db <run_handler_trampoline  ; key 'RUN'
                .db <cas_handler_trampoline  ; key 'CAS'
                .db <clr_handler             ; key 'CLR'
                .db <pc_handler              ; key 'PC'
                .db <acc_handler             ; key 'ACC'

run_handler_trampoline:  JMP  run_handler
out_handler_trampoline:  JMP  out_handler
cal_handler_trampoline:  JMP  cal_handler
step_handler_trampoline: JMP  step_handler
cas_handler_trampoline:  JMP  cas_handler

; ### Initialization code
init:
                MOV  R0, #$1e
                MOV  @R0, #$00  ; Set R6' to 0
                MOV  R0, #$20   ; Clear 0x28 ...
                MOV  R3, #$28   ;
init1:
                MOV  @R0, #$00  ;
                INC  R0         ;
                DJNZ R3, init1  ; ... bytes of RAM
                MOV  R6, #$00
                MOV  R7, #$00
                MOV  R0, #$1a
                MOV  @R0, #$01  ; Set R1' to 1
                CALL print_pc
                ANL  P2, #$cf   ; P2 &= 1100 1111 --> 8155 /CE == 0, /CE == 0
                ANL  P2, #$bf   ; P2 &= 1011 1111 --> 8155 /Reset == 0
                ANL  P2, #$ef   ; P2 &= 1110 1111 --> 8155 /CE == 0: Enable "internal" 8155
                ORL  P2, #$20   ; P2 |= 0010 0000 --> /CE == 1: Disable CP3 8155
                MOV  R0, #$00
                MOV  A, #$0f
                MOVX @R0, A     ; Write 0000 1111 to command register: Port A and B are OUTPUT, Port C is ALT2
                MOV  R0, #$02
                MOV  A, #$ff
                MOVX @R0, A     ; Write 1111 1111 to Port B
                ANL  P2, #$df   ; P2 &= 1101 1111 --> /CE == 0: Enable CP3 8155
                ORL  P2, #$10   ; P2 |= 0001 0000 --> 8155 /CE == 1: Disable "internal" 8155
                MOV  R0, #$00
                MOV  A, #$0d
                MOVX @R0, A     ; Write 0000 1101 to command register: Port A is OUTPUT, Port B is INPUT, Port C is ALT2
                MOV  R0, #$01
                MOV  A, #$ff
                MOVX @R0, A     ; Write 1111 1111 to Port A
                MOV  R0, #$03
                MOVX @R0, A     ; Write 1111 1111 to Port A

; Test whether extension is available:
                ANL  P2, #$5f   ; P2 &= 0101 1111 --> /CE == 0, IO == 0
                ORL  P2, #$10   ; P2 |= 0001 0000 --> 8155 /CE == 1
                MOV  R0, #$13
                MOV  A, #$5a
                MOVX @R0, A     ; Store $5a at $13
                CLR  A
                MOVX A, @R0     ; Read again from $13
                XRL  A, #$5a    ; Test if it is what we wrote?
                MOV  R0, #MEM_SIZE   ; Load address of "memory size"
                JZ   cp3_present
                MOV  @R0, #$7f  ; 127 words of memory
init_cont:
                MOV  R3, #$04   ; 4 ports in total
                MOV  R0, #LAST_BYTE_P1   ; load address of "last byte written to port 1"
                MOV  A, #$ff    ; set all bits
portloop:
                MOV  @R0, A     ; store it in "last byte written to port 1"
                INC  R0         ; move to next port byte
                DJNZ R3, portloop ; and continue while there are still ports left.
                ANL  P2, #$4f     ; P2 &= 0100 1111 --> 8155 /CE == 0, /CE == 0, IO == 0: Enable both 8155 in Memory mode
                CALL clear_ram
                EN   TCNTI        ; enable Timer/Counter interrupts
                STRT T            ; and start the timer

wait_key:
                MOV  R0, #$1e    ; Wait for key press: 0x1e is R6', used in interrupt
                MOV  A, @R0      ; check for key
                JZ   wait_key    ; no key pressed, continue waiting
                MOV  R1, #$44    ;
                MOV  @R1, A      ; Store pressed key in 0x44
                MOV  A, <keypress_jmpp_table ; Load jump table base
                ADD  A, @R0      ; Add the currently pressed key
                MOV  @R0, #$00   ; clear R6'
                JMPP @A          ; jump to key handler

cp3_present:
                MOV  @R0, #$ff   ; 255 words of memory
                JMP  init_cont

pc_handler:
                MOV  A, R6       ; Load # of digits entered
                JZ   zero_digits ; jump if zero
                XRL  A, #$03     ; is it 3 digits?
                JNZ  error_f001  ; error if not
                MOV  R0, #KBUF   ; Load address of 1st decoded digit
                CALL convert_and_check_address
                MOV  R0, #STATUS    ; Load status byte...
                MOV  A, @R0      ; ... to A
                JB2  clear_addr_ovfl_error_f004_trampoline
                MOV  R0, #$20    ; Set left-most digit...
                MOV  @R0, #$73   ; ... to 'P'
                MOV  R0, #KBUF   ; address of 1st decoded digit
                MOV  R2, #$02    ; 3 digits to convert
                CALL digits_to_number
                MOV  R0, #PC  ; load address of PC
                MOV  @R0, A      ; store entered number in PC
                MOV  R6, #$00    ; reset # of entered digits
                JMP  wait_key


clear_addr_ovfl_error_f004_trampoline:
                JMP  clear_addr_ovfl_error_f004

zero_digits:
                CALL print_pc
                JMP  wait_key

acc_handler:
                CALL print_accu
                JMP  wait_key

clr_handler:
                CALL clear_display  ; Clear display
                MOV  R6, #$00       ; Clear entered digits
                JMP  wait_key

error_f001:
                MOV  R4, #$01
                JMP  show_error     ; F-001

first_digit:
                CALL clear_display
                JMP  digit_handler_cont

digit_handler:
                MOV  A, R6          ; load # of inputted keys
                JZ   first_digit    ; if it's the first key, clear screen first
                CLR  C              ; clear carry
                ADD  A, #$fb        ; add 251 (== -5 % 256) to check if we entered more than 5 digits
                JC   error_f001     ; jump if >5 digits.
digit_handler_cont:
                MOV  A, R6          ; load # of inputted keys
                ADD  A, #KBUF       ; add base address of decoded keys
                MOV  R0, A          ; and load this address
                MOV  R1, #$44       ; load address of "last key pressed"
                MOV  A, @R1         ; load last key pressed
                DEC  A              ; subtract 1 ('0' is keycode 1, '1' is keycode 2, ... see jump table)
                MOV  @R0, A         ; and store it in the number area
                MOV  R2, A          ; move it to R2 ...
                CALL append_digit   ; ... and print it out.
                JMP  wait_key       ;

clear_addr_ovfl_error_f004_trampoline2:
                JMP  clear_addr_ovfl_error_f004

inp_not3_trampoline:
                JMP  inp_not3

inp_handler:
                MOV  A, R6             ; Load # of entered digits
                XRL  A, #$03           ; is it 3?
                JNZ  inp_not3_trampoline ; Jump if not.
                MOV  A, #$f7           ; 1111 0111: "Memory full" bit ...
                CALL clear_status_bits ; ... is cleared from status
                MOV  R0, #KBUF         ; load address of entered digits
                CALL convert_and_check_address
                MOV  R0, #STATUS       ; load ...
                MOV  A, @R0            ; status byte
                JB2  clear_addr_ovfl_error_f004_trampoline2
                MOV  R0, #KBUF         ; address of 1st decoded digit
                MOV  R2, #$02          ; 3 digits to convert
                CALL digits_to_number  ; convert them
                MOV  R7, A             ; set current I/O pos to entered addres.
inp_done:
                MOV  R6, #$00    ; Reset # of entered digits
                MOV  R0, #$20    ; Set left-most digit...
                MOV  @R0, #$79   ; ... to 'E'
                JMP  wait_key

inp_not3:
                MOV  A, R6       ; Load # of entered digits
                XRL  A, #$05     ; is it 5?
                JNZ  error_f001_trampoline ; Jump if not.
                MOV  R0, #STATUS ; load status byte...
                MOV  A, @R0      ; ... to A
                JB3  error_f004  ; bail out if memory was full
                MOV  R0, #KBUF   ; load address of entered digits
                MOV  R2, #$01    ; 2 digits to convert
                CALL digits_to_number
                CLR  C           ; clear carry
                MOV  R1, A       ; save number in R1
                ADD  A, #$e7     ; add (256 - 25) to check for max opcode
                JC   error_f004  ; if carry is set, value was > 25 -> invalid opcode
                MOV  R0, #$29    ; load address if 3rd entered digit
                MOV  R2, #$02    ; 3 digits to convert
                CALL digits_to_number
                JF0  error_f004  ; report F-004 if there was an overflow
                MOV  R4, A       ; Store operand in R4
                MOV  A, R1       ; Move opcode ...
                MOV  R2, A       ; ... to R2
                MOV  A, R7       ; move R7 (current I/O pointer) to A
                CALL compute_effective_address
                MOV  A, R2       ; Store opcode in...
                MOVX @R1, A      ; ... current pos' MSB
                INC  R1          ; move to LSB
                MOV  A, R4       ; Store operand in...
                MOVX @R1, A      ; ... current pos' LSB
                MOV  R0, #MEM_SIZE ; Load mem-size address
                MOV  A, R7       ; compare current i/o pos ...
                XRL  A, @R0      ; ... with mem-size
                JZ   inp_mark_mem_full ; if it's the same, mark "memory full"
                INC  R7          ; otherwise, move to the next position
                JMP  inp_done
inp_mark_mem_full:
                MOV  A, #$08 ; 0000 1000: "memory full" bit
                CALL set_status_bits
                JMP  inp_done

error_f001_trampoline:
                JMP  error_f001

clear_addr_ovfl_error_f004:
                MOV  A, #$fb           ; 1111 1011
                CALL clear_status_bits ; clear "address overflow" bit

error_f004:
                MOV  R4, #$04
                JMP  show_error     ; F-004


; Convert 3 single digits to a number
; -----------------------------------
; Converts single digits to a number, e.g. from 1,3,7 to 137
; Inputs:
; - R0: Address of most significant digit
; - R2: Number of digits - 1, must be <= 2
; Outputs
; - A: the resulting number
; - F0: Set if there was an overflow
digits_to_number:
                MOV  A, R1     ; save R1 in...
                MOV  R3, A     ; ... R3
                MOV  R1, #$3c  ; Destination Adresse: scratch area
                MOV  R4, #$03  ; Size: 3
                MOV  A, @R0    ; Copy from (R0)
                MOV  @R1, A    ; to (R1)
                INC  R0        ;
                INC  R1        ;
                DJNZ R4, $0154 ; Continue while not 3 bytes copied
                MOV  A, R3     ;
                MOV  R1, A     ; Restore R1
                MOV  R0, #$3c  ; Load address of first digit
                MOV  A, @R0    ; Load digit
                CLR  C         ; Clear carry...
                CLR  F0        ; ... and F0. No overflow yet
                RLC  A         ; * 2
                JC   dtn_overflow
                RLC  A         ; * 2
                JC   dtn_overflow
                ADD  A, @R0    ; + digit
                JC   dtn_overflow
                RLC  A         ; * 2. A is now: (@R0*2*2+@R0)*2 == 10*@R0
                JC   dtn_overflow
                INC  R0        ; move to next digit
                ADD  A, @R0    ; add next digit to A
                JC   dtn_overflow
                MOV  @R0, A    ; current digit = 10 * prev digit + current digit
                DJNZ R2, $0161 ; continue loop while we have digits left.
                MOV  A, @R0    ; Load final result to A
                RET            ; Return
dtn_overflow:
                CPL  F0        ; Set F0 to mark overflow
                CLR  C         ; Clear carry
                RET            ; Return


; Clear display
; --------------
;
clear_display:
                MOV  R2, #$06  ; 6 digits to clear
                MOV  R0, #$20  ; load address of "Video RAM"
cd_loop:
                MOV  @R0, #$00 ; clear segments of that Video RAM address
                INC  R0        ; move to next address
                DJNZ R2, cd_loop ; continue while there's more to clear
                RET

; Print one byte
; --------------
; Print a value from internal RAM.
;
; Input:
;   - R0: Address of value to be printed
;   - R6: Offset into "Video RAM"
;   - F0: If set, don't print hundreds
;
; Output:
;   - R6: Set to Zero
print_value:
                MOV  A, @R0       ; Load value to print
                CALL itoa
                JF0  no_hundreds
                CALL print_digit
print_tens_and_ones:
                MOV  A, R4        ; Load 10s
                CALL print_digit  ; Print them
                MOV  A, R2        ; Load 1s
                CALL print_digit  ; Print
                MOV  R6, #$00     ; Set digits entered to 0 again. ??????
                RET
no_hundreds:
                CLR  F0
                JMP  print_tens_and_ones

; Fetch and print external RAM
; ----------------------------
; Read 2 bytes from external RAM and print it.
;
; Input:
;   - R1: External address
;
; Output:
;   - $39: Content of last byte read
fetch_and_print_ram:
                MOVX A, @R1      ; Read from external RAM
                MOV  R0, #$39    ; Address to store value in is 0x39
                MOV  @R0, A      ; Store value
                MOV  R6, #$00    ; print "high byte": start at 0 in display RAM
                CLR  F0          ; and set...
                CPL  F0          ; ... F0: "no hundreds" for print
                CALL print_value ; print it
                INC  R1          ; read next address
                MOVX A, @R1      ; from external RAM
                MOV  R0, #$39    ; store it...
                MOV  @R0, A      ; ... in 0x39
                MOV  R6, #$02    ; print "low byte": start at 2
                CALL print_value ; print value
                RET

; Append digit to Video RAM
; -------------------------
; Shift number in Video RAM to the left and append 1 digit.
;
; Input:
;   - R2: Digit to append
;   - R6: Number of digits shown (not used)
;
; Output:
;   - R6: Number of digits shown
append_digit:
                MOV  R0, #$21    ; Target address: 2nd digit of Video RAM
                MOV  R1, #$22    ; Source address: 3nd digit of Video RAM
                MOV  R3, #$04    ; 4 digits to copy
                MOV  A, @R1      ; move from source
                MOV  @R0, A      ; to target
                INC  R0          ; increment target pointer
                INC  R1          ; increment source pointer
                DJNZ R3, $01b0   ; continue while there are digits left
                MOV  A, #$00     ; Move R2...
                ADD  A, R2       ; ... to A
                MOVP3 A, @A      ; Load segments for that digit ...
                MOV  R0, #$25    ; ... and store it...
                MOV  @R0, A      ; ... in the right-most digit
                INC  R6          ; increment digits entered.
                RET


cas_stop_pressed:
                CLR  F0
                CALL clear_access_ram_extension_status_bit
                JMP  save_done

cas_handler:
                CALL clear_display
                CALL clear_access_ram_extension_status_bit
                MOV  R0, #$20      ; Set left-most digit...
                MOV  @R0, #$23     ; .. to 'CAS' symbol
                ORL  P1, #$c0      ; P1 |= 1100 0000 --> CassData == 1, CassWR == 1
                MOV  R0, #STATUS
                MOV  R4, #$3c      ; Loop max 60 times
                MOV  R3, #$fa      ; Delay for...
                CALL delay_millis  ; ... 250 millis
                MOV  A, @R0        ; check content of #STATUS
                JB0  cas_stop_pressed  ; if bit 0 is set
                DJNZ R4, $01d2     ; wait for max. 15 secs
                CLR  A             ; Address 0
                CALL compute_effective_address; Compute effective address to select RAM chip (internal 8155)
                CALL save_memory
                JF0  cas_stop_pressed
                MOV  R0, #MEM_SIZE      ; Load memory size...
                MOV  A, @R0        ; ... to A
                JB7  cas_block_2   ; if >128, save 2nd half as well
                JMP  save_done     ;
cas_block_2:
                MOV  A, #$ff       ; Load address > 128
                CALL compute_effective_address; Compute effective address to select RAM chip (CP3's 8155)
                CALL save_memory
                JF0  cas_stop_pressed_trampoline

save_done:
                ANL  P1, #$7f
                CALL clear_display
                MOV  R0, #$20    ; Set left-most digit...
                MOV  @R0, #$63   ; ... to 'ᵒ'
                MOV  R6, #$00
                JMP  wait_key
cas_stop_pressed_trampoline:
                JMP  cas_stop_pressed

out_handler:
                MOV  A, #$f7 ; 1111 0111 -> A
                CALL clear_status_bits
                MOV  A, R6        ; Load number of digits that were entered
                JZ   no_digits    ; jump if zero
                XRL  A, #$03      ; is it 3?
                JNZ  not_3_digits ; go to not_3_digits if not
                MOV  R0, #KBUF
                CALL convert_and_check_address
                MOV  R0, #STATUS     ; load status byte ...
                MOV  A, @R0       ; ... to A
                JB2  $023a
                MOV  R0, #KBUF    ; address of 1st decoded digit
                MOV  R2, #$02     ; 3 digits to convert
                CALL digits_to_number
                MOV  R7, A        ; Save A in R7 (last in/out position)
out_print:
                CALL clear_display
                MOV  A, R7       ; Restore A
                CALL compute_effective_address
                CALL fetch_and_print_ram
print_C:
                MOV  R0, #$20    ; Set left-most digit...
                MOV  @R0, #$39   ; ... to 'C'
                JMP  wait_key
no_digits:
                CLR  C
                MOV  A, R7      ; Move current in/out position to A
                ADD  A, #$01    ; increment
                MOV  R7, A      ; and store it in in/out position
                JC   out_overflow
                MOV  R0, #MEM_SIZE   ; load memory size...
                MOV  A, @R0     ; ... into A
                JB7  out_print  ; CP3 available, not need to check range
                MOV  A, R7      ; check if in/out position...
                JB7  out_overflow_low ; ... is >= 128
                JMP  out_print  ; otherwise, print it
out_overflow:
                MOV  R7, #$ff   ; set in/out pos to 255
out_f004:
                JMP  clear_addr_ovfl_error_f004
out_overflow_low:
                MOV  R7, #$7f   ; set in/out pos to 127
                JMP  out_f004   ; and report f-004
not_3_digits:
                MOV  A, R6      ; load number of digits entered
                XRL  A, #$01    ; is it 1?
                JZ   one_digit  ; yes
one_digit_f001:
                JMP  error_f001 ; otherwise, it's an error
one_digit:
                MOV  R0, #KBUF  ; Load address of decoded keys
                MOV  A, @R0     ; load first digit
                XRL  A, #$09    ; is it 9?
                JNZ  one_digit_f001 ; no, error F-001
                CALL clear_display
                MOV  R0, #$20    ; Set left-most digit...
                MOV  @R0, #$39   ; ... to 'C'
                MOV  R0, #$07    ; Load address 7 (== R7)
                MOV  R6, #$02    ; 3 digits to print
                CALL print_value 
                JMP  wait_key

; Timer/Counter interrupt
; -----------------------
; - R5: Mask used for keyboard reading and display addressing
; - R3: Internal scratch register: Loop vars, computations
timer:
                SEL  RB1        ; Switch to register bank 1
                XCH  A, R7      ; Restore A from last interrupt
                MOV  A, #$e0    ; Reset timer...
                MOV  T, A       ; ... to 0xe0 (== 224) --> 2560 micros per timer interrupt
                DJNZ R2, $026b  ;
                MOV  R1, #$25
                MOV  R5, #$fe   ; Init: current row selection mask
                MOV  R2, #$06   ; Init: 6 rows
                MOV  R4, #$35
                ORL  P2, #$a0   ; P2 |= 1010 0000 --> IO == 1, /CE == 1
                ANL  P2, #$ef   ; P2 &= 1110 1111 --> 8155 /CE == 0
                CLR  A          ;
                MOV  R0, #$01   ; Address Port A
                MOVX @R0, A     ; Write 0 -> Port A
                MOV  A, R5
                MOV  R0, #$03   ; Address Port C
                MOVX @R0, A     ; Write row selection -> Port C
                MOV  A, @R1
                MOV  R0, #$01
                MOVX @R0, A     ; (0x25) -> Port A; Write one character to display
                ORL  P2, #$0f   ; P2 |= 0000 1111: Prepare to read from bit 0 - 4
                MOVD A, P4      ; ????? write to lower nibble of port 2, then read from it? why not just read from it?
                CLR  C
                MOV  R3, #$04   ; Test (keypad) bits read: 4 bits in total
                RRC  A          ; shift lowest bit to carry
                JC   keymask_bit_set ; jump if set
                DJNZ R3, $0281  ; not set, carry on with loop.
                JMP  no_key_pressed ; no key pressed
keymask_bit_set:
                MOV  A, R2      ;
                DEC  A
                RL   A
                RL   A
                INC  A          ; A = 4 * (R2 - 1) + 1; R2 is '6 - physical row', i.e. R2 is 2..6
                XCH  A, R3      ; R3 = 4 * (R2 - 1) + 1; A = bit_num
                CPL  A          ; Compute two's complement...
                INC  A          ; ... of A --> A == -bit_num
                ADD  A, R3      ; A = 4 * (row - 1) + 1 - bit_num
                MOV  R6, A      ; Store A in R6 (== last key press)
                JB7  no_key_pressed ; Bit 7 Set? -> 2bc
                MOV  A, R4
                MOV  R0, A
                MOV  A, R6
                XRL  A, @R0     ; (R4) == R6 (same key pressed as during last interrupt?)
                JZ   key_still_pressed
                MOV  A, R6
                MOV  @R0, A        ; remember key press for next interrupt
                MOV  A, R6         ; compare pressed key...
                XRL  A, #$0f       ; ... with "STP".
                JZ   timer_stop_pressed; ; jump if equal
                MOV  A, R6         ; compare pressed key...
                XRL  A, #$0e       ; ... with "STEP".
                JZ   step_pressed  ; jump if equal

move_to_next_row:
                MOV  A, R5         ; Load row selection mask
                RL   A             ; rotate to next position
                MOV  R5, A         ; Update mask
                DEC  R1            ; Move to previous Video RAM pos
                DEC  R4            ; Move to previous 'key pressed state' position
                MOV  R0, #STATUS      ; Load status byte...
                MOV  A, @R0        ; ... to A
                JB4  enable_ram_extension
                JB6  enable_io
                ANL  P2, #$7f ; otherwise, disable IO: P2 &= 0111 1111 --> IO == 0
end_of_intr:
                XCH  A, R7    ; save A for next interrupt
                SEL  RB0      ; switch back to register bank 0
                EN   TCNTI    ; enable interrupts again
                RETR          ; and continue execution

key_still_pressed:
                MOV  R6, #$00 ; don't register the key press, it wasn't released in between
                JMP  move_to_next_row

no_key_pressed:
                MOV  A, R4
                MOV  R0, A
                MOV  @R0, #$00 ; clear "last keypress state"
                JMP  move_to_next_row

timer_stop_pressed:
                MOV  A, #$01  ; mask 0000 0001: 'STP pressed'
                CALL set_status_bits
                JMP  move_to_next_row

step_pressed:
                MOV  A, #$02  ; mask 0000 0010  'STEP pressed'
                CALL set_status_bits
                JMP  move_to_next_row

enable_ram_extension:
                ANL  P2, #$df  ; P2 &= 1101 1111 --> /CE == 0
                ORL  P2, #$10  ; P2 |= 0001 0000 --> 8155 /CE == 1
                JMP  $02b0

enable_io:
                ORL  P2, #$80  ; P2 |= 1000 0000 --> IO == 1
                JMP  $02b4
                RET


; Convert Integer value to single digits ("itoa")
; -----------------------------------------------
; Converts the value in A to 3 decimal digits.
;
; Input:
;   - A: Value to convert
;
; Output:
;   - A: hundreds
;   - R2: ones
;   - R4: tens
itoa:
                CLR  C
                MOV  R4, #$00
                ADD  A, #$f6    ; Subtract 10 (== Add 246 mod 256)
                JNC  $02e4      ; Carry not set -> A was < 10
                CLR  C          ; reset carry
                INC  R4         ; Count tens
                JMP  $02dc      ; check again.
                ADD  A, #$0a    ; undo last subtract
                MOV  R2, A      ; Store lowest digit (ones) in R2
                CLR  C
                MOV  A, R4      ; Bring tens to Accumulator
                MOV  R4, #$00   ; Set hundreds to 0
                ADD  A, #$f6    ; same as above, but now effectively count hundreds
                JNC  $02f3      ; Carry not set -> A was < 10
                CLR  C          ; reset carry
                INC  R4         ; count hundreds
                JMP  $02eb      ; check again
                ADD  A, #$0a    ; undo last subtract
                XCH  A, R4      ; Store hundreds in A, tens in R4
                CLR  C
                RET

; DEAD CODE FROM HERE ON?
                NOP
                ORL  BUS, #$c4
                NOP
                NOP
                NOP
                NOP
                INC  R6


    .org $300
; Segment codes for digits 0 .. 9

                .db  $3f  ; Digit '0'
                .db  $06  ; Digit '1'
                .db  $5b  ; Digit '2'
                .db  $4f  ; Digit '3'
                .db  $66  ; Digit '4'
                .db  $6d  ; Digit '5'
                .db  $7d  ; Digit '6'
                .db  $27  ; Digit '7'
                .db  $7f  ; Digit '8'
                .db  $6f  ; Digit '9'


; Compute the effective address of a VM byte
; ------------------------------------------
; Compute the effective address, also selecting
; the correct 8155 chip
; Input:
;   - A: VM address (0 .. 255)
; Output:
;   - R1: 8155 address of the first byte
compute_effective_address:
                JB7  upper_half ; Jump if addr >= 128
                MOV  R3, A
                MOV  A, #$ef    ; mask 1110 1111 ; 'access RAM extension'
                CALL clear_status_bits
                ORL  P2, #$20   ; Disable extension RAM: P2 |= 0010 0000 --> /CE == 1
                ANL  P2, #$ef   ; Enable default RAM: P2 &= 1110 1111 --> 8155 /CE == 0
                MOV  A, R3
cea_end:
                RL   A          ; Effective Address = 2 * A
                MOV  R1, A
                RET
upper_half:
                ADD  A, #$80    ; bring address to lower half
                CLR  C          ; clear carry
                MOV  R3, A      ; store address in R3
                MOV  A, #$10    ; mask 0001 0000: 'access RAM extension'
                CALL set_status_bits
                ORL  P2, #$10   ; P2 |= 0001 0000 --> 8155 /CE == 1
                ANL  P2, #$df   ; P2 &= 1101 1111 --> /CE == 0
                MOV  A, R3
                JMP  cea_end

; Clear 256 bytes of external RAM (both main and CP3)
; ----------------------------------------------------
clear_ram:
                CLR  A          ; clear A
                MOV  R1, A      ; Use it as counter
cr_l:
                MOVX @R1, A     ; clear memory
                DJNZ R1, cr_l   ; continue until we're done.
                RET

; Set bits in the status byte
;-----------------------------
; Input:
;   - A: mask of bits to set in the status register
set_status_bits:
                MOV  R0, #STATUS
                ORL  A, @R0
                MOV  @R0, A
                RET

; Clear bits in the status byte
;------------------------------
; Input:
;   - A: mask of bits to keep in the status register
clear_status_bits:
                MOV  R0, #STATUS
                ANL  A, @R0
                MOV  @R0, A
                RET

; Convert and check address
; -------------------------
; Converts the 3 entered digits into a single address
; and checks that the address is in range.
; Inputs:
; - R0: Address of most significant digit
; Outputs
; - A: the resulting number
convert_and_check_address:
                MOV  R2, #$02         ; 3 digits to convert
                CALL digits_to_number ; convert to number
                JF0  caca_ovfl        ; overflow?
                JB7  caca_check_cp3   ; > 127? check memory size
caca_done:
                RET
caca_check_cp3:
                MOV  R0, #MEM_SIZE         ; load address of memory size
                MOV  A, @R0           ; load memory size
                JB7  caca_done        ; if > 127, all is fine
                JMP  caca_ovfl2       ; otherwise, mark invalid address
caca_ovfl:
                CLR  F0
caca_ovfl2:
                MOV  A, #$04 ; mask 0000 0100, "address overflow"
                CALL set_status_bits
                JMP  caca_done

; Checks whether address in A is valid
; ------------------------------------
; Inputs:
; - A: address to check
; Outputs:
; - Flag F0: set if address is invalid
check_address:
                CLR  F0           ; clear result flag
                JB7  ca_2         ; check memory if >= 128
ca_end:
                RET               ; everything < 128 is valid
ca_2:
                MOV  R0, #MEM_SIZE     ; load memory size...
                MOV  A, @R0       ; ... to A
                JB7  ca_end       ; address is valid if CP3 is installed
                CPL  F0           ; otherwise, set flag
                JMP  ca_end

; Check operand address and load effective address if valid
; -------------------------------------------------------------
; Inputs:
; - R1: address of operand
; Outputs
; - R1: Effective address
; - F0: Set if address is invalid
check_and_load_effective_address:
                MOVX A, @R1       ; Load operand to A ...
                MOV  R3, A        ; ... and to R3
                CALL check_address; check if it's valid
                JF0  cacea_end    ; bail out if not valid
                MOV  A, R3        ; Restore operand...
                CALL compute_effective_address ; ... and compute address
cacea_end:
                RET

; Check operand address and test operand and Accu
; -----------------------------------------------
; Inputs:
; - R1: address of operand
; Outputs
; - R0: Accu LSB address
; - R1: Effective address of LSB
; - F0: Set if address is invalid
; - A: Accu MSB or operand cell MSB
check_and_test_operand_and_accu:
                MOVX A, @R1      ; Load operand to A ...
                MOV  R3, A       ; ... and to R3
                CALL check_address ; check if it's valid
                JF0  cata_end    ; bail out if not valid
                MOV  A, R3       ; Restore operand...
                CALL compute_effective_address ; ... and compute address
                MOVX A, @R1      ; Load MSB
                JNZ  cata_end    ; bail out if it's not data (00.xxx)
                INC  R1          ; move to LSB
                MOV  R0, #ACCU   ; Load Accu MSB address
                MOV  A, @R0      ; Load Accu MSB
                JNZ  cata_end    ; bail out if it's not data (00.xxx)
                INC  R0          ; move to Accu LSB
cata_end:       RET

; Check single bit and set Accu
; -----------------------------
; Inputs:
; - A: Value to check for the bit
; - R4: Pin number (1 .. 8)
; Outputs:
; - A: 0 if bit was not set, 1 if it was set
check_bit:
                DJNZ R4, cb_rot ; bit at pos 0 yet? jump to rotate if not
                JB0  cb_bit_set
                CLR  A          ; bit not set -> clear A
                RET
cb_bit_set:     MOV  A, #$01    ; Store 1 in A
                RET
cb_rot:         RR   A          ; rotate to right
                JMP  check_bit  ; jump to check if bit is at correct pos

; Turn internal 8155 to IO mode, disable CP3 8155
; -----------------------------------------------
enable_internal_io:
                MOV  A, #$40    ; mask 0100 0000 ; 'enable IO'
                CALL set_status_bits
                MOV  A, #$ef    ; mask 1110 1111 ; 'access RAM extension'
                CALL clear_status_bits
                ORL  P2, #$a0   ; P2 |= 1010 0000 --> /CE == 1, IO == 1
                ANL  P2, #$ef   ; P2 &= 1110 1111 --> 8155 /CE == 0
                RET

; Turn CP3 8155 to IO mode, disable internal 8155
; -----------------------------------------------
enable_cp3_io:
                MOV  A, #$50    ; mask 0101 0000: 'enable IO' and 'access RAM extension'
                CALL set_status_bits
                ANL  P2, #$df   ; P2 &= 1101 1111 --> /CE == 0
                ORL  P2, #$90   ; P2 |= 1001 0000 --> 8155 /CE == 1, IO == 1
                RET

; Stores R0 in VM's Accumulator
; -----------------------------
; Inputs:
; - R0: Value to be stored
save_to_accu:
                MOV  R0, #ACCU
                MOV  @R0, #$00
                INC  R0
                MOV  @R0, A
                RET

; Write single bit to byte
; ------------------------
; Inputs:
; - A: the bit to write (1 .. 8)
; - R0: address of value to write (1 to set bit, 0 to clear bit)
; - R1: address of the byte where to write it
; Outputs:
; - A: the new byte value (with bit set/cleared)
write_bit:
                MOV  R2, A           ; Save bit
                CLR  C               ;
                CPL  C               ; Set carry
                CLR  A               ; Clear A
set_bit_rot:    RLC  A               ; Shift bit in
                DJNZ R2, set_bit_rot ; Continue shifting if necessary
                MOV  R2, A           ; Move bit mask to R2
                MOV  A, @R0          ; load value
                JZ   clear_bits      ; jump if bit is to be set to 0
                MOV  A, R2           ; Move mask to A
                ORL  A, @R1          ; OR with target byte
store_result:   MOV  @R1, A          ; Store in target byte
                RET
clear_bits:     MOV  A, R2           ; Move mask to A
                CPL  A               ; negate mask
                ANL  A, @R1          ; AND with target byte
                JMP  store_result

; Delay for n millis
; ------------------
; Inputs:
; - R3: number of millis to delay
delay_millis:
                MOV  R2, #$c8   ; cycles: 2
dm_l1:
                DJNZ R2, dm_l1  ; cycles: + 200 * 2
                DJNZ R3, delay_millis  ; cycles: R3 * 201 * 2
                RET             ; cycles: R3 * 201 * 2 + 1


; Save one block of RAM (256 bytes) to tape
;------------------------------------------
; Correct 8155 needs to be selected already.
; Note that RAM is not written sequentially! First, Address 0 is stored, then 254 ... 1 decending!
save_memory:
                MOV  R1, #$00     ; Load start address of RAM
byteloop:
                MOV  R0, #STATUS     ; Load status byte...
                MOV  A, @R0       ; ... to A
                JB0  stop_pressed_3; jump if stop is pressed.
                CLR  C
                MOV  R0, #$08     ; 8 bits to send
                MOVX A, @R1       ; Read a byte from RAM
bitloop:
                RRC  A            ; LSB -> Carry
                JNC  write_0      ; jump if 0 bit
                ANL  P1, #$7f     ; P1 &= 01111111 -> clear CassData
                MOV  R3, #$1e     ; 30 millis
                CALL delay_millis
                ORL  P1, #$80     ; P1 |= 10000000 -> set CassData
                MOV  R3, #$3c     ; 60 millis
                CALL delay_millis
save_cont:
                DJNZ R0, bitloop  ; next bit
                DJNZ R1, byteloop ; next byte
save_end:
                CLR  C
                RET
write_0:
                ANL  P1, #$7f     ; P1 &= 01111111 -> clear CassData
                MOV  R3, #$3c     ; 60 millis
                CALL delay_millis
                ORL  P1, #$80     ; P1 |= 10000000 -> set CassData
                MOV  R3, #$1e     ; 30 millis
                CALL delay_millis
                JMP  save_cont    ; back to main loop

stop_pressed_3:
                CLR  F0
                CPL  F0           ; Set F0 to mark that we were stopped
                MOV  A, #$fe      ; mask 1110 1111 ; 'access RAM extension'
                CALL clear_status_bits
                JMP  save_end


clear_access_ram_extension_status_bit:
                MOV  A, #$fe      ; mask 1110 1111 ; 'access RAM extension'
                CALL clear_status_bits
                RET

; ????? Dead code?
                NOP
                .db $ef, $54
                XCH  A, R0
                .db $d2, $36
                NOP
                EN   I

; Dispatch table for opcodes
opcode_jmpp_table:
                .db  <opcode_inv_trampoline  ; opcode 00
                .db  <opcode_HLT             ; opcode 01: HLT
                .db  <opcode_ANZ             ; opcode 02: ANZ
                .db  <opcode_VZG             ; opcode 03: VZG
                .db  <opcode_AKO             ; opcode 04: AKO
                .db  <opcode_LDA             ; opcode 05: LDA
                .db  <opcode_ABS             ; opcode 06: ABS
                .db  <opcode_ADD             ; opcode 07: ADD
                .db  <opcode_SUB_trampoline  ; opcode 08: SUB
                .db  <opcode_SPU             ; opcode 09: SPU
                .db  <opcode_VGL_trampoline  ; opcode 10: VGL
                .db  <opcode_SPB             ; opcode 11: SPB
                .db  <opcode_VGR_trampoline  ; opcode 12: VGR
                .db  <opcode_VKL_trampoline  ; opcode 13: VKL
                .db  <opcode_NEG             ; opcode 14: NEG
                .db  <opcode_UND             ; opcode 15: UND
                .db  <opcode_P1E_trampoline  ; opcode 16: P1E
                .db  <opcode_P1A_trampoline  ; opcode 17: P1A
                .db  <opcode_P2A_trampoline  ; opcode 18: P2A
                .db  <opcode_LIA             ; opcode 19: LIA
                .db  <opcode_AIS             ; opcode 20: AIS
                .db  <opcode_SIU             ; opcode 21: SIU
                .db  <opcode_P3E_trampoline  ; opcode 22: P3E
                .db  <opcode_P4A_trampoline  ; opcode 23: P4A
                .db  <opcode_P5A_trampoline  ; opcode 24: P5A

opcode_inv_trampoline: JMP  opcode_inv
opcode_SUB_trampoline: JMP  opcode_SUB
opcode_VGL_trampoline: JMP  opcode_VGL
opcode_VGR_trampoline: JMP  opcode_VGR
opcode_VKL_trampoline: JMP  opcode_VKL
opcode_P1E_trampoline: JMP  opcode_P1E
opcode_P1A_trampoline: JMP  opcode_P1A
opcode_P2A_trampoline: JMP  opcode_P2A
opcode_P3E_trampoline: JMP  opcode_P3E
opcode_P4A_trampoline: JMP  opcode_P4A
opcode_P5A_trampoline: JMP  opcode_P5A

dispatch_opcode:
                MOVX A, @R1         ; load opcode
                ADD  A, <opcode_jmpp_table ; add base of opcode jump table
                INC  R1             ; point to operand
                JMPP @A

opcode_HLT:     MOV  A, #$01           ; mask: 0000 0001: 'STP pressed'
                CALL clear_status_bits ; clear ALL but 'STP pressed'
                MOV  R0, #PC
                MOV  A, @R0            ; Load VM PC into A
                MOV  R0, #MEM_SIZE
                XRL  A, @R0            ; Compare VM PC with Mem size
                JZ   hlt_no_incr       ; If equal, don't increment PC
                CLR  F0
                CPL  F0                ; Set F0 to signal "stop execution"
                JMP  inc_pc            ; and increment PC
hlt_no_incr:    JMP  stop_execution

opcode_AKO:
                MOV  R0, #ACCU     ; load address of Accu MSB
                MOV  @R0, #$00         ; write 0 to it
                INC  R0                ; move to LSB
                MOVX A, @R1            ; load operand
                MOV  @R0, A            ; store it to Accu LSB
                JMP  inc_pc

opcode_ABS:
                CALL check_and_load_effective_address
                JF0  error_f003_trampoline
                MOV  R0, #ACCU ; Load Accu MSB...
                MOV  A, @R0        ; ... into A
                MOVX @R1, A        ; and store it in operand
                INC  R0            ; move on to LSB of Accu
                INC  R1            ; and Operand
                MOV  A, @R0        ; load Accu's LSB
                MOVX @R1, A        ; and store it too.
                JMP  inc_pc

error_f003_trampoline:
                JMP  error_f003

opcode_LDA:
                CALL check_and_load_effective_address
                JF0  error_f003_trampoline
                MOV  R0, #ACCU    ; load Accu MSB address
                MOVX A, @R1      ; load msb
                MOV  @R0, A      ; store it in Accu MSB
                INC  R0          ; move to Accu LSB address ...
                INC  R1          ; ... and operand LSB address
                MOVX A, @R1      ; Copy next byte...
                MOV  @R0, A      ; ... to A LSB.
                JMP  inc_pc

opcode_LIA:
                CALL check_and_load_effective_address
                JF0  error_f003_trampoline
                INC  R1          ; treat loaded address as operand address; move on to LSB ...
                JMP  opcode_LDA  ; ... and do and LDA

opcode_AIS:
                CALL check_and_load_effective_address
                JF0  error_f003_trampoline
                INC  R1          ; treat loaded address as operand address; move on to LSB ...
                JMP  opcode_ABS  ; ... and do and LDA

opcode_VZG:
                MOVX A, @R1      ; load operand into A
                JZ   end_VZG     ; if zero, no delay necessary
                MOV  R2, A       ; move # of millis to R2
vzg_l4:
                MOV  R3, #$01    ; cycles: 2
vzg_l3:
                MOV  R4, #$01    ; cycles: + 2
vzg_l2:
                MOV  R5, #$c8    ; cycles: + 2
vzg_l1:
                DJNZ R5, vzg_l1  ; cycles: + 200 * 2
                DJNZ R4, vzg_l2  ; cycles: + 2
                DJNZ R3, vzg_l3  ; cycles: + 2
                DJNZ R2, vzg_l4  ; cycles: + 2 -> 412 cycles -> a little more than 1 ms per loop. No idea why they added the additional loops...
end_VZG:
                JMP  inc_pc

opcode_SPU:
                MOVX A, @R1      ; load operand
                MOV  R0, #PC  ; load address of PC
                MOV  R3, A       ; save new PC in R3 (will be restored from R3 at end_of_instr)
                MOV  @R0, A      ; store new address in PC
                JMP  end_of_instr

opcode_SPB:
                MOV  R0, #STATUS    ; Load address of status byte
                MOV  A, @R0      ; Load status byte
                JB5  spb_cond_true ; comparison was true -> execute jump
                JMP  inc_pc      ; comparuson was false, move on with next instr
spb_cond_true:
                JMP  opcode_SPU

opcode_SIU:
                CALL check_and_load_effective_address
                JF0  error_f003_trampoline
                INC  R1          ; treat loaded address as operand address; move on to LSB ...
                JMP  opcode_SPU  ; ... and do a SPU

opcode_NEG:
                MOV  R0, #ACCU   ; Load Accu's MSB...
                MOV  A, @R0          ; ... into A
                JNZ  error_f005      ; Error if Accu does not contain data
                CLR  C               ; Clear Carry
                INC  R0              ; Load Accu's LSB...
                MOV  A, @R0          ; ... into A
                ADD  A, #$fe         ; .. add 254 ...
                JC   error_f005      ; if it overflows, A was > 1
                MOV  A, @R0          ; Load Accu again
                JZ   $04b9           ; if 0, set it to 1
                MOV  @R0, #$00       ; otherwise, set  it to 0
                JMP  inc_pc
neg_was_zero:
                MOV  @R0, #$01
neg_done:
                JMP  inc_pc

error_f005:
                MOV  R4, #$05
                JMP  show_error     ; F-005

opcode_UND:
                MOV  R0, #ACCU  ; Load Accu's MSB...
                MOV  A, @R0         ; ... into A
                JNZ  error_f005     ; Error if Accu does not contain data
                CLR  C              ; Clear Carry
                INC  R0             ; Load Accu's LSB...
                MOV  A, @R0         ; ... into A
                ADD  A, #$fe        ; .. add 254 ...
                JC   error_f005     ; if it overflows, A was > 1
                CALL check_and_load_effective_address
                JF0  error_f003_trampoline
                MOVX A, @R1         ; Load Operand's MSB
                JNZ  error_f005     ; F-005 if it's not data
                CLR  C
                INC  R1             ; Move to Operand's LSB
                MOVX A, @R1         ; Load Operand
                ADD  A, #$fe        ; Add 254 (== 256 - 2)
                JC   error_f005     ; F-005 if > 1
                MOV  R0, #ACCU_LSB  ; Load Accu LSB address
                MOV  A, @R0         ; and load Accu LSB
                JZ   neg_done       ; if 0, result will be 0, too
                MOVX A, @R1         ; otherwise, load Operand LSB
                JZ   und_zero       ; jump if zero
                MOV  @R0, #$01      ; store 1 in Accu LSB
                JMP  inc_pc
und_zero:
                MOV  @R0, #$00      ; store 0 in Accu LSB
                JMP  inc_pc

opcode_ANZ:
                CALL print_accu
                JMP  inc_pc

execute_sub_trampoline:
                JMP  execute_sub

error_f006_trampoline:
                JMP  error_f006

opcode_ADD:
                CALL check_and_test_operand_and_accu ; Check that both operand cell and accu contain data (00.xxx)
                JF0  error_f003_trampoline
                JNZ  error_f005
                JF1  execute_sub_trampoline ; if F1 is set, it's a SUB
                CLR  C           ; clear carry
                MOVX A, @R1      ; load operand cell content
                ADD  A, @R0      ; add Accu LSB
                JC   error_f006_trampoline
                MOV  @R0, A      ; Store result of addition
                JMP  inc_pc

error_f003_trampoline2:
                JMP  error_f003

error_f006:
                MOV  R4, #$06
                JMP  show_error     ; F-006

error_f005_trampoline:
                JMP  error_f005

opcode_SUB:
                CLR  F1
                CPL  F1          ; Set F1 to mark SUB
                JMP  opcode_ADD

execute_sub:
                CLR  F1          ; Clear F1, we don't need it anymore
                MOVX A, @R1      ; load operand cell content
                MOV  R3, A       ; Save it in R3
                XRL  A, @R0      ; Compare to Accu LSB
                JZ   ps_zero     ; Just store zero if Operand and Accu LSB are equal
                MOV  A, R3       ; Restore operand cell content
                JZ   $0525       ; No change needed if operand is 0
                CPL  A           ; Negate A
                CLR  C           ; clear carry
                ADD  A, #$01     ; A is 2's complement of A now
                ADD  A, @R0      ; Add it to Accu LSB
                JNC  error_f006  ; check for overflow
                CLR  C
                MOV  A, @R0      ; Load Accu
execute_sub_loop:
                DEC  A           ; Decrement...
                DJNZ R3, execute_sub_loop ; ... and keep on while we have more to subtract. Why are they doing that if they already computed the result to check for overflow?
                MOV  @R0, A      ; Finally, store the result in Accu LSB again.
ps_sub_zero:
                JMP  inc_pc
ps_zero:
                MOV  @R0, #$00   ; Store 0 in Accu LSB
                JMP  inc_pc

opcode_VGL:
                CALL clear_comparison_result
                CALL check_and_test_operand_and_accu
                JF0  error_f003_trampoline2
                JNZ  error_f005_trampoline
                MOVX A, @R1   ; Load operand cell content LSB
                XRL  A, @R0   ; Compare to Accu LSB
                JZ   vgl_true ; jump if equal
vgl_end:
                CLR  C        ; clear carry
                JMP  inc_pc
vgl_true:
                CALL set_comparison_result
                JMP  vgl_end

opcode_VKL:
                CALL clear_comparison_result
                CALL check_and_test_operand_and_accu
                JF0  error_f003_trampoline2
                JNZ  error_f005_trampoline
                MOVX A, @R1   ; Load operand cell content LSB
                JZ   vgl_end  ; if zero, no need to compare
                CPL  A        ; Negate A
                CLR  C        ; clear carry
                ADD  A, #$01  ; add 1: A contains now 2s complement
                ADD  A, @R0   ; add Accu LSB
                JNC  vgl_true ; jump if (-op + Accu) < 0
                JMP  vgl_end

opcode_VGR:
                CALL clear_comparison_result
                CALL check_and_test_operand_and_accu
                JF0  error_f003_trampoline2
                JNZ  error_f005_trampoline
                MOV  A, @R0   ; Load Accu LSB
                JZ   vgl_end  ; If zero, it can't be greater than anything
                CPL  A        ; Negate A
                CLR  C        ; clear carry
                ADD  A, #$01  ; add 1: A contains now 2s complement
                MOV  R3, A    ; Save in R3
                MOVX A, @R1   ; Load operand cell content LSB
                ADD  A, R3    ; add -Accu to operand
                JNC  vgl_true ; jump if (-Accu + operand) < 0
                JMP  vgl_end

single_pin_trampoline:
                JMP  single_pin

opcode_P1E:
                MOVX A, @R1                ; load operand
                JNZ  single_pin_trampoline ; Jump if a single pin is selected
                ORL  P1, #$ff              ; Set all pins to "input mode"
                IN   A, P1                 ; Read value from port
                CALL save_to_accu          ; and save it in the accu
                JMP  inc_pc

single_pin:
                MOV  R4, A          ; Save pin number in R4
                CLR  C
                ADD  A, #$f7        ; Add 247 (== -8 in 1s complement)
                JC   error_f005_trampoline ; jump if pin is > 8
                MOV  A, R4          ; Restore pin number
                MOV  R2, A          ; save it in R2
                CLR  C              ;
                CPL  C              ; set Carry
                CLR  A              ; clear A
single_pin_shift:
                RLC  A              ; Shift A to compute bit mask
                DJNZ R2, $057f      ; Continue shifting if necessary
                MOV  R1, #LAST_BYTE_P1       ; load address of "last byte written to port 1"
                ORL  A, @R1         ; OR the selected pin into it
                OUTL P1, A          ; Write to port to prepare it for reading (see section 2.1.4 on page 2-5 in 8040.pdf)
                IN   A, P1          ; read port into A
                CALL check_bit      ; Set A according to whether bit is set
                CALL save_to_accu   ; and store it in Accu
                JMP  inc_pc

opcode_P1A:
                MOV  R0, #ACCU_LSB ; load address of Accu LSB
                MOVX A, @R1        ; load operand
                MOV  R2, A         ; copy operand to R2
                MOV  R1, #LAST_BYTE_P1      ; load address of "last byte to port 1"
                JNZ  p1a_single_pin; Single pin out?
                MOV  A, @R0        ; load Accu LSB
                MOV  @R1, A        ; store last byte written to port 1
write_to_p1:
                OUTL P1, A         ; write byte to 8049 port 1
                JMP  inc_pc

p1a_single_pin:
                CLR  C
                ADD  A, #$f7          ; A = (A + 247) % 255 ==  A = A - 8
                JC   error_f005_trampoline; jump if A > 8
                MOV  A, R2            ; move copied operand back to A
                CALL write_bit        ; write bit (0 or 1) to pin number into "last byte p1". R0 points to Accu LSB
                JMP  write_to_p1

opcode_P2A:
                MOVX A, @R1        ; load operand...
                MOV  R2, A         ; ... and store it in R2
                CALL enable_internal_io  ; enable IO on internal 8155
                MOV  R1, #LAST_BYTE_P2      ; load address of "last byte to p2"
                MOV  R4, #$02      ; load port address (2) to R4
write_to_port:
                MOV  R0, #ACCU_LSB ; Load address of Accu LSB
                MOV  A, R2         ; load instr operand
                JNZ  p2a_single_pin; jump to single pin if != 0
                MOV  A, @R0        ; load Accu LSB
                MOV  @R1, A        ; store last byte written to port 2
p2a_write_val:
                XCH  A, R4         ; Move port address stored in R4...
                MOV  R0, A         ; ... to R0 ...
                XCH  A, R4         ; and restore A again
                MOVX @R0, A        ; write A to port
                MOV  A, #$bf       ; 1011 1111 (IO enabled)
                CALL clear_status_bits ; Clear "IO enabled" bit in status reg
                ANL  P2, #$7f    ; P2 &= 0111 1111 --> IO == 0
                JMP  inc_pc
p2a_single_pin:
                CLR  C           ; Clear Carry
                ADD  A, #$f7     ; Add 247 ( == 255 - 8)
                JC   invalid_pin ; show F-005 if > 8
                MOV  A, R2       ; Move operand (pin number) back to A
                CALL write_bit   ; write bit (0 or 1) to pin number into "last byte p2". R0 points to Accu LSB
                JMP  p2a_write_val

opcode_P3E:
                MOVX A, @R1        ; load operand...
                MOV  R4, A         ; ... and store it in R4
                CALL enable_cp3_io
                MOV  R0, #$02      ; load port address (2)
                MOV  A, R4         ; load operand
                JNZ  p3e_single_pin ; check if single pin read
                MOVX A, @R0        ; full port read: read port into A
p3e_save_to_accu:
                CALL save_to_accu  ; and store it in Accu
                MOV  A, #$bf     ; 1011 1111 (IO enabled)
                CALL clear_status_bits ; Clear "IO enabled" in status bit
                ANL  P2, #$7f    ; P2 &= 0111 1111 --> IO == 0
                JMP  inc_pc
p3e_single_pin:
                CLR  C           ; Clear Carry
                ADD  A, #$f7     ; Add 247 ( == 255 - 8)
                JC   invalid_pin ; show F-005 if > 8
                MOVX A, @R0      ; Read port 2
                CALL check_bit   ; set A according to whether bit is set
                JMP  p3e_save_to_accu
invalid_pin:
                MOV  A, #$bf     ; 1011 1111 (IO enabled)
                CALL clear_status_bits ; Clear "IO enabled" in status bit
                ANL  P2, #$7f    ; P2 &= 0111 1111 --> IO == 0
                JMP  error_f005

opcode_P4A:
                MOVX A, @R1    ; load operand...
                MOV  R2, A     ; ... and store it in R2
                CALL enable_cp3_io ; Enable extension's 8155
                MOV  R1, #LAST_BYTE_P4  ; load address of "last byte to p4"
                MOV  R4, #$01  ; load port address (1)
                JMP  write_to_port

opcode_P5A:
                MOVX A, @R1    ; load operand...
                MOV  R2, A     ; ... and store it in R2
                CALL enable_cp3_io ; Enable extension's 8155
                MOV  R1, #LAST_BYTE_P5  ; load address of "last byte to p5"
                MOV  R4, #$03  ; load port address (3)
                JMP  write_to_port

run_handler:
                MOV  A, R6      ; Load # of entered digits
                XRL  A, #$01    ; is it 1?
                JNZ  not_one    ; jump if not.
                MOV  R0, #KBUF  ; load entered digit ...
                MOV  A, @R0     ; ... to A
                XRL  A, #$09    ; is it 9?
                JZ   demo_9     ; jump if yes
                MOV  A, @R0     ; load again
                XRL  A, #$08    ; is i 8?
                JZ   demo_8     ; jump if yes
not_one:
                CALL clear_display
                MOV  R6, #$00
                MOV  R0, #STATUS
                MOV  A, @R0         ; load status byte
                JB0  stop_pressed_2 ; jump if STP pressed
check_step_key:
                MOV  R0, #STATUS
                MOV  A, @R0         ; load status byte
                JB1  step_pressed_2 ; jump if STEP pressed

step_handler:
                MOV  R0, #PC
                MOV  A, @R0
                CALL compute_effective_address
                CLR  C
                MOVX A, @R1         ; Load MSB
                ADD  A, #$e7        ; add (255 - 24)
                JC   opcode_inv ; jump if > 24
                JMP  dispatch_opcode

inc_pc:
                CLR  C
                MOV  R0, #PC
                MOV  A, @R0         ; Load VM PC
                ADD  A, #$01
                MOV  @R0, A         ; PC = PC + 1
                MOV  R3, A
                JC   error_f003     ; Jump if PC overflowed
end_of_instr:
                MOV  R0, #MEM_SIZE
                MOV  A, @R0         ; Load extension status
                JB7  cp3_installed
                MOV  A, R3          ; Restore PC to A
                JB7  error_f003     ; show error if PC >= 128
cp3_installed:
                JF0  stop_execution ;
                MOV  R0, #STATUS
                MOV  A, @R0         ; load status byte
                JB1  pc_handler_trampoline ; jump if STEP pressed. If not, we're done.
                MOV  R0, #STATUS
                MOV  A, @R0         ; Load status byte
                JB0  print_content_of_addr_0 ; print addr 0 if STP pressed
                JMP  step_handler   ; Otherwise, move on with the next instr

stop_pressed_2:
                CALL clear_access_ram_extension_status_bit
                JMP  check_step_key

step_pressed_2:
                MOV  A, #$fd
                CALL clear_status_bits
                JMP  step_handler

opcode_inv:
                MOV  R4, #$02    ; F-002
                JMP  show_error

error_f003:
                MOV  R4, #$03    ; F-003
                JMP  show_error

print_content_of_addr_0:
                MOV  A, #$ef     ; mask 1110 1111 ; 'access RAM extension'
                CALL clear_status_bits
                ORL  P2, #$20    ; P2 |= 0010 0000 --> /CE == 1
                ANL  P2, #$ef    ; P2 &= 1110 1111 --> 8155 /CE == 0
                MOV  R1, #$00
                CALL fetch_and_print_ram
                JMP  print_C

pc_handler_trampoline:
                JMP  pc_handler

; ??????? NEVER REACHED?
                JMP  wait_key

stop_execution:
                CLR  F0
                MOV  R6, #$00   ; set # of digits entered to 0
                JMP  pc_handler ; print PC and back to key loop


demo_9:
                JMP  demo_countdown

demo_8:
                JMP  demo_reactiontest

demo_reactiontest:
                CALL clear_display
                CALL clear_access_ram_extension_status_bit
                MOV  R0, #$43      ; Load delay in secs address
                INC  @R0           ; increment it
                MOV  A, @R0        ; load it to A
dr_delay:
                MOV  R4, #$04      ; loop 4 times...
dr_delay2:
                MOV  R3, #$fa      ; 250 millis each time
                CALL delay_millis
                DJNZ R4, dr_delay2 ;
                DEC  A
                JNZ  dr_delay      ; Continue delaying until 0 secs left.
                ; A is 0 at this place
                CLR  C
dr_loop:
                MOV  R0, #$05      ; Load address of value to be printed (5 == R5)
                MOV  R5, A         ; Store A at this address
                MOV  R6, #$02      ; start at Video RAM offset 2
                CALL print_value   ; print value
                MOV  R1, #STATUS   ; load status byte address
                MOV  A, @R1        ; load status byte
                JB0  dr_stop_pressed ; break if stop was pressed
                MOV  A, R5         ; otherwise, increment A
                ADD  A, #$01       ; by 1
                JC   dr_done       ; break if overflow happened
                MOV  R3, #$01      ; 1 milli
                CALL delay_millis
                JMP  dr_loop
dr_stop_pressed:
                CALL clear_access_ram_extension_status_bit
dr_done:
                MOV  R6, #$00      ; reset # of digits entered
                MOV  R0, #$43      ; load delay in secs address
                MOV  A, R5         ; store last reaction time...
                ANL  A, #$07       ; ... modulo 8 ...
                MOV  @R0, A        ; ... as next delay.
                CLR  C             ; clear carry
                JMP  wait_key      ; and back to main loop

; Print PC
; --------
; Print the current PC.
print_pc:
                CALL clear_display
                MOV  R0, #$20      ; Set left-most digit...
                MOV  @R0, #$73     ; ... to 'P'
                MOV  R0, #PC       ; Load address of VM's PC
                MOV  R6, #$02      ; Start at Video RAM offset 2
                CALL print_value   ; print the value
                RET

demo_countdown:
                CALL clear_display
                MOV  R3, #$ff    ; 255 millis
                CALL delay_millis
                MOV  R3, #$ff    ; 255 millis
                CALL delay_millis
                MOV  R1, #$09    ; start at 9
                MOV  R4, #$0a    ; loop counter
dc_loop:
                MOV  A, #$00     ; move 0 to A
                ADD  A, R1       ; add countdown pos
                MOVP3 A, @A      ; Load segment for that digit
                MOV  R3, #$06    ; 6 digits to write
                MOV  R0, #$25    ; load last digit address of "Video RAM"
dc_display_loop:
                MOV  @R0, A      ; store segment mask in Video RAM
                DEC  R0          ; move to next Video RAM address
                DJNZ R3, dc_display_loop; continue storing digits
                MOV  R3, #$ff     ; 255 millis
                CALL delay_millis
                MOV  R3, #$ff     ; 255 millis
                CALL delay_millis
                DEC  R1           ; Decrement countdown number
                DJNZ R4, dc_loop  ; if not done, continue counting down
                MOV  R6, #$00     ; reset # of digits entered
                JMP  wait_key


; Print Accumulator
; -----------------
; Print the value in the Accumulator
print_accu:
                MOV  R6, #$00    ; Load Offset into Video RAM
                CLR  F0          ;
                CPL  F0          ; Set F0: "no hundreds" for print
                MOV  R0, #ACCU    ; Load Accu MSB address
                CALL print_value ; and print it
                MOV  R0, #ACCU_LSB ; Load Accu LSB address
                MOV  R6, #$02    ; Offset into Video RAM
                CALL print_value ; and print it
                MOV  R0, #$20    ; Set left-most digit...
                MOV  @R0, #$77   ; ... to 'A'
                RET

; Print Error code and jump into keyboard handler
; -----------------------------------------------
; Print the error code that's stored in R4, and then jumps
; back to the keyboard handler. THIS IS NOT A SUBROUTINE.
; Input:
;   - R4: Error code
show_error:
                CALL clear_display
                CLR  C
                CLR  F0
                CLR  F1
                MOV  R0, #$20       ; Set left-most digit...
                MOV  @R0, #$71      ; ... to 'F'
                MOV  R0, #$04       ; Load address of error code
                MOV  R6, #$02       ; Offset into Video RAM
                CALL print_value    ; And print it
                JMP  wait_key

cal_handler:
                CALL clear_display
                CALL clear_access_ram_extension_status_bit
                MOV  R0, #$20    ; Set left-most digit...
                MOV  @R0, #$1c   ; .. to 'CAL' symbol
                CLR  A
                CALL compute_effective_address ; Compute effective addres of 0 to enable main 8155
                ANL  P1, #$bf    ; P1 &= 1011 1111: clear CassWR
                ORL  P1, #$80    ; P1 |= 1000 0000: set CassData
wait_cas:
                MOV  R0, #STATUS    ; Load status byte...
                MOV  A, @R0      ; .. to A
                JB0  cal_stop_pressed; jump if STP pressed
                IN   A, P1       ; Read port 1
                JB7  wait_cas    ; Wait while CassData is not low
                CLR  A           ;
                MOV  R1, A       ; Set current address to write data to to 0
                MOV  R5, A       ; Set "current byte value" to 0
                MOV  R0, #$08    ; 8 bits to go
cal_read_loop:
                CALL cassette_read_bit
                JF1  cal_end     ; jump if no data from cassette
                CALL cal_store_bit ; store bit
                MOV  A, R0       ; compare bits to read...
                XRL  A, #$08     ; ... to 8
                JNZ  cal_read_loop ; jump if not a full byte was read yet
                MOV  R0, #STATUS ; load address of status byte
                MOV  A, @R0      ; load status byte
                JB0  cal_stop_pressed ; jump if STP was pressed
                MOV  R0, #$08    ; reset bits to read count to 8
                MOV  A, R1       ; load current address
                JNZ  cal_read_loop ; continue reading if there's still data left
                ; First block of data is read now. Check if there's a CP3 installed
                ; and if so, read 2nd block.
                MOV  R0, #MEM_SIZE ; load...
                MOV  A, @R0        ; ... memory size
                JB7  cal_read_2nd_block ; carry on with 2nd block if mem size > 127
                JMP  cal_done
cal_read_2nd_block:
                MOV  A, #$80       ; load address 128
                CALL compute_effective_address ; compute effective address (and select the correct 8155)
                CLR  A
                MOV  R1, A         ; Set current address to write data to to 0
                MOV  R5, A         ; Set "current byte value" to to 0
                MOV  R0, #$08      ; 8 bits to go
cal_read_loop2:
                CALL cassette_read_bit
                JF1  cal_show_f007 ; Show error if no data from cassette.
                CALL cal_store_bit ; store bit read
                MOV  A, R0         ; compare bits to read...
                XRL  A, #$08       ; ... to 8
                JNZ  cal_read_loop2 ; jump if not a full byte was read yet
                MOV  R0, #STATUS   ; load address of status byte
                MOV  A, @R0        ; Load status byte
                JB0  cal_stop_pressed ; jump if STP pressed
                MOV  R0, #$08      ; reset bits to read count to 8
                MOV  A, R1         ; load current address
                JNZ  cal_read_loop2 ; continue reading if there's still data left
cal_done:
                CALL clear_display
                MOV  R0, #$20    ; Set left-most digit...
                MOV  @R0, #$5c   ; ... to 'o'
                CLR  C
                CLR  F0
                CLR  F1
                MOV  R6, #$00    ; Reset # of digits entered
                JMP  wait_key
cal_end:
                MOV  A, R1       ; Load destination address
                JZ   wait_cas    ; if zero, we never read anything, start over
cal_show_f007:
                MOV  R4, #$07    ; otherwise, show F-007 (invalid data from cassette)
                JMP  show_error
cal_stop_pressed:
                CALL clear_access_ram_extension_status_bit
                JMP  cal_done


; Read a single bit from cassette
; -------------------------------
; Reads a single bit from cassette (or in fact anything else on port 1 bit 7)
; Outputs:
; - F0: set if bit is 1
; - F1: set if no data received
cassette_read_bit:
                CLR  A         ; Clear A...
                MOV  R2, A     ; Set "low count" to 0
                MOV  R3, A     ; Set "high count to 0
                CLR  C         ; and clear...
                CLR  F0        ; ... all the ...
                CLR  F1        ; ... flags
                ORL  P1, #$80  ; Set high CassData on Port 1
crb_read_loop:
                IN   A, P1     ; Read Port 1 ...
                JB7  crb_low_done ; ...bail out if it's not low
crb_inc_low_count:
                MOV  A, R2     ; otherwise, load "low count"
                ADD  A, #$01   ; and add 1
                JC   crb_done  ; overflow == end of data
                MOV  R2, A     ; Update low count
                MOV  R4, #$64  ; Delay for 100*20 micros == 2 millis
                CALL delay_20micros
                JMP  crb_read_loop
crb_low_done:
                MOV  R4, #$c8  ; Delay for 200*20 micros == 4 millis to filter out any glitches
                CALL delay_20micros
                IN   A, P1     ; Read port 1 again
                JB7  crb_still_high    ; Still high, let's move on
                JMP  crb_inc_low_count ; was just a glitch, increase low count
crb_still_high:
                MOV  R4, #$64  ; Delay for 100*20 micros == 2 millis
                CALL delay_20micros
                IN   A, P1     ; Read Port 1
                JB7  crb_inc_high_count
                MOV  R4, #$c8 ; Not high anymore, delay for 200*20 micros == 4 millis to check for glitches
                CALL delay_20micros
                IN   A, P1    ; Read P1 again
                JB7  crb_inc_high_count    ; Still high, just a glitch; continue counting
                ; Now, we're done counting low and high impulses.
                MOV  A, R2     ; load "low count"
                ADD  A, #$fd   ; compare to 3 (== 256 - 253)
                JNC  crb_done  ; jump if it's < 3
                CLR  C         ; clear carry again
                MOV  A, R3     ; load "high count"
                ADD  A, #$fd   ; compare to 3 (== 256 - 253)
                JNC  crb_done  ; jump if it's < 3
                CLR  C         ; clear carry again
                MOV  A, R2     ; load "low count"
                CPL  A         ; negate (one's complement)
                ADD  A, R3     ; add high count
                JNC  crb_end   ; jump if low <= high
                CPL  F0        ; signal bit 1
crb_end:
                RET
crb_inc_high_count:
                MOV  A, R3
                ADD  A, #$01
                JC   crb_done
                MOV  R3, A
                JMP  crb_still_high
crb_done:
                CPL  F1        ; Set F1
                RET

; Delay for a multiple of 20 micros
; ---------------------------------
; Inputs:
; - R4: number of 20 micros delays
delay_20micros:
                NOP
                NOP
                NOP
                NOP
                NOP
                NOP
                DJNZ R4, delay_20micros
                RET

; Store bit read from cassette to memory
; --------------------------------------
; Inputs:
; - R0: bits left to read in current byte
; - R1: physical address of current byte
; - R5: current byte
; - F0: bit to store
; Outputs:
; - R5: updated byte
; - R0: bits left to read in current byte
cal_store_bit:
                CLR  C         ;
                JF0  csb_set_carry
csb_cont:
                MOV  A, R5        ; Load current byte
                RRC  A            ; Rotate bit in
                MOV  R5, A        ; update current byte
                DJNZ R0, csb_done ; decrement bit count
                MOV  A, R5        ; no bits left in current byte; store R5...
                MOVX @R1, A       ; ...in memory
                DEC  R1           ; move to next address
                MOV  R0, #$08     ; reset bits to read
csb_done:
                RET
csb_set_carry:
                CPL  C            ; bit is set -> set carry
                JMP  csb_cont


; Clear result of last comparison in status byte
; ----------------------------------------------
clear_comparison_result:
                MOV  A, #$df           ; 0b1101 1111
                CALL clear_status_bits ; clear bit 5: result of last comparison
                RET

; Set result of last comparison in status byte
; --------------------------------------------
set_comparison_result:
                MOV  A, #$20         ; mask 0010 0000
                CALL set_status_bits ; clear bit 5: comparison is true
                RET


; Print single digit
; ------------------
; Moves a single digit store in A to display video to position R6, and
; increases R6.
;
; Input:
;   - A: Digit to print (0..9)
;   - R6: Digit offset (0..4)
;
; Output:
;   - R6: Next digit offset
;
print_digit:
                MOV  R3, A    ;
                MOV  A, #$00  ;
                ADD  A, R3    ; A = A + 0?? Why this?
                MOVP3 A, @A   ; Load segment for digit in A
                MOV  R3, A    ; Store segment in R3
                MOV  A, R6    ; Digit offset to A
                ADD  A, #$20  ; add display base address
                INC  A        ; make room for leftmost indicator
                MOV  R0, A    ; move to address register
                MOV  A, R3    ;
                MOV  @R0, A   ; Store segment data in display
                INC  R6       ; move to next digit
                RET


; Dead code.
                ADD  A, R5
                .db  $d6
                ADDC A, R0
                DJNZ R3, $0700
                OUTL BUS, A
