package aufgabe3;

import java.util.LinkedList;
import java.util.Random;

public class Steuerung {
    LinkedList<Wagen> wagenQueue = new LinkedList<>();
    //Wagen wagens[];
    public Steuerung(){}

    public void initWagenQueue(LinkedList<Wagen> queue){
        this.wagenQueue = queue;
    }

    //Funktion für den Wächter der Abfahrt
    public boolean wagenAnErsterStelle(Wagen wagen){
        return (wagenQueue.indexOf(wagen) == 0);
    }

    //Funktion für den Wächter des Drehkreuzes   (freiePlätze > 0)
    public int freiePlätzeGesamt(){
        int gesamt = 0;
        for (Wagen wagen : wagenQueue) {
            gesamt += wagen.freiePlätze;
        }
        return gesamt;
    }
    public synchronized void passagier(){
        //Wächter: freiePlätze > 0
        while(! (freiePlätzeGesamt() > 0)){
            try {
                System.out.println("Drehkreuz wait for empty wagen....");
                wait();
            } catch (InterruptedException e) {}
        }

        //zufälligen nicht vollen Wagen raussuchen
        Wagen wagen;
        Random rand = new Random();
        int randIndex = rand.nextInt(wagenQueue.size());
        int nextWagen = randIndex;
        do {
            wagen = wagenQueue.get(nextWagen);
            nextWagen = (nextWagen + 1) % wagenQueue.size();
        }while(wagen.getFreiePlätze() == 0 && nextWagen != randIndex);

        if(wagen.freiePlätze > 0 ) {
            wagen.passagier();
            System.out.println("passagier eingestiegen! Wagen " +  wagenQueue.indexOf(wagen));

        }else{
            System.out.print("Fehler: Wagen ist unerwartet voll!");
        }
        notifyAll();
    }

    public synchronized void abfahrt(Wagen wagen){
        //Wächter wagen.getFreiePlätze() == 0 && wagen ist an erster position
        while(! (wagen.getFreiePlätze() == 0 && wagenAnErsterStelle(wagen))){
            try {
                System.out.println("Wagen " + wagenQueue.indexOf(wagen) + " wait for passengers...");
                wait();
            } catch (InterruptedException e) {}
        }
        System.out.println("Abfahrt! Wagen" + wagenQueue.indexOf(wagen));
        //notifyAll();  //nicht benötigt da sich hier nichts geändert hat was für andere Threads interessant ist - erst beim aussteigen
    }

    public synchronized void aussteigen(Wagen wagen){
        wagen.clear();  //wagen ist wieder leer Drehkreuz kann wieder arbeiten -> notifyAll
        wagenQueue.addLast(wagenQueue.pop()); //reihenfolge ändert sich - nächster Wagen kann evtl Losfahren -> notifyAll
        notifyAll();
    }

}
