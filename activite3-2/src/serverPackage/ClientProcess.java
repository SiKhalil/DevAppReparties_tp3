package serverPackage;

import commonPackage.Operation;
import commonPackage.Resultat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientProcess extends Thread {
    private final Socket socket;
    private final int numero;

    public ClientProcess(Socket socket, int numero) {
        this.socket = socket;
        this.numero = numero;
    }

    private static Resultat calculer(Operation operation) {
        double x = operation.getOperande1();
        double y = operation.getOperande2();
        switch (operation.getOperateur()) {
            case '+':
                return new Resultat(x + y);
            case '-':
                return new Resultat(x - y);
            case '*':
                return new Resultat(x * y);
            case '/':
                if (y == 0) {
                    return new Resultat("Erreur : division par zéro");
                }
                return new Resultat(x / y);
            default:
                return new Resultat("Erreur : opérateur inconnu");
        }
    }

    @Override
    public void run() {
        try (Socket s = socket) {
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());

            out.writeObject(Integer.valueOf(numero));
            out.flush();
            while (true) {
                Object recu = in.readObject();
                if (!(recu instanceof Operation)) {
                    break;
                }
                Operation operation = (Operation) recu;
                Resultat resultat = calculer(operation);
                String detail = operation.getOperande1() + " " + operation.getOperateur() + " "
                        + operation.getOperande2() + " -> " + resultat;
                Compteur.incrementerEtAfficher(numero, detail);
                out.writeUnshared(resultat);
                out.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client n°" + numero + " : connexion interrompue (" + e.getMessage() + ")");
        }
        System.out.println("Client n°" + numero + " déconnecté");
    }
}
