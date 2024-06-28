; Listing 17: Blinkprogramm mit 4 Zuständen

        .ORG 1

start   AKO 000     ; In den Akku „0" laden
        P1A 000     ; Akku-Inhalt als Dualzahl auf Port 1 bringen
        VZG 250     ; 250 ms verzögern
        AKO 001     ; In den Akku „1" laden
        P1A 000     ; Akku-Inhalt als Dualzahl auf Port 1 bringen
        VZG 250     ; 250 ms verzögern
        AKO 002     ; In den Akku eine „2" laden
        P1A 000     ; Akku-Inhalt als Dualzahl auf Port 1 bringen
        VZG 250     ; 250 ms verzögern
        AKO 003     ; In den Akku eine „3" laden
        P1A 000     ; Akku-Inhalt als Dualzahl auf Port 1 bringen
        VZG 250     ; 250 ms verzögern
        SPU start   ; Zurück nach 001 springen
