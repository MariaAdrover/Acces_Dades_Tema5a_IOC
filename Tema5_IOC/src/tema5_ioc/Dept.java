package tema5_ioc;

import java.util.ArrayList;

/*
La classe Dept incorpora la col·lecció d’empleats i admetrem que la col·lecció 
pugui no estar inicialitzada (valor null), fet que indicarà que no s’han 
recuperat els empleats des de la base de dades. D’aquesta manera, 
podrem recuperar de la BD els departaments amb o sense empleats, 
segons ens convingui.
 */
public class Dept {

    private String codi; // atribut
    private String nom;
    private String localitat;
    private ArrayList<Emp> empleats;

    public Dept(String codi, String nom, String localitat) {
        // Suposam que codi és sa primary key de l'empleat
        if (codi == null || codi.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("No es pot crear un departament sense codi, ja que és la PK");
        }

        this.codi = codi;
        this.nom = nom;
        this.localitat = localitat;
        this.empleats = new ArrayList<>();
    }

    public void mostrarDades() {
        System.out.println("------------------------------------");
        System.out.println("DEPARTAMENT - codi " + this.codi);
        System.out.println("nom: " + this.nom);
        System.out.println("localitat: " + this.localitat);
        System.out.println("Empleats:");
        int quantEmpl = this.empleats.size();
        if (quantEmpl == 0) {
            System.out.println("No s'han carregat les dades dels empleats");
        } else {
            for (int i = 0; i < quantEmpl; i++) {
                this.empleats.get(i).mostrarDades();
            }
        }
        System.out.println("------------------------------------");
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setLocalitat(String localitat) {
        this.localitat = localitat;
    }

    public void setEmpleats(ArrayList<Emp> empleats) throws Exception {
        if (empleats != null) {
            // comprovar que tots els empleats són del departament
            for (int i = 0; i < empleats.size() && empleats.get(i).getDept().equals(codi); i++) {
                if (empleats.get(i).getDept() != this.codi) {

                }
            }
        }

        this.empleats = empleats;
    }

    public String getCodi() {
        return codi;
    }

    public String getNom() {
        return nom;
    }

    public String getLocalitat() {
        return localitat;
    }

    public ArrayList<Emp> getEmpleats() {
        return empleats;
    }

}
