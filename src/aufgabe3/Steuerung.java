package aufgabe3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Steuerung {
    private static final int nWagen = 3;
    ArrayList<Wagen> wagenList; //
    volatile int nextWagen = 0;
    volatile int freiePlätzeArray[] = new int[nWagen];   //ersetzt i, j, k im Modell

    public Steuerung(){
        Arrays.fill(freiePlätzeArray, 10);
    }

    public void setWagenArray(ArrayList<Wagen> newList){
        this.wagenList = newList;
    }

    //Funktion für den Wächter des Drehkreuzes (freiePlätze > 0)
    public int freiePlätzeGesamt(){
        int gesamt = 0;
        for (int freiePlätze : freiePlätzeArray) {
            gesamt += freiePlätze;
        }
        return gesamt;
    }
    public synchronized void passagier(){
        //Wächter: freiePlätze > 0
        while(! (freiePlätzeGesamt() > 0)){
            try {
                System.out.println("Drehkreuz: wait for empty wagen....");
                wait();
            } catch (InterruptedException e) {}
        }

        //zufälligen nicht vollen Wagen raussuchen
        Random rand = new Random();
        int randIndex = rand.nextInt(wagenList.size());
        int nextIndex = randIndex;
        do {
            nextIndex = (nextIndex + 1) % wagenList.size();
        }while(freiePlätzeArray[nextIndex] == 0 && nextIndex != randIndex);

        if(freiePlätzeArray[nextIndex] > 0 ) {
            freiePlätzeArray[nextIndex]--;
            System.out.println("Wagen " + nextIndex + ": passagier eingestiegen! ");

        }else{
            System.out.print("Fehler: Wagen ist unerwartet voll!");
        }
        notifyAll();
    }

    public synchronized void abfahrt(Wagen wagen){
        int wagenIndex = wagenList.indexOf(wagen);
        //Wächter when(i == 0 && nextWagen==0)
        while(! (freiePlätzeArray[wagenIndex] == 0 && nextWagen == wagenIndex)){
            try {
                System.out.println("Wagen " + wagenList.indexOf(wagen) + ": wait for passengers...");
                wait();
            } catch (InterruptedException e) {}
        }
        System.out.println("Wagen " + wagenList.indexOf(wagen) + ": Abfahrt! ");
        //notifyAll();  //nicht benötigt da sich hier nichts geändert hat was für andere Threads interessant ist - erst beim aussteigen
    }

    public synchronized void aussteigen(Wagen wagen){
        int wagenIndex = wagenList.indexOf(wagen);
        freiePlätzeArray[wagenIndex] = 10;//wagen ist wieder leer Drehkreuz kann wieder arbeiten -> notifyAll
        nextWagen = (nextWagen + 1) % nWagen; //reihenfolge ändert sich - nächster Wagen kann evtl Losfahren -> notifyAll
        notifyAll();
    }

}
