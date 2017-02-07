; Listing 13: „Regenbogen"-Programm

        .ORG 1

start   P1E 001     ; Information von Klemme 1 des Port 1 in den Akku bringen
        ABS 100     ; Akku-Inhalt in Zelle 100 speichern
        P1E 002     ; Information von Klemme 2 des Port 1 in den Akku bringen
        UND 100     ; Akku-Inhalt und Inhalt v. Speicherz. 100 UND-verknüpfen
        ANZ         ; Akku-Inhalt anzeigen
        SPU start   ; Zurück nach 001 springen
