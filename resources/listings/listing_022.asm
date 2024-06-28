; Listing 22: Multiplikation 25 x 9

        .ORG 1

        AKO 000     ; \
        ABS 100     ; |
        ABS 101     ; |_ Zahlenwerte im Datenbereich speichern
        AKO 001     ; |
        ABS 102     ; /
        LDA 100     ; Lade den Inhalt der Ergebniszelle in den Akku
loop    ADD 103     ; Addiere „25" dazu (erster Faktor)
        ABS 100     ; Speichere das Additionsergebnis
        LDA 101     ; Lade den Inhalt der Zählzelle in den Akku
        ADD 102     ; Addiere „1" dazu
        ABS 101     ; Speichere den neuen Zählerstand
        VGL 104     ; Prüfe (vergleiche), ob bereits 9 mal (zweiter Faktor) addiert wurde
        LDA 100     ; Lade den Inhalt der Ergebniszelle in den Akku
        SPB display ; Wenn Gleichheit bei 012, dann springe zur Anzeige
        SPU loop    ; Wenn nicht, fahre auf 007 mit dem Addieren fort
display ANZ         ; Zeige das Ergebnis an
        SPU display ; Springe zurück zum Anzeigen

        .ORG 100
        .DB ?       ; Ergebniszelle (wird zu Beginn auf 00.000 gesetzt)
        .DB ?       ; Zählzelle (wird zu Beginn auf 00.000 gesetzt)
        .DB 1       ; Schrittweite
        .DB 25      ; Erster Faktor
        .DB 9       ; Zweiter Faktor
