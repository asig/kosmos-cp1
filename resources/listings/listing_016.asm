; Listing 16: Wechselblinker mit dem P1A 000-Befehl

        .ORG 1

start   AKO 001     ; In den Akku „1" laden
        P1A 000     ; Akku-Inhalt als Dualzahl auf Port 1 bringen
        VZG 250     ; 250 ms verzögern
        AKO 002     ; In den Akku „2" laden
        P1A 000     ; Akku-Inhalt als Dualzahl auf Port 1 bringen
        VZG 250     ; 250 ms verzögern
        SPU start   ; Zurück nach 001 springen
