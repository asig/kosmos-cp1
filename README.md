# KOSMOS CP1

An emulator for the **Kosmos CP1 "Computer Praxis"** experimental computer kit.

## What's this?

This is an emulator for the Kosmos CP1 "Computer Praxis" experimental kit that
was sold in German-speaking Europe in the early 1980s.

The Kosmos CP1 was intended to introduce children to programming. To do so, it
used a virtual machine with a very simple machine language. Programs were
written in assembler and then manually translated into opcodes. The computer
was accompanied by an excellent manual that not only explained the virtual
machine, but also contained a well-written introduction to the inner workings
of a computer.

Unlike many other CP1 simulators, **kosmos-cp1** does not only simulate the
CP1's virtual machine. Instead, it is a cycle-accurate emulation of the underlying
hardware, including the Intel 8049 microcontroller and Intel 8155 I/O and RAM
devices used by the original system.

As a result, you get the "full" CP1 experience and can run not only your own
programs but also the examples from the original manuals, including classics
such as **"9 RUN"** and **"8 RUN"**.

## Features

### Hardware emulation

The current version emulates:

- CP1 base unit
- CP3 memory extension
- CP5 universal input/output interface

The CP2 cassette interface is not emulated yet.

### Save states

The emulator supports saving and loading the complete emulator state (CPU,
RAM, and I/O device state) to and from a file. This makes it easy to pause a
session and resume it later, or to snapshot the machine at an interesting
point without having to re-enter or re-run a program from scratch.

### Development tools

For convenience, the emulator includes an integrated assembler for the CP1
assembly language.

If you have ever written assembly code (and I bet you did if you're interested
in this emulator :-) ), the syntax should feel immediately familiar.

The assembler also includes a collection of sample programs taken from the
original manuals and other sources.

### Debugging support

The emulator contains a built-in debugger that allows you to:

- Inspect CPU state
- Inspect RAM and I/O devices
- Single-step through execution
- Set breakpoints
- Follow instruction execution in real time
- Break on specific instructions (currently MOVX)

This makes the emulator useful not only for reliving childhood memories, but
also for understanding how the original hardware and firmware work.

## Screenshots

*(Screenshots would fit nicely here.)*

## Accuracy

The goal of this project is to emulate the actual CP1 hardware rather than
reimplementing its virtual machine.

Internally, the emulator contains implementations of:

- Intel 8049 microcontroller
- Intel 8155 RAM/I/O/timer device
- CP1 firmware and hardware behaviour
- CP3 memory expansion
- CP5 I/O extension

This allows software to interact with the emulated machine exactly as it would
with the real hardware.

## Further Reading

You can find scans of the original manuals at:

http://www.retrozone.ch/cp1/

If you are interested in retro educational computers, the original manuals are
highly recommended. They are surprisingly well written and still make for an
excellent introduction to low-level programming.

## Building

### Linux

#### Dependencies

```bash
sudo apt install build-essential cmake qt6-base-dev qt6-svg-dev nsis
```

Optionally, if you are using a GNOME desktop:

```bash
sudo apt install qt6-gtk-platformtheme adwaita-qt6
```

#### Release build

```bash
cmake . -B build/Release -DCMAKE_BUILD_TYPE=Release
cd build/Release
make -j$(nproc)
```

#### Debug build

```bash
cmake . -B build/Debug -DCMAKE_BUILD_TYPE=Debug
cd build/Debug
make -j$(nproc)
```

### Windows

#### Dependencies

To compile the binary on Windows, you need:

- Qt 6 (including the MinGW toolchain)
- CMake
- MinGW
- NSIS 3 (optional, for building the installer)

Qt can be downloaded from:

https://www.qt.io/download-open-source

NSIS is available from:

https://sourceforge.net/projects/nsis/

#### Building

The easiest way to build the application and installer is to run:

```bat
build_win_release.bat
```

You may need to adjust the following variables inside the script:

- `QT_HOME`
- `NSIS_HOME`

to match your local installation.

**Note:** Make sure the source path does not contain spaces. MinGW's resource
compiler tends to get confused otherwise.

## Usage

After starting the emulator, you'll see the **Panel** window that reproduces the
front panel of the CP1, including the CP5 LEDs and switches.

Additional windows can be opened from the **Windows** menu.

### CPU Window

The CPU window contains:

- CP1 ROM disassembly
- Intel 8049 state and memory
- Main unit Intel 8155 state and memory
- CP3 Intel 8155 state and memory

The debugger supports:

- Single stepping
- Continue execution
- Breakpoints (double-click a disassembly line)
- Instruction breakpoints (currently MOVX)

### Panel Window

The panel window tries to reproduce the original CP1 as closely as possible.

Apart from being useful, it is also a lot more fun to look at than rows of
register values :-)

### Assembler Window

The assembler window allows you to:

- Write CP1 assembly programs
- Assemble source code directly inside the emulator
- Load sample programs
- Transfer assembled programs into emulated memory

The syntax is intentionally simple and closely follows the style used in the
original manuals.

### Loading and Saving Emulator State

The full emulator state can be saved to a file and loaded again later. This
captures CPU, RAM, and I/O device state, so a session can be paused and
resumed exactly where it left off, or a particular machine state can be
snapshotted for later use.

This is available from the **State** menu:

- **Load State** (`Ctrl+L`)
- **Save State** (`Ctrl+S`)

## Sample Programs

The repository contains a collection of sample programs, including examples from
the original CP1 documentation.

These are a good starting point if you are new to the machine or simply want to
verify that everything is working correctly.

## Project Status

Version 2.0.0 was a complete rewrite of the original Java implementation in
modern C++ and Qt.

Planned improvements can be found in:

- `TODO`

## Credits

- The UI uses Fugue Icons by Yusuke Kamiyamane (http://p.yusukekamiyamane.com/)
- The keyboard font seems to be "Newhouse DT Black" or "Pragmatica Black Regular".
  However, "Helvetica" is close enough. The bundled fonts are
   - https://www.download-free-fonts.com/details/92227/helvetica-black, and
   - https://www.download-free-fonts.com/details/37282/helvetica-light

## Contributing

Bug reports, pull requests, corrections, historical information, and additional
CP1 documentation are always welcome.

Especially if you still own original CP1 hardware :-)

## License

Copyright (c) 2012-2026 Andreas Signer <asigner@gmail.com>

Licensed under [GPLv3](https://www.gnu.org/licenses/gpl-3.0).