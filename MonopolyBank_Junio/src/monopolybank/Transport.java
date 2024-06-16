
package monopolybank;

public class Transport extends Property{
    
    //Atributos
    private int[] costStaying; //Precio del alquiler dependiendo del número de estaciones que el jugador tenga
    private transient TextTerminal textTerminal; //Terminal para interacción con el usuario

    //Constructor
    public Transport(int id, String desc, String configInfo , int price, boolean mortaged, int mValue) {
        super(id, desc, configInfo, price, mortaged, mValue);
        textTerminal = TextTerminal.getInstance();
        String[] splitInfo = configInfo.split(";"); //Separa la línea según los ";"
        this.costStaying = copyInfo(splitInfo);
    }
    
    //Métodos
    
    //Obtener el valor del alquiler a pagar
    @Override
    public int getPaymentForRent(){
        if(this.getNumberTransport() >= 0){
            if(this.getMortgaged()){ //Modificación 2
                return (int) Math.round(this.costStaying[this.getNumberTransport()] * 0.3);
            } else{
                return this.costStaying[this.getNumberTransport()];
            }
        }
        return 0;
    }
    
    //Obtenemos el número de estaciones que tiene el jugador
    private int getNumberTransport(){
        int cont = 0;
        for(Property property : this.getOwner().getProperties()){
            if(property instanceof Transport){
                cont++; 
            }
        }
        return cont;
    }
    
    //Mostrar un resumen del pago
    private void showPaymentSummary(int amount, Player player){
        textTerminal = TextTerminal.getInstance();
        if(this.getMortgaged()){ //Modificación 1
            textTerminal.showln("El jugador &" + player.getColor() + "& usara la propiedad &" + this.getName() + 
                    "& con &" + this.getNumberTransport() + "& estaciones, que esta hipotecada. Por ello, pagara &" + amount +
                    "& al jugador &" + this.getOwner().getColor() + "& (1/3 de su valor)");
        } else{
            textTerminal.showln("El jugador &" + player.getColor() + "& usara la propiedad &" + this.getName() + "& con &" + this.getNumberTransport() + 
                    "& estaciones. Por ello, pagara &" + amount + "& al jugador &" + this.getOwner().getColor());  
        }
    }
    
    //Mostrar resumen de la compra
    private void showPurchaseSummary(int amount, Player player){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("Se va a realizar la compra de la propiedad &" + this.getName() + "& por parte del jugador &" + 
            player.getColor() + "& por un importe de &" + amount + "& euros");
    }
    
    //Copiamos la información a un array de enteros
    private int[] copyInfo(String[] info){
        int[] auxArray = new int[4]; //Array auxiliar que rellenaremos con la información que necesitamos
        int i = -1;
        for(int j = 3; j <= 6; j++){ //Recorremos el array de información en las posiciones específica dónde se encuentra la información
            auxArray[++i] = Integer.parseInt(info[j]); //Asignamos cada valor a cada posición del array
        }
        return auxArray;
    }
    
    //Realizar una operación
    @Override
    public void doOperation(Player player){
        if(this.getOwner() == null){
            this.showPurchaseSummary(this.getPrice(), player);
            if(player.pay(this.getPrice(), false)){
                player.setProperty(this);
                this.setOwner(player);
            }
        } else if(this.getOwner().equals(player)){
            this.doOwnerOperations();
        } else if(!this.getMortgaged()){
            int payment = this.getPaymentForRent();
            this.showPaymentSummary(payment, player);
            if(!player.pay(payment, true)){
                player.setBankrupt(this.getOwner());
            } else{
                this.getOwner().setBalance(payment);
            } 
        }
    }
    
    //Realizar una operación de un propietario
     @Override
    public void doOwnerOperations(){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("1.Hipotecar");
        textTerminal.showln("2.Deshipotecar");
         switch(textTerminal.read()){
            case 1->{
                if(this.getMortgaged()){
                    textTerminal.error("Esta propiedad esta hipotecada");
                } else{
                    this.getOwner().mortgage(this);
                }
            }
            case 2->{
                if(this.getMortgaged()){
                    this.getOwner().demortgage(this);
                } else{
                    textTerminal.error("Esta propiedad no esta hipotecada");
                }
            }
            default -> textTerminal.error("Opcion incorrecta");
        }
    }
}
