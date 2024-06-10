
package monopolybank;

import java.io.Serializable;

public class MonopolyCode implements Serializable{
    
    //Atributos
    private String description;
    private int id;
    private transient TextTerminal textTerminal;
    
    //Constructor
    public MonopolyCode(int id, String desc, String configInfo){
        textTerminal = TextTerminal.getInstance();
        this.id = id;
        this.description = desc;
    }
    
    //Métodos
    
    //Pasar a String
    public String toString(Object info){
        return String.valueOf(info);
    }
    
    //Realizar una operación
    public void doOperation(Player p){}
    
    //Obtener el id
    public int getId(){
        return this.id;
    }
}
