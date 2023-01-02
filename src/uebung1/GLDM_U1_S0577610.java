package uebung1;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1_S0577610 implements PlugIn {

    final static String[] choices = {
            "Schwarzes Bild",
            "Gelbes Bild",
            "Belgische Fahne",
            "Schwarz/Weiss Verlauf",
            "Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
            "USA Fahne",
            "Tschechische Fahne"
    };

    private String choice;

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

        GLDM_U1_S0577610 imageGeneration = new GLDM_U1_S0577610();
        imageGeneration.run("");
    }

    public void run(String arg) {

        int width  = 566;  // Breite
        int height = 400;  // Hoehe

        // RGB-Bild erzeugen
        ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
        ImageProcessor ip = imagePlus.getProcessor();

        // Arrays fuer den Zugriff auf die Pixelwerte
        int[] pixels = (int[])ip.getPixels();

        dialog();

        ////////////////////////////////////////////////////////////////
        // Hier bitte Ihre Aenderungen / Erweiterungen

        if ( choice.equals("Schwarzes Bild") ) {
            generateBlackImage(width, height, pixels);
        }

        if ( choice.equals("Gelbes Bild") ) {
            generateYellowImage(width, height, pixels);
        }

        if ( choice.equals("Belgische Fahne") ) {
            generateBelgianFlag(width, height, pixels);
        }

        if ( choice.equals("Schwarz/Weiss Verlauf") ) {
            generateBlackWhiteGradient(width, height, pixels);
        }

        if ( choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf") ) {
            generateBlackRedBlueGradient(width, height, pixels);
        }

        if ( choice.equals("USA Fahne") ) {
            generateUSAFlag(width, height, pixels);
        }

        if ( choice.equals("Tschechische Fahne") ) {
            generateCzechFlag(width, height, pixels);
        }

        ////////////////////////////////////////////////////////////////////

        // neues Bild anzeigen
        imagePlus.show();
        imagePlus.updateAndDraw();
    }




    private void generateBlackImage(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen


                // schwarze Farbfläche
                int r = 0;
                int g = 0;
                int b = 0;

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateYellowImage(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                //gelbe Fläche
                int r = 255;
                int g = 255;
                int b = 0;

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }
    private void generateBelgianFlag(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte

            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r;
                int g;
                int b;

                //schwarzer Streifen
                if(x<=188){
                    r = 0;
                    g = 0;
                    b = 0;
                }

                // gelber Streifen
                else if(x>188 && x<=377){
                    r = 255;
                    g = 255;
                    b = 0;
                }

                //roter Streifen
                else {
                    r = 255;
                    g = 0;
                    b = 0;
                }
                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateBlackWhiteGradient(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte


        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte

            for (int x=0; x<width; x++) {
                int pos = (y*width + x); // Arrayposition bestimmen

                //der x Wert entspricht dem Grauanteil, jedoch muss da ein der jeweilige RGB Wert 255 nicht übersteigen kann,
                //der x Wert noch durch 2.219 geteilt werden (566/255 = 2.219)
               int r = (int) (x/2.219);
               int g = (int) (x/2.219);
               int b = (int) (x/2.219);

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }

    }

    private void generateBlackRedBlueGradient(int width, int height, int[] pixels) {

        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte

            for (int x=0; x<width; x++) {
                int pos = (y*width + x); // Arrayposition bestimmen

                //Rot-Wert nimmt in x-Achse zu, Blau-Wert in y-Achse. Auch hier muss der x-y Wert
                //noch durch 2.219 geteilt werden,da der jeweilige RGB Wert 255 nicht übersteigen kann - flachere Steigung


                int r = (int) (x/2.219);
                int g = (int) (0/2.219);
                int b = (int) (y/2.219);


                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    //dieser Ansatz hat nicht funktioniert :(, nächster Ansatz schon:)
    /** private void generateUSAFlag1(int width, int height, int[] pixels) {

        int stripeHeight = 400/13;
        int roundedHeight = stripeHeight * 13;
        int missingPixels = 400 - roundedHeight;
        int missingPixelsPerStripe = missingPixels/13;

        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte

            for (int x=0; x<width; x++) {
                int pos = (y*width + x); // Arrayposition bestimmen

                int r = 0;
                int g = 0;
                int b = 0;

                if(x <= (0.40 * width ) && y <= ((stripeHeight*7)+missingPixels/13*7)){
                    // Blaues Rechteck in der linken oberen Ecke
                    // verläuft bündig mit 7 Streifen, daher y <= stripeHeight*7
                    // missingPixels müssen noch anteilig dazuaddiert werden

                    r = 60;
                    g = 59;
                    b = 110;
                }

                else {
                    if((y%((stripeHeight + (missingPixelsPerStripe))*2))<=stripeHeight){

                    r = 178;
                    g = 34;
                    b = 52;}

                     else {

                        r = 255;
                        g = 255;
                        b = 255;}
                }

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    } */

    private void generateUSAFlag(int width, int height, int[] pixels) {

        // int stripeHeight = 400/13;
        int stripeHeight = 31;
        int roundedHeight = stripeHeight * 13;
        int missingPixels = 400 - roundedHeight;
        //int missingPixelsPerStripe = missingPixels/13; did not work


        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte

            for (int x=0; x<width; x++) {
                int pos = (y*width + x); // Arrayposition bestimmen

                int r = 0;
                int g = 0;
                int b = 0;

                //if(x <= (0.40 * width ) && y <= ((stripeHeight*7)+missingPixels/13*7)){ hat nicht funktioniert,
                // Probleme wegen Runden
                    if(x <= (0.40 * width ) && y <= (216)){
                    // Blaues Rechteck in der linken oberen Ecke
                    // verläuft bündig mit 7 Streifen, daher y <= stripeHeight*7
                    // missingPixels müssen noch anteilig dazuaddiert werden

                    r = 60;
                    g = 59;
                    b = 110;
                }

                else {
                    //rote Streifen setzen, roter plus weißer Streifen erstreckt sich über ins. 62 Pixel
                    if((y % 62 >= 0) && (y % 62 < 31)){

                        r = 178;
                        g = 34;
                        b = 52;}

                    //die überbleibenden Streifen weiß einfärben
                    else {

                        r = 255;
                        g = 255;
                        b = 255;}
                }


                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateCzechFlag(int width, int height, int[] pixels) {

        for (int y = 0; y < height; y++) {
            // Schleife ueber die x-Werte

            for (int x = 0; x < width; x++) {
                int pos = (y * width + x); // Arrayposition bestimmen

                int r = 0;
                int g = 0;
                int b = 0;


                // die untere Hälfte der Flagge wird rot
                if (y > 200) {
                    r = 215;
                    g = 20;
                    b = 26;
                }

                // die obere Hälfte der Flagge wird weiß
                else if (y <= 200) {
                    r = 255;
                    g = 255;
                    b = 255;
                }
                //if ((x<=283) && ((x * (y - 1)) <=56600)){} hat nicht funktioniert
                // das blaue Dreieck konstruieren, indem vom y-Wert beider oberen Ecken aus jeweils eine Diagn. verläuft
                else if (((y) >= x) && ((400 - x) >= y)) {
                        r = 17;
                        g = 69;
                        b = 126;
                }

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
    }



    private void dialog() {
        // Dialog fuer Auswahl der Bilderzeugung
        GenericDialog gd = new GenericDialog("Bildart");

        gd.addChoice("Bildtyp", choices, choices[0]);


        gd.showDialog();	// generiere Eingabefenster

        choice = gd.getNextChoice(); // Auswahl uebernehmen

        if (gd.wasCanceled())
            System.exit(0);
    }



}
