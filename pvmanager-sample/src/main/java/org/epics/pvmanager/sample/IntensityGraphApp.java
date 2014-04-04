/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sample;

import org.epics.graphene.ColorScheme;
import org.epics.graphene.IntensityGraph2DRendererUpdate;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.ValueColorScheme;
import org.epics.graphene.ValueColorSchemes;
import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.graphene.ExpressionLanguage.*;
import org.epics.pvmanager.graphene.IntensityGraph2DExpression;

/**
 *
 * @author carcassi
 */
public class IntensityGraphApp extends BaseGraphApp<IntensityGraph2DRendererUpdate> {

    public IntensityGraphApp() {
        dataFormulaField.setModel(new javax.swing.DefaultComboBoxModel<String>(
                new String[] { "sim://gaussianWaveform",
                    "sim://sine2DWaveform(1,50,45,100,100,0.1)",
                    "=arrayWithBoundaries(arrayOf(1,3,2,4,3,5), range(-10,10))",
                    "=caHistogram(\"histo\")"}));
    }

    @Override
    protected IntensityGraph2DExpression createExpression(String dataFormula) {
        IntensityGraph2DExpression plot = intensityGraphOf(formula(dataFormula));
        plot.update(plot.newUpdate().valueColorScheme(colorScheme));
        return plot;
    }
    
    private ValueColorScheme colorScheme = ValueColorSchemes.JET;
    
    public ValueColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(ValueColorScheme colorScheme) {
        this.colorScheme = colorScheme;
        if (graph != null) {
            graph.update(graph.newUpdate().valueColorScheme(colorScheme));
        }
    }

    @Override
    protected void openConfigurationDialog() {
        IntensityGraphDialog dialog = new IntensityGraphDialog(new javax.swing.JFrame(), true, this);
        dialog.setTitle("Configure...");
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        main(IntensityGraphApp.class);
    }
    
}
