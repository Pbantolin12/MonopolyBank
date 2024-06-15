
package monopolybank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentCharge extends MonopolyCode {
    
    //Atributos
    private int amount;
    private transient TextTerminal textTerminal;
    private int deposit; //Modificación 2 (parking gratuito)
    
    //Constructor
    public PaymentCharge(int id, String desc, String configInfo) {
        super(id, desc, configInfo);
        textTerminal = TextTerminal.getInstance();
        String[] splitInfo = configInfo.split(";"); //Separa la línea según los ";"
        this.amount = 0;
        List<Integer> amountList = searchAmount(splitInfo[2]);
        if(!amountList.isEmpty()){
            this.amount = amountList.get(0);
        }
        this.deposit = 0;
    }
    
    //Métodos
    
    //Realizar la operación
    public void doOperation(Player player, FreeParking parking){
        textTerminal = TextTerminal.getInstance();
        this.showSummary(player, amount, parking);
    }
    
    //Mostrar resumen
    public void showSummary(Player player, int amount, FreeParking parking){
         if(amount > 0){
            textTerminal.showln("El jugador " + player.getColor() + " cobrara de la banca " + amount + " euros");
            player.setBalance(amount);
        } else{
            textTerminal.showln("El jugador " + player.getColor() + " aportara al parking gratuito " + Math.abs(amount) + " euros");
            parking.setDeposit(Math.abs(amount));
            player.setBalance(amount);
        }
    }
    
    //Buscamos el valor del pago
    private List<Integer> searchAmount(String text){
        //Local var
        List<Integer> amountList = new ArrayList<>();
        //Code
        Pattern numberPattern = Pattern.compile("-?(\\d+)"); // [-? --> Signo negativo opcional \\d+€ --> Un dígito o más] Buscamos este patrón
        Matcher amount = numberPattern.matcher(text); //Buscamos el patrón en el String recibido
        while(amount.find()){
           amountList.add(Integer.parseInt(amount.group()));
           Collections.sort(amountList); //Ordenamos de menor a mayor por si el precio del hotel viene antes que el de la casa
        }
        return amountList;
    }
}
