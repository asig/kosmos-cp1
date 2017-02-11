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

import com.asigner.cp1.ui.actions.WindowAction;
import com.asigner.cp1.ui.widgets.ActionMenuItem;
import com.google.common.collect.Lists;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import java.util.List;

public abstract class Window {

    public interface Listener {
        void windowOpened(Window window);
        void windowClosed(Window window);
    }

    private final List<Listener> listeners = Lists.newLinkedList();
    private final WindowManager windowManager;
    private final String name;
    private boolean isOpen;

    public Window(WindowManager windowManager, String name) {
        this.windowManager = windowManager;
        this.name = name;
        this.isOpen = false;
    }

    public void addWindowListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeWindowListener(Listener listener) {
        listeners.remove(listener);
    }

    protected void fireWindowOpened() {
        isOpen = true;
        listeners.forEach(l -> l.windowOpened(this));
    }

    protected void fireWindowClosed() {
        isOpen = false;
        listeners.forEach(l -> l.windowClosed(this));
    }

    public String getName() {
        return name;
    }

    abstract public void open();

    public boolean isOpen() {
        return isOpen;
    }

    protected void addWindowMenu(Menu parent) {
        Menu windowMenu = new Menu(parent);
        for (Window window : windowManager.getWindows()) {
            new ActionMenuItem(windowMenu, SWT.NONE, new WindowAction(window));
        }
        
        MenuItem windowsItem = new MenuItem(parent, SWT.CASCADE);
        windowsItem.setText("Windows");
        windowsItem.setMenu(windowMenu);

    }
}
