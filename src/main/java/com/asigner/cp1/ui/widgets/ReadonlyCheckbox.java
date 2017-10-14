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

package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.util.SWTResources;
import com.google.common.collect.ImmutableMap;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.util.Map;

public class ReadonlyCheckbox extends CLabel {

    private Map<Boolean, Image> images = ImmutableMap.<Boolean, Image>builder()
            .put(true, SWTResources.getImage("/com/asigner/cp1/ui/widgets/checkbox-checked.png"))
            .put(false, SWTResources.getImage("/com/asigner/cp1/ui/widgets/checkbox-unchecked.png"))
            .build();

    private boolean checked;

    public ReadonlyCheckbox(Composite parent, int style) {
        super(parent, style);
        this.checked = false;
        updateImage();
    }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            updateImage();
            redraw();
        }
    }

    private void updateImage() {
        this.setImage(images.get(checked));
    }

    @Override
    protected void checkSubclass() {
    }
}
