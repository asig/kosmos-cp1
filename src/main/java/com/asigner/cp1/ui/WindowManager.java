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

import com.google.common.collect.Lists;

import java.util.List;

public class WindowManager {

    private final List<Window> windows = Lists.newArrayList();

    private int openCount;

    public WindowManager() {
        openCount = 0;
    }

    public int getOpenCount() {
        return openCount;
    }

    public void addWindow(Window window) {
        windows.add(window);
        window.addWindowListener(new Window.Listener() {
            @Override
            public void windowOpened(Window window) {
                openCount++;
            }

            @Override
            public void windowClosed(Window window) {
                openCount--;
            }
        });
    }

    public List<Window> getWindows() {
        return windows;
    }
    
    public void openAll() {
        for (Window w : windows) {
            w.open();
        }
    }

}
