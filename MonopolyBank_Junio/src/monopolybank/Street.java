
package monopolybank;

public class Street extends Property {
    
    //Atributos 
    private int builtHouses;
    private int housePrice;
    private int[] costStayingWithHouses;
    private transient TextTerminal textTerminal;
    
    //Constructor
    public Street(int id, String desc, String configInfo, TextTerminal terminal, int price, boolean mortaged, int mValue) {
        super(id, desc, configInfo, terminal, price, mortaged, mValue);
        String[] splitInfo = configInfo.split(";"); //Separa la línea según los ";"
        this.textTerminal = terminal;
        this.builtHouses = 0;
        this.housePrice = Integer.parseInt(splitInfo[9]);
        this.costStayingWithHouses = copyInfo(splitInfo);
    }
   
    
    //Métodos
    
    //Obtener el valor del alquiler a pagar
    @Override
    public int getPaymentForRent(){
        for(Property property:this.getOwner().getProperties()){
            if(this.equals(property) && this.getBuiltHouses() >= 0 && !property.getMortgaged()){
                return this.costStayingWithHouses[this.getBuiltHouses()];
            } else{
                textTerminal.error("No se ha encontrado la propiedad");
            }
        }
        return 0;
    }
    
    //Mostrar un resumen del pago
    private void showPaymentSummary(int amount, Player player){
        textTerminal.showln("El jugador " + player.getColor() + " usara la propiedad " + this.getName() + " con " + this.getBuiltHouses() + 
                " casas. Por ello, pagara " + amount + " al jugador " + this.getOwner().getColor());
    }
    
    //Mostrar resumen de la compra de una propiedad
    private void showPurchaseSummary(int amount, Player player){
        textTerminal.showln("Se va a realizar la compra de la propiedad " + this.getName() + " por parte del jugador " + 
                player.getColor() + " por un importe de " + amount + " euros");
    }
    
    //Mostrar resumen de la compra o venta de casas
    private void showHousePurchaseSummary(int amount, Player player, int nHouses){
        String numberHouses;
        numberHouses = switch (nHouses) {
            case 2, -2 -> "dos";
            case 3, -3 -> "tres";
            case 4, -4 -> "cuatro";
            case 5, -5 -> "cinco";
            default -> "una";
        };
        if(nHouses >= 0){
            textTerminal.showln("Se va a realizar la compra de " + numberHouses + " casa para la propiedad " + this.getName() + " por parte del jugador " + 
                player.getColor() + " por un importe de " + amount + " euros");
        } else{
            textTerminal.showln("Se va a realizar la venta de " + numberHouses + " casa para la propiedad " + this.getName() + " por parte del jugador " + 
                player.getColor() + " por un importe de " + Math.abs(amount) + " euros");
        }
   }
    
    //Obtener el precio de una casa
    public int getHousePrice(){
        return this.housePrice;
    }
    
    //Obtener las casas construidas
    public int getBuiltHouses(){
        return this.builtHouses;
    }
    
    //Establecemos las casas de la propiedad
    public void setBuiltHouses(Player player, int number){
        if(!this.getMortgaged()){
            if(number >= 0){
                if(this.getBuiltHouses() + number <= 5){
                    showHousePurchaseSummary(this.getHousePrice() * number, player, number);
                    player.pay(this.getBuiltHouses(), false);
                    this.builtHouses += number;  
                } else{
                    textTerminal.error("Demasiadas casas para edificar");
                }
            } else if(number < 0){
                if(this.getBuiltHouses() + number >= 0){
                    showHousePurchaseSummary(this.getHousePrice() * Math.abs(number), player, number);
                    player.setBalance(this.getHousePrice());
                    this.builtHouses += number; 
                } else{
                    textTerminal.error("No hay tantas casas edificadas");
                }
            }
        }
        else{
            textTerminal.error("Propiedad hipoteceada no se permite esta accion");
        }
    }
    
    //Copiamos la información a un array de enteros
    private int[] copyInfo(String[] info){
        //Local var
        int[] auxArray = new int[6]; 
        int i = -1;
        
        //Code
        for(int j = 3; j < 8; j++){ //Recorremos el array de información en las posiciones específica dónde se encuentra la información
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
        } else{
            textTerminal.showln("El jugador " + player.getColor() + " usara la propiedad " + this.getName() + " que esta hipotecada, por lo que no pagara nada");  
        }
    }
    
    //Realizar una operación de un propietario
    @Override
    public void doOwnerOperations(){
        textTerminal.showln("1.Hipoteca");
        textTerminal.showln("2.Comprar casas");
        textTerminal.showln("3.Vender casas");
        textTerminal.show(">>Introduzca una opcion: ");
        switch(textTerminal.read()){
            case 1 -> {
                textTerminal.showln("1.Hipotecar");
                textTerminal.showln("2.Deshipotecar");
                textTerminal.show(">>Introduzca una opcion: ");
                int opt = textTerminal.read();
                if(opt == 1 && !this.getMortgaged()){
                    this.setMortgaged(true);
                } else if(opt == 1 && this.getMortgaged()){
                    textTerminal.error("Esta propiedad esta hipotecada");
                } else if(opt == 2 && this.getMortgaged()){
                    this.setMortgaged(false);
                } else if(opt == 2 && !this.getMortgaged()){
                    textTerminal.error("Esta propiedad no esta hipotecada");
                } else{
                    textTerminal.error("Opcion incorrecta");
                }
            }
            case 2 -> {
                textTerminal.show(">>Introduzca el numero de casas que deseas comprar: ");
                this.setBuiltHouses(this.getOwner(), textTerminal.read());
            }
            case 3 -> {
               
                textTerminal.show(">>Introduzca el numero de casas que deseas vender: ");
                int nHouses = textTerminal.read() * -1;
                this.setBuiltHouses(this.getOwner(), nHouses);
            }
            default -> textTerminal.error("Opcion incorrecta");
        }
    }
}
