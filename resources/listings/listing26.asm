; Listing 26: Schiebeprogramm

;•      •
;•      •

        .ORG 88

loop    LIA 100     ; Lade in den Akku den Inhalt der Zelle, deren Adresse in Zelle 100 steht
        AIS 101     ; Speichere den Akku-Inhalt in der Zelle, deren Adresse in Zelle 101 steht
        LDA 101     ; Lade den Inhalt von Zelle 101 in den Akku
        SUB 103     ; Subtrahiere „1" vom Akku-Inhalt
        ABS 101     ; Speichere den neuen Akku-Inhalt in Zelle 101
        LDA 100     ; Lade den Inhalt der Zelle 100 in den Akku
        SUB 103     ; Subtrahiere „1" vom Akku-Inhalt
        ABS 100     ; Speichere den neuen Akku-Inhalt in Zelle 100
        VKL 102     ; Ist der Akku-Inhalt kleiner als die Anfangsadresse des Urprogramms?
        SPB halt    ; Wenn ja, springe zum Halt
        SPU loop    ; Wenn nein, beginne wieder von vorn
halt    HLT         ; Halte an

        .ORG 100
        .DB 7       ; Endadresse des Urprogramms
        .DB 13      ; Endadresse des Zielprogramms
        .DB 1       ; Anfangsadresse des Urprogramms
        .DB 1       ; Schrittweite
