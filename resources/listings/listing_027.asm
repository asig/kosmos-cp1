; Listing 27: Automatisch schneller werdender Zähler

        .ORG 1

        AKO 000     ; \
        ABS 100     ; |
        AKO 120     ; |
        ABS 101     ; |
        AKO 001     ; |_ Zahlenwerte im Datenbereich speichern
        ABS 102     ; |
        AKO 030     ; |
        ABS 103     ; |
        ABS 104     ; /
loop    LIA 104     ; Lade in den Akku den Inhalt der Zelle, deren Adresse in Zelle 127 steht
        LDA 104     ; Lade in den Akku den Inhalt der Zelle 104 (Anfangsadresse)
        ADD 102     ; Addiere „1" dazu ...
        ABS 104     ; ... und speichere den neuen Akku-Inhalt in Zelle 104
        VKL 101     ; Ist Akku-Inhalt noch kleiner als „120"? (Endadresse)
        SPB 010     ; Wenn ja, lade weitere Speicherzellen-Inhalte in den Akku
        LDA 100     ; Wenn nein, so ist der Durchlauf beendet, daher lade Inhalt von Zählerzelle
        ADD 102     ; Addiere „1" dazu ...
        ABS 100     ; ... und speichere den neuen Akku-Inhalt in Zelle 100
        ANZ         ; Zeige ihn an
        LDA 103     ; Lade die Anfangsadresse in den Akku
        ADD 102     ; Addiere „1" dazu ...
        ABS 103     ; ... und speichere die neue Anfangsadresse in Zelle 103
        ABS 104     ; Speichere auch hier die neue Anfangsadresse
        VKL 101     ; Ist Akku-Inhalt noch kleiner als Endadresse?
        SPB loop    ; Wenn ja, beginne den nächsten Durchlauf mit der neuen Startadresse
        HLT         ; Wenn nein, halte an

        .ORG 100
        .DB 0       ; Speicherzelle für Zählerstand (Anfangswert 00.000)
        .DB 120     ; Vergleichszahl für Endadresse
        .DB 1       ; Schrittweite
        .DB 30      ; / Vergleichszahlen für
        .DB 30      ; \ Anfangsadressen
