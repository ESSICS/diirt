/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.Arrays;
import java.util.List;

/**
 * Operators to constructs expression of PVs that the {@link PVManager} will
 * be able to monitor.
 *
 * @author carcassi
 */
public class ExpressionLanguage {
    private ExpressionLanguage() {}

    public static <T> DesiredRateExpression<List<T>>
            queueOf(SourceRateExpression<T> expression) {
        return new DesiredRateExpression<List<T>>(expression,
                new QueueCollector<T>(expression.getFunction()),
                expression.getDefaultName());
    }

    public static <T> DesiredRateExpression<List<T>>
            timedCacheOf(SourceRateExpression<T> expression, TimeDuration maxIntervalBetweenSamples) {
        return new DesiredRateExpression<List<T>>(expression,
                new TimedCacheCollector<T>(expression.getFunction(), maxIntervalBetweenSamples),
                expression.getDefaultName());
    }

    /**
     * Expression that returns (only) at the desired rate the latest value computed
     * from a {@code SourceRateExpression}.
     *
     * @param <T> result type
     * @param expression expression read at the source rate
     * @return a new expression
     */
    public static <T> DesiredRateExpression<T> latestValueOf(SourceRateExpression<T> expression) {
        // TODO This should use a cache of size one
        DesiredRateExpression<List<T>> queue = queueOf(expression);
        return new DesiredRateExpression<T>(queue,
                new LastValueAggregator<T>((Collector<T>) queue.getFunction()),
                expression.getDefaultName());
    }

    /**
     * A user provided single argument function.
     *
     * @param <R> result type
     * @param <A> argument type
     */
    public static interface OneArgFunction<R, A> {
        R calculate(A arg);
    }

    /**
     * A user provided double argument function.
     *
     * @param <R> result type
     * @param <A1> first argument type
     * @param <A2> second argument type
     */
    public static interface TwoArgFunction<R, A1, A2> {
        R calculate(A1 arg1, A2 arg2);
    }

    /**
     * An expression that represents the result of a user provided function.
     *
     * @param <R> result type
     * @param <A> argument type
     * @param function the user provided function
     * @param argExpression expression for the function argument
     * @return a new expression
     */
    public static <R, A> DesiredRateExpression<R> resultOf(final OneArgFunction<R, A> function,
            DesiredRateExpression<A> argExpression) {
        String name = function.getClass().getSimpleName() + "(" + argExpression.getDefaultName() + ")";
        final Function<A> arg = argExpression.getFunction();
        return new DesiredRateExpression<R>(argExpression, new Function<R>() {
            @Override
            public R getValue() {
                return function.calculate(arg.getValue());
            }
        }, name);
    }

    /**
     * An expression that represents the result of a user provided function.
     *
     * @param <R> result type
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param function the user provided function
     * @param argExpression expression for the function argument
     * @return a new expression
     */
    public static <R, A1, A2> DesiredRateExpression<R> resultOf(final TwoArgFunction<R, A1, A2> function,
            DesiredRateExpression<A1> arg1Expression, DesiredRateExpression<A2> arg2Expression) {
        String name = function.getClass().getSimpleName() + "(" + arg1Expression.getDefaultName() +
                ", " + arg2Expression.getDefaultName() + ")";
        final Function<A1> arg1 = arg1Expression.getFunction();
        final Function<A2> arg2 = arg2Expression.getFunction();
        @SuppressWarnings("unchecked")
        final List<DesiredRateExpression<? extends Object>> argExpressions =
                Arrays.asList(arg1Expression, arg2Expression);
        return new DesiredRateExpression<R>(argExpressions,
                new Function<R>() {
                    @Override
                    public R getValue() {
                        return function.calculate(arg1.getValue(), arg2.getValue());
                    }
                }, name);
    }
}
