
package monopolybank;

public class Property extends MonopolyCode {
    
    //Atributos
    private int price;
    private boolean mortaged;
    private int mortgageValue;
    private String propertyType;
    private Player owner;
    private String configTextLine;
    private transient TextTerminal textTerminal;
    
    //Constructor
    public Property(int id, String desc, String configInfo, int price, boolean mortaged, int mValue) {
        super(id, desc, configInfo);
        textTerminal = TextTerminal.getInstance();
        this.price = price;
        this.mortaged = mortaged;
        this. mortgageValue = mValue;
        this.propertyType = desc;
        this.configTextLine = configInfo;
    }
    
    //Métodos
    
    //Obtener lo que tiene que pagar el jugador de alquiler
    public int getPaymentForRent(){
        //Local var
        int cont = 0;
        
        //Code
        textTerminal = TextTerminal.getInstance();
        textTerminal.show(">>Introduce el numero marcado en los dados: ");
        int num = textTerminal.read();
        for(Property property: this.getOwner().getProperties()){
            if(this.getClass().equals(property.getClass())){
                cont++;
            }
        }
        switch(cont){
            case 1 -> {
                return 4*num;
            }
            case 2 -> {
                return 10*num;
            }
            default -> {
                textTerminal.error("La propiedad no pertenece al jugador");
                textTerminal.info("Operacion cancelada");
                return 0;
            }
        }
    }
    
    //Obtener el propietario
    public Player getOwner(){
        return this.owner;
    }
    
    //Establecer propietario
    public void setOwner(Player player){
        this.owner = player;
    }
    
    //Obtener el estado de la propiedad (hipoecada o no)
    public boolean getMortgaged(){
        return this.mortaged;
    }
    
    //Cambiar el estado de la propiedad
    public void setMortgaged(boolean state){
        this.mortaged = state;
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
    public void doOwerOperations(){}
    
    //Mostrar resumen
    public void showSummary(){}
    
    //Obtener el nombre de la propiedad
    public String getName(){
        String[] splitInfo = this.configTextLine.split(";"); //Separa la línea según los ";"
        return splitInfo[2];
    }
    
    //Realizar las operaciones de un propietario
    public void doOwnerOperations(){}
}
