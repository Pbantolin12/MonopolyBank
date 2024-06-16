
package monopolybank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepairsCard extends MonopolyCode{
    
    //Atributos
    private int amountForHouse; //Precio de cada casa
    private int amountForHotel; //Precio de un hotel
    private transient TextTerminal textTerminal; //Terminal para la interacción con el usuario
    
    //Constructor    
    public RepairsCard(int id, String desc, String configTextLine){
        super(id, desc, configTextLine);
        textTerminal = TextTerminal.getInstance();
        String[] splitInfo = configTextLine.split(";"); //Separa la línea según los ";"
        List<Integer> amounts = searchAmount(splitInfo[2]);
        this.amountForHouse = amounts.get(0);
        this.amountForHotel = amounts.get(1);
    }
    
    //Realizar la operación de la carta
    public void doOperation(Player player){
        this.showSummary(player, calculateAmountHouses(player) * this.amountForHouse + 
                calculateAmountHotel(player) * this.amountForHotel);
    }
    
    //Calcular cuántas casas tiene el jugador
    private int calculateAmountHouses(Player player){
        //Local var
        int houses = 0;
        
        //Code
        for(Property property : player.getProperties()){
            if(property instanceof Street street){
                if(street.getBuiltHouses() == 5){
                   houses += 4;
                } else{
                    houses += street.getBuiltHouses();
                }
            }
        }
        return houses;
    }
    
    //Calcular cuántos hoteles tiene el jugador
    private int calculateAmountHotel(Player player){
        //Local var
        int hotel = 0;
        
        //Code
        for(Property property : player.getProperties()){
            if(property instanceof Street street && street.getBuiltHouses() == 5){
                hotel++;
            }
        }
        return hotel;
    }
    
    //Mostrar resumen del pago de reparaciones
    public void showSummary(Player player, int amount){
        textTerminal = TextTerminal.getInstance();
        textTerminal.info("El jugador &" + player.getColor() + "& pagara &" + this.amountForHouse +
                        "& por casa y &" + this.amountForHotel + "& por hotel");
                textTerminal.showln("El jugador &" + player.getColor() + "& pagara &" + calculateAmountHouses(player)*this.amountForHouse +
                        "& euros por &" + calculateAmountHouses(player) + "& casas y &" + calculateAmountHotel(player)*this.amountForHotel + 
                        "& euros por &" + calculateAmountHotel(player) + "& hoteles");
                int toPay = calculateAmountHouses(player)*this.amountForHouse + calculateAmountHotel(player)*this.amountForHotel;
                player.pay(amount, true);
    }
    
    //Buscar valores numéricos en la descripción de la carta
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
        Collections.sort(amountList); //Ordena de menor a mayor
        return amountList;
    }
}