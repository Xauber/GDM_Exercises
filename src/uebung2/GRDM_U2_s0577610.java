package uebung2;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 Opens an image window and adds a panel below the image
 */
public class GRDM_U2_s0577610 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;


    public static void main(String args[]) {
        //new ImageJ();
        //IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
        //IJ.open("C:\\Users\\Schall\\Downloads\\orchid.jpg");
        IJ.open("/Users/benedictlippold/Desktop/orchid.jpg");

        GRDM_U2_s0577610 pw = new GRDM_U2_s0577610();
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


    class CustomWindow extends ImageWindow implements ChangeListener {

        private JSlider jSliderBrightness;
        private JSlider jSliderContrast;
        private JSlider jSliderSaturation;
        private JSlider jSliderHue;
        private double brightness;
        private double contrast;
        private double saturation;
        private double hue;


        // Tipp von Max Decken, da meine Slider an einigen Stellen komische Dinge veranstaltet haben..
        // --> unten command Wörter abfragen
        private String command;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSilder("Helligkeit", 0, 256, 128);
            jSliderContrast = makeTitledSilder("Kontrast", 0, 10, 5);
            jSliderSaturation = makeTitledSilder("Sättigung", 0, 8, 4);
            jSliderHue = makeTitledSilder("Hue", 0, 360, 0);
            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
            panel.add(jSliderSaturation);
            panel.add(jSliderHue);
            add(panel);
            pack();
        }

        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {

            JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
            Dimension preferredSize = new Dimension(width, 50);
            slider.setPreferredSize(preferredSize);
            TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
                    string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                    new Font("Sans", Font.PLAIN, 11));
            slider.setBorder(tb);
            slider.setMajorTickSpacing((maxVal - minVal)/10 );
            slider.setPaintTicks(true);
            slider.addChangeListener(this);

            return slider;
        }

        private void setSliderTitle(JSlider slider, String str) {
            TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
                    str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                    new Font("Sans", Font.PLAIN, 11));
            slider.setBorder(tb);
        }

        public void stateChanged( ChangeEvent e ){
            JSlider slider = (JSlider)e.getSource();

            if (slider == jSliderBrightness) {
                brightness = slider.getValue()-128;
                String str = "Helligkeit " + brightness;
                setSliderTitle(jSliderBrightness, str);
                command = "bright";
            }

            if (slider == jSliderContrast) {
                if(slider.getValue() <= 5) contrast = (slider.getValue()/10.0)*2;
                else contrast = (slider.getValue() -5) *2;
                String str = "Kontrast " + contrast;
                setSliderTitle(jSliderContrast, str);
                command = "contr";
            }

            if (slider == jSliderSaturation) {

                if (slider.getValue() < 4) saturation = (slider.getValue()*2)/8.0;
                else saturation = slider.getValue() -3;
                String str = "Sättigung " + saturation;
                setSliderTitle(jSliderSaturation, str);
                command = "sat";

            }

            if (slider == jSliderHue) {
                hue = slider.getValue();
                String str = "Hue " + hue;
                setSliderTitle(jSliderHue, str);
                command = "hu";
            }

            changePixelValues(imp.getProcessor());

            imp.updateAndDraw();
        }


        private void changePixelValues(ImageProcessor ip) {

            // Array fuer den Zugriff auf die Pixelwerte
            int[] pixels = (int[])ip.getPixels();

            for (int y=0; y<height; y++) {
                for (int x=0; x<width; x++) {
                    int pos = y*width + x;
                    int argb = origPixels[pos];  // Lesen der Originalwerte

                    int r = (argb >> 16) & 0xff;
                    int g = (argb >>  8) & 0xff;
                    int b =  argb        & 0xff;



                    //Transformation zu YUV - Formeln aus Aufgabenstellung übernommen
                    int yValue = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                    int u = (int) ((b - yValue) * 0.493);
                    int v = (int) ((r - yValue) * 0.877);



                    //Helligkeit verändern
                    if (command.equals("bright")) {
                        yValue = (int) (yValue + brightness);
                    }

                    //Kontrast verändern
                    if (command.equals("contr")) {
                        yValue = (int) (contrast * (yValue - 128) + 128);
                        u = (int) (contrast * u);
                        v = (int) (contrast * v);
                    }

                    //Sättigung verändern
                    if(command.equals("sat")) {
                        u = (int) (u * saturation);
                        v = (int) (v * saturation);
                    }

                    //Hue verändern
                    if(command.equals("hu")) {
                        double radHue = Math.toRadians(hue);
                        u = (int) ((Math.cos(radHue) + (-Math.sin(radHue))) * u);
                        v = (int) (Math.sin(radHue) + (Math.cos(radHue)) * v);
                    }



                    //Transformation zu RGB (Rücktransformation) Formeln aus Aufgabenstellung übernommen
                    int rn = (int) (yValue + v/0.877);
                    int bn = (int) (yValue + u/0.493);
                    int gn = (int) (1/0.587 * yValue - 0.299/0.587*rn - 0.114/0.587 * bn);



                    // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
                    //Limiter für positiven Überlauf
                    if(rn > 255) {rn = 255;
                    }
                    if(gn > 255) {gn = 255;
                    }
                    if(bn > 255) {bn = 255;
                    }

                    //Limiter für negativen Überlauf
                    if(rn < 0) {rn = 0;
                    }
                    if(gn < 0) {gn = 0;
                    }
                    if(bn < 0) {bn = 0;
                    }

                    pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                }
            }
        }

    } // CustomWindow inner class
}