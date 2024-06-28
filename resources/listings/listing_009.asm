; Listing 9: Stoppuhr mit externer Start/Stop-Taste

        .ORG 1

start   AKO 000         ; \
        ABS 100         ; |
        ABS 102         ; |_ Zahlenwerte im Datenbereich speichern
        AKO 001         ; |
        ABS 101         ; /
wait1   P1E 008         ; Bringe die Information von Port 1 / 8 in den Akku
        VGL 100         ; Prüfe, ob Akku-Inhalt „0" ist
        SPB 010         ; Wenn ja, springe nach 010
        SPU wait1       ; Wenn nein, springe zurück nach 006
wait2   P1E 008         ; Bringe die Information von Port 1/8 in den Akku
        VGL 101         ; Prüfe, ob Akku-Inhalt „1" ist
        SPB press       ; Wenn ja, springe zum Laden nach 014
        SPU wait2       ; Wenn nein, springe zurück nach 010
press   LDA 102         ; Lade den Inhalt der Zählzelle in den Akku
add     ADD 101         ; Addiere „1" dazu
        ABS 102         ; Speichere den neuen Akku-Inhalt in Zelle 102
        ANZ             ; Zeige ihn an
        VZG 087         ; Verzögere 87 ms
        P1E 008         ; Bringe die Information von Port 1 / 8 in den Akku
        VGL 100         ; Prüfe, ob Akku-Inhalt „0" ist
        LDA 102         ; Lade den Inhalt der Zählzelle in den Akku
        SPB disp        ; Wenn Akku-Inhalt „0" war, springe zum Anzeigen
        SPU add         ; Wenn nicht, springe zum Addieren
disp    ANZ             ; Zeige den Akku-Inhalt an
        VZG 250         ; Verzögere 250 ms
        VZG 250         ; Verzögere nochmal 250 ms
        SPU start       ; Springe zum Anfang zurück

        .ORG 100
        .DB 0           ; Vergleichszahl
        .DB 1           ; Vergleichszahl und Schrittweite
