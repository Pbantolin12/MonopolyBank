
package monopolybank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentCharge extends MonopolyCode{
    
    //Atributos
    private int amount; //Cantidad a pagar o recibir
    private transient TextTerminal textTerminal; // Terminal para la interacción con el usuario
    
    //Constructor
    public PaymentCharge(int id, String desc, String configInfo){
        super(id, desc, configInfo); //Llama al constructor de la clase base MonopolyCode
        textTerminal = TextTerminal.getInstance(); //Obtiene la instancia del terminal
        String[] splitInfo = configInfo.split(";"); //Separa la línea según los ";"
        this.amount = 0;
        List<Integer> amountList = searchAmount(splitInfo[2]); //Busca y obtiene la cantidad a pagar o recibir
        if (!amountList.isEmpty()){
            this.amount = amountList.get(0); //Si la lista no está vacía, asigna el primer monto encontrado
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
        if (amount > 0){
            //Si la cantidad es positiva, el jugador recibe dinero de la banca
            textTerminal.showln("El jugador " + player.getColor() + " cobrara de la banca " + amount + " euros");
            player.setBalance(amount); //Incrementa el saldo del jugador
        } else{
            //Si la cantidad es negativa, el jugador aporta al parking gratuito
            textTerminal.showln("El jugador " + player.getColor() + " aportara al parking gratuito " + Math.abs(amount) + " euros");
            parking.setDeposit(Math.abs(amount)); //Incrementa el depósito del parking gratuito
            player.setBalance(amount); //Disminuye el saldo del jugador
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
        return amountList; //Devuelve la lista de montos encontrados
    }
}