; Listing 8: Würfelprogramm mit VKL

        .ORG 1

start   AKO 001     ; In den Akku eine „1" laden
        ABS 101     ; Akku-Inhalt in Speicherzelle 101 speichern
loop    ABS 000     ; Akku-Inhalt in Speicherzelle 000 speichern
        ADD 101     ; Zum Akku-Inhalt „1" addieren
        VKL 100     ; Prüfen, ob Akku-Inhalt kleiner als „7" ist
        SPB loop    ; Wenn ja, zum Speichern springen
        SPU start   ; Wenn nein, bei 001 neu beginnen

        .ORG 100
        .DB 7       ; Vergleichszahl
        .DB 1       ; Schrittweite
