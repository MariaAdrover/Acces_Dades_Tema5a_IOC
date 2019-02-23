package tema5_ioc;

import java.io.IOException;

public class ProvaGestorBD {

    public static void main(String[] args) {
        // 1. Obrir connexió
        GestorBD gestor = new GestorBD("open empresa", "localhost", 1984, "admin", "admin");
        Dept d;

        /* 2. Cercar el departament amb un codi determinat.
         No carregar informació sobre els seus empleats.
        d = gestor.getDeptSenseEmp("d20");
        if (d != null) {
            // Mostrar les dades del departament
            d.mostrarDades();
        }*/
        
        
        //......................................................................
        /* 3. Cercar el departament pel seu codi i carregar les dades dels seus empleats
        
        d = gestor.getDeptAmbEmp("d20");
        if (d != null) {
            // Mostrar les dades del departament
            d.mostrarDades();
        }*/
        
        
        //......................................................................
        /* 4. Inserir nou departament
        // Cream el departament
        d = new Dept("f18", "singers", "Palma");
        // Cream tres empleats i els assignam al departament
        Emp e = new Emp("e7369", "f18");//ya existe
        e.setCognom("Sanchez");
        Emp f = new Emp("e1111", "f18");//no existe
        f.setCognom("Pep");
        Emp g = new Emp("e2222", "f18", "apellido", "oficio", "3-12-2000", 5000, 40, "e5555");//no existe
        g.setCognom("Pau");
        d.getEmpleats().add(e);
        d.getEmpleats().add(f);
        d.getEmpleats().add(g);
        
        // Inserim el departament
        try {
            gestor.insertDept(d);
        } catch (IOException ex) {
            ex.getMessage();
        }
        
        // Mostram xml del departament amb codi f18 i els seus empleats
        gestor.showDeptXmlByCodi("f18");
        gestor.showEmpsXmlByDeptCodi("f18"); */
        
        
        //......................................................................
        /* 5.a Eliminar dept i reubicar emp        
        gestor.showDeptXmlByCodi("d10");
        gestor.showEmpsXmlByDeptCodi("d10");
        gestor.deleteDept("d10", "f18");
        gestor.showDeptXmlByCodi("d10");
        gestor.showEmpsXmlByDeptCodi("d10");
        gestor.showDeptXmlByCodi("f18");
        gestor.showEmpsXmlByDeptCodi("f18");*/ 
        
        //......................................................................        
        /* 5.b Eliminar departament i els seus empleats     
        gestor.showDeptXmlByCodi("d20");
        gestor.showEmpsXmlByDeptCodi("d20");
        gestor.deleteDept("d20", null);   
        gestor.showDeptXmlByCodi("d20");
        gestor.showEmpsXmlByDeptCodi("d20");        */
        
        //......................................................................
        /* 6. Substituir el departament "d30" que te 6 empleats pel departament "888"
        System.out.println("......................................................................");
        System.out.println("       SUBSTITUIREM EL DEPARTAMENT d30 PER UN NOU DEPARTAMENT 888");
        System.out.println("......................................................................");
        // Mostram les dades del departament que volem substituir
        Dept old = gestor.getDeptAmbEmp("d30");
        old.mostrarDades();
        
        // Cream i inserim un nou departament
        Dept nou = new Dept("888", "nouDept", "PalmaII");
        
        // Reemplaçam el departament d30 pel 888, i traslladam els empleats del d30 al 888
        gestor.replaceDept(nou, old.getCodi());
        
        System.out.println("......................................................................");
        System.out.println("                         DADES ACTUALITZADES");
        System.out.println("......................................................................");
        // Actualitzem les dades dels objectes old i nou
        old = gestor.getDeptAmbEmp("d30");
        nou = gestor.getDeptAmbEmp("888");        
        // Mostram les dades del nou departament substitut (888);
        nou.mostrarDades();*/

        // Tanquem la sessió
        gestor.tancarSessió();
    }

}
