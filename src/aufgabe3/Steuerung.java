package aufgabe3;

import java.util.LinkedList;
import java.util.Random;

public class Steuerung {
    LinkedList<Wagen> wagenQueue = new LinkedList<>();
    Wagen wagens[];
    public Steuerung(Wagen[] wagens){
        this.wagens = wagens;
    }

    public int freiePlätzeGesamt(){
        int gesamt = 0;
        for (Wagen wagen : wagens) {
            gesamt += wagen.freiePlätze;
        }
        return gesamt;
    }
    public synchronized void passagier(){
        //Wächter: freiePlätze > 0
        while(! (freiePlätzeGesamt() > 0)){
            try {
                System.out.println("Drehkreuz wait....");
                wait();
            } catch (InterruptedException e) {}
        }

        //zufälligen nicht vollen Wagen raussuchen
        Wagen wagen;
        Random rand = new Random();
        int randIndex = rand.nextInt(wagens.length);
        int nextWagen = randIndex;
        do {
            System.out.println("Freien Wagen suchen...");
            wagen = wagens[nextWagen];
            nextWagen = (nextWagen + 1) % wagens.length;
        }while(wagen.getFreiePlätze() == 0 && nextWagen != randIndex);

        System.out.println("Wagen gefunden!");
        if(wagen.freiePlätze > 0 ) {
            wagen.passagier();
        }else{
            System.out.print("Fehler: Wagen ist unerwartet voll!");
        }
        notifyAll();
    }

    public synchronized void abfahrt(Wagen wagen){
        //Wächter 
        while(! (wagen.getFreiePlätze() == 0)){
            try {
                System.out.println("Wagen wait...");
                wait();
            } catch (InterruptedException e) {}
        }
        System.out.println("Abfahrt!");
        notifyAll();
    }

    public synchronized void aussteigen(Wagen wagen){
        wagen.clear();
    }

}
