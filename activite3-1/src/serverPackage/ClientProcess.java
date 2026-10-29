package serverPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientProcess extends Thread {
    private static final Pattern OPERATION =
            Pattern.compile("^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$");

    private final Socket socket;
    private final int numero;

    public ClientProcess(Socket socket, int numero) {
        this.socket = socket;
        this.numero = numero;
    }

    private static String formater(double valeur) {
        if (valeur == Math.rint(valeur) && Math.abs(valeur) < 1e15) {
            return String.valueOf((long) valeur);
        }
        return String.valueOf(valeur);
    }

    private static String calculer(double x, String operateur, double y) {
        switch (operateur) {
            case "+":
                return formater(x + y);
            case "-":
                return formater(x - y);
            case "*":
                return formater(x * y);
            case "/":
                return y == 0 ? "Erreur : division par zéro" : formater(x / y);
            default:
                return "Erreur : opérateur inconnu";
        }
    }

    @Override
    public void run() {
        try (Socket s = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.println("Bienvenue ! Vous êtes le client n°" + numero);
            String ligne;
            while ((ligne = in.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty()) {
                    continue;
                }
                if (ligne.equalsIgnoreCase("FIN")) {
                    break;
                }
                Matcher m = OPERATION.matcher(ligne);
                String resultat;
                if (!m.matches()) {
                    resultat = "Erreur : syntaxe incorrecte (format : nombre opérateur nombre)";
                } else {
                    resultat = calculer(Double.parseDouble(m.group(1)), m.group(2), Double.parseDouble(m.group(3)));
                }
                System.out.println("Client n°" + numero + " : " + ligne + " -> " + resultat);
                out.println(resultat);
            }
        } catch (IOException e) {
            System.out.println("Client n°" + numero + " : connexion interrompue (" + e.getMessage() + ")");
        }
        System.out.println("Client n°" + numero + " déconnecté");
    }
}
