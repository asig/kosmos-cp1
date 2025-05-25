#!/bin/bash

TMPDIR=$(mktemp -d)

# Create ICO file for Windows
ICONS=""
for sz in 16 24 32 48 256; do
    f=${TMPDIR}/icon-${sz}x${sz}.png
    ICONS="${ICONS} ${f}"
    inkscape -w ${sz} artwork/icon.svg -o ${TMPDIR}/t.png
    convert ${TMPDIR}/t.png -gravity center -background none -extent ${sz}x${sz} ${f}
    # convert artwork/icon.svg -size ${sz}x${sz} ${f}
done
convert ${ICONS} resources/ui/icon.ico

# Create icns file for Mac (    )
mkdir ${TMPDIR}/icon.iconset
for sz in 16 32 128 256 512; do
    f=${TMPDIR}/icon.iconset/icon_${sz}x${sz}.png
    inkscape -w ${sz} artwork/icon.svg -o ${TMPDIR}/t.png
    convert ${TMPDIR}/t.png -gravity center -background none -extent ${sz}x${sz} ${f}

    f=${TMPDIR}/icon-${sz}x${sz}@2x.png
    (( sz2 = 2 * sz ))
    inkscape -w ${sz2} artwork/icon.svg -o ${TMPDIR}/t.png
    convert ${TMPDIR}/t.png -gravity center -background none -extent ${sz2}x${sz2} ${f}
done
png2icns resources/ui/icon.icns ${TMPDIR}/icon.iconset/*.png

# Create PNG files for Linux
for sz in 16 24 32 48 64 128 256; do
    f=resources/ui/icon-${sz}x${sz}.png
    inkscape -w ${sz} artwork/icon.svg -o ${f}
    convert ${f} -gravity center -background none -extent ${sz}x${sz} ${f}
done
