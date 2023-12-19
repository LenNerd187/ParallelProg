package aufgabe3;

import java.util.ArrayList;

public class Achterbahn {

    private static int nWagen = 3;
    //private static int nPlätze = 10;

    public static void main(String[] args){

        Steuerung steuerung = new Steuerung();
        ArrayList<Wagen> wagenList = new ArrayList<Wagen>();
        for (int i = 0; i < nWagen; i++) {
            wagenList.add(new Wagen(steuerung));
        }
        steuerung.setWagenArray(wagenList);
        Drehkreuz drehkreuz = new Drehkreuz(steuerung);

        //start Threads
        drehkreuz.start();
        for (Wagen wagen : wagenList) {
            wagen.start();
        }


    }

}
