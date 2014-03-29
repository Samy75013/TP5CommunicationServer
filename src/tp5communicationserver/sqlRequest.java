/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp5communicationserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import static tp5communicationserver.sqlRequest.conn;

/**
 *
 * @author baptman
 */
public class sqlRequest {

    //classe contenant les connexion à la bdd et l'ensemble de nos requêtes
    static Connection conn;
    static String url = "jdbc:mysql://localhost/piecestheatre"; //fonctionne avec dpisep.isep.fr ou en localhost en 
    //importer le fichier sql joint 

    //Permet de stocker la liste des noms des pièces de théâtre dans la liste du fichier TP5CommunicationServer
    public static void getListPlayFromDatabase() {
        connexionDatabase();
        getPlayName();
        disconnectDatabase();
    }

    //Permet d'enregistrer la reservation récupérer du client et de la stocker dans la bdd (insertReservation)
    //Permet aussi de mettre à jour le nombre de place restante en fonction de la réservation effectué dans la bdd
    public static void performActionFromClient() {
        connexionDatabase();
        insertReservation();//enregistrement
        updatePlaceNumber();//MAJ
        disconnectDatabase();
    }

    //Fonction permettant de se connecter à la bdd
    public static void connexionDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, "root", "");
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.err.println(ex.getMessage());
        } catch (InstantiationException ex) {
            System.err.println(ex.getMessage());
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //Fonction permettant de se déconnecter de la bdd
    public static void disconnectDatabase() {
        try {
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //requête permettant d'insérer la réservation du client dans la bdd
    public static void insertReservation() {
        System.out.print("\n[Performing INSERT reservation in table chegaray_abtout_reservation] ... ");
        try {
            TP5CommunicationServer.idResa = countIdReservation();
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO chegaray_abtout_reservation   "
                    + "VALUES (" + TP5CommunicationServer.idResa + ", '" + TP5CommunicationServer.name + "', '" + TP5CommunicationServer.firstname + "', NOW(), " + TP5CommunicationServer.placeNumber + ", " + getIdTheaterPlay() + ")");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //requête permettant de remplir la table reservation manuellement (utilisé pour le debut du projet)
    public static void insertReservationExample(int id, String name, String firstname, int placeNumber, int idPlay) {
        System.out.print("\n[Performing INSERT reservationExample in table chegaray_abtout_reservation] ... ");
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO chegaray_abtout_reservation   "
                    + "VALUES (" + id + ", '" + name + "', '" + firstname + "', NOW(), " + placeNumber + ", " + idPlay + ")");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //requête permettant d'insérer les pièces manuellement dans la bdd
    public static void insertPieceExample(int id, String pieceName, int placeAvailable) {
        System.out.print("\n[Performing INSERT] ... ");
        System.out.println("TETETSTSTSTSTTSTSST");
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO chegaray_abtout_piecetheatre   "
                    + "VALUES (" + id + ", '" + pieceName + "', " + placeAvailable + ")");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //requête permettant d'afficher les réservations
    public static void selectReservation() {
        System.out.println("\n[OUTPUT FROM SELECT]");
        String query = "SELECT * FROM chegaray_abtout_reservation";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String s = rs.getString("id");
                String n = rs.getString("nom");
                String n1 = rs.getString("prenom");
                String n2 = rs.getString("date");
                System.out.println(s + "   " + n + "   " + n1 + "   " + n2);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //requête permettant d'afficher les pièces de la bdd
    public static void selectPiece() {
        System.out.println("\n[OUTPUT FROM SELECT table piece]");
        String query = "SELECT * FROM chegaray_abtout_piecetheatre";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String s = rs.getString("id");
                String n = rs.getString("nom");
                String n1 = rs.getString("nombrePlace");
                System.out.println(s + "   " + n + "   " + n1);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //requête permettant d'avoir l'id de la prochaine réservation
    public static int countIdReservation() {
        int nextIdNumber = 0;
        System.out.println("\n[OUTPUT FROM countIdReservation]");
        String query = "SELECT COUNT(*) FROM chegaray_abtout_reservation";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            rs.next();
            nextIdNumber = rs.getInt(1);
            nextIdNumber++;
            System.out.println(nextIdNumber);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return nextIdNumber;
    }

    //requête permettant de récupérer l'id du théâtre en fonction du nom de la pièce selectionénes par le client
    public static int getIdTheaterPlay() {
        int idTheaterPlay = 0;
        System.out.println("\n[OUTPUT FROM SELECT theatre id]");
        String query = "SELECT id FROM chegaray_abtout_piecetheatre WHERE nom='" + TP5CommunicationServer.pieceName + "'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            rs.next();
            idTheaterPlay = rs.getInt(1);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return idTheaterPlay;
    }

    //requête permettant de stocker la liste des noms des pièces de théâtre dans la liste du fichier TP5CommunicationServer
    public static void getPlayName() {
        List<String> listPlay = new ArrayList<String>();
        System.out.println("\n[OUTPUT FROM getPlayName]");
        String query = "SELECT nom FROM chegaray_abtout_piecetheatre";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                listPlay.add(rs.getString("nom")); //on stocke chaque nom dans une liste temporaire
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        TP5CommunicationServer.listPlayFromDatabase = listPlay; //on stocker cette liste dans liste de TP5CommunicationServer
        System.out.println("[ENDDD]");
    }

    //permet de mettre à jour le nombre de place disponible dans une pièce en fonction du nombre de place
    //que contient la réservation du client
    public static void updatePlaceNumber() {
        System.out.println("\n[OUTPUT FROM delete]");
        String query = "UPDATE chegaray_abtout_piecetheatre SET nombrePlace=nombrePlace-" + TP5CommunicationServer.placeNumber + " WHERE nom='" + TP5CommunicationServer.pieceName + "'";
        try {
            Statement st = conn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //requête permettant d'obtenir le nombre de place diponible pour la pièce sélectionnée par le client
    public static String getPlaceAvailableRequest(String pieceCombobox) {
        connexionDatabase();
        System.out.println("\n[OUTPUT FROM SELECT nomrePlace]");
        String query = "SELECT nombrePlace FROM chegaray_abtout_piecetheatre WHERE nom='" + pieceCombobox + "'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String s = rs.getString("nombrePlace");
                System.out.println(s);
                int nb = Integer.parseInt(s);
                //return nb;
                disconnectDatabase();
                return s;

            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return "nulll";
    }
}
