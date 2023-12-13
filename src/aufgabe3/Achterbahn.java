package aufgabe3;

public class Achterbahn {

    private static int nWagen = 3;
    private static int nPlätze = 10;

    public static void main(String[] args){
        Wagen[] wagens = new Wagen[nWagen];
        Steuerung steuerung = new Steuerung(wagens);
        for (int i = 0; i < nWagen; i++) {
            wagens[i] = new Wagen(steuerung);
        }
        Drehkreuz drehkreuz = new Drehkreuz(steuerung);
        drehkreuz.start();
        for (Wagen wagen : wagens) {
            wagen.start();
        }


    }

}
