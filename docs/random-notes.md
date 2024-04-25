Clock Frequency
===============
Oscillator: 6 MHz, divided by 3 to get CLK, 5 steps per cycle -> 400kHz cycles -> 2.5 microsecs per cycle

Timer
=====
Timer is using ALE clock (400 kHz) with a :32 divider -> 80 microsecs per count

Display/Keypad
==============

Display and Keypad are handled at the same time:
for pos = 0 to 7:
   set bit "pos" on the 8155's Port C to 0 (all others to 1)
   write segment mask to 8155's port A. 1 --> segment is lit
   read lower nibble of 8049's port 2

Cassette interface
==================
Used frequencies:
- 1 kHz == 5V
- 2 kHz == 0V

Endianness
==========
The VM stores values big endian.