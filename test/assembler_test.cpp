/*
 * Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <QTest>
#include <QString>

#include "assembler.h"

namespace kosmos_cp1::assembler {

class AssemblerTest: public QObject
{
    Q_OBJECT

public:
    AssemblerTest() {}

private:
    void checkContent(const std::vector<uint8_t>& actual, const std::vector<uint8_t>& expected) {
        for (int i = 0; i < expected.size(); i++) {
            QCOMPARE(actual[i], expected[i]);
        }
        for (int i = expected.size(); i < 256; i++) {
            QCOMPARE(actual[i], 0);
        }
    }

private slots:
    void testBasics() {
        QString source(
                " LDA 100 ; 05.100 Inhalt von Zelle 100 in den Akku laden                  \n"
                " ANZ     ; 02.000; Akku-Inhalt anzeigen                                   \n"
                " VZG 250 ; 03.250 250 ms verzögern                                        \n"
                " LDA 101 ; 05.101 Inhalt von Zelle 101 in den Akku laden                  \n"
                " ANZ     ; 02.000 Akku-Inhalt anzeigen                                    \n"
                " VZG 250 ; 03.250 250 ms verzögern                                        \n"
                " LDA 102 ; 05.102 Inhalt von Zelle 102 in den Akku laden                  \n"
                " ANZ     ; 02.000 Akku-Inhalt anzeigen                                    \n"
                " VZG 250 ; 03.250 250 ms verzögern                                        \n"
                " LDA 103 ; 05.103 Inhalt von Zelle 103 in den Akku laden                  \n"
                " ANZ     ; 02.000 Akku-Inhalt anzeigen                                    \n"
                " VZG 250 ; 03.250 250 ms verzögern                                        \n"
                " LDA 104 ; 05.104 Inhalt von Zelle 104 in den Akku laden                  \n"
                " ANZ     ; 02.000 Akku-Inhalt anzeigen                                    \n"
                " VZG 250 ; 03.250 250 ms verzögern                                        \n"
                " HLT     ; 01.000 Anhalten                                                \n"
                " .DB 11  ; 00.011                                                         \n"
                " .DB 22  ; 00.022                                                         \n"
                " .DB 33  ; 00.033 Zahlenwerte, die der Reihe nach angezeigt werden sollen \n"
                " .DB 44  ; 00.044                                                         \n"
                " .DB 55  ; 00.055                                                         \n");

        Assembler assembler(source);
        std::vector<QString> errors = assembler.assemble();
        QCOMPARE(errors.size(), 0);
        std::vector<uint8_t> code = assembler.code();
        QCOMPARE(code.size(), 256);
        checkContent(code,{
                5,100,
                2, 0,
                3,250,
                5,101,
                2,0,
                3,250,
                5,102,
                2,0,
                3,250,
                5,103,
                2,0,
                3,250,
                5,104,
                2,0,
                3,250,
                1,0,
                0,11,
                0,22,
                0,33,
                0,44,
                0,55});

    }

    void testForwardReference() {
        QString source(
                "    LDA L \n"
                "    AKO L \n"
                "L   HLT \n");

        Assembler assembler(source);
        std::vector<QString> errors = assembler.assemble();
        QCOMPARE(errors.size(), 0);
        std::vector<uint8_t> code = assembler.code();
        QCOMPARE(code.size(), 256);
        checkContent(code,{
                5,2,
                4, 2,
                1,0});
    }

    void testBackwardReference() {
        QString source(
                "    .ORG 5 \n"
                "    AKO 0 \n"
                "L   AKO 0 \n"
                "    AKO 0 \n"
                "    SPU  L \n");

        Assembler assembler(source);
        std::vector<QString> errors = assembler.assemble();
        QCOMPARE(errors.size(), 0);
        std::vector<uint8_t> code = assembler.code();
        QCOMPARE(code.size(), 256);
        checkContent(code,{
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                4, 0,
                4, 0,
                4, 0,
                9, 6});
    }

    void testRaw() {
        QString source(
                "    .RAW 12.123, 23.234, 22.222 \n"
                "    .RAW 11.111 \n");

        Assembler assembler(source);
        std::vector<QString> errors = assembler.assemble();
        QCOMPARE(errors.size(), 0);
        std::vector<uint8_t> code = assembler.code();
        QCOMPARE(code.size(), 256);
        checkContent(code,{
                12, 123,
                23, 234,
                22, 222,
                11, 111});
    }

    void testOrg() {
        QString source(
                " .ORG 5 \n"
                " LDA 100 \n"
                ".ORG 10 \n"
                " VZG 250\n");

        Assembler assembler(source);
        std::vector<QString> errors = assembler.assemble();
        QCOMPARE(errors.size(), 0);
        std::vector<uint8_t> code = assembler.code();
        QCOMPARE(code.size(), 256);
        checkContent(code,{
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                5,100,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                3,250});
    }

    void testEqu() {
        QString source(
                " .EQU foo, 250  \n"
                " VZG foo \n");

        Assembler assembler(source);
        std::vector<QString> errors = assembler.assemble();
        QCOMPARE(errors.size(), 0);
        std::vector<uint8_t> code = assembler.code();
        QCOMPARE(code.size(), 256);
        checkContent(code,{
                3,250
        });
    }


    void cleanupTestCase() {
    }

};

}

QTEST_MAIN(kosmos_cp1::assembler::AssemblerTest)

#include "assembler_test.moc"
