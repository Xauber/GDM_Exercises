package uebung3;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 Opens an image window and adds a panel below the image
 */
public class GRDM_U3_s0577610_Korrektur  implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Rot-Kanal", "Graustufen", "Negativ", "Binärbild-Schwarz/Weiß", "Binärbild-Schwarz/Weiß-5Stufig", "Binärbild-Schwarz/Weiß-10Stufig","Binärbild-horiz. Fehlerdiff.", "Sepia",  "6-Farbig"};


    public static void main(String args[]) {

       // IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
        //IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");
        IJ.open("/Users/benedictlippold/IdeaProjects/GDM_Übungen/src/uebung3/Bear.jpg");

        GRDM_U3_s0577610_Korrektur pw = new GRDM_U3_s0577610_Korrektur();
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

            // Array zum Zurückschreiben der Pixelwerte
            int[] pixels = (int[])ip.getPixels();


            // a)Original des Bildes
            if (method.equals("Original")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;

                        pixels[pos] = origPixels[pos];
                    }
                }
            }

            if (method.equals("Rot-Kanal")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        //int g = (argb >>  8) & 0xff;
                        //int b =  argb        & 0xff;

                        int rn = r;
                        int gn = 0;
                        int bn = 0;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }
            // b) Negativ des Bildes
            if (method.equals("Negativ")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn = 255-r;
                        int gn = 255-g;
                        int bn = 255-b;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            //c) Graustufebild
            if (method.equals("Graustufen")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int mittelwert = (r + g + b)/3;
                        int rn = mittelwert;
                        int gn = mittelwert;
                        int bn = mittelwert;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;


                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            //d) Binärbild-Schwarz/Weiß nur minimale oder maximale Werte
            if (method.equals("Binärbild-Schwarz/Weiß")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int mittelwert = (r + g + b)/3;
                        int rn = mittelwert;
                        int gn = mittelwert;
                        int bn = mittelwert;


                        if(mittelwert>=128){
                            rn = 255;
                            gn = 255;
                            bn = 255;
                        }

                        else if (mittelwert<128){
                            rn = 0;
                            gn = 0;
                            bn = 0;
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;


                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }


            //d) Binärbild-Schwarz/Weiß
            if (method.equals("Binärbild-Schwarz/Weiß-5Stufig")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int mittelwert = (r + g + b)/3;
                        int rn = mittelwert;
                        int gn = mittelwert;
                        int bn = mittelwert;

                        if(mittelwert<64){
                            rn = 0;
                            gn = 0;
                            bn = 0;
                        }

                        else if ((mittelwert>=64) && (mittelwert<128)){
                            rn = 64;
                            gn = 64;
                            bn = 64;
                        }

                        else if ((mittelwert>=128) && (mittelwert<192)){
                            rn = 128;
                            gn = 128;
                            bn = 128;
                        }

                        else if ((mittelwert>=192) && (mittelwert<255)){
                            rn = 192;
                            gn = 192;
                            bn = 192;
                        }

                        else if ((mittelwert==255)){
                            rn = 255;
                            gn = 255;
                            bn = 255;
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;


                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }
            //d) Binärbild-Schwarz/Weiß 10-Stufig
            if (method.equals("Binärbild-Schwarz/Weiß-10Stufig")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int mittelwert = (r + g + b)/3;
                        int rn = mittelwert;
                        int gn = mittelwert;
                        int bn = mittelwert;

                        float schwellwert = (float) ( mittelwert/ 28.33);

                        if(schwellwert < 1){
                            rn = 0;
                            gn = 0;
                            bn = 0;
                        }

                        else if ((schwellwert >=1) && (schwellwert < 2)){
                            rn = 28;
                            gn = 28;
                            bn = 28;
                        }

                        else if ((schwellwert >=2) && (schwellwert < 3)){
                            rn = 56;
                            gn = 56;
                            bn = 56;
                        }

                        else if ((schwellwert >=3) && (schwellwert < 4)){
                            rn = 84;
                            gn = 84;
                            bn = 84;
                        }

                        else if ((schwellwert >=4) && (schwellwert < 5)){
                            rn = 112;
                            gn = 112;
                            bn = 112;
                        }

                        else if ((schwellwert >=5) && (schwellwert < 6)){
                            rn = 140;
                            gn = 140;
                            bn = 140;
                        }

                        else if ((schwellwert >=6) && (schwellwert < 7)){
                            rn = 168;
                            gn = 168;
                            bn = 168;
                        }

                        else if ((schwellwert >=7) && (schwellwert < 8)){
                            rn = 196;
                            gn = 196;
                            bn = 196;
                        }

                        else if ((schwellwert >=8) && (schwellwert < 9)){
                            rn = 224;
                            gn = 224;
                            bn = 224;
                        }

                        else if (schwellwert >=9){
                            rn = 255;
                            gn = 255;
                            bn = 255;
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;


                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }
            //e) Binärbild-horiz. Fehlerdiff. korrigiert
            if (method.equals("Binärbild-horiz. Fehlerdiff.")) {


                for (int y=0; y<height; y++) {
                    int fehler = 0;
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        //Grenze von welcher der Fehler abhängig ist
                        int grenze = 255/2;

                        int mittelwert = (r + g + b)/3;
                        int rn = mittelwert;
                        int gn = mittelwert;
                        int bn = mittelwert;

                        int grauWertKorrigiert = mittelwert + fehler;

//                        if(mittelwert > grenze){
//                            fehler = (255 - grauWertKorrigiert)* -1;
//                        }
//                        else {
//                            fehler = mittelwert + grauWertKorrigiert;
//                        }
                        
                        if(grauWertKorrigiert > grenze){
                        	fehler = (255 - grauWertKorrigiert)* -1;
                        	grauWertKorrigiert = 255;
                        }
                        else {
                        	fehler = grauWertKorrigiert;
                        	grauWertKorrigiert = 0;
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;


                        pixels[pos] = (0xFF<<24) | (grauWertKorrigiert<<16) | (grauWertKorrigiert<<8) | grauWertKorrigiert;
                    }
                }
            }


            //f) Sepia
            if (method.equals("Sepia")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int mittelwert = ( r + g + b)/3;


                        int rn = mittelwert +45;
                        int gn = mittelwert +25;
                        int bn =  mittelwert -10;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;

                    }
                }
            }

            //g) 6-Farbig - Fehler gefunden, klappt jetzt:)

            if (method.equals("6-Farbig")) {
            	
            	 /* 1. Alice Blue = 240,248,255
                2. Dark Blue = 0,0,139
                3. aquamarine4 = 69,139,116
                4. brown1      = 255,64,64
                5. gray13      = 33,33,33
                6. roseyBrown1 =   255,193,193 */
            	Color[] farben;
                farben = new Color[6];
                farben [0] = new Color(240,248,255);
                farben [1] = new Color(0,0,139);
                farben [2] = new Color(69,139,116);
                farben [3] = new Color(255,64,64);
                farben [4] = new Color(33,33,33);
                farben [5] = new Color(255,193,193);
                
                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        double momDistance;
                        double smallDistance = 195075;
                        Color nearestColor;
                        nearestColor = null;
                        for (int j=0; j < farben.length; j++) {
                            int xRed = (farben[j].getRed() - r);
                            int xGreen = (farben[j].getGreen() - g);
                            
                            int xBlue = (farben[j].getGreen() - g);

                           double distance = Math.pow(Math.pow(xRed, 2) + Math.pow(xGreen, 2) + Math.pow(xBlue, 2), 1.0/2.0);
                           momDistance = distance;

                        if(momDistance< smallDistance){
                        	
                               smallDistance = momDistance;
                               nearestColor = farben[j];
                           }
                        }

                        int rn = nearestColor.getRed();
                        int gn = nearestColor.getGreen();
                        int bn = nearestColor.getBlue();


                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;


                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;

                    }
                }
            }



        }


    } // CustomWindow inner class
}