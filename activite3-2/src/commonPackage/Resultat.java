package commonPackage;

import java.io.Serializable;

public class Resultat implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double valeur;
    private final String erreur;

    public Resultat(double valeur) {
        this.valeur = valeur;
        this.erreur = null;
    }

    public Resultat(String erreur) {
        this.valeur = Double.NaN;
        this.erreur = erreur;
    }

    public boolean estValide() {
        return erreur == null;
    }

    public double getValeur() {
        return valeur;
    }

    public String getErreur() {
        return erreur;
    }

    @Override
    public String toString() {
        if (erreur != null) {
            return erreur;
        }
        if (valeur == Math.rint(valeur) && Math.abs(valeur) < 1e15) {
            return String.valueOf((long) valeur);
        }
        return String.valueOf(valeur);
    }
}
