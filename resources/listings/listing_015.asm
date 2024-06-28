; Listing 15: Prüfprogramm für Negation

        .ORG 1

        AKO 001     ; „1" in den Akku laden
loop    ANZ         ; Akku-Inhalt anzeigen
        VZG 250     ; 250 ms verzögern
        NEG         ; Akku-Inhalt negieren
        SPU loop    ; Zurück zum Anzeigen springen
