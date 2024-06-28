; Listing 14: Heizungssteuerung

        .ORG 1

start   P1E 001     ; Information von Port 1 / 1 in den Akku bringen
        ABS 100     ; Akku-Inhalt in 100 speichern
        P1E 002     ; Information von Port 112 in den Akku bringen
        UND 100     ; UND-Verknüpfung mit Zelle 100
        ANZ         ; Anzeigen
        P2A 001     ; Akku-Inhalt an Port 2/ 1 ausgeben
        VZG 250     ; \
        VZG 250     ; |_ 1 Sekunde verzögern
        VZG 250     ; |
        VZG 250     ; /
        SPU start   ; Sprung Zurück an den Anfang

        .ORG 100
        .DB 0       ; Zwischenspeicher
