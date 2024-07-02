cmake_minimum_required(VERSION 3.5)

project(kosmos-cp1-cpp VERSION 0.1 LANGUAGES CXX)

set(CMAKE_INCLUDE_CURRENT_DIR ON)

set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

#
# Prevent building in the source directory
#

if(PROJECT_SOURCE_DIR STREQUAL PROJECT_BINARY_DIR)
  message(FATAL_ERROR "In-source builds not allowed. Please make a new directory (called a build directory) and run CMake from there.\n")
endif()


find_package(QT
    NAMES Qt6
    REQUIRED COMPONENTS
        Core
        Network
        Svg
        SvgWidgets
        Test
        Widgets
)

find_package(Qt6
    REQUIRED COMPONENTS
        Core
        Network
        Svg
        SvgWidgets
        Test
        Widgets
)

add_subdirectory(deps/fmt)

#
# Main
#
set(PROJECT_SOURCES
        src/assembler.h
        src/assembler.cpp
        src/executorthread.h
        src/executorthread.cpp
        src/main.cpp
        src/performancemeasurer.h
        src/performancemeasurer.cpp
        src/throttler.cpp
        src/throttler.h
        src/throttler.cpp
        src/emulation/dataport.h
        src/emulation/dataport.cpp
        src/emulation/intel8049.h
        src/emulation/intel8049.cpp
        src/emulation/intel8155.h
        src/emulation/intel8155.cpp
        src/emulation/ram.h
        src/emulation/ram.cpp
        src/ui/basewindow.h
        src/ui/basewindow.cpp
        src/ui/panel/cp1colors.h
        src/ui/panel/cp1colors.cpp
        src/ui/panel/cp1panelwidget.h
        src/ui/panel/cp1panelwidget.cpp
        src/ui/panel/cp1button.h
        src/ui/panel/cp1button.cpp
        src/ui/panel/cp1display.h
        src/ui/panel/cp1display.cpp
        src/ui/panel/cp1keyboard.h
        src/ui/panel/cp1keyboard.cpp
        src/ui/panel/cp1sevensegmentwidget.h
        src/ui/panel/cp1sevensegmentwidget.cpp
        src/ui/panel/cp5leds.h
        src/ui/panel/cp5leds.cpp
        src/ui/panel/cp5panelwidget.h
        src/ui/panel/cp5panelwidget.cpp
        src/ui/panel/cp5switches.h
        src/ui/panel/cp5switches.cpp
        src/ui/panel/cp5switch.h
        src/ui/panel/cp5switch.cpp
        src/ui/panel/panelwindow.h
        src/ui/panel/panelwindow.cpp
        src/ui/assembler/assemblerwindow.h
        src/ui/assembler/assemblerwindow.cpp
        src/ui/cpu/cpuwindow.h
        src/ui/cpu/cpuwindow.cpp
        src/ui/cpu/checkboxaction.h
        src/ui/cpu/checkboxaction.cpp
        src/ui/cpu/i8049disassemblywidget.h
        src/ui/cpu/i8049disassemblywidget.cpp
        src/ui/cpu/i8049statuswidget.h
        src/ui/cpu/i8049statuswidget.cpp
        src/ui/cpu/i8155statuswidget.h
        src/ui/cpu/i8155statuswidget.cpp
        src/ui/cpu/memorywidget.h
        src/ui/cpu/memorywidget.cpp
        src/ui/windowmanager.h
        src/ui/windowmanager.cpp
        src/ui/resources.h
        src/ui/resources.cpp
        src/util/disassembler.h
        src/util/disassembler.cpp
        resources/resources.qrc
)

#add_compile_options(-fsanitize=address)
#add_link_options(-fsanitize=address)

qt_add_executable(kosmos-cp1-cpp
    MANUAL_FINALIZATION
    ${PROJECT_SOURCES}
)
include_directories(
    deps/fmt/include
    src
)

target_link_libraries(kosmos-cp1-cpp
    PRIVATE
        Qt${QT_VERSION_MAJOR}::Core
        Qt${QT_VERSION_MAJOR}::Network
        Qt${QT_VERSION_MAJOR}::SvgWidgets
        Qt${QT_VERSION_MAJOR}::Widgets
        fmt
)

set_target_properties(kosmos-cp1-cpp PROPERTIES
    MACOSX_BUNDLE_GUI_IDENTIFIER my.example.com
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

install(TARGETS kosmos-cp1-cpp
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR})

qt_finalize_executable(kosmos-cp1-cpp)

#
# TOOLS
#

set(TOOLS_GENERATOR_SOURCES
    tools/generator/resources/resources.qrc
    tools/generator/main.cpp
    tools/generator/buttons.cpp
    tools/generator/buttons.h
    tools/generator/colors.cpp
    tools/generator/colors.h
    tools/generator/leds.cpp
    tools/generator/leds.h
    tools/generator/save.cpp
    tools/generator/save.h
    tools/generator/seven_segment.cpp
    tools/generator/seven_segment.h
    tools/generator/switches.h
    tools/generator/switches.cpp
)

qt_add_executable(generator
    MANUAL_FINALIZATION
    ${TOOLS_GENERATOR_SOURCES}
)

target_link_libraries(generator
    PRIVATE
        Qt${QT_VERSION_MAJOR}::Core
        Qt${QT_VERSION_MAJOR}::Network
        Qt${QT_VERSION_MAJOR}::Svg
        Qt${QT_VERSION_MAJOR}::Widgets
)

set_target_properties(generator PROPERTIES
    MACOSX_BUNDLE_GUI_IDENTIFIER my.example.com
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

qt_finalize_executable(generator)


##
## TESTS
##
#enable_testing(true)

#qt_add_executable(disassembler_6502_test
#    MANUAL_FINALIZATION
#    test/disassembler_6502_test.cpp
#    src/symtab.h
#    src/symtab.cpp
#    src/disassembler.h
#    src/disassembler.cpp
#    src/disassembler_6502.h
#    src/disassembler_6502.cpp
#)
#add_test(NAME disassembler_6502_test COMMAND disassembler_6502_test)

#target_link_libraries(disassembler_6502_test
#    PRIVATE
#        Qt${QT_VERSION_MAJOR}::Core
#        Qt${QT_VERSION_MAJOR}::Test
#)
#qt_finalize_executable(disassembler_6502_test)

#qt_add_executable(disassembler_z80_test
#    MANUAL_FINALIZATION
#    test/disassembler_z80_test.cpp
#    src/symtab.h
#    src/symtab.cpp
#    src/disassembler.h
#    src/disassembler.cpp
#    src/disassembler_z80.h
#    src/disassembler_z80.cpp
#)
#add_test(NAME disassembler_z80_test COMMAND disassembler_z80_test)

#target_link_libraries(disassembler_z80_test
#    PRIVATE
#        Qt${QT_VERSION_MAJOR}::Core
#        Qt${QT_VERSION_MAJOR}::Test
#)
#qt_finalize_executable(disassembler_z80_test)


#qt_add_executable(watches_test
#    MANUAL_FINALIZATION
#    test/watches_test.cpp
#    src/watches.h
#    src/watches.cpp
#)
#add_test(NAME watches_test COMMAND watches_test)

#target_link_libraries(watches_test
#    PRIVATE
#        Qt${QT_VERSION_MAJOR}::Core
#        Qt${QT_VERSION_MAJOR}::Test
#)
#qt_finalize_executable(watches_test)
