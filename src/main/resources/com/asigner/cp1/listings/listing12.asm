; Listing 12: Wechselblinker mit externer Start/Stop-Taste

        .ORG 1

start   AKO 000     ; In den Akku eine „0" laden
        P1A 001     ; Akku-Inhalt auf Klemme 1 des Port 1 ausgeben
        P1A 002     ; Akku-Inhalt auf Klemme 2 des Port 1 ausgeben
wait1   P1E 008     ; Information von Klemme 8 des Port 1 in den Akku bringen
        VGL 100     ; Akku-Inhalt mit Inhalt v. Zelle 100 vergleichen
        SPB l1      ; Wenn Gleichheit, dann nach 008 springen
        SPU wait1   ; Wenn nicht Gleichheit, nach 004 springen
l1      P1E 008     ; Information v. Klemme 8 des Port 1 in den Akku bringen
        VGL 101     ; Akku-Inhalt mit Inhalt von Zelle 101 vergleichen
        SPB l2      ; Wenn Gleichheit, dann auf 012 springen
        SPU l1      ; Wenn nicht Gleichheit, auf 008 springen
l2      AKO 001     ; In den Akku eine „1" laden
        P1A 001     ; Akku-Inhalt auf Klemme 1 des Port 1 ausgeben
        AKO 000     ; In den Akku eine „0" laden
        P1A 002     ; Akku-Inhalt auf Klemme 2 des Port 1 ausgeben
        VZG 250     ; 250 ms verzögern
        AKO 000     ; In den Akku eine „0" laden
        P1A 001     ; Akku-Inhalt auf Klemme 1 des Port 1 ausgeben
        AKO 001     ; In den Akku eine „1" laden
        P1A 002     ; Akku-Inhalt auf Klemme 2 des Port 1 ausgeben
        VZG 250     ; 250 ms verzögern
        P1E 008     ; Information v. Klemme 8 des Port 1 in den Akku bringen
        VGL 100     ; Akku-Inhalt mit Inhalt von Zelle 100 vergleichen
        SPB l3      ; Wenn Gleichheit, dann auf 026 springen
        SPU l2      ; Wenn nicht Gleichheit, auf 012 springen
l3      VZG 250     ; Verzögere 250 ms
        VZG 250     ; Verzögere nochmals 250 ms
        SPU wait1   ; Springe zum Anfang (nach 004) zurück

        .ORG 100
        .DB 0       ; 1. Vergleichszahl
        .DB 1       ; 2. Vergleichszahl
