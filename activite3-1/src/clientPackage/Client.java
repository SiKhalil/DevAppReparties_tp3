package clientPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    private static final int PORT = 1234;
    private static final Pattern OPERATION =
            Pattern.compile("^\\s*-?\\d+(?:\\.\\d+)?\\s*[+\\-*/]\\s*-?\\d+(?:\\.\\d+)?\\s*$");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            String ip;
            if (args.length > 0) {
                ip = args[0];
            } else {
                System.out.print("Adresse IP du serveur : ");
                ip = scanner.nextLine().trim();
            }
            System.out.println("Je suis un client pas encore connecté…");
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(ip), PORT), 5000);
            System.out.println("je suis un client connecté");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            System.out.println("Serveur : " + in.readLine());
            while (true) {
                System.out.print("Entrez une opération (ex : 55 * 25) ou FIN pour quitter : ");
                String ligne = scanner.nextLine().trim();
                if (ligne.equalsIgnoreCase("FIN")) {
                    out.println("FIN");
                    break;
                }
                if (!OPERATION.matcher(ligne).matches()) {
                    System.out.println("Syntaxe incorrecte : utilisez le format  nombre opérateur nombre  avec + - * /");
                    continue;
                }
                out.println(ligne);
                String resultat = in.readLine();
                if (resultat == null) {
                    System.out.println("Le serveur a fermé la connexion");
                    break;
                }
                System.out.println("Résultat : " + resultat);
            }
            in.close();
            out.close();
            socket.close();
            System.out.println("Connexion fermée");
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }
}
