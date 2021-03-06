/**
 * Copyright (C) 2010-18 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.diirt.datasource.graphene;

import java.util.Arrays;
import org.diirt.datasource.BasicTypeSupport;
import org.diirt.datasource.NotificationSupport;
import org.diirt.datasource.ReadFunction;
import org.diirt.datasource.TypeSupport;
import org.diirt.datasource.vtype.DataTypeSupport;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.datasource.expression.DesiredRateExpression;
import org.diirt.datasource.expression.DesiredRateExpressionImpl;
import org.diirt.datasource.expression.DesiredRateExpressionList;
import org.diirt.datasource.expression.DesiredRateExpressionListImpl;
import org.diirt.util.array.ListNumbers;
import org.diirt.vtype.VTable;
import org.diirt.vtype.ValueFactory;

/**
 *
 * @author carcassi
 */
public class ExpressionLanguage {
    private ExpressionLanguage() {}

    static {
        // Add support for Epics types.
        DataTypeSupport.install();
        // Add support for Basic types
        BasicTypeSupport.install();
        TypeSupport.addTypeSupport(NotificationSupport.immutableTypeSupport(Graph2DResult.class));
    }

    public static HistogramGraph2DExpression histogramGraphOf(
            DesiredRateExpression<?> arrayData) {
        return new HistogramGraph2DExpression(arrayData);
    }

    public static IntensityGraph2DExpression intensityGraphOf(
            DesiredRateExpression<?> arrayData) {
        return new IntensityGraph2DExpression(arrayData);
    }

    public static LineGraph2DExpression lineGraphOf(DesiredRateExpression<? extends VNumberArray> vDoubleArray) {
        return lineGraphOf(vDoubleArray, null, null, null);
    }

    public static LineGraph2DExpression lineGraphOf(final DesiredRateExpression<? extends VNumberArray> yArray,
            final DesiredRateExpression<? extends VNumber> xInitialOffset,
            final DesiredRateExpression<? extends VNumber> xIncrementSize) {
        DesiredRateExpression<VTable> data = new DesiredRateExpressionImpl<>(createList(yArray, xInitialOffset, xIncrementSize),
        new ReadFunction<VTable>() {

            @Override
            public VTable readValue() {
                VNumberArray values = yArray.getFunction().readValue();
                VNumber offset = xInitialOffset.getFunction().readValue();
                VNumber increment = xIncrementSize.getFunction().readValue();

                if (values == null || offset == null || increment == null) {
                    return null;
                }

                return ValueFactory.newVTable(Arrays.<Class<?>>asList(double.class, double.class),
                        Arrays.asList("X", "Y"),
                        Arrays.<Object>asList(ListNumbers.linearList(offset.getValue().doubleValue(), increment.getValue().doubleValue(), values.getData().size()),
                        new ListDoubleView(values.getData())));
            }
        }, "data");

        return lineGraphOf(data, null, null, null);
    }

    public static LineGraph2DExpression lineGraphOf(final DesiredRateExpression<? extends VNumberArray> xVDoubleArray,
            final DesiredRateExpression<? extends VNumberArray> yVDoubleArray) {
        DesiredRateExpression<VTable> data = new DesiredRateExpressionImpl<>(createList(xVDoubleArray, yVDoubleArray),
        new ReadFunction<VTable>() {

            @Override
            public VTable readValue() {
                VNumberArray xValues = xVDoubleArray.getFunction().readValue();
                VNumberArray yValues = yVDoubleArray.getFunction().readValue();

                if (xValues == null || yValues == null) {
                    return null;
                }

                return ValueFactory.newVTable(Arrays.<Class<?>>asList(double.class, double.class),
                        Arrays.asList("X", "Y"),
                        Arrays.<Object>asList(new ListDoubleView(xValues.getData()),
                        new ListDoubleView(yValues.getData())));
            }
        }, "data");

        return lineGraphOf(data, null, null, null);
    }
    public static LineGraph2DExpression lineGraphOf(
            DesiredRateExpression<?> tableData,
            DesiredRateExpression<?> xColumnName,
            DesiredRateExpression<?> yColumnName,
            DesiredRateExpression<?> tooltipColumnName) {
        return new LineGraph2DExpression(tableData, xColumnName, yColumnName, tooltipColumnName);
    }

    public static MultilineGraph2DExpression multilineGraphOf(
            DesiredRateExpression<?> tableData,
            DesiredRateExpression<?> xColumnName,
            DesiredRateExpression<?> yColumnName) {
        return new MultilineGraph2DExpression(tableData, xColumnName, yColumnName);
    }

    public static MultiAxisLineGraph2DExpression multiAxisLineGraphOf(
            DesiredRateExpression<?> tableData,
            DesiredRateExpression<?> xColumnName,
            DesiredRateExpression<?> yColumnName) {
        return new MultiAxisLineGraph2DExpression(tableData, xColumnName, yColumnName);
    }

    public static SparklineGraph2DExpression sparklineGraphOf(
            DesiredRateExpression<?> tableData,
            DesiredRateExpression<?> xColumnName,
            DesiredRateExpression<?> yColumnName) {
        return new SparklineGraph2DExpression(tableData, xColumnName, yColumnName);
    }

    public static ScatterGraph2DExpression scatterGraphOf(
            DesiredRateExpression<?> tableData,
            DesiredRateExpression<?> xColumnName,
            DesiredRateExpression<?> yColumnName,
            DesiredRateExpression<?> tooltipColumnName) {
        return new ScatterGraph2DExpression(tableData, xColumnName, yColumnName, tooltipColumnName);
    }

    public static BubbleGraph2DExpression bubbleGraphOf(
            DesiredRateExpression<?> tableData,
            DesiredRateExpression<?> xColumnName,
            DesiredRateExpression<?> yColumnName,
            DesiredRateExpression<?> sizeColumnName,
            DesiredRateExpression<?> tooltipColumnName) {
        return new BubbleGraph2DExpression(tableData, xColumnName, yColumnName, sizeColumnName, tooltipColumnName);
    }

    @SafeVarargs
    static <T> DesiredRateExpressionList<T> createList(DesiredRateExpressionList<? extends T>... expressions) {
        DesiredRateExpressionList<T> list = new DesiredRateExpressionListImpl<T>();
        for (DesiredRateExpressionList<? extends T> exp : expressions) {
            if (exp != null) {
                list.and(exp);
            }
        }
        return list;
    }

    static <T> ReadFunction<T> functionOf(DesiredRateExpression<T> exp) {
        if (exp == null) {
            return null;
        } else {
            return exp.getFunction();
        }
    }

}
