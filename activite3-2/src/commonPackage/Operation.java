package commonPackage;

import java.io.Serializable;

public class Operation implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double operande1;
    private final char operateur;
    private final double operande2;

    public Operation(double operande1, char operateur, double operande2) {
        this.operande1 = operande1;
        this.operateur = operateur;
        this.operande2 = operande2;
    }

    public double getOperande1() {
        return operande1;
    }

    public char getOperateur() {
        return operateur;
    }

    public double getOperande2() {
        return operande2;
    }
}
