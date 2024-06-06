
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
    public Player(int id, TextTerminal terminal, String name, Color color){
        this.id = id;
        this.textTerminal = terminal;
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
        if(!mandatory){ //Pago no obligatorio
            if(amount <= this.balance){
                textTerminal.showln("1.Pagar");
                textTerminal.showln("2.Cancelar");
                textTerminal.show(">>Introduzca la opcion: ");
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
                if(this.propertiesMortaged.isEmpty()){
                    this.balance -= amount;
                    sellActives(Math.abs(this.balance), true);
                    if(this.balance > 0){
                        return true;
                    } else{
                        while(this.balance < 0 || this.propertiesOwned.size() == 0){
                            this.showProperties();   
                            textTerminal.show(">>Introduzca el id de la propiedad a hipotecar: ");
                            int id = textTerminal.read();
                            Property property = searchProperty(id);
                            if(property != null){
                                propertiesOwned.remove(property);
                                propertiesMortaged.add(property);
                                property.setMortgaged(true);
                                this.balance += property.getMortgageValue();
                                textTerminal.info(property.getName() + "ha sido hipotecado");
                                textTerminal.info("Ingresados " + property.getMortgageValue() + "euros");
                            } else{
                                textTerminal.error("No se ha encontrado la propiedad");
                            }
                        }
                        if(this.balance >= 0){
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }
    
    //Mostrar las propiedades que  tiene el jugador
    private void showProperties(){
        textTerminal.showln("LISTA DE PROPIEDADES");
        for(Property property : this.propertiesOwned){
            textTerminal.showln("[" + property.getId() + "] " + property.getConfigTextLine());
        } 
    }
    
    //Establecer bancarrota
    public void setBankrupt(Player newOwner){
        for(int i = 0; i < propertiesOwned.size(); i++){
            propertiesOwned.get(i).setOwner(newOwner);
            propertiesOwned.remove(i);
        }
        this.bankrupt = true;
    }
    
    //Vender los activos del jugador
    private void sellActives(int target, boolean mandatory){     //CAMBIAR LA OBLIGATORIEDAD
        int collected = 0;
        Property property = null;
        do{
            this.showProperties();   
            textTerminal.show(">>Introduce el id de la propiedad: ");
            int id = textTerminal.read();
            property = searchProperty(id);
            if(property != null){
                if(property instanceof Street){
                    Street street = (Street) property;
                
                    if(street.getBuiltHouses() > 0){
                        street.setBuiltHouses(this, -1);
                        collected += street.getHousePrice();
                    } else if(!street.getMortgaged()){
                        mortgage(street);
                        collected += street.getMortgageValue();
                    }
                } else if(!property.getMortgaged()){  
                    mortgage(property);
                    collected += property.getMortgageValue();
                }
            } else{
                textTerminal.error("La propiedad no pertenece al jugador");
                textTerminal.info("Operacion cancelada");
            }
        } while(collected < target && thereAreThingsToSell());  
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
    
    //Obtenemos si hay cosas que  vender o no
    private boolean thereAreThingsToSell(){
        return !this.propertiesOwned.isEmpty();
    }
    
    //Saber si el jugador está en bancarrota
    public boolean getBankrupt(){
        if(this.balance <= 0 && this.propertiesOwned.isEmpty()){
            for(Property property : this.getPropertiesMortaged()){
                property.setOwner(null);
                this.getPropertiesMortaged().remove(property);
            }
            return true;
        }
        return false;
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
    }
}
