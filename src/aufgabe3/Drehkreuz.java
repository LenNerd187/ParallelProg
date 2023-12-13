package aufgabe3;

public class Drehkreuz extends Thread{
    Steuerung steuerung;
    public Drehkreuz(Steuerung steuerung){
        this.steuerung = steuerung;
    }
    @Override
    public void run() {
        while(true) {
            steuerung.passagier();
        }
    }
}
