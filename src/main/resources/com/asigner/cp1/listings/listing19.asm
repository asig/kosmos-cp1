; Listing 19: Automatisch speichernder Lottozahlen-Generator

        .ORG 1

        AKO 001     ; \
        ABS 100     ; |
        AKO 030     ; |
        ABS 104     ; |
        AKO 049     ; |_ Zahlenwerte im Datenbereich speichern
        ABS 101     ; |
start   AKO 000     ; |
        ABS 103     ; |
        ABS 102     ; /

add     ADD 100     ; Addiere ,,1 " zum Akku-Inhalt
        ANZ         ; Zeige den Akku-Inhalt an
        ABS 102     ; Speichere ihn in der Zählzelle
        P1E 001     ; Bringe die Information von Klemme 1 des Port 1 in den Akku
        VGR 103     ; Prüfe, ob Taste 1 nicht gedrückt ist
        LDA 102     ; Lade den Inhalt der Zählzelle in den Akku
        SPB ng      ; Wenn Taste nicht gedrückt, springe zum Vergleich nach 025
        AIS 104     ; Speichere den Akku-Inhalt in der Speicherzelle, deren Adresse in 104 steht
        LDA 104     ; Lade den Inhalt von 104 in den Akku
        ADD 100     ; Addiere „1 " dazu
        ABS 104     ; Speichere den neuen Akku-Inhalt in 104
check   P1E 001     ; Bringe die Information von Klemme 1 des Port 1 in den Akku
        VGL 103     ; Prüfe, ob Taste 1 gedrückt ist
        SPB check   ; Wenn Taste gedrückt, springe zur Abfrage zurück
        SPU start   ; Sonst springe zurück zu einem neuen Durchlauf
ng      VGL 101     ; Prüfe, ob die Zählgrenze erreicht ist
        SPB start   ; Wenn ja, springe zum Anfang zurück
        SPU add     ; Sonst springe zum Addieren

        .ORG 100
        .DB 001     ; Schrittweite und Vergleichszahl
        .DB 049     ; Vergleichszahl
        .DB 0       ; Zählzelle
        .DB 0       ; Vergleichszahl
        .DB 030     ; Startadresse
