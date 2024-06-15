
package monopolybank;

public class Property extends MonopolyCode {
    
    //Atributos
    private int price; //Precio 
    private boolean mortgaged; //Estado de la hipoteca
    private int mortgageValue; //Valor de la hipoteca
    private Player owner; //Jugador propietario
    private String configTextLine; //Texto de configuración
    
    //Constructor
    public Property(int id, String desc, String configInfo, int price, boolean mortgaged, int mortgageValue) {
        super(id, desc, configInfo);
        this.price = price;
        this.mortgaged = mortgaged;
        this.mortgageValue = mortgageValue;
        this.configTextLine = configInfo;
    }
    
    //Métodos
    
    //Obtener lo que tiene que pagar el jugador de alquiler
    public int getPaymentForRent(){
        return 0; //Implementar lógica según las reglas del juego
    }
      
    //Obtener el propietario
    public Player getOwner(){
        return this.owner;
    }
    
    //Establecer propietario
    public void setOwner(Player player){
        this.owner = player;
    }
    
    //Obtener el estado de la propiedad (hipotecada o no)
    public boolean getMortgaged(){
        return this.mortgaged;
    }
    
    //Cambiar el estado de la propiedad
    public void setMortgaged(boolean state){
        this.mortgaged = state;
    }
    
    //Obtener el valor de la hipoteca
    public int getMortgageValue(){
        return this.mortgageValue;
    }
    
    //Obtener el precio de la propiedad
    public int getPrice(){
        return this.price;
    }
    
    //Obtener la descripción de la propiedad
    public String getConfigTextLine(){
        return this.configTextLine;
    }
    
    //Realizar las operaciones de un propietario
    public void doOwnerOperations(){
        //Implementar acciones específicas para el propietario
    }
    
    //Mostrar resumen
    public void showSummary(){
        //Implementar cómo se muestra un resumen de la propiedad
    }
    
    //Obtener el nombre de la propiedad
    public String getName(){
        String[] splitInfo = this.configTextLine.split(";"); //Separar la línea según los ";"
        return splitInfo[2]; //Suponiendo que el nombre se encuentra en el índice 2
    }
}