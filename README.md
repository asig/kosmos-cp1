# KOSMOS CP1

## What's this?
This is an emulator for the Kosmos CP1 "Computer Praxis" experimental kit that
was sold in german-speaking Europe in the early 80s of the last century.

The Kosmos CP1 was intended to introduce children to programming. To do so, it
used a virtual machine with a very simple machine language. Programs were
written in Assembler, and then had to be manually translated to op-codes.
The computer was accompanied by a pretty good manual that not only explained
the virtual machine, but also contained a well written introduction to
the inner workings of a computer.

Unlike other programs, kosmos-cp1 does not only simulate the Kosmos CP1's
virtual machine, but is a cycle-exact emulation of the underlying hardware
(an Intel 8049 and an Intel 8155). Therefore, you'll get the "full" CP1
experience, and can also run the demo programs described in the manual,
"9 RUN" and "8 RUN".

### What is currently emulated?
The current version emulates the following components:
- CP1 base unit
- CP3 memory extension
- CP5 universal input/output interface

The CP2 cassette interface is not emulated yet, but memory can be read from and
written to files.

### Additional features
For your convenience, the emulator comes with an integrated assembler for
CP1's assembly language. If you ever wrote assembly code (and I bet you did
if you're interested in this emulator :-) ) you will have no problems
understanding the syntax. Also, a lot of the sample programs from the manuals
are included.

### Further reading
You can find scans of all the manuals at http://www.retrozone.ch/cp1/

## Building and running
```
./gradlew run
```

## Usage
After starting the Kosmos CP1 emulator, you see 2 windows:
- The "CPU" windows containing:
   - The disassembly of the Kosmos CP1's EEPROM,
   - the complete state and memory of the main Intel 8049,
   - the complete state and memory of the main unit's 8155, and finally
   - the complete state and memory of the CP3's 8155.
- The "Panel" window that shows the display and controls of the CP1 main unit,
  as well as the LEDs and switches of the CP5 universal I/O interface

Additionally, there is an "Assembler" window (not visible after startup) that
you can use to write programs in CP1 assembly language.

### CPU Window
The emulator will start in "trace execution" mode. This means that the state is
constantly updated in the CPU window, with quite a large impact on the speed of
the emulated system. You can turn this off with the checkbox in the toolbar.

Also, the CPU window features a simple debugger that lets you single-step
through the exection, set breakpoints (double-click on the disassembly line),
and break on certain instructions (currently only MOVX).

### Panel Window
Not a lot to say about that. It tries to reproduce the actual CP1 as closely as
possible (and looks IMHO way nicer than the simulators out there :-) ).

### Assembler Window
TODO write something up

### Loading and storing programs
TODO write something up


## Resources
- The UI uses Fugue Icons by Yusuke Kamiyamane (http://p.yusukekamiyamane.com/)
- The keyboard font seems to be "Newhouse DT Black" or "Pragmatica Black Regular".
  However, "Helvetica" is close enough. The bundled fonts are
   - https://www.download-free-fonts.com/details/92227/helvetica-black, and
   - https://www.download-free-fonts.com/details/37282/helvetica-light

