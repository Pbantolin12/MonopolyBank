
package monopolybank;

import java.io.Serializable;
import java.util.Map;

public class MonopolyCode implements Serializable{
    
    //Atributos
    private String description; //Descripción del código
    private int id; //Identificador del código
    private transient TextTerminal textTerminal; //Terminal para la interacción con el usuario
    
    //Constructor
    public MonopolyCode(int id, String desc, String configInfo){
        textTerminal = TextTerminal.getInstance(); //Obtiene la instancia del terminal
        this.id = id; //Inicializa el identificador
        this.description = desc; //Inicializa la descripción
    }
    
    //Métodos
    
    //Pasar a String
    public String toString(Object info){
        return String.valueOf(info);
    }
    
    //Realizar una operación
    public void doOperation(Player p, Map<Integer, Player> playerList){}
    
    //Obtener el id
    public int getId(){
        return this.id;
    }
}