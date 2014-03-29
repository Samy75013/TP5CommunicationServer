/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp5communicationserver;

import java.sql.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class TP5CommunicationServer {

    Connection conn;
    ServerSocket serverSocket;
    static String name;
    static String firstname;
    static String pieceName;
    static int placeNumber;
    static List<String> listPlayFromDatabase;
    static int idResa;
    String pieceNameCombobox;

    public static void main(String[] args) throws IOException {
        TP5CommunicationServer server = new TP5CommunicationServer();
        server.begin(8089);
    }

    public void begin(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            System.out.println("Waiting for clients to connect on port " + port + "...");
            new ProtocolThread(serverSocket.accept()).start();
        }
    }

    class ProtocolThread extends Thread {

        Socket socket;
        PrintWriter out_socket;
        BufferedReader in_socket;

        public ProtocolThread(Socket socket) {
            System.out.println("Accepting connection from " + socket.getInetAddress() + "...");
            this.socket = socket;
            try {
                out_socket = new PrintWriter(socket.getOutputStream(), true);
                in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                Boolean getClientResa = false;
                if (in_socket.readLine().contains("send playList")) {
                    //On envoi la liste de spièces de théâtre à la demande du client
                    socketSendPlayList();//onction pour envoyer la liste des pièces aux clients
                } else {
                    System.out.println("ERREUR: envoie de la liste des pièces");
                }
                while (!getClientResa) {
                    //Boucle pour mettre à jour le nombre de places restantes
                    socketUpdatePlaceAvailable(); //Fonction pour mettre à jour le nombre de places disponibles
                    if (in_socket.readLine().contains("save resa")) {
                        //Boucle si le client demande de sauvegrader la résa
                        socketGetReservation(); //fonction pour récupérer la réservation + appelle fonctionpour stocker dans la bdd 
                        //et renvoyer l'id de réservation au client
                    } else {
                        System.out.println("ERREUR Insertion reservation");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("Closing connection.");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Fonction pour envoyer la liste des pièces au client
        public void socketSendPlayList() throws IOException {
            sqlRequest.getListPlayFromDatabase(); //On récupère la liste des pièces de théâtres de la bdd
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream()); //On crée un objet pour envoyer l'arrayList
            objectOutput.writeObject(listPlayFromDatabase);
            objectOutput.flush(); //On envoi l'arrayList
        }

        //Fonction pour envoyer au client le nombre de place disponible en fonction de la pièce qu'a selectionné le client
        public void socketUpdatePlaceAvailable() throws IOException {
            Boolean changeComboboxPieceName = true;
            while (changeComboboxPieceName) {
                if (in_socket.readLine().contains("go")) {
                    pieceNameCombobox = in_socket.readLine();
                    System.out.println("nom pièce recup chez client: " + pieceNameCombobox);
                    //on récupère le résultat de la réquète dans la bdd pour le nombre de place de la pièce que le client à choisit
                    String s = sqlRequest.getPlaceAvailableRequest(pieceNameCombobox);
                    out_socket.println(s);
                    out_socket.flush();
                    System.out.println("Nombre de place restantes" + s);
                    System.out.println(s);
                } else {
                    System.out.println("Erreur récupération nombre de place restante dans la fonction socketUpdatePlaceAvailable");
                    changeComboboxPieceName = false;
                }
            }
            changeComboboxPieceName = true;
        }

        public void socketGetReservation() throws IOException {
            //on récupère les info entrées par le client
            name = in_socket.readLine();
            firstname = in_socket.readLine();
            pieceName = in_socket.readLine();
            placeNumber = Integer.parseInt(in_socket.readLine());
            System.out.println("Entrées de l'utilisateur:  " + name + " " + firstname + " " + pieceName + " " + placeNumber);
            //on éxecute la réquête permettant l'insertion dans la base
            sqlRequest.performActionFromClient();
            //ENvoi de l'id de la résa au client
            System.out.println("id renvoyer au client: " + idResa);
            out_socket.println(idResa);
        }
    }
}
