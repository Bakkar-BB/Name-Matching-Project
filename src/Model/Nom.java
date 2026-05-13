package Model;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
public class Nom {
    private final String nomBrut;
    private final String idNom;
    private List<String> nomPretraite;


    public Nom(String nomBrut){
        this.nomBrut=nomBrut;
        this.idNom=UUID.randomUUID().toString();
        this.nomPretraite= new ArrayList<>();
        this.nomPretraite.add(nomBrut);
    }
    public Nom(String nomBrut , String idNom){
        this.nomBrut=nomBrut;
        this.idNom=idNom;
        this.nomPretraite=new ArrayList<>();
    }
    public String getIdNom(){
        return idNom;
    }
    public  String getNomBrut(){
        return nomBrut;
    }
    public  List<String> getNomPretraite(){
        return List.copyOf(nomPretraite);
    }
    public void setNomPretraite(List<String> names){
        this.nomPretraite=names;
    }

}
