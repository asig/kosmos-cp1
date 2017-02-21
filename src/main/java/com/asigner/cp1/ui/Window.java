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

import com.asigner.cp1.ui.actions.AboutAction;
import com.asigner.cp1.ui.actions.PreferencesAction;
import com.asigner.cp1.ui.actions.QuitAction;
import com.asigner.cp1.ui.actions.WindowAction;
import com.asigner.cp1.ui.platform.CocoaUiEnhancer;
import com.asigner.cp1.ui.widgets.ActionMenuItem;
import com.google.common.collect.Lists;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import java.util.List;
import java.util.function.Function;

public abstract class Window {

    private static final String APP_NAME = "Kosmos CP1";
    private static final CocoaUiEnhancer enhancer = new CocoaUiEnhancer(APP_NAME);

    // Shared actions
    private static QuitAction quitAction = new QuitAction();
    private static PreferencesAction preferencesAction = new PreferencesAction();
    private static AboutAction aboutAction = new AboutAction();

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

    protected Menu createFileMenu(Menu parent, Function<Menu, MenuItem>... menuCreators) {
        Display display = parent.getDisplay();

        Menu fileMenu = null;
        if (!OS.isMac()) {
            fileMenu = createMenuInternal(parent, "&File", menuCreators);
            new MenuItem(fileMenu, SWT.SEPARATOR);
            new ActionMenuItem(fileMenu, SWT.PUSH, preferencesAction);
            new MenuItem(fileMenu, SWT.SEPARATOR);
            new ActionMenuItem(fileMenu, SWT.PUSH, quitAction);
        } else {
            enhancer.hookApplicationMenu( display, quitAction, aboutAction, preferencesAction);
            if (menuCreators.length > 0) {
                fileMenu = createMenuInternal(parent, "&File", menuCreators);
            }
        }
        return fileMenu;
    }

    protected Menu createHelpMenu(Menu parent, Function<Menu, MenuItem>... menuCreators) {
        Display display = parent.getDisplay();

        Menu helpMenu = null;
        if (!OS.isMac()) {
            helpMenu = createMenuInternal(parent, "Help", menuCreators);
            new MenuItem(helpMenu, SWT.SEPARATOR);
            new ActionMenuItem(helpMenu, SWT.PUSH, aboutAction);
        } else {
            enhancer.hookApplicationMenu( display, quitAction, aboutAction, preferencesAction);
            if (menuCreators.length > 0) {
                helpMenu = createMenuInternal(parent, "Help", menuCreators);
            }
        }
        return helpMenu;
    }

    private Menu createMenuInternal(Menu parent, String text, Function<Menu, MenuItem>... menuCreators) {
        Menu menu = new Menu(parent);
        MenuItem item = new MenuItem(parent, SWT.CASCADE);
        item.setText(text);
        item.setMenu(menu);
        for (Function<Menu, MenuItem> creator : menuCreators) {
            creator.apply(menu);
        }
        return menu;
    }

    protected Menu createWindowMenu(Menu parent) {
        Menu windowMenu = new Menu(parent);
        for (Window window : windowManager.getWindows()) {
            new ActionMenuItem(windowMenu, SWT.NONE, new WindowAction(window));
        }

        MenuItem windowsItem = new MenuItem(parent, SWT.CASCADE);
        windowsItem.setText("Windows");
        windowsItem.setMenu(windowMenu);

        return windowMenu;
    }
}
