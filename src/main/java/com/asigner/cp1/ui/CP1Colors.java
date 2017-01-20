/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.asigner.cp1.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class CP1Colors {
    public static final Color PANEL_BACKGROUND = SWTResources.getColor(new RGB(47,47,47));
    public static final Color GREEN = SWTResources.getColor(new RGB(59, 66, 55));
    public static final Color GREEN_LIGHT = SWTResources.getColor(new RGB(89, 99, 83));
    public static final Color GREEN_DARK = SWTResources.getColor(new RGB(30, 34, 28));

    public static final Color SEGMENT_ON = SWTResources.getColor(new RGB(255, 0, 0));
    public static final Color SEGMENT_OFF = SWTResources.getColor(new RGB(64, 0, 0));
    public static final Color SEGMENT_BG = SWTResources.getColor(new RGB(32, 0, 0));
}
