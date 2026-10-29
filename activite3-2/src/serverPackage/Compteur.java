package serverPackage;

public class Compteur {
    private static int total = 0;

    private Compteur() {
    }

    public static synchronized int incrementerEtAfficher(int numeroClient, String detail) {
        total++;
        System.out.println("[Compteur global = " + total + "] Client n°" + numeroClient + " : " + detail);
        return total;
    }

    public static synchronized int getTotal() {
        return total;
    }
}
