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

public class OS {
    public static boolean isLinux() {
        return System.getProperty( "os.name" ).equals( "Linux" );
    }

    public static boolean isMac() {
        return System.getProperty( "os.name" ).equals( "Mac OS X" );
    }

    public static boolean isWin() {
        return System.getProperty( "os.name" ).toLowerCase().contains("windows");
    }

    public static String getAppDataDirectory() {
        if (OS.isMac()) {
            return System.getProperty("user.home") + "/Library/Application Support";
        }

        String appdata = System.getenv("APPDATA");
        if (appdata != null) {
            // Windows
            return appdata;
        }

        // Linux
        appdata = System.getenv("XDG_DATA_HOME");
        if (appdata != null) {
            return appdata;
        } else {
            // Fall back to recommendation in https://standards.freedesktop.org/basedir-spec/basedir-spec-latest.html
            return System.getProperty("user.home") + "/.local/share";
        }
    }

    public static String getConfigDirectory() {
        if (OS.isMac() || OS.isWin()) {
            return getAppDataDirectory();
        }

        // Linux
        String dir = System.getenv("XDG_CONFIG_HOME");
        if (dir == null) {
            dir = System.getProperty("user.home") + "/.config";
        }
        return dir;
    }

}
