
package monopolybank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable{
    
    //Atributos
    private Color color;
    private String name;
    private int balance;
    private boolean bankrupt;
    private int id;
    private transient TextTerminal textTerminal;
    private List<Property> propertiesOwned; //Lista de propiedades adquiridas
    private List<Property> propertiesMortaged; //Lista de propiedades hipotecadas
    
    //Constructor
    public Player(int id , String name, Color color){
        textTerminal = TextTerminal.getInstance();
        this.id = id;
        this.name = name;
        this.color = color;
        this.balance = 1500;
        this.bankrupt = false;
        this.propertiesOwned = new ArrayList<>();
        this.propertiesMortaged = new ArrayList<>();
    }
    
    //Métodos
    
    //Convertir a String
    public String toString(Object info){
        return String.valueOf(info);
    }
    
    //Obtener el balance del jugador
    public int getBalance(){
        return this.balance;
    }
    
    //Hacer operaciones con el dinero del jugador
    public void setBalance(int amount){
        this.balance += amount;
    }
    
    //Pagar (devolvemos True si se ha pagado y False si no se ha pagado)
    public boolean pay(int amount, boolean mandatory){
        textTerminal = TextTerminal.getInstance();
        if(!mandatory){ //Pago no obligatorio
            if(amount <= this.balance){
                textTerminal.showln("1.Pagar");
                textTerminal.showln("2.Cancelar");
                textTerminal.show(">>Introduzca una opcion: ");
                switch(textTerminal.read()){
                    case 1 -> {
                        this.balance -= amount;
                        return true;
                    }
                    case 2 -> {
                        textTerminal.info("Operacion cancelada");
                        return false;
                    }
                }
            } else {
                textTerminal.showln("No hay suficiente dinero para pagar");
                return false;
            }
        } else{ //pago obligatorio
            if(amount <= this.balance){
                this.balance -= amount;
                return true;
            } else{
                this.balance -= amount;
                sellActives(true);
                if(this.balance > 0){
                    return true;
                } else{
                    if(this.propertiesMortaged.isEmpty()){
                        while(!this.propertiesOwned.isEmpty()){
                            textTerminal.info("Dinero insuficiente, se van a hipotecar propiedades");
                            this.showProperties();   
                            textTerminal.show(">>Introduce el id de la propiedad: ");
                            int id = textTerminal.read();
                            Property property = searchProperty(id);
                            if(property != null){
                                mortgage(property);
                            } else{
                                textTerminal.error("No se ha encontrado la propiedad");
                            }
                        }
                    }
                }
                if(this.balance >= 0){
                    return true;
                } else{
                    this.setBankrupt(this);
                    return false;
                }
            }
        }
        return false;
    }
    
    //Mostrar las propiedades que  tiene el jugador
    private void showProperties(){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("LISTA DE PROPIEDADES");
        for(Property property : this.propertiesOwned){
            textTerminal.showln("[" + property.getId() + "] " + property.getName());
        } 
    }
    
    //Vender activos del jugador
    private void sellActives(boolean mandatory){
        //Local var
        int id;
        Property property = null;
        
        //Code
        textTerminal = TextTerminal.getInstance();
        if(!mandatory){
            this.showProperties();   
            textTerminal.show(">>Introduce el id de la propiedad: ");
            id = textTerminal.read();
            property = searchProperty(id);
            if(property != null && property instanceof Street street){
                textTerminal.showln("1.Vender");
                textTerminal.showln("2.Cancelar");
                textTerminal.show(">>Introduzca una opcion: ");
                switch(textTerminal.read()){
                    case 1 -> {
                        textTerminal.show(">>Introduzca el numero de casas que desea vender (disponibles " + street.getBuiltHouses() + "): ");
                        int nHousesSell = textTerminal.read();
                        street.sellHouses(this, nHousesSell);
                    }
                    case 2 -> {
                        textTerminal.info("Operacion cancelada");
                    }
                }
            } else{
                textTerminal.error("La propiedad no pertenece al jugador");
            }
        } else {
            while(this.balance < 0 && thereAreHouseToSell()){
                this.showProperties();   
                textTerminal.show(">>Introduce el id de la propiedad: ");
                id = textTerminal.read();
                property = searchProperty(id);
                if(property != null){
                    if(property instanceof Street street){
                        street.sellHouses(this, street.getBuiltHouses());
                    }
                } else{
                    textTerminal.error("La propiedad no pertenece al jugador");
                }
            }
            if(this.balance < 0){
                textTerminal.showln("No hay suficiente dinero para pagar");
            }
        }
    }
    
    //Saber si hay casas que vender o no
    private boolean thereAreHouseToSell(){
        //Local var
        int nPropertyHasHouses = 0;
        for(Property property : this.propertiesOwned){
            if(property instanceof Street street){
                if(street.getBuiltHouses() > 0){
                    nPropertyHasHouses++;
                }
            }
        }
        return nPropertyHasHouses != 0;
    }
    
    
    //Buscamos la propiedad en la lista de propiedades en posesión
    private Property searchProperty(int id){
        for(Property property : this.propertiesOwned){
                if(property.getId() == id){
                    return property;
                }
        }
        return null;
    }
    
    //Obtener las propiedades
    public List<Property> getProperties(){
        return this.propertiesOwned;
    }
    
    //Obtenemos las propiedades hipotecadas
     public List<Property> getPropertiesMortaged(){
        return this.propertiesMortaged;
    }
    
    //Transpasar propiedades entre jugadores
    public void traspaseProperties(Player newOwner, Property property){
        this.propertiesOwned.remove(property);
        property.setOwner(newOwner);
        newOwner.setProperty(property);
    }
    
    //Establecer una propiedad
    public void setProperty(Property property){
        this.propertiesOwned.add(property);
    }
    
    //Saber si el jugador está en bancarrota
    public boolean getBankrupt(){
        if(this.balance <= 0 && this.propertiesOwned.isEmpty()){
            for(Property property : this.getPropertiesMortaged()){
                property.setOwner(null);
                this.getPropertiesMortaged().remove(property);
            }
            this.bankrupt = true;
            return this.bankrupt;
        }
        this.bankrupt = false;
        return this.bankrupt;
    }
    
    //Establecer bancarrota
    public void setBankrupt(Player newOwner){
        for(int i = 0; i < propertiesOwned.size(); i++){
            propertiesOwned.get(i).setOwner(newOwner);
            propertiesOwned.remove(i);
        }
        textTerminal.showln("El jugador " + this.name + " esta en bancarrota");
        this.bankrupt = true;
    }
    
    //Obtener el color del jugador
    public Color getColor(){
        return this.color;
    }
    
    //Obtener el nombre del jugador
    public String getName(){
        return this.name;
    }
    
    //Obtener el id del jugador
    public int getId(){
        return this.id;
    }
    
    //Hipotecar una propiedad
    public void mortgage(Property property){
        property.setMortgaged(true);
        this.propertiesOwned.remove(property);
        this.propertiesMortaged.add(property);
        this.balance += property.getMortgageValue();
        textTerminal.info(property.getName() + "ha sido hipotecado");
        textTerminal.info("Ingresados " + property.getMortgageValue() + "euros");
    }
    
    //Aceptar venta de una casa
    public void sell(Street street, int nHouses){
        textTerminal.showln("1.Vender");
        textTerminal.showln("2.Cancelar");
        textTerminal.show(">>Introduzca una opcion: ");
        switch(textTerminal.read()){
            case 1 -> this.balance += street.getHousePrice() * nHouses;
            case 2 -> textTerminal.showln("Operacion cancelada");
        }
    }
}
