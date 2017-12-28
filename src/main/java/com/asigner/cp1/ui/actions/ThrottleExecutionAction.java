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

import com.asigner.cp1.ui.ExecutorThread;

public class ThrottleExecutionAction extends BaseAction {

    public ThrottleExecutionAction(ExecutorThread executorThread) {
        super(executorThread, "Throttle to 100%");
        super.setChecked(executorThread.isThrottled());
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        executorThread.setThrottled(checked);
    }
}
