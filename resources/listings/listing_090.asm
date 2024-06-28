; CP5-Listing 1: LED-Test

        .ORG 1

        AKO 128     ; \
        ABS 101     ; |_ Vorbelegen des Datenbereiches
        AKO 001     ; |
loop    ABS 100     ; /
        P2A 000     ;  Akku-Inhalt ausgeben
        VZG 250     ; 1/4 Sekunde verzögern
        VGL 101     ; Akku gleich »128«?
        SPB 003     ; wenn ja, von vorn anfangen
        LDA 100     ;
        ADD 100     ; sonst Akku-Inhalt verdoppeln und...
        SPU loop    ; zu einem neuen Durchgang springen