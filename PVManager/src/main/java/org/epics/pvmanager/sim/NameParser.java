/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse variable names and create simulated signals.
 *
 * @author carcassi
 */
class NameParser {

    static final Pattern doublePattern = Pattern.compile("\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*");
    static final Pattern commaSeparatedDoubles = Pattern.compile("\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*(,\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*)*");
    static final Pattern functionAndParameter = Pattern.compile("(\\w+)(\\((\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*(,\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*)*)\\))?");

    /**
     * Parses a comma separated list of arguments and returns them as a list.
     *
     * @param string a comma separated list of arguments; if null or empty returns
     * the empty list
     * @return the list of parsed arguments
     */
    static List<Object> parseParameters(String string) {
        // Argument is empty
        if (string == null || "".equals(string))
            return Collections.emptyList();

        // Validate input
        if (!commaSeparatedDoubles.matcher(string).matches()) {
            throw new IllegalArgumentException("Arguments must be a comma separated list of double values (was " + string + ")");
        }

        // Parse parameters
        Matcher matcher = doublePattern.matcher(string);
        List<Object> parameters = new ArrayList<Object>();
        while (matcher.find()) {
            String parameter = matcher.group();
            Double value = Double.parseDouble(parameter);
            parameters.add(value);
        }

        return parameters;
    }

    /**
     * Parse a function with parameters and returns a list where the first
     * element is the function name and the others are the parsed arguments.
     *
     * @param string a string representing a function
     * @return the name and the parameters
     */
    static List<Object> parseFunction(String string) {
        Matcher matcher = functionAndParameter.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Syntax error: function like xxx(num1, num2, ...)");
        }

        List<Object> parameters = new ArrayList<Object>();
        parameters.add(matcher.group(1));
        parameters.addAll(parseParameters(matcher.group(3)));
        return parameters;
    }

    /**
     * Given a string representing a function call, finds the appropriate call
     * matching the function name, and the appropriate constructor and instanciates
     * it.
     *
     * @param string the function call
     * @return the function
     */
    static SimFunction<?> createFunction(String string) {
        List<Object> parameters = parseFunction(string);
        StringBuilder className = new StringBuilder("org.epics.pvmanager.sim.");
        int firstCharPosition = className.length();
        className.append((String) parameters.get(0));
        className.setCharAt(firstCharPosition, Character.toUpperCase(className.charAt(firstCharPosition)));

        try {
            @SuppressWarnings("unchecked")
            Class<SimFunction<?>> clazz = (Class<SimFunction<?>>) Class.forName(className.toString());
            Object[] constructorParams = parameters.subList(1, parameters.size()).toArray();
            Class[] types = new Class[constructorParams.length];
            for (int i = 0; i < types.length; i++) {
                types[i] = constructorParams[i].getClass();
            }
            return clazz.getConstructor(types).newInstance(constructorParams);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Function " + parameters.get(0) + " is not defined");
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Wrong parameter number for function " + parameters.get(0));
        } catch (SecurityException ex) {
            throw new RuntimeException("Constructor for " + parameters.get(0) + " should be at least package private");
        } catch (InstantiationException ex) {
            throw new RuntimeException("Constructor for " + parameters.get(0) + " failed", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Constructor for " + parameters.get(0) + " should be at least package private");
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Wrong parameter type for function " + parameters.get(0));
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Constructor for " + parameters.get(0) + " failed", ex);
        }
    }
}
