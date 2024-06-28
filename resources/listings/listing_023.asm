; Listing 23: Division

        .ORG 1

        AKO 000     ; \
        ABS 102     ; |_ Zahlenwerte im Datenbereich
        ABS 101     ; |
        AKO 103     ; /
        LDA 100     ; Dividenden in den Akku laden ...
        ABS 000     ; und in der Restzelle speichern
loop    LDA 000     ; Restzelle in den Akku laden
        VKL 101     ; Kleiner als Divisor?
        SPB display ; Wenn ja, keine Division möglich, zum Anzeigen springen
        SUB 101     ; Wenn nein, Divisor subtrahieren
        ABS 000     ; Zwischenergebnis in „Restzelle" speichern
        LDA 102     ; Inhalt der „Ergebniszelle" in den Akku laden
        ADD 103     ; Eine „1" addieren ...
        ABS 102     ; ... und wieder in „Ergebniszelle" speichern
        SPU loop    ; Einen neuen Durchlauf beginnen
display LDA 102     ; Inhalt der „Ergebniszelle" in den Akku laden
        ANZ         ; Ergebnis anzeigen
        SPU display ; Zurückspringen zum Anzeigen

        .ORG 100
        .DB ?       ; Dividend
        .DB ?       ; Divisor
        .DB ?       ; „Ergebniszelle"

        .ORG 0
        .DB ?       ; „Restzelle"
