; Listing 18: Blinkprogramm mit zwei Lämpchen im Parallelbetrieb

        .ORG 1

start   AKO 000 ; In den Akku eine „0" laden
        P1A 000 ; Akku-Inhalt als Dualzahl auf Port 1 bringen
        VZG 250 ; 250 ms verzögern
        AKO 003 ; In den Akku eine „3" laden
        P1A 000 ; Akku-Inhalt als Dualzahl auf Port 1 bringen
        VZG 250 ; 250 ms verzögern
        SPU start ; Zurück nach 001 springen
