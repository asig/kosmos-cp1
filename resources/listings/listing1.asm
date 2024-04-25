; Listing 1: Speicherzellen-Inhalte automatisch anzeigen

        .ORG 1
        
        LDA 100 ; Inhalt von Zelle 100 in den Akku laden
        ANZ     ;  Akku-Inhalt anzeigen
        VZG 250 ; 250 ms verzögern
        LDA 101 ; Inhalt von Zelle 101 in den Akku laden
        ANZ     ; Akku-Inhalt anzeigen
        VZG 250 ; 250 ms verzögern
        LDA 102 ; Inhalt von Zelle 102 in den Akku laden
        ANZ     ; Akku-Inhalt anzeigen
        VZG 250 ; 250 ms verzögern
        LDA 103 ; Inhalt von Zelle 103 in den Akku laden
        ANZ     ; Akku-Inhalt anzeigen
        VZG 250 ; 250 ms verzögern
        LDA 104 ; Inhalt von Zelle 104 in den Akku laden
        ANZ     ; Akku-Inhalt anzeigen
        VZG 250 ; 250 ms verzögern
        HLT     ; Anhalten

        .ORG 100
        .DB 11 ; \
        .DB 22 ; |
        .DB 33 ; |_ Zahlenwerte, die der Reihe nach angezeigt werden sollen
        .DB 44 ; |
        .DB 55 ; /
