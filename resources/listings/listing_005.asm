; Listing 5: Zähler, der 50 mal bis 25 zählt

        .ORG 1

start   LDA 100     ; Inhalt von Zelle 100 in den Akku laden
        ANZ         ; Akku-Inhalt anzeigen
        VZG 250     ; 250 ms verzögern
l1      ADD 100     ; Zum Akku-Inhalt den Inhalt von Zelle 100 addieren
        ANZ         ; Akku-Inhalt anzeigen
        VZG 250     ; 250 ms verzögern
        VGL 101     ; Akku-Inhalt mit Inhalt von Zelle 101 vergleichen
        SPB e1      ; Wenn Gleichheit, dann auf 010 springen
        SPU l1      ; Wenn nicht Gleichheit, dann auf 004 springen
e1      LDA 103     ; Inhalt von Zelle 103 in den Akku laden
        ADD 100     ; Zum Akku-Inhalt den Inhalt von Zelle 100 addieren
        ABS 103     ; Akku-Inhalt in Zelle 103 speichern
        VGL 102     ; Akku-Inhalt mit dem Inhalt von Zelle 102 vergleichen
        SPB e2      ; Wenn Gleichheit, dann auf 016 springen
        SPU start   ; Wenn nicht Gleichheit, dann auf 001 springen
e2      HLT         ; Anhalten

        .ORG 100
        .DB 1       ; Schrittweite
        .DB 25      ; 1. Vergleichszahl
        .DB 50      ; 2. Vergleichszahl

