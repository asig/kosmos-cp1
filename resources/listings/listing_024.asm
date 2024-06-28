; Listing 24: 1-Minuten-Verzögerung

;   •   •   •       ; \
;   •   •   •       ; |_ Irgendein Programmteil
;   •   •   •       ; /

        .ORG 68

loop    VZG 250     ; Verzögere 250 ms
        LDA 100     ; Lade den Inhalt von Zelle 100 in den Akku
        SUB 101     ; Subtrahiere „1"
        ABS 100     ; Speichere neuen Akku-Inhalt in 100
        VGR 102     ; Ist der Akku-Inhalt größer als „0"
        SPB loop    ; Wenn ja, springe zurück zum Verzögern

        .ORG 100
        .DB 240     ; Zählerzelle
        .DB 1       ; Schrittweite
        .DB 0       ; Vergleichszahl
