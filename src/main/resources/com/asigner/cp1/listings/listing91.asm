; CP5-Listing 2: Schalter-Test

        .ORG 1

loop    P1E 000     ;Port 1 einlesen und...
        ANZ         ;anzeigen
        P2A 000     ;an Port 2 ausgeben
        SPU loop    ;vorn vorn beginnen
