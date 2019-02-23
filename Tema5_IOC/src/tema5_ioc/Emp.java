package tema5_ioc;

/*
La classe Emp incorpora el codi del departament al qual pertany l’empleat 
(enlloc d’una referència a l’objecte departament). 
D’aquesta manera podrem recuperar de la BD un empleat sense haver de 
recuperar el departament.

I respecte l’associació <téPerCap – ésCapDe>, la classe Emp incorpora 
el codi de l’empleat que és el cap (en cas de tenir un cap) o null en cas contrari.
 */
public class Emp {

    private String codi; // atribut requerit 
    private String cognom;
    private String ofici;
    private String dataAlta;
    private int salari;
    private int comissio;
    private String cap;
    private String dept; // atribut requerit

    public Emp(String codi, String dept) {
        // Suposam que codi és sa primary key de l'empleat
        if (codi == null || codi.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("No es pot crear un empleat sense el codi de l'empleat i del seu departament");
        }

        this.codi = codi;
        this.dept = dept;
    }

    public Emp(String codi, String dept, String cognom, String ofici, String dataAlta, int salari, int comissio,
            String cap) {

        // Suposam que codi és sa primary key de l'empleat
        if ((codi == null || codi.equalsIgnoreCase("")) || (dept == null || dept.equalsIgnoreCase(""))) {
            throw new IllegalArgumentException("No es pot crear un empleat sense el codi de l'empleat i del seu departament");
        }

        this.codi = codi;
        this.dept = dept;
        this.cognom = cognom;
        this.ofici = ofici;
        this.dataAlta = dataAlta;
        this.salari = salari;
        this.comissio = comissio;
        this.cap = cap;
    }

    public void mostrarDades() {
        System.out.println("........");
        System.out.println("EMPLEAT - codi " + this.codi);
        System.out.println("dept: " + this.dept);
        System.out.println("cognom: " + this.cognom);
        System.out.println("ofici: " + this.ofici);
        System.out.println("dataAlta: " + this.dataAlta);
        System.out.println("salari: " + this.salari);
        System.out.println("comissio: " + this.comissio);
        System.out.println("cap: " + this.cap);
        System.out.println("........");
    }

    public void setCognom(String cognom) {
        this.cognom = cognom;
    }

    public void setOfici(String ofici) {
        this.ofici = ofici;
    }

    public void setDataAlta(String dataAlta) {
        this.dataAlta = dataAlta;
    }

    public void setSalari(int salari) {
        this.salari = salari;
    }

    public void setComissio(int comissio) {
        this.comissio = comissio;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public void setDept(String dept) {
        if (dept == null || dept.equalsIgnoreCase("")) {
            System.out.println("El codi del departament no pot estar buid ni ser null");
        } else {
            this.dept = dept;
        }
    }

    public String getCodi() {
        return codi;
    }

    public String getCognom() {
        return cognom;
    }

    public String getOfici() {
        return ofici;
    }

    public String getDataAlta() {
        return dataAlta;
    }

    public int getSalari() {
        return salari;
    }

    public int getComissio() {
        return comissio;
    }

    public String getDept() {
        return dept;
    }

    public String getCap() {
        return cap;
    }

}
