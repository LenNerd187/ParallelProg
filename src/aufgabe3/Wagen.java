package aufgabe3;

public class Wagen extends Thread{

    Steuerung steuerung;

    public Wagen(Steuerung steuerung){
        this.steuerung = steuerung;
    }



    @Override
    public void run() {
        while(true) {
            steuerung.abfahrt(this);
            steuerung.aussteigen(this);
        }
    }
}
