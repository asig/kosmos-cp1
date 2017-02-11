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

package com.asigner.cp1.ui.actions;

import com.asigner.cp1.ui.Window;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import java.awt.event.WindowListener;
import java.util.Timer;
import java.util.TimerTask;

public class WindowAction extends Action {

    private final Window window;

    public WindowAction(Window window) {
        super(window.getName());
        this.window = window;
        setEnabled(!window.isOpen());
        window.addWindowListener(new Window.Listener() {
            // For some reason, the menu item is still in disposed state when this is called.
            // In this case, we just create a timer that enables the action after some delay.
            // I have *NO* clue how to do this properly...

            @Override
            public void windowOpened(Window window) {
                setEnabledDelayed(false);
            }

            @Override
            public void windowClosed(Window window) {
                setEnabledDelayed(true);
            }
        });
    }

    private void setEnabledDelayed(boolean enabled) {
        try {
            setEnabled(enabled);
        } catch (SWTException e) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Display.getDefault().asyncExec(() ->  setEnabled(enabled));
                }
            }, 200);
        }
    }

    @Override
    public void run() {
        window.open();
    }

}
