package serverPackage;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Server {
    private static final int PORT = 1234;
    private static int nbClients = 0;

    private static InetAddress adresseReelle() throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                continue;
            }
            Enumeration<InetAddress> adresses = ni.getInetAddresses();
            while (adresses.hasMoreElements()) {
                InetAddress a = adresses.nextElement();
                if (a instanceof Inet4Address && a.isSiteLocalAddress()) {
                    return a;
                }
            }
        }
        return InetAddress.getLocalHost();
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Je suis un serveur multi-thread en attente de clients");
            System.out.println("Adresse IP : " + adresseReelle().getHostAddress() + " - Port : " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                nbClients++;
                System.out.println("Client n°" + nbClients + " connecté : " + socket.getRemoteSocketAddress());
                new ClientProcess(socket, nbClients).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
