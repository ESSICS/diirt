/**
 * Copyright (C) 2010-18 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.diirt.vtype;

import java.util.List;
import org.diirt.util.array.ListDouble;
import org.diirt.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class IVDoubleArray extends IVNumberArray implements VDoubleArray {

    private final ListDouble data;

    public IVDoubleArray(ListDouble data, ListInt sizes,
            Alarm alarm, Time time, Display display) {
        this(data, sizes, null, alarm, time, display);
    }

    public IVDoubleArray(ListDouble data, ListInt sizes, List<ArrayDimensionDisplay> dimDisplay,
            Alarm alarm, Time time, Display display) {
        super(sizes, dimDisplay, alarm, time, display);
        this.data = data;
    }

    @Override
    public ListDouble getData() {
        return data;
    }

}
