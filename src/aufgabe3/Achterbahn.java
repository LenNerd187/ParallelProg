package aufgabe3;

import java.util.LinkedList;

public class Achterbahn {

    private static int nWagen = 3;
    //private static int nPlätze = 10;

    public static void main(String[] args){
        LinkedList<Wagen> wagenQueue = new LinkedList<>();
        Steuerung steuerung = new Steuerung();
        for (int i = 0; i < nWagen; i++) {
            wagenQueue.add(new Wagen(steuerung));
        }
        steuerung.initWagenQueue(wagenQueue);
        Drehkreuz drehkreuz = new Drehkreuz(steuerung);


        drehkreuz.start();
        for (Wagen wagen : wagenQueue) {
            wagen.start();
        }


    }

}
