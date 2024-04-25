; Listing 4: Unendlicher Zähler

        .ORG 1

start   LDA 100   ; Inhalt von Zelle 100 in den Akku laden
        ANZ       ; Akku-Inhalt anzeigen
        VZG 250   ; 250 ms verzögern
loop    ADD 100   ; Zum Akku-Inhalt Inhalt von Zeile 100 addieren
        ANZ       ; Akku-Inhalt anzeigen
        VZG 250   ; 250 ms verzögern
        VGL 101   ; Akku-Inhalt mit Inhalt von Zelle 101 vergleichen
        SPB start ; Wenn Gleichheit dann auf 001 springen
        SPU loop  ; Wenn nicht Gleichheit, auf 004 springen

        .ORG 100
        .DB 1     ; Schrittweite
        .DB 25    ; Vergleichszahl
