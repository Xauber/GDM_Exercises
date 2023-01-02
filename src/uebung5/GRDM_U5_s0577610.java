package uebung5;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 Opens an image window and adds a panel below the image
 */

//Lösung Benedict Lippold Übung Nr.5
public class GRDM_U5_s0577610 implements PlugIn{


    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Weich", "Hochpass", "Scharfzeichnung"};


    public static void main(String args[]) {

        IJ.open("/Users/benedictlippold/IdeaProjects/GDM_Übungen/src/uebung5/sail.jpg");

        GRDM_U5_s0577610 pw = new GRDM_U5_s0577610();
        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp==null)
            imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);

        storePixelValues(imp.getProcessor());

        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        origPixels = ((int []) ip.getPixels()).clone();
    }


    class CustomCanvas extends ImageCanvas {

        CustomCanvas(ImagePlus imp) {
            super(imp);
        }

    } // CustomCanvas inner class


    class CustomWindow extends ImageWindow implements ItemListener {

        private String method;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            JComboBox cb = new JComboBox(items);
            panel.add(cb);
            cb.addItemListener(this);

            add(panel);
            pack();
        }

        public void itemStateChanged(ItemEvent evt) {

            // Get the affected item
            Object item = evt.getItem();

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("Selected: " + item.toString());
                method = item.toString();
                changePixelValues(imp.getProcessor());
                imp.updateAndDraw();
            }

        }


        private void changePixelValues(ImageProcessor ip) {

            // Array zum Zur�ckschreiben der Pixelwerte
            int[] pixels = (int[])ip.getPixels();

            if (method.equals("Original")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;

                        pixels[pos] = origPixels[pos];
                    }
                }
            }

            if (method.equals("Weich")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte


                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn = 0;
                        int gn = 0;
                        int bn = 0;

                        int counter = 0;

                        // for-loops um die beanchbarten Pixel des aktuellen Pixels durchzugehen (des 3x3 Kernels)
                        for (int xBenachbart = -1; xBenachbart <= 1; xBenachbart++) {
                            for (int yBenachbart = 0 - width; yBenachbart <= width; yBenachbart = yBenachbart + width) {

                                int xBenachbartNeu = xBenachbart;
                                int yBenachbartNeu = yBenachbart;

                                // Kernelpixel, welche außerhalb des Randes liegen,
                                // nehmen den Wert  des gegenüberliegenden Pixels an

                                if (xBenachbart == -1 && x == 0)
                                    xBenachbart = width - 1;

                                if (xBenachbart == 1 && x == width - 1)
                                    xBenachbart = -1 * (width - 1);
                                    //xBenachbart = 0;

                                if (yBenachbart == -1 * width && y == 0)
                                    yBenachbart = (height - 1) * (width);

                                if (yBenachbart == width && y == height - 1)
                                    yBenachbart = -1 * (height - 1) * width;

                                int posNeu = pos + yBenachbart + xBenachbart;

                                // Alle Pixel im Kernel werden aufaddiert
                                rn += (origPixels[posNeu] >> 16) & 0xff;
                                gn += (origPixels[posNeu] >> 8) & 0xff;
                                bn += (origPixels[posNeu]) & 0xff;

                                counter++;

                                xBenachbart = xBenachbartNeu;
                                yBenachbart = yBenachbartNeu;
                            }
                        }

                        //hier muss noch geteilt werden, da die Werte vorher aufsummiert werden
                        rn = rn / counter;
                        gn = gn / counter;
                        bn = bn / counter;


                        // Limiter
                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;

                    }
                }
            }



            if (method.equals("Hochpass")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn = 0;
                        int gn = 0;
                        int bn = 0;

                        // for-loops um die beanchbarten Pixel des aktuellen Pixels durchzugehen (des 3x3 Kernels)
                        for (int xBenachbart = -1; xBenachbart <= 1; xBenachbart++) {
                            for (int yBenachbart = 0 - width; yBenachbart <= width; yBenachbart = yBenachbart + width) {
                                int xBenachbartNeu = xBenachbart;
                                int yBenachbartNeu = yBenachbart;

                                // außerhalb des Randes liegende Kernelpixel,
                                // nehmen den Wert  des gegenüberliegenden Pixels an

                                if (xBenachbart == -1 && x == 0)
                                    xBenachbart = width - 1;

                                if (xBenachbart == 1 && x == width - 1)
                                    xBenachbart = -1 * (width - 1);

                                if (yBenachbart == -1 * width && y == 0)
                                    yBenachbart = (height - 1) * (width);

                                if (yBenachbart == width && y == height - 1)
                                    yBenachbart = -1 * (height - 1) * width;

                                int posNeu = pos + xBenachbart + yBenachbart;

                                if (xBenachbart == 0 && yBenachbart == 0) {
                                    rn += ((origPixels[posNeu] >> 16) & 0xff) *8/9;
                                    gn += ((origPixels[posNeu] >> 8) & 0xff) *8/9;
                                    bn += ((origPixels[posNeu]) & 0xff) *8/9;
                                }
                                else {
                                    rn += ((origPixels[posNeu] >> 16) & 0xff) /-9;
                                    gn += ((origPixels[posNeu] >> 8) & 0xff) /-9;
                                    bn += ((origPixels[posNeu]) & 0xff) /-9 ;
                                }
                                xBenachbart = xBenachbartNeu;
                                yBenachbart = yBenachbartNeu;
                            }
                        }

                        // Offset addieren
                        rn = rn +128;
                        gn = gn +128;
                        bn = bn +128;

                        // Limiter
                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
                    }
                }
            }


            if (method.equals("Scharfzeichnung")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn = 0;
                        int gn = 0;
                        int bn = 0;

                        int counter = 0;

                        // for-loops um die beanchbarten Pixel des aktuellen Pixels durchzugehen (des 3x3 Kernels)
                        for (int xBenachbart = -1; xBenachbart <= 1; xBenachbart++) {
                            for (int yBenachbart = 0 - width; yBenachbart <= width; yBenachbart = yBenachbart + width) {
                                int xBenachbartNeu = xBenachbart;
                                int yBenachbartNeu = yBenachbart;

                                // außerhalb des Randes liegende Kernelpixel,
                                // nehmen den Wert  des gegenüberliegenden Pixels an

                                if (xBenachbart == -1 && x == 0)
                                    xBenachbart = width - 1;
                                if (xBenachbart == 1 && x == width - 1)
                                    xBenachbart = -1 * (width - 1);
                                if (yBenachbart == -1 * width && y == 0)
                                    yBenachbart = (height - 1) * (width);
                                if (yBenachbart == width && y == height - 1)
                                    yBenachbart = -1 * (height - 1) * width;

                                int posNeu = pos + xBenachbart + yBenachbart;

                                // Hauptpixel muss 17/9 multipliziert werden
                                if (xBenachbart == 0 && yBenachbart == 0) {
                                    rn += ((origPixels[posNeu] >> 16) & 0xff) *17/9;
                                    gn += ((origPixels[posNeu] >> 8) & 0xff) *17/9;
                                    bn += ((origPixels[posNeu]) & 0xff) *17/9;
                                }

                                // anderen Pixel des Kernels wieder durch /-9 teilen
                                else {
                                    rn += ((origPixels[posNeu] >> 16) & 0xff) /-9;
                                    gn += ((origPixels[posNeu] >> 8) & 0xff) /-9;
                                    bn += ((origPixels[posNeu]) & 0xff) /-9;
                                }
                                counter++;

                                xBenachbart = xBenachbartNeu;
                                yBenachbart = yBenachbartNeu;;
                            }
                        }

                        // Limiter
                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;


                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
                    }
                }
            }


        }
    } // CustomWindow inner class
}

