/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author carcassi
 */
public class ShowResizableImage extends javax.swing.JFrame {

    /**
     * Creates new form ShowImage
     */
    public ShowResizableImage() {
        initComponents();
        imagePanel.setImage(new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR));
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                renderer.update(new Histogram1DRendererUpdate()
                        .imageHeight(getRootPane().getHeight())
                        .imageWidth(getRootPane().getWidth()));
                imagePanel.setImage(new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR));
                redrawHistogram();
            }
            
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagePanel = new org.epics.graphene.ImagePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        imagePanel.setLayout(new java.awt.FlowLayout());
        getContentPane().add(imagePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private Histogram1D hist;
    private Histogram1DRenderer renderer = new Histogram1DRenderer(300, 200);

    public void setHistogram(Histogram1D hist) {
        this.hist = hist;
        pack();
        redrawHistogram();
    }
    
    private int count = 0;
    private long totalTime;
    
    private void redrawHistogram() {
        count++;
        long startTime = System.nanoTime();
        BufferedImage image = (BufferedImage) imagePanel.getImage();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, hist);
        imagePanel.setImage(image);
        long endTime = System.nanoTime();
        totalTime += endTime - startTime;
        if (count % 100 == 0)
            System.out.println(count + " " + (totalTime / count));
    }
    
    

    public static void showHistogram(final Histogram1D hist) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ShowResizableImage frame = new ShowResizableImage();
                frame.setHistogram(hist);
                frame.setVisible(true);
            }
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        Histogram1D hist = new Hist1DT2();
        showHistogram(hist);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.epics.graphene.ImagePanel imagePanel;
    // End of variables declaration//GEN-END:variables
}
