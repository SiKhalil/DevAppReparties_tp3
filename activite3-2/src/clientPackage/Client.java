package clientPackage;

import commonPackage.Operation;
import commonPackage.Resultat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private static final int PORT = 1234;
    private static final Pattern OPERATION = Pattern.compile("^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$");

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
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Integer numero = (Integer) in.readObject();
            System.out.println("Serveur : vous êtes le client n°" + numero);
            while (true) {
                System.out.print("Entrez une opération (ex : 55 * 25) ou FIN pour quitter : ");
                String ligne = scanner.nextLine().trim();
                if (ligne.equalsIgnoreCase("FIN")) {
                    out.writeObject(null);
                    out.flush();
                    break;
                }
                Matcher m = OPERATION.matcher(ligne);
                if (!m.matches()) {
                    System.out.println("Syntaxe incorrecte : utilisez le format  nombre opérateur nombre  avec + - * /");
                    continue;
                }
                Operation operation = new Operation(Double.parseDouble(m.group(1)), m.group(2).charAt(0), Double.parseDouble(m.group(3)));
                out.writeUnshared(operation);
                out.flush();
                Resultat resultat = (Resultat) in.readObject();
                System.out.println("Résultat : " + resultat);
            }
            in.close();
            out.close();
            socket.close();
            System.out.println("Connexion fermée");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        scanner.close();
    }
}
