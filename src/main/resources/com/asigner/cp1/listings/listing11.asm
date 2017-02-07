; Listing 11: Wechselblinker

        .ORG 1

start   AKO 000     ; In den Akku eine „0 " laden
        P1A 001     ; Akku-Inhalt auf Klemme 1 des Port 1 bringen
        AKO 001     ; In den Akku eine „1 " laden
        P1A 002     ; Akku-Inhalt auf Klemme 2 des Port 1 bringen
        VZG 250     ; 250 ms verzögern
        AKO 001     ; In den Akku eine „1 " laden
        P1A 001     ; Akku-Inhalt auf Klemme 1 des Port 1 bringen
        AKO 000     ; In den Akku eine „0 " laden
        P1A 002     ; Akku-Inhalt auf Klemme 2 des Port 1 bringen
        VZG 250     ; 250 ms verzögern
        SPU start   ; Zurück nach 001 springen
