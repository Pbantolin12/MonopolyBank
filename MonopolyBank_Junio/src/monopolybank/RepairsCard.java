
package monopolybank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepairsCard extends MonopolyCode {
    
    //Atributos
    private int amountForHouse;
    private int amountForHotel;
    private transient TextTerminal textTerminal = TextTerminal.getInstance();;
    
    //Constructor    
    public RepairsCard(int id, String desc, String configTextLine) {
        super(id, desc, configTextLine);
        String[] splitInfo = configTextLine.split(";"); //Separa la línea según los ";"
        this.amountForHouse = searchAmount(splitInfo[2]).get(0);
        this.amountForHotel = searchAmount(splitInfo[2]).get(1);
    }
    
    //Métodos
    
    //Realizar una la operación
    public void doOperation(Player player){
        this.showSummary(player, calculateAmountHouses(player) * this.amountForHouse + calculateAmountHotel(player) * this.amountForHotel);
    }
    
    //Obtenemos cuantas casas tiene el jugador
    private int calculateAmountHouses(Player player){
        //Local var
        int houses = 0;
        
        //Code
        for(Property property : player.getProperties()){
            if(property instanceof Street){
                if(((Street) property).getBuiltHouses() == 5){
                   houses += 4;
                } else{
                    houses += ((Street) property).getBuiltHouses();
                }
            }
        }
        return houses;
    }
    
    //Obtenemos cuantos hoteles tiene el jugador
    private int calculateAmountHotel(Player player){
        //Local var
        int hotels = 0;
        
        //Code
        for(Property property : player.getProperties()){
            if(property instanceof Street street){
                if(street.getBuiltHouses() == 5){
                   hotels++;
                }
            }
        }
        return hotels;
    }
    
    //Mostrar resumen
    public void showSummary(Player player, int amount){
        textTerminal = TextTerminal.getInstance();
        textTerminal.showln("El jugador " + player.getColor() + " pagara a la banca " + amount + " euros");
        player.setBalance(-amount);
    }
    
    //Buscamos los valores numéricos correspondientes a los precios en la descripción de la carta 
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
