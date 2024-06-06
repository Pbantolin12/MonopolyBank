
package monopolybank;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Game implements Serializable{
    
    //Atributos
    private transient TextTerminal textTerminal;
    private String configMonopolyDir = "..\\config\\MonopolyCode.txt";
    private String fileDir = "..\\config\\oldGames\\";
    private Map<Integer, Player> playerList;
    private HashMap<Integer, MonopolyCode> idTypeMap;
    private String fileName;
    
    //Constructor
    public Game(TextTerminal textTerminal, String fileName) throws FileNotFoundException, IOException{
        this.textTerminal = textTerminal;
        this.idTypeMap = new HashMap<>();
        this.playerList = new HashMap<>();
        this.fileName = fileName;
        this.fileDir = this.fileDir.concat(this.fileName);
        createPlayers();
        readFile();
        save();
    }
    
    //Métodos
    
    public void play() throws IOException{
        //Local var
        int option = 0;
        
        //Code
        while(playerList.size() > 1 && option != 3){
            if(this.textTerminal == null){
                this.textTerminal = new TextTerminal();
            }
            textTerminal.showln("1.Introducir codigo de la tarjeta");
            textTerminal.showln("2.Mostrar resumen");
            textTerminal.showln("3.Salir");
            option = textTerminal.read();
            switch(option){
                case 1 -> {
                    textTerminal.show(">>Introduzca el codigo de la tarjeta: ");
                    int cardCode = textTerminal.read(); //id de la carta
                    MonopolyCode card = this.idTypeMap.get(cardCode); //Obtenemos la carta según el id
                    textTerminal.show(">>Introduzca el codigo de jugador: ");
                    int playerCode = textTerminal.read(); //id del jugador
                    Player player = this.playerList.get(playerCode); //Obtenemos el jugador mediante su id
                    if(player.getBankrupt()){
                        this.removePlayer(player);
                    } else{
                        card.doOperation(player);
                        save();
                    }
                }
                case 2 -> this.showGameState();
                case 3 -> {
                    save();
                    textTerminal.info("Saliendo");
                }
                default -> textTerminal.error("Opcion incorrecta");
            }
        } 
    }
    
    //Crear los jugadores
    private void createPlayers(){
        //Local var
        int nPlayers;
        Scanner scan = new Scanner(System.in);
        String selectedColors;
        boolean repeated = true;
        
        //Code
        do{
            textTerminal.show(">>Introduzca el numero de jugadores: ");
            nPlayers = textTerminal.read();
            if(nPlayers < 2 || nPlayers > 4){
                textTerminal.error("El numero de jugadores introducidos no es correcto");
            } else{
                do{
                    textTerminal.nextLine();
                    textTerminal.showln("Colores disponibles: ");
                    for(Color colorAux:Color.values()){
                        textTerminal.showln(colorAux.toString());
                    }
                    textTerminal.show(">>Introduzca la primera letra de cada color: ");
                    selectedColors = scan.nextLine();
                    selectedColors = clearString(selectedColors);
                    if(selectedColors.length() != nPlayers){
                        textTerminal.error("El numero de colores elegido no es correcto");
                    } else{
                        repeated = charRepeated(selectedColors); //Comprobamos si hay colores repetidos
                        if (repeated){
                            textTerminal.error("Hay colores repetidos");
                        } else{
                            for(char colorLetter:selectedColors.toCharArray()){  //Recorremos el String de colores seleccionados letra por letra
                                switch(colorLetter){
                                    case 'r', 'R' -> {
                                        textTerminal.info("Creando nuevo jugador [rojo] --> Id: 1");
                                        textTerminal.show(">>Introduzca el nombre del jugador: ");
                                        String nameP = scan.nextLine();
                                        playerList.put(1, new Player(1, textTerminal, nameP, Color.rojo));
                                         
                                    }
                                    case 'v', 'V' -> {
                                        textTerminal.info("Creando nuevo jugador [verde] --> Id: 2");
                                        textTerminal.show(">>Introduzca el nombre del jugador: ");
                                        String nameP = scan.nextLine();
                                        playerList.put(2, new Player(2, textTerminal, nameP, Color.verde));
                                    }
                                    case 'a', 'A' -> {
                                        textTerminal.info("Creando nuevo jugador [azul] --> Id: 3");
                                        textTerminal.show(">>Introduzca el nombre del jugador: ");
                                        String nameP = scan.nextLine();
                                        playerList.put(3, new Player(3, textTerminal, nameP, Color.azul));
                                    }
                                    case 'n', 'N' -> {
                                       
                                        textTerminal.info("Creando nuevo jugador [negro] --> Id: 4");
                                        textTerminal.show(">>Introduzca el nombre del jugador: ");
                                        String nameP = scan.nextLine();
                                        playerList.put(4,new Player(4, textTerminal, nameP, Color.negro));
                                    }
                                }
                            }
                        }
                    }
                } while(selectedColors.length() != nPlayers || repeated);
            }
        } while(nPlayers < 2 || nPlayers > 4);
    }
    
    //Tratamos el string introducido por el usuario
    private String clearString(String str){
        str = str.replace(" ", ""); //Eliminamos los posibles espacios
        str = str.replace(",", ""); //Eliminamos las posibles comas
        return str;
    }
    
    //Comprobamos que no se repiten letras
    private boolean charRepeated(String text){ 
        //Local var
        Set<Character> aux = new HashSet<>(); //Nuevo conjunto para almacenar las letras
       
        //Code
        for(char character:text.toCharArray()){ //Recorremos el string caracter por caracter
            if(!aux.contains(character)) //Si la letra no está ne el conjunto la introducimos
               aux.add(character);
            else return true; //Si la letra está en el conjunto devolvemos verdadero
        }
        return false;
    }
    
    //Cargar las cartas y propiedades 
    private void loadMonopolyCodes(String configInfo){
        //Local var
        String propertyType = getCodeClass(configInfo);
        int id = getId(configInfo);
        
        //Code
        switch(propertyType){
            case "REPAIRS_CARD" -> setMonopolyCode(id, new RepairsCard(id, propertyType, configInfo, textTerminal));
            case "PAYMENT_CHARGE_CARD" -> setMonopolyCode(id, new PaymentCharge(id, propertyType, configInfo, textTerminal));
            case "STREET" -> setMonopolyCode(id, new Street(id, propertyType, configInfo, textTerminal, getPrice(getMortageValue(configInfo)), false, getMortageValue(configInfo)));
            case "TRANSPORT" -> setMonopolyCode(id, new Transport(id, propertyType, configInfo, textTerminal, getPrice(getMortageValue(configInfo)), false, getMortageValue(configInfo)));
            case "SERVICE" -> setMonopolyCode(id, new Service(id, propertyType, configInfo, textTerminal, getPrice(getMortageValue(configInfo)), false, getMortageValue(configInfo)));
        }
    }
    
    //Dividimos el string separado por ';' y los devolvemos como un array de string
    private String[] splitConfigInfo(String cInfo){
        return cInfo.split(";"); //Separa la línea según los ";"
    }
    
    //Obtenemos el tipo de carta
    private String getCodeClass(String line){
        return splitConfigInfo(line)[1];
    }
    
    //Obtenemos el id de la carta
    private int getId(String line){
        return Integer.parseInt(splitConfigInfo(line)[0]);
    }
    
    //Obtenemos el precio de la hipoteca
    private int getMortageValue(String line){
        String[] lineArray =  splitConfigInfo(line);
        return Integer.parseInt(lineArray[lineArray.length - 1]);
    }
    
    //Obtenemos le precio
    private int getPrice(int mortageValue){
        return mortageValue * 2; //El precio de una carta es el doble de su hipoteca
    }
    
    //Añadimos el id y la carta al mapa de las cartas del juego
    public void setMonopolyCode(int id, MonopolyCode mCode){
        idTypeMap.put(id, mCode);
    }
    
    //Eliminar jugador
    private void removePlayer(Player player){
        if(player.getBankrupt()){
            this.playerList.remove(player.getId());
            textTerminal.info("El jugador[" + player.getColor() + "] " + player.getName() + " ha sido eliminado de la partida");
            if(this.playerList.size() == 1){
                textTerminal.showln("||--El jugador [" + this.playerList.get(0).getColor() + "] " + this.playerList.get(0).getName() + " ha ganado la partida--||");
            }
        }
    }
    
    //Lee el archivo y proporciona líneas a loadMonopolyCodes()
    private void readFile() throws FileNotFoundException{
        //Local var
        File fileToRead = new File(configMonopolyDir); //Archivo del que leemos la información
        Scanner scan = new Scanner(fileToRead);
        String line;
        
        //Code
        while(scan.hasNextLine()){
            line = scan.nextLine();
            loadMonopolyCodes(line);
        }
    }
    
    //Guardamos la partida
    private void save() throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileDir))) {
            out.writeObject(this);
        } catch(IOException saveError){
            throw new IOException("Error al guardar el juego", saveError);
        }
    }
    
    private void showGameState(){
        textTerminal.showln(" _________________________");
        textTerminal.showln("|__RESUMEN DE LA PARTIDA__|");
        Collection<Player> playersList = playerList.values();
        for(Player player : playersList){
            textTerminal.showln("---------------------------");
            textTerminal.showln("-> Jugador: " + player.getColor());
            textTerminal.showln("-> Dinero: " + player.getBalance());
            textTerminal.showln("PROPIEDADES SIN HIPOTECAR");
            for(Property property : player.getProperties()){
                textTerminal.showln("-> Propiedad: [" + property.getId() + "] " + property.getName());
                if(property instanceof Street){
                    textTerminal.showln("--> Casas: " + ((Street) property).getBuiltHouses());
                }
                textTerminal.showln("--> Valor: " + property.getPrice());
                textTerminal.showln("--> Valor hipoteca: " + property.getMortgageValue());
            }
            textTerminal.showln("PROPIEDADES HIPOTECADAS");
            for(Property property : player.getPropertiesMortaged()){
                textTerminal.showln("Propiedad: [" + property.getId() + "] " + property.getName());
                if(property instanceof Street){
                    textTerminal.showln("--> Casas: " + ((Street) property).getBuiltHouses());
                }
                textTerminal.showln("--> Valor: " + property.getPrice());
                textTerminal.showln("--> Valor hipoteca: " + property.getMortgageValue());
            }
        }
    }
}
