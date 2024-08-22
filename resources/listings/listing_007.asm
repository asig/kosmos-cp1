; Listing 7: Würfelprogramm mit VGR

        .ORG 1

start   AKO 001     ; In den Akku eine „1" laden
        ABS 101     ; Akku-Inhalt in Speicherzelle 101 speichern
loop    ABS 000     ; Akku-Inhalt in Speicherzelle 000 speichern
        ADD 101     ; Zum Akku-Inhalt „1" addieren
        VGR 100     ; Prüfen, ob Akku-Inhalt größer als „6" ist
        SPB start   ; Wenn ja, bei 001 neu beginnen
        SPU loop    ; Wenn nein, zum Speichern springen

        .ORG 100
        .DB 6       ; Vergleichszahl
        .DB 1       ; Schrittweite
