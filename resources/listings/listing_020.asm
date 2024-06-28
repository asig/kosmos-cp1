; Listing 20: Reisebüro-Computer

        .ORG 1

        AKO 001     ; \
        ABS 123     ; |
        AKO 000     ; |
        ABS 124     ; |
        ABS 126     ; |_ Zahlenwerte im Datenbereich abspeichern
        AKO 119     ; |
        ABS 125     ; |
        AKO 030     ; |
        ABS 127     ; /
loop    LIA 127     ; Akku indirekt laden. Die Lade-Adresse steht in Zelle 127
        VGL 124     ; Ist Akku-Inhalt gleich „0"?
        SPB is_zero ; Wenn ja, nach 019 springen
        LDA 127     ; Wenn nein, den Inhalt von 127 in den Akku laden
        ADD 123     ; Zum Akku-Inhalt „1 " addieren
        ABS 127     ; Neuen Akku-Inhalt in 127 speichern
        VGR 125     ; Ist Akku-Inhalt größer als „119"?
        SPB max     ; Wenn ja, nach 023 springen
        SPU loop    ; Wenn nein, nach 010 springen
is_zero LDA 126     ; Inhalt von 126 in den Akku laden
        ADD 123     ; Zum Akku-Inhalt eine „1 " addieren
        ABS 126     ; Neuen Akku-Inhalt in 126 speichern
        SPU 013     ; Nach 013 springen
max     LDA 126     ; Inhalt von 126 in den Akku laden
l2      ANZ         ; Akku-Inhalt anzeigen
        SPU l2      ; Nach 024 springen (Dauerschleife, Akku-Inhalt soll angezeigt bleiben)

        .ORG 123
        .DB 1       ; Schrittweite
        .DB 0       ; Vergleichszahl
        .DB 119     ; Schlußadresse
        .DB 0       ; „Betten-Zählzelle"
        .DB 30      ; Startadresse
