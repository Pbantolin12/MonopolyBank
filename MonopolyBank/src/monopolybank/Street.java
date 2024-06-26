
package monopolybank;

import java.util.Map;

public class Street extends Property {
    
    //Atributos 
    private int builtHouses; //Casas construidas
    private int housePrice; //Precio de una casa
    private int[] costStayingWithHouses; //Precio del alquiler dependiendo de las casas que tenga 
    private transient TextTerminal textTerminal; //Terminal para interacción con el usuario
    
    //Constructor
    public Street(int id, String desc, String configInfo , int price, boolean mortaged, int mValue) {
        super(id, desc, configInfo, price, mortaged, mValue);
        textTerminal = TextTerminal.getInstance();
        String[] splitInfo = configInfo.split(";"); //Separa la línea según los ";"
        this.builtHouses = 0; //Inicialmente no hay casas construidas
        this.housePrice = Integer.parseInt(splitInfo[9]);
        this.costStayingWithHouses = copyInfo(splitInfo);
    }
   
    
    //Métodos
    
    //Obtener el valor del alquiler a pagar
    @Override
    public int getPaymentForRent(){
        textTerminal = TextTerminal.getInstance();
        for(Property property:this.getOwner().getProperties()){
            if(this.equals(property) && this.getBuiltHouses() >= 0 && !property.getMortgaged()){
                return this.costStayingWithHouses[this.getBuiltHouses()];
            } else if(property.getMortgaged()){  //Modificación 1 (si la propiedad está hipotecada se cobra 1/3 del alquiler)
                return (int) Math.round(this.costStayingWithHouses[this.getBuiltHouses()] * 0.3);
            } 
            else{
                textTerminal.error("No se ha encontrado la propiedad");
            }
        }
        return 0;
    }
    
    //Mostrar un resumen del pago
    private void showPaymentSummary(int amount, Player player){
        textTerminal = TextTerminal.getInstance();
        if(this.getMortgaged()){ //Modificación 1
            textTerminal.showln("El jugador &" + player.getColor() + "& usara la propiedad &" + this.getName() + 
                    "& que esta hipotecada. Por ello, pagara &" + amount + "& al jugador &" + this.getOwner().getColor() + "& (1/3 de su valor)");
        } else{
        textTerminal.showln("El jugador &" + player.getColor() + "& usara la propiedad &" + this.getName() + "& con &" + this.getBuiltHouses() + 
                    "& casas. Por ello, pagara &" + amount + "& al jugador &" + this.getOwner().getColor());
        }
    }
    
    //Mostrar resumen de la compra de una propiedad
    private void showPurchaseSummary(int amount, Player player){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("Se va a realizar la compra de la propiedad &" + this.getName() + "& por parte del jugador &" + 
                player.getColor() + "& por un importe de &" + amount + "& euros");
    }
    
    //Mostrar resumen de la compra o venta de casas
    private void showHousePurchaseSummary(int amount, Player player, int nHouses){
        //Local var
        String numberHouses;
        
        //Code
        textTerminal = TextTerminal.getInstance();
        numberHouses = switch (nHouses) {
            case 2, -2 -> "dos";
            case 3, -3 -> "tres";
            case 4, -4 -> "cuatro";
            case 5, -5 -> "cinco";
            default -> "una";
        };
        if(nHouses >= 0){
            textTerminal.showln("Se va a realizar la compra de &" + numberHouses + "& casa para la propiedad &" + 
                    this.getName() + "& por parte del jugador &" +  player.getColor() + "& por un importe de &" + amount + "& euros");
        } else{
            textTerminal.showln("Se va a realizar la venta de &" + numberHouses + "& casa para la propiedad &" + 
                    this.getName() + "& por parte del jugador &" + player.getColor() + "& por un importe de &" + amount + "& euros");
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
    public void setBuiltHouses(int number){
        this.builtHouses += number;
    }
    
    //Comprar casas
    public void buyHouses(Player player, int number){
        textTerminal = TextTerminal.getInstance();
        if(this.getOwner().equals(player)){
            if(!this.getMortgaged()){
                if(this.getBuiltHouses() + number <= 5){
                    showHousePurchaseSummary(this.getHousePrice() * number, player, number);
                    player.pay(this.getHousePrice() * number, false);
                    setBuiltHouses(number);  
                } else{
                    textTerminal.error("Demasiadas casas para edificar");
                }       
            } else{
                textTerminal.error("La propiedad esta hipotecada");
            }
        } else{
            textTerminal.error("La propiedad no pertenece al jugador");
        }
    }
    
    //Vender casas
    public void sellHouses(Player player, int number){
        textTerminal = TextTerminal.getInstance();
        if(this.getOwner().equals(player)){
            if(!this.getMortgaged()){
                if(this.getBuiltHouses() - number >= 0){
                    showHousePurchaseSummary(this.getHousePrice() * number, player, -number);
                    player.sell(this, number);
                    setBuiltHouses(-number);
                } else{
                    textTerminal.error("No hay suficientes casas para vender");
                }       
            } else{
                textTerminal.error("La propiedad esta hipotecada");
            }
        } else{
            textTerminal.error("La propiedad no pertenece al jugador");
        }
    }
    
    
    //Copiamos la información a un array de enteros
    private int[] copyInfo(String[] info){
        //Local var
        int[] auxArray = new int[6]; 
        int i = -1;
        
        //Code
        for(int j = 3; j <= 8; j++){ //Recorremos el array de información en las posiciones específica dónde se encuentra la información
            auxArray[++i] = Integer.parseInt(info[j]); //Asignamos cada valor a cada posición del array
        }
        return auxArray;
    }
    
    //Realizar una operación
    @Override
    public void doOperation(Player player, Map<Integer, Player> playerList){
        if(this.getOwner() == null){
            this.showPurchaseSummary(this.getPrice(), player);
            player.pay(this.getPrice(), false);
            player.setProperty(this);
            this.setOwner(player);
        } else if(this.getOwner().equals(player)){
            this.doOwnerOperations(playerList);
        } else if(!this.getMortgaged()){
            this.showPaymentSummary(this.getPaymentForRent(), player);
                if(!player.pay(this.getPaymentForRent(), true)){
                    player.setBankrupt(this.getOwner());
                } else{
                    this.getOwner().setBalance(this.getPaymentForRent());
                }
        } else{
            textTerminal.showln("El jugador &" + player.getColor() + "& usara la propiedad &" + this.getName() + 
                    "& que esta hipotecada, por lo que no pagara nada");  
        }
    }
    
    //Realizar una operación de un propietario
    @Override
    public void doOwnerOperations(Map<Integer, Player> playerList){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("1.Hipoteca");
        textTerminal.showln("2.Comprar casas");
        textTerminal.showln("3.Vender casas");
        textTerminal.showln("4.Traspasar propiedad");
        textTerminal.show(">>Introduzca una opcion: ");
        switch(textTerminal.read()){
            case 1 -> {
                textTerminal.showln("1.Hipotecar");
                textTerminal.showln("2.Deshipotecar");
                textTerminal.show(">>Introduzca una opcion: ");
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
            case 2 -> {
                textTerminal.show(">>Introduzca el numero de casas que deseas comprar: ");
                this.buyHouses(this.getOwner(), textTerminal.read());
            }
            case 3 -> {
                textTerminal.show(">>Introduzca el numero de casas que deseas vender: ");
                int nHouses = textTerminal.read();
                this.sellHouses(this.getOwner(), nHouses);
            }
            case 4 ->{
                 textTerminal.show(">>Introduzca el precio de venta: ");
                int sellPrice = textTerminal.read();
                textTerminal.show(">>Introduzca el codigo de jugador: ");
                int idPlayer = textTerminal.read();
                Player player = playerList.get(idPlayer);
                this.getOwner().traspaseProperties(player, this, sellPrice);  
            }
            default -> textTerminal.error("Opcion incorrecta");
        }
    }
}
