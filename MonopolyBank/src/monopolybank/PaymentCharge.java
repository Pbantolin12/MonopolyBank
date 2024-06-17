
package monopolybank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentCharge extends MonopolyCode{
    
    //Enumeración del tipo de carta
    private enum Type{
        Jail, Go, Back, Free_jail, Any, Collect, Pay;
    }
    
    //Atributos
    private int amount; //Cantidad a pagar o recibir
    private transient TextTerminal textTerminal; // Terminal para la interacción con el usuario
    private Type cardType; //Tipo de carta (Moverse por el tablero, ir a la cárcel...)
    private String message; //Mensaje de la carta 
    
    //Constructor
    public PaymentCharge(int id, String desc, String configInfo){
        super(id, desc, configInfo); //Llama al constructor de la clase base MonopolyCode
        textTerminal = TextTerminal.getInstance(); //Obtiene la instancia del terminal
        String[] splitInfo = configInfo.split(";"); //Separa la línea según los ";"
        this.amount = 0;
        searchText(splitInfo[2]); //Buscamos el tipo de carta
        this.message = splitInfo[2];
        List<Integer> amountList = searchAmount(splitInfo[2]); //Busca y obtiene la cantidad a pagar o recibir
        if (!amountList.isEmpty()){
            this.amount = amountList.get(0); //Asigna el primer valor encontrado
            if(this.amount < 0){
                if(this.cardType.equals(Type.Any)){
                    this.cardType = Type.Pay;
                }
            } else{
                if(this.cardType.equals(Type.Any)){
                    this.cardType = Type.Collect;
                }
            }
        }
    }
    
    //Métodos
    
    //Realizar la operación
    public void doOperation(Player player, FreeParking parking){
        textTerminal = TextTerminal.getInstance(); //Obtiene la instancia del terminal
        this.showSummary(player, amount, parking); //Muestra el resumen de la operación
    }
    
    //Mostrar resumen
    public void showSummary(Player player, int amount, FreeParking parking){
        switch(this.cardType){
            case Jail ->{
                textTerminal.showln("Jugador &" + player.getColor() +
                        "& ve a la carcel, si pasas por la casilla de salida no cobraras los 200 euros");
                textTerminal.nextLine();
                textTerminal.showln("1.Usar carta de libertad");
                textTerminal.showln("2.No hacer nada");
                if(textTerminal.read() == 1){
                    player.outOfJail();
                }
            }
            case Go ->{
                textTerminal.showln(message);
                textTerminal.showln("Si el jugador ha pasado por la casilla de salida cobra 200 euros");
                textTerminal.showln("1.Ha pasado por la casilla de salida");
                textTerminal.showln("2.No ha pasado por la casilla de salida");
                textTerminal.show(">>Introduzca una opcion: ");
               if(textTerminal.read() == 1){
                    player.setBalance(200);
                }
            }
            case Back ->{
                textTerminal.showln(player.getColor() + " " + message);
            }
            case Free_jail ->{
                textTerminal.showln(message);
                textTerminal.showln("1.Guardar");
                textTerminal.showln("2.Vender");
                textTerminal.show(">>Introduzca una opcion: ");
                switch(textTerminal.read()){
                    case 1 ->{
                        textTerminal.info("Carta guardada");
                        player.setJailCard(true);
                    }
                    case 2 ->{
                        textTerminal.showln("Carta vendida por 200 euros");
                    }
                }
            }
            case Any -> textTerminal.showln(message);
            case Pay ->{
                //El jugador aporta al parking gratuito
                textTerminal.showln("El jugador &" + player.getColor() + "& aportara al parking gratuito &" + Math.abs(amount) + "& euros");
                parking.setDeposit(Math.abs(amount)); //Incrementa el depósito del parking gratuito
                player.pay(amount, true); //Disminuye el saldo del jugador
            }
            case Collect ->{
                //El jugador recibe dinero de la banca
                textTerminal.showln("El jugador &" + player.getColor() + "& cobrara de la banca &" + amount + "& euros");
                player.setBalance(amount); //Incrementa el saldo del jugador
            }
        }
    }
    
    //Buscar el valor del pago
    private List<Integer> searchAmount(String text){
        //Local var
        List<Integer> amountList = new ArrayList<>();
        
        //Code
        Pattern numberPattern = Pattern.compile("-?(\\d+)"); //Patrón para buscar números (posibles negativos)
        Matcher amount = numberPattern.matcher(text); // Busca el patrón en el texto recibido
        while (amount.find()){
            amountList.add(Integer.parseInt(amount.group())); //Añade el número encontrado a la lista
            Collections.sort(amountList); //Ordena la lista de menor a mayor por si el precio del hotel viene antes que el de la casa
        }
        return amountList; //Devuelve la lista de cantidades encontradas
    }
    
    //Buscar y clasificar según el tipo de carta de pago que sea
    private void searchText(String text){
        //Buscamos VE a alguna casilla
        Pattern textPattern = Pattern.compile("VE A");
        Matcher textFound = textPattern.matcher(text);
        if(textFound.find()){
            this.cardType = Type.Go;
        } 
        //Buscamos CARCEL
        textPattern = Pattern.compile("VE A LA CARCEL");
        textFound = textPattern.matcher(text);
        if(textFound.find()){
            this.cardType = Type.Jail;
        }
        //Buscamos colocate en alguna casilla
        textPattern = Pattern.compile("COLOCATE");
        textFound = textPattern.matcher(text);
        if(textFound.find()){
            this.cardType = Type.Go;
        } 
        //Buscamos retrocede a alguna casilla
        textPattern = Pattern.compile("RETROCEDE");
        textFound = textPattern.matcher(text);
        if(textFound.find()){
            this.cardType = Type.Back;
        }
        //Buscamos quedas libre de la cárcel
        textPattern = Pattern.compile("QUEDAS LIBRE DE LA CARCEL");
        textFound = textPattern.matcher(text);
        if(textFound.find()){
            this.cardType = Type.Free_jail;
        } else if(this.cardType == null){
            this.cardType = Type.Any;
        }
    }
}