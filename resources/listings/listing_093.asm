; CP5-Listing 4: Lichtpunkt fangen

        .ORG 1
        
start   AKO 128     ; \
        ABS 101     ; |_ Vorbelegen des Datenbereiches
l2      AKO 001     ; |
l3      ABS 100     ; /
        P1E 000     ; Port 1 einlesen...
        ABS 102     ; und in 102 speichern
        AKO 255     ; "255" laden...
        SUB 102     ; und den eingelesenen Portwert abziehen
        VGL 100     ; ist die Taste betätigt?
        SPB l       ; wenn ja, springe nach 020
        LDA 100     ; sonst lade den Inhalt von 100 und...
        P2A 000     ; gib ihn aus
        VZG 050     ; verzögere 50 ms
        P1E 000     ; lies Port 1 ein
        ABS 102     ; speichere in 102
        AKO 255     ; lade "255"
        SUB 102     ; und subtrahiere den eingelesenen Portwert
        VGL 100     ; ist die Taste betätigt?
        SPB end     ; wenn ja, springe zum Halt
l       LDA 100     ; sonst lade nochmal den Inhalt von 100...
        VGL 101     ; und prüfe, ob er schon "128" ist
        SPB l2      ; wenn ja, beginne wieder von vorn
        ADD 100     ; sonst verdopple den Akku-Inhalt und...
        SPU l3      ; beginne einen neuen Durchlauf
end     HLT         ; anhalten
        SPU start   ; beginne ein neues Spiel bei 001