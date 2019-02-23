package tema5_ioc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

public class GestorBD {

    ClientSession sessio;

    //Constructor, que estableix la connexió amb la BD
    public GestorBD(String dbName, String host, int port, String user, String pwd) {
        try {
            sessio = new ClientSession(host, port, user, pwd);
            sessio.execute(dbName);
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    //per tancar la connexió amb la BDº
    public void tancarSessió() {
        try {
            if (sessio != null) {
                sessio.execute("close");
                sessio.close();
            }
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    public Dept getDeptSenseEmp(String codiDept) {
        Dept departament = null;
        ClientQuery q;

        try {

            String consulta = "/empresa/departaments/dept[@codi=\"" + codiDept + "\"]/@codi/string()";
            q = sessio.query(consulta);
            String codi = q.execute();
            q.close();

            if (codi.trim().equalsIgnoreCase("")) {
                if (codiDept != null) {
                    System.out.println("No existeix cap departament amb el codi " + codiDept);
                }
                return null; // ----------------------------------------------->
            } else {
                consulta = "/empresa/departaments/dept[@codi=\"" + codiDept + "\"]/nom/text()";
                q = sessio.query(consulta);
                String nom = q.execute();
                q.close();
                consulta = "/empresa/departaments/dept[@codi=\"" + codiDept + "\"]/localitat/text()";
                q = sessio.query(consulta);
                String localitat = q.execute();
                q.close();
                departament = new Dept(codiDept, nom, localitat);
            }

        } catch (IOException ex) {
            System.out.println("error");
            ex.getMessage();
        }

        return departament;
    }

    private Emp crearEmpleat(String dataEmp[]) {
        Emp empleat = null;

        if (dataEmp[0] == null || dataEmp[1] == null) {
            System.out.println("No es pot crear un empleat sense el codi de l'empleat i/o del seu departament");
        } else {
            String codiEmp = dataEmp[0];
            String codiDept = dataEmp[1];
            String cognom;
            String ofici;
            String dataAlta;
            int salari;
            String cap;
            int comissio;

            if (dataEmp[2].trim().equalsIgnoreCase("")) {
                cognom = null;
            } else {
                cognom = dataEmp[2];
            }

            if (dataEmp[3].trim().equalsIgnoreCase("")) {
                ofici = null;
            } else {
                ofici = dataEmp[3];
            }

            if (dataEmp[4].trim().equalsIgnoreCase("")) {
                dataAlta = null;
            } else {
                dataAlta = dataEmp[4];
            }

            if (dataEmp[5].trim().equalsIgnoreCase("")) {
                salari = 0;
            } else {
                salari = Integer.parseInt(dataEmp[5].trim());
            }

            if (dataEmp[6].trim().equalsIgnoreCase("")) {
                cap = null;
            } else {
                cap = dataEmp[6];
            }

            if (dataEmp[7].trim().equalsIgnoreCase("")) {
                comissio = 0;
            } else {
                comissio = Integer.parseInt(dataEmp[7].trim());
            }

            empleat = new Emp(codiEmp, codiDept, cognom, ofici, dataAlta, salari, comissio, cap);
        }
        return empleat;
    }

    public Dept getDeptAmbEmp(String codiDept) {
        //getDeptAmbEmp, per obtenir un departament des de la BD, amb els seus empleats (col·lecció d’empleats, 
        //que en particular pot estar buida, fet que indicarà que el departament no té cap empleat).
        Dept departament = this.getDeptSenseEmp(codiDept);

        if (departament != null) {
            ArrayList<Emp> empleats = departament.getEmpleats();

            try {
                String consulta = "for $x in doc(\"empresa\")//emp "
                        + "where $x/@dept = \"" + codiDept + "\" "
                        + "return concat"
                        + "(\"/\",$x/@codi,\",\",$x/@dept,\",\",$x/cognom,\",\",$x/ofici,\",\","
                        + "$x/dataAlta,\",\",$x/salari,\",\",$x/@cap,\",\",$x/comissio,\" \")";

                String resultat = sessio.query(consulta).execute();
                String[] emps = resultat.split("/");
                String[] dataEmp;
                for (int i = 1; i < emps.length; i++) {
                    dataEmp = emps[i].split(",");
                    // Cream l'empleat amb les dades obtingudes
                    Emp empleat = crearEmpleat(dataEmp);

                    // Afegim l'empleat a l'array d'empleats del departament
                    empleats.add(empleat);
                }

            } catch (IOException ex) {
                ex.getMessage();
            }
        }

        return departament;
    }

    private void createCodiXmlDept(Dept departament) {
        ClientQuery q;
        try {
            String insert = "insert node <dept codi=\"" + departament.getCodi() + "\">";

            // Si tiene seteado el nombre, lo incluimos...
            if (departament.getNom() != null) {
                insert += "<nom>" + departament.getNom() + "</nom>";
            }

            // si tiene seteada la localidad, la incluimos...
            if (departament.getLocalitat() != null) {
                insert += "<localitat>" + departament.getLocalitat() + "</localitat>";
            }

            insert += "</dept> into /empresa/departaments";

            q = sessio.query(insert);
            q.execute();
            q.close();

        } catch (IOException ex) {
            ex.getMessage();
        }

    }

    public void insertDept(Dept departament) throws IOException {

        if (!existDept(departament.getCodi())) {
            // Si el departament que volem inserim no existeix...
            // ...inserim el codi xml del nou departament
            createCodiXmlDept(departament);            

            // Si els empleats del departament són a la llista vol dir que ja existeixen
            // Eliminarem l'empleat i el tornarem a crear amb les noves dades
            // Els que no existeixen els crearem
            for (int i = 0; i < departament.getEmpleats().size(); i++) {
                Emp empleat = departament.getEmpleats().get(i);
                String codiEmp = empleat.getCodi();

                if (this.existEmp(codiEmp)) {
                    System.out.println("L'empleat amb el codi " + codiEmp 
                            + " ja existeix. Crearem el nou codi i eliminarem l'antic per actualitzar les seves dades");
                    String order = "delete node //emp[@codi='" + codiEmp + "']";
                    sessio.query(order).execute();
                } else {
                    System.out.println("L'empleat amb el codi " + codiEmp + " NO existeix. Crearem el seu codi");                    
                }
                createCodiXmlEmp(empleat, departament);
            }
        } else {
            System.out.println("El departamento ya existe.");
        }
    }

    private void createCodiXmlEmp(Emp empleat, Dept departament) {

        try {
            String insert = "insert node <emp codi=\"" + empleat.getCodi() + "\" ";// codi
            insert += " dept = \"" + departament.getCodi() + "\" ";// departament

            if (empleat.getCap() != null) {
                insert += "cap = \"" + empleat.getCap() + "\""; //cap
            }

            insert += ">";

            if (empleat.getCognom() != null) {
                insert += "<cognom>" + empleat.getCognom() + "</cognom>"; //cognom
            }

            if (empleat.getOfici() != null) {
                insert += "<ofici>" + empleat.getOfici() + "</ofici>";//ofici
            }

            if (empleat.getDataAlta() != null) {
                insert += "<dataAlta>" + empleat.getDataAlta() + "</dataAlta>";//dataAlta
            }

            if (empleat.getSalari() != 0) {
                insert += "<salari>" + empleat.getSalari() + "</salari>";//salari
            }

            if (empleat.getComissio() != 0) {
                insert += "<comissio>" + empleat.getComissio() + "</comissio>";//comissio
            }

            insert += "</emp> into /empresa/empleats";
            sessio.query(insert).execute();
        } catch (IOException ex) {
            ex.getMessage();
        }

    }

    private void eliminarDeptiEmps(String codiDept) {
        // Elimina els empleats del departament codiDept
        // Si algún dels empleats és cap, elimina l'atribut cap
        try {
            String order;
            order = "let $dept := /empresa/departaments/dept[@codi=\"" + codiDept + "\"] "
                    + "return ( "
                    + "  delete node $dept, "
                    + "  for $emp in /empresa/empleats/emp[@dept=\"" + codiDept + "\"] "
                    + "  return ( "
                    + "    delete node $emp, "
                    + "    for $cap in /empresa/empleats/emp[@cap=$emp/@codi/string()]/@cap "
                    + "    return delete node $cap ))";
            ClientQuery cq = sessio.query(order);
            cq.execute();
            cq.close();
        } catch (IOException ex) {
            ex.getMessage();
        }

    }

    private void eliminarDeptReassignantEmps(String codiDept, String nouDept) {
        // consulta delete para que borre dept con la id del parametro
        // reasignamos sus empleados a otro departamento
        try {
            String order;

            order = "let $dept:=/empresa/departaments/dept[@codi=\"" + codiDept + "\"] "
                    + "return ( "
                    + "  delete node $dept, "
                    + "  for $emp in /empresa/empleats/emp[@dept=\"" + codiDept + "\"]/@dept "
                    + "  return replace value of node $emp with \"" + nouDept + "\")";

            ClientQuery cq = sessio.query(order);
            cq.execute();
            cq.close();

        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    public void deleteDept(String codiDept, String nouDept) {
        // Mètode per eliminar un departament de la BD
        // Si nouDept és null s'eliminen els empleats.
        // Si nouDept NO és null, els empleats són reassignats a aquest departament

        Dept dept = this.getDeptSenseEmp(codiDept);
        Dept nouD = this.getDeptSenseEmp(nouDept);

        if (dept == null) {
            System.out.println("El departament que vols borrar no existeix.");
        } else if (dept != null && nouD != null) {
            this.eliminarDeptReassignantEmps(codiDept, nouDept);
        } else {
            this.eliminarDeptiEmps(codiDept);
        }

    }

    public void showDeptXmlByCodi(String deptCodi) {
        // Donat un codi de departament, mostra el codi de l'arxiu .xml del departament

        System.out.println("----------------------------------------------");
        System.out.println("Codi XML del departament amb codi " + deptCodi);
        try {
            String qer = "for $x in doc(\"empresa\")//dept where $x/@codi = \"" + deptCodi + "\" return $x";
            String resultatXml = sessio.query(qer).execute();
            System.out.println(resultatXml);
        } catch (IOException ex) {
            Logger.getLogger(GestorBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showEmpsXmlByDeptCodi(String deptCodi) {
        // Donat un codi de departament, mostra el codi de l'arxiu .xml de 
        // tots els empleats que estan en aquest departament        

        System.out.println("----------------------------------------------");
        System.out.println("Codi XML dels empleats amb departament amb codi " + deptCodi);
        try {
            String qer = "for $x in doc(\"empresa\")//emp where $x/@dept = \"" + deptCodi + "\" return $x";
            String resultatXml = sessio.query(qer).execute();
            System.out.println(resultatXml);
        } catch (IOException ex) {
            Logger.getLogger(GestorBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void replaceDept(Dept nouDept, String oldDept) {
        //insert nou departament amb uns empleats (si els empleats existeixen els elimina)
        // + delete departament substituit, passant els empleats del dept borrat al nou departament -->

        Dept old = this.getDeptSenseEmp(oldDept);
        if (nouDept == null || old == null) {
            System.out.println("Per reemplaçar un departament per un altre, ambdos han de existir.");
        } else {
            // Cream el codi del nou departament i dels seus empleats (si aqlgun dels empleats ja existeix, s'actualitzen les seves dades amb les del nou
            try {
                this.insertDept(nouDept);
            } catch (IOException ex) {
                ex.getMessage();
            }

            // Eliminam l'antic departament, reassignant els seus empleats al nou departament
            this.eliminarDeptReassignantEmps(oldDept, nouDept.getCodi());

        }
    }

    private boolean existDept(String codiDept) {
        boolean exist = true;
        try {
            String cad = "/empresa/departaments/dept[@codi=\"" + codiDept + "\"]";
            ClientQuery cq = sessio.query(cad);
            String resultat = cq.execute();
            cq.close();
            if (resultat.length() == 0) {
                exist = false;
            }
        } catch (Exception ex) {
            ex.getMessage();
        }

        return exist;
    }

    private boolean existEmp(String codiEmp) {
        boolean exist = true;
        try {
            String cad = "/empresa/empleats/emp[@codi=\"" + codiEmp + "\"]";
            ClientQuery cq = sessio.query(cad);
            String resultat = cq.execute();
            cq.close();
            if (resultat.length() == 0) {
                exist = false;
            }
        } catch (Exception ex) {
            ex.getMessage();
        }

        return exist;
    }

}
