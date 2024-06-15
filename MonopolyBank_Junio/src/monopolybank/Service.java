
package monopolybank;

public class Service extends Property{
    
    // Atributos
    private transient TextTerminal textTerminal; //Terminal para interacción con el usuario
    
    //Constructor
    public Service(int id, String desc, String configInfo, int price, boolean mortaged, int mValue){
        super(id, desc, configInfo, price, mortaged, mValue); //Llama al constructor de la clase base
        textTerminal = TextTerminal.getInstance(); //Obtiene la instancia del terminal
    }
    
    //Métodos
    
    //Obtiene el valor del alquiler a pagar
    @Override
    public int getPaymentForRent(){
        textTerminal = TextTerminal.getInstance();
        textTerminal.show(">>Introduce el numero marcado en los dados: ");
        int num = textTerminal.read(); //Lee el número del dado
        int amount;
        //Calcula el alquiler dependiendo del número de servicios del propietario y el número del dado
        amount = switch(this.getNumberService()){
            case 1 -> 4 * num;
            case 2 -> 10 * num;
            default -> 0;
        };
        //Aplica descuento si la propiedad está hipotecada
        if(this.getMortgaged()){
            return (int) Math.round(amount * 0.3);
        } else {
            return amount;
        }
    }
    
    //Obtiene el número de cartas que posee el jugador
    private int getNumberService(){
        //Local var
        int count = 0;
        
        //Code
        //Cuenta cuántas propiedades de tipo Service posee el jugador
        for(Property property : this.getOwner().getProperties()){
            if(property instanceof Service){
                count++;
            }
        }
        return count;
    }
    
    //Muestra un resumen del pago
    private void showPaymentSummary(int amount, Player player){
        textTerminal = TextTerminal.getInstance();
        //Muestra el resumen del pago considerando si la propiedad está hipotecada
        if(this.getMortgaged()){
            textTerminal.showln("El jugador " + player.getColor() + " usara la propiedad " + this.getName() + " con " +
                    this.getNumberService() + " servicios, que esta hipotecada. Por ello, pagara " + amount + " al jugador " + 
                    this.getOwner().getColor() + " (1/3 de su valor)");
        } else{
            textTerminal.showln("El jugador " + player.getColor() + " usara la propiedad " + this.getName() + " con " +
                    this.getNumberService() + " servicios. Por ello, pagara " + amount + " al jugador " + this.getOwner().getColor());
        }
    }
    
    //Muestra un resumen de la compra
    private void showPurchaseSummary(int amount, Player player){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("Se va a realizar la compra de la propiedad " + this.getName() + " por parte del jugador " +
                player.getColor() + " por un importe de " + amount + " euros");
    }
    
    //Realizar una operación
    @Override
    public void doOperation(Player player){
        if(this.getOwner() == null){
            //Si la propiedad no tiene dueño, se muestra el resumen de compra y se realiza la transacción
            this.showPurchaseSummary(this.getPrice(), player);
            player.pay(this.getPrice(), false);
            player.setProperty(this);
            this.setOwner(player);
        } else if(this.getOwner().equals(player)){
            // Si el jugador es el propietario, se realizan operaciones específicas del propietario
            this.doOwnerOperations();
        } else if(!this.getMortgaged()){
            //Si la propiedad tiene un dueño distinto al jugador y no está hipotecada, se calcula y muestra el pago por alquiler
            int payment = this.getPaymentForRent();
            this.showPaymentSummary(payment, player);
            if(!player.pay(payment, true)){ //Si el jugador no puede pagar, se declara en bancarrota
                player.setBankrupt(this.getOwner());
            } else{
                this.getOwner().setBalance(payment); //Se añade el pago al balance del propietario
            }
        }
    }
    
    //Realiza operaciones específicas del propietario
    @Override
    public void doOwnerOperations(){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("1. Hipotecar");
        textTerminal.showln("2. Deshipotecar");
        textTerminal.show(">>Introduzca una opcion: ");
        switch(textTerminal.read()){
            case 1 ->{
                if(!this.getMortgaged()){
                    this.setMortgaged(true); //Hipoteca la propiedad si no está hipotecada
                } else{
                    textTerminal.error("La propiedad esta hipotecada");
                }
            }
            case 2 ->{
                if(this.getMortgaged()){
                    this.setMortgaged(false); //Deshipoteca la propiedad si está hipotecada
                } else{
                    textTerminal.error("La propiedad no esta hipotecada");
                }
            }
            default -> textTerminal.error("Opcion incorrecta");
        }
    }
}
