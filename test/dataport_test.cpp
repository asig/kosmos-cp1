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


#include "emulation/dataport.h"

namespace kosmos_cp1::emulation {

class DataportRecorder: public QObject
{
    Q_OBJECT

public:
    uint8_t bits_[8] = {0,0,0,0,0,0,0,0};

    uint8_t val() {
        return bits_[7] << 7 | bits_[6] << 6 | bits_[5] << 5 | bits_[4] << 4 | bits_[3] << 3 | bits_[2] << 2 | bits_[1] << 1 | bits_[0];
    }

    void clear() {
        bits_[0] = 0;
        bits_[1] = 0;
        bits_[2] = 0;
        bits_[3] = 0;
        bits_[4] = 0;
        bits_[5] = 0;
        bits_[6] = 0;
        bits_[7] = 0;
    }

public slots:
    void bit0Written(int val) {
        bits_[0] = val;
    }

    void bit1Written(int val) {
        bits_[1] = val;
    }

    void bit2Written(int val) {
        bits_[2] = val;
    }

    void bit3Written(int val) {
        bits_[3] = val;
    }

    void bit4Written(int val) {
        bits_[4] = val;
    }

    void bit5Written(int val) {
        bits_[5] = val;
    }

    void bit6Written(int val) {
        bits_[6] = val;
    }

    void bit7Written(int val) {
        bits_[7] = val;
    }
};


class DataportTest: public QObject
{
    Q_OBJECT

public:
    DataportTest() {}


private slots:
    void testBits() {
        DataPort p("port");
        DataportRecorder rec;

        connect(&p, &DataPort::bit0Written, &rec, &DataportRecorder::bit0Written);
        connect(&p, &DataPort::bit1Written, &rec, &DataportRecorder::bit1Written);
        connect(&p, &DataPort::bit2Written, &rec, &DataportRecorder::bit2Written);
        connect(&p, &DataPort::bit3Written, &rec, &DataportRecorder::bit3Written);
        connect(&p, &DataPort::bit4Written, &rec, &DataportRecorder::bit4Written);
        connect(&p, &DataPort::bit5Written, &rec, &DataportRecorder::bit5Written);
        connect(&p, &DataPort::bit6Written, &rec, &DataportRecorder::bit6Written);
        connect(&p, &DataPort::bit7Written, &rec, &DataportRecorder::bit7Written);


        for(uint8_t i = 0; i < 8; i++) {
            rec.clear();
            p.write(1 << i);
            for(int j = 0; j < 8; j++) {
                if (j == i) {
                    QVERIFY(rec.bits_[j] == 1);
                } else {
                    QVERIFY(rec.bits_[j] == 0);
                }
            }
            QVERIFY(rec.val() == 1 << i);
        }
    }

    void cleanupTestCase() {
    }

};

}

QTEST_MAIN(kosmos_cp1::emulation::DataportTest)

#include "dataport_test.moc"

