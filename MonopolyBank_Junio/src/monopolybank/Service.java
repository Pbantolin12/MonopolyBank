
package monopolybank;

public class Service extends Property {
    
    //Atributos
    private int[] costStaying; 
    private transient TextTerminal textTerminal = TextTerminal.getInstance();;
    
    //Constructor
     public Service(int id, String desc, String configInfo , int price, boolean mortaged, int mValue) {
        super(id, desc, configInfo, price, mortaged, mValue);
        textTerminal = TextTerminal.getInstance();
        String[] splitInfo = configInfo.split(";"); //Separa la línea según los ";"
        this.costStaying = copyInfo(splitInfo);
    }
     
    //Métodos
    
    //Obtener el valor del alquiler a pagar
    @Override
    public int getPaymentForRent(){
        textTerminal = TextTerminal.getInstance();
        textTerminal.show(">>Introduce el numero marcado en los dados: ");
        int num = textTerminal.read();
        int nService = this.getNumberService();
        return switch (nService) {
            case 1 -> 4*num;
            case 2 -> 10*num;
            default -> 0;
        };
    }
    
    //Obtenemos el número de cartas que posee el jugador
    private int getNumberService(){
        //Local var
        int cont = 0;
        
        //Code
        for(Property property: this.getOwner().getProperties()){
            if(this.getClass().equals(property.getClass())){
                cont++;
            }
        }
        return cont;
    }
    
    //Mostrar un resumen del pago
    private void showPaymentSummary(int amount, Player player){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("El jugador " + player.getColor() + " usara la propiedad " + this.getName() + " con " + this.getNumberService() + 
            " servicios. Por ello, pagara " + amount + " al jugador " + this.getOwner().getColor());  
    }
    
    //Mostrar resumen de la compra
    private void showPurchaseSummary(int amount, Player player){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("Se va a realizar la compra de la propiedad " + this.getName() + " por parte del jugador " + 
            player.getColor() + " por un importe de " + amount + " euros");
    }
    
    //Copiamos la información a un array de enteros
    private int[] copyInfo(String[] info){
        //Local var
        int[] auxArray = new int[2]; //Array auxiliar que rellenaremos con la información que necesitamos
        int i = -1;
        
        //Code
        for(int j = 3; j <= 4; j++){ //Recorremos el array de información en las posiciones específica dónde se encuentra la información
            auxArray[++i] = Integer.parseInt(info[j]); //Asignamos cada valor a cada posición del array
        }
        return auxArray;
    }
    
    //Realizar una operación
    @Override
     public void doOperation(Player player){
        if(this.getOwner() == null){
            this.showPurchaseSummary(this.getPrice(), player);
            player.pay(this.getPrice(), false);
            player.setProperty(this);
            this.setOwner(player);
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
        if(textTerminal.read() == 1 && !this.getMortgaged()){
            this.setMortgaged(true);
        } else if(textTerminal.read() == 1 && this.getMortgaged()){
            textTerminal.error("Esta propiedad esta hipotecada");
        } else if(textTerminal.read() == 2 && this.getMortgaged()){
            this.setMortgaged(false);
        } else if(textTerminal.read() == 2 && !this.getMortgaged()){
            textTerminal.error("Esta propiedad no esta hipotecada");
        } else{
            textTerminal.error("Opcion incorrecta");
        }
    }
}
