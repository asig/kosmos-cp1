; Listing 3: Automatischer Zähler mit Digitalanzeige

        .ORG 1

        LDA 100     ; Inhalt von Zelle 100 in den Akku laden
        ANZ         ; Akku-Inhalt anzeigen
        VZG 250     ; 250 ms verzögern
loop    ADD 100     ; Zum Akku-Inhalt den Inhalt von Zelle 100 addieren
        ANZ         ; Akku-Inhalt anzeigen
        VZG 250     ; 250 ms verzögern
        SPU loop    ; Zum Addieren auf Adresse 004 springen

        .ORG 100
        .DB 1       ; Schrittweite
