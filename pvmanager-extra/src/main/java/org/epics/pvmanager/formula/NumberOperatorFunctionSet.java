/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

/**
 *
 * @author carcassi
 */
public class NumberOperatorFunctionSet extends FormulaFunctionSet {

    public NumberOperatorFunctionSet() {
        super(new FormulaFunctionSetDescription("numericOperators", "Operators for numeric scalar")
                .addFormulaFunction(new TwoArgNumericFormulaFunction("+", "Numeric addition", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 + arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericFormulaFunction("-", "Numeric subtraction", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 - arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericFormulaFunction("*", "Numeric multiplication", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 * arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericFormulaFunction("/", "Numeric division", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 / arg2;
                    }
                })
                );
    }


}
