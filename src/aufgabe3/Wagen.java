package aufgabe3;

public class Wagen extends Thread{

    Steuerung steuerung;
    int freiePlätze = 10;


    public Wagen(Steuerung steuerung){
        this.steuerung = steuerung;
    }
    public int getFreiePlätze(){
        return freiePlätze;
    }

    public void passagier(){
        freiePlätze--;
    }

    public void clear(){
        freiePlätze = 10;
    }

    @Override
    public void run() {
        while(true) {
            steuerung.abfahrt(this);
            steuerung.aussteigen(this);
        }
    }
}
