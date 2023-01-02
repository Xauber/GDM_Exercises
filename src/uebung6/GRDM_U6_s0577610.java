package uebung6;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.gui.GenericDialog;
import ij.gui.NewImage;


public class GRDM_U6_s0577610 implements PlugInFilter {

    ImageProcessor ip; //ImageProcessor object
    ImagePlus imp; // ImagePlus object
    int[] origPixels;
    int width;
    int height;
    String[] items = {"Kopie", "Nearest Neighbor", "Bilinear"};



    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        }
        return DOES_RGB + NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }

        public static void main (String args[]){

            IJ.open("/Users/benedictlippold/IdeaProjects/GDM_Übungen/src/uebung6/component.jpg");
            GRDM_U6_s0577610 pw = new GRDM_U6_s0577610();
            pw.ip = IJ.getProcessor();
            pw.imp = IJ.getImage();
            pw.run(pw.ip);

        }

        public void run(ImageProcessor ip){

            String[] dropdownmenue = {"Kopie", "Nearest Neighbor", "Bilinear"};
            GenericDialog gd = new GenericDialog("scale");
            gd.addChoice("Methode", dropdownmenue, dropdownmenue[0]);
            gd.addNumericField("Hoehe:", 500, 0);
            gd.addNumericField("Breite:", 400, 0);
            gd.showDialog();

            String method = gd.getNextChoice();

            int height_n = (int) gd.getNextNumber(); // _n fuer das neue skalierte Bild
            int width_n = (int) gd.getNextNumber();

            int width = ip.getWidth();  // Breite bestimmen
            int height = ip.getHeight(); // Hoehe bestimmen

            //height_n = height;
            //width_n  = width;

            ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
                    width_n, height_n, 1, NewImage.FILL_BLACK);

            ImageProcessor ip_n = neu.getProcessor();


            int[] pix = (int[]) ip.getPixels();
            int[] pix_n = (int[]) ip_n.getPixels();

            if (method.equals("Kopie")) {

                for (int y_n = 0; y_n < height_n; y_n++) {
                    for (int x_n = 0; x_n < width_n; x_n++) {
                        int y = y_n;
                        int x = x_n;

                        if (y < height && x < width) {
                            int pos_n = y_n * width_n + x_n;
                            int pos = y * width + x;

                            pix_n[pos_n] = pix[pos];
                        }
                    }
                }
            }

            if (method == "Nearest Neighbor") {

                // Skalierungsfaktor - vom Original zum skalierten Bild
                double scaleFactorX = (double) width / width_n;
                double scaleFactorY = (double) height / height_n;

                // Schleife über das neue Bild
                for (int y_n = 0; y_n < height_n; y_n++) {
                    for (int x_n = 0; x_n < width_n; x_n++) {

                        // Nächster Nachbarpixel - pos_n
                        int pos_n = y_n * width_n + x_n;

                        //Pixel in Relation zum Faktor setzen
                        //Originalpixel auslesen
                        int xAlt = (int) (x_n * scaleFactorX);
                        int yAlt = (int) (y_n * scaleFactorY);

                        int pos = yAlt * width + xAlt;
                        pix_n[pos_n] = pix[pos];
                    }
                }
            }

            if (method == "Bilinear") {
                // Die Umsetzung der bilinearen Interpolation wurde mir (und ein paar anderen) von Nermin Rustic erklärt,
                // da ich mich bei der Bestimmung von h und v, welche nachher in die Formel eingestzt werden müssen, verrant hatte.


                // Orientierung an den Slides zur bilinearen Interpolation
                // Skalierungsfaktor zwischen altem und neuem Bild berechnen
                double scaleX = (double)(width - 1) / (width_n - 1);
                double scaleY = (double)(height - 1) / (height_n - 1);

                //Pixelpositionen initialisieren, (für die vier umliegenden Bildpunkte)
                int A, B, C, D = 0;

                // Schleife ueber das neue Bild
                for (int y_n = 0; y_n < height_n; y_n++) {
                    for (int x_n = 0; x_n < width_n; x_n++) {

                        int pos_n = y_n * width_n + x_n;

                        //Pixel & Faktor kombinieren
                        int xAlt = (int) (scaleX * x_n);
                        int yAlt = (int) (scaleY * y_n);

                        //Abstand vom alten zum neuen Pixel bestimmen (v und h aus der Formel)
                        double h = (scaleX * x_n) - xAlt;
                        double v = (scaleY * y_n) - yAlt;

                        int pos = xAlt + yAlt * width;

                        //Pixel  A, B, C, D
                        A = pix[pos];
                        B = pix[pos];
                        C = pix[pos];
                        D = pix[pos];

                        //Randbehandlung
                        if (xAlt != width-1) {
                            B = pix[pos+1];
                            if (yAlt == height-1) {
                                D = pix[pos+1];
                            } else {
                                D = pix[pos+1+width];
                            }
                        }

                        else if (yAlt != height-1) {
                            C = pix[pos+width];
                            if (xAlt == width-1) {
                                D = pix[pos+width];
                            } else {
                                D = pix[pos+1+width];
                            }
                        }

                        //RGB Werte von A, B, C, D
                        int rA = (A >> 16) & 0xff;
                        int gA = (A >> 8) & 0xff;
                        int bA = A & 0xff;

                        int rB = (B >> 16) & 0xff;
                        int gB = (B >> 8) & 0xff;
                        int bB = B & 0xff;

                        int rC = (C >> 16) & 0xff;
                        int gC = (C >> 8) & 0xff;
                        int bC = C & 0xff;

                        int rD = (D >> 16) & 0xff;
                        int gD = (D >> 8) & 0xff;
                        int bD = D & 0xff;

                        //Formel aus den Slides der Vorlesungen für die bilineare Interpolation
                        int rn = (int) (rA*(1-h)*(1-v) + rB*h*(1-v) + rC*(1-h)*v + rD*h*v);
                        int gn = (int) (gA*(1-h)*(1-v) + gB*h*(1-v) + gC*(1-h)*v + gD*h*v);
                        int bn = (int) (bA*(1-h)*(1-v) + bB*h*(1-v) + bC*(1-h)*v + bD*h*v);

                        // Limiter nur um sicherzugehen, dass nichts überläuft
                        if(rn >= 255) rn = 255;
                        if(gn >= 255) gn = 255;
                        if(bn >= 255) bn = 255;

                        if(rn < 0) rn = 0;
                        if(gn < 0) gn = 0;
                        if(bn < 0) bn = 0;

                        pix_n[pos_n] = (0xff<<24) | (rn<<16) | (gn<<8) | (bn);
                    }
                }
            }

            // neues Bild anzeigen
            neu.show();
            neu.updateAndDraw();
        }

    private void showAbout() {
        IJ.showMessage("Version von Ben");
    }
}
