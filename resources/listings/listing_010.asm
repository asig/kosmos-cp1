; Listing 10: Einfacher Blinker

        .ORG 1

start   AKO 000     ; In den Akku eine „0" laden
        P1A 001     ; Akku-Inhalt auf Klemme 1 des Port 1 bringen
        VZG 100     ; 100 ms verzögern
        AKO 001     ; In den Akku „1" laden
        P1A 001     ; Akku-Inhalt auf Klemme 1 des Port 1 bringen
        VZG 100     ; 100 ms verzögern
        SPU start   ; Zurück nach 001 springen
