/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
/**
 * <p align="center"><img alt="pvManager" src="http://pvmanager.diirt.org/images/PVManagerLogo150.png"></p>
 * <div style="float: right; margin-top: -170px" id="contents"></div>
 * 
 * 
 * <h1>Examples</h1>
 * 
 * <p>You can find examples in <code>link org.diirt.datasource.sample</code> package.
 * We stopped including examples in the documentation itself because they tend
 * to become stale. We link to actual code that is part of the build, so that
 * there is more of a guarantee that they stay correct</p>
 * 
 * <ol>
 *     <li><a href="http://pvmanager.diirt.org/xref/org/epics/pvmanager/sample/ConfigurationExamples.html">Configuration examples</a> - 
 * how to configure pvmanager to read from the right datasource and dispatch 
 * events on the right thread.</li>
 *     <li><a href="http://pvmanager.diirt.org/xref/org/epics/pvmanager/sample/BasicExamples.html">Basic examples</a> - 
 * how to read/write from a single channel.</li>
 *     <li><a href="http://pvmanager.diirt.org/xref/org/epics/pvmanager/sample/MultipleChannelExamples.html">Multiple channel examples</a> - 
 * how to read/write multiple channels at a time.</li>
 *     <li><a href="http://pvmanager.diirt.org/xref/org/epics/pvmanager/sample/VTypeExamples.html">VType examples</a> - 
 * how to work with actual type and how to aggregate them in bigger structures.</li>
 * </ol>
 * 
 * <h1> Package description</h1>
 * 
 * This package contains all the basic components of the PVManager framework
 * and the basic support for the language to define the creation.
 * <p>
 * There are two distinct parts in the PVManager framework. The first part
 * includes all the elements that deal with data directly: read from various
 * sources ({@link org.diirt.datasource.DataSource}), performing computation ({@link org.diirt.datasource.ReadFunction}),
 * collecting data ({@link org.diirt.datasource.Collector}), scanning at the UI rate ({@link org.diirt.datasource.PVDirector})
 * and notify on appropriate threads.
 * <p>
 * The second part consists of an expression language that allows to define
 * how to connect the first set of objects with each other. {@link org.diirt.datasource.expression.SourceRateExpression}
 * describes data as it's coming out at the network rate, {@link org.diirt.datasource.expression.DesiredRateExpression}
 * defines data at the scanning rate for the UI, and {@link org.diirt.datasource.ExpressionLanguage}
 * defines static methods that define the operator in the expression language.
 * <p>
 * Users can extend both the first part (by extending support for different types,
 * providing different support for different data source or creating new computation
 * elements) and the second part (by extending the language to support other cases.
 * All support for data types is relegated to separate packages: you can use
 * the same style to extend the framework to your needs.
 * <p>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 * <br>
 */
package org.diirt.datasource;

