
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
    private transient TextTerminal textTerminal; //Terminal para la interacción con el usuario
    private String configMonopolyDir = "..\\config\\MonopolyCode.txt"; //Ruta del archivo de configuración
    private String fileDir = "..\\config\\oldGames\\"; //Directorio donde se guardan las partidas
    private Map<Integer, Player> playerList; //Mapa que almacena los jugadores
    private HashMap<Integer, MonopolyCode> idTypeMap; //Mapa que almacena las cartas del juego
    private String fileName; //Nombre del archivo de la partida
    private FreeParking parking; //Modificación 2
    
    //Constructor
    public Game(String fileName) throws FileNotFoundException, IOException{
        textTerminal = TextTerminal.getInstance(); //Obtiene la instancia del terminal
        this.idTypeMap = new HashMap<>(); //Inicializa el mapa de cartas
        this.playerList = new HashMap<>(); //Inicializa el mapa de jugadores
        this.fileName = fileName; //Asigna el nombre del archivo de la partida
        this.fileDir = this.fileDir.concat(this.fileName); //Completa la ruta del archivo
        createPlayers(); //Crea los jugadores
        readFile(); //Lee el archivo de configuración
        save(); //Guarda la partida
    }
    
    //Métodos
    
    //Método principal del juego
    public void play() throws IOException{
        int option = 0; //Opción del menú
        
        textTerminal = TextTerminal.getInstance(); //Obtiene la instancia del terminal
        while(playerList.size() > 1 && option != 3) { //Mientras haya más de un jugador y no se seleccione la opción de salir
            textTerminal.nextLine(); 
            textTerminal.showln(" ____________________________________");
            textTerminal.showln("|________ACCIONES_DISPONIBLES________|");
            textTerminal.showln("| 1.Introducir codigo de la tarjeta  |");
            textTerminal.showln("| 2.Mostrar resumen                  |");
            textTerminal.showln("| 3.Salir                            |");
            textTerminal.showln("|____________________________________|");
            textTerminal.show(">>Introduzca una opcion: ");
            option = textTerminal.read();
            switch(option){
                case 1 -> {
                    textTerminal.show(">>Introduzca el codigo de la tarjeta: ");
                    int cardCode = textTerminal.read();
                    MonopolyCode card = this.idTypeMap.get(cardCode);
                    textTerminal.show(">>Introduzca el codigo de jugador: ");
                    int playerCode = textTerminal.read(); 
                    Player player = this.playerList.get(playerCode); 
                    if(player.getBankrupt()) { //Si el jugador está en bancarrota
                        this.removePlayer(player); //Elimina al jugador
                    } else{
                        if(card instanceof PaymentCharge paymentCard) {
                            paymentCard.doOperation(player, parking); //Realiza la operación de la tarjeta de pago
                        } else{
                            card.doOperation(player); //Realiza la operación de la carta
                            save(); //Guarda la partida
                        }
                    }
                }
                case 2 -> this.showGameState(); //Muestra el estado del juego
                case 3 ->{
                    save(); //Guarda la partida
                    textTerminal.info("Saliendo...");
                    System.exit(0); //Sale del juego
                }
                default -> textTerminal.error("Opcion incorrecta");
            }
        } 
    }
    
    //Crea los jugadores
    private void createPlayers(){
        int nPlayers;
        Scanner scan = new Scanner(System.in);
        String selectedColors;
        boolean repeated = true;
        
        do {
            textTerminal.show(">>Introduzca el numero de jugadores: ");
            nPlayers = textTerminal.read();
            if(nPlayers < 2 || nPlayers > 4) {
                textTerminal.error("El numero de jugadores introducidos no es correcto");
            } else {
                do {
                    textTerminal.nextLine();
                    textTerminal.showln("Colores disponibles: ");
                    for(Color colorAux : Color.values()) {
                        textTerminal.showln(colorAux.toString());
                    }
                    textTerminal.show(">>Introduzca la primera letra de cada color: ");
                    selectedColors = scan.nextLine();
                    selectedColors = clearString(selectedColors);
                    if(selectedColors.length() != nPlayers) {
                        textTerminal.error("El numero de colores elegido no es correcto");
                    } else {
                        repeated = charRepeated(selectedColors); 
                        if(repeated) {
                            textTerminal.error("Hay colores repetidos");
                        } else {
                            for(char colorLetter : selectedColors.toCharArray()) {  
                                switch(colorLetter) {
                                    case 'r', 'R' -> {
                                        textTerminal.info("Creando nuevo jugador [rojo] --> Id: 1");
                                        textTerminal.show(">>Introduzca el nombre del jugador: ");
                                        String nameP = scan.nextLine();
                                        playerList.put(1, new Player(1, nameP, Color.rojo));
                                    }
                                    case 'v', 'V' -> {
                                        textTerminal.info("Creando nuevo jugador [verde] --> Id: 2");
                                        textTerminal.show(">>Introduzca el nombre del jugador: ");
                                        String nameP = scan.nextLine();
                                        playerList.put(2, new Player(2, nameP, Color.verde));
                                    }
                                    case 'a', 'A' -> {
                                        textTerminal.info("Creando nuevo jugador [azul] --> Id: 3");
                                        textTerminal.show(">>Introduzca el nombre del jugador: ");
                                        String nameP = scan.nextLine();
                                        playerList.put(3, new Player(3, nameP, Color.azul));
                                    }
                                    case 'n', 'N' -> {
                                        textTerminal.info("Creando nuevo jugador [negro] --> Id: 4");
                                        textTerminal.show(">>Introduzca el nombre del jugador: ");
                                        String nameP = scan.nextLine();
                                        playerList.put(4,new Player(4, nameP, Color.negro));
                                    }
                                }
                            }
                        }
                    }
                } while(selectedColors.length() != nPlayers || repeated);
            }
        } while(nPlayers < 2 || nPlayers > 4);
    }
    
    //Elimina espacios y comas de la cadena
    private String clearString(String str){
        str = str.replace(" ", ""); //Elimina espacios
        str = str.replace(",", ""); //Elimina comas
        return str;
    }
    
    //Comprueba que no haya letras repetidas
    private boolean charRepeated(String text){ 
        //Local var
        Set<Character> aux = new HashSet<>(); 
       
        //Code
        for(char character : text.toCharArray()){ 
            if(!aux.contains(character)) 
                aux.add(character);
            else return true; 
        }
        return false;
    }
    
    //Carga las cartas y propiedades del juego
    private void loadMonopolyCodes(String configInfo){
        //Local var
        String propertyType = getCodeClass(configInfo);
        int id = getId(configInfo);
        
        //Code
        switch(propertyType){
            case "REPAIRS_CARD" -> setMonopolyCode(id, new RepairsCard(id, propertyType, configInfo));
            case "PAYMENT_CHARGE_CARD" -> setMonopolyCode(id, new PaymentCharge(id, propertyType, configInfo));
            case "STREET" -> setMonopolyCode(id, new Street(id, propertyType, configInfo, 
                    getPrice(getMortageValue(configInfo)), false, getMortageValue(configInfo)));
            case "TRANSPORT" -> setMonopolyCode(id, new Transport(id, propertyType, configInfo, 
                    getPrice(getMortageValue(configInfo)), false, getMortageValue(configInfo)));
            case "SERVICE" -> setMonopolyCode(id, new Service(id, propertyType, configInfo, 
                    getPrice(getMortageValue(configInfo)), false, getMortageValue(configInfo)));
            case "FREEPARKING" ->{ //Modificación 2
                    this.parking = new FreeParking(id, propertyType, configInfo);
                    setMonopolyCode(id,  parking);
            }   
        }
    }
    
    //Divide la cadena de configuración y la devuelve como un array de strings
    private String[] splitConfigInfo(String cInfo){
        return cInfo.split(";"); //Separa la línea según los ";"
    }
    
    //Obtiene el tipo de carta
    private String getCodeClass(String line){
        return splitConfigInfo(line)[1];
    }
    
    //Obtiene el id de la carta
    private int getId(String line){
        return Integer.parseInt(splitConfigInfo(line)[0]);
    }
    
    //Obtiene el valor de la hipoteca
    private int getMortageValue(String line){
        String[] lineArray =  splitConfigInfo(line);
        return Integer.parseInt(lineArray[lineArray.length - 1]);
    }
    
    //Obtiene el precio de la propiedad
    private int getPrice(int mortageValue){
        return mortageValue * 2; //El precio de una carta es el doble de su hipoteca
    }
    
    //Añade la carta al mapa de cartas del juego
    public void setMonopolyCode(int id, MonopolyCode mCode){
        idTypeMap.put(id, mCode);
    }
    
    //Elimina un jugador
    private void removePlayer(Player player){
        if(player.getBankrupt()){
            this.playerList.remove(player.getId());
            textTerminal.info("El jugador " + player.getColor() + player.getName() + " ha sido eliminado de la partida");
            if(this.playerList.size() == 1){
                textTerminal.showln("||--El jugador " + this.playerList.get(0).getColor() + this.playerList.get(0).getName() + " ha ganado la partida--||");
            }
        }
    }
    
    //Lee el archivo de configuración
    private void readFile() throws FileNotFoundException{
        //Local var
        File fileToRead = new File(configMonopolyDir); //Archivo del que se lee la información
        Scanner scan = new Scanner(fileToRead);
        String line;
        
        //Code
        while(scan.hasNextLine()){
            line = scan.nextLine();
            loadMonopolyCodes(line);
        }
    }
    
    //Guarda la partida
    private void save() throws IOException{
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileDir))){
            out.writeObject(this);
        } catch(IOException saveError){
            throw new IOException("Error al guardar el juego", saveError);
        }
    }
    
    //Muestra el estado del juego
    private void showGameState(){
        textTerminal.showln(" _____________________________");
        textTerminal.showln("|____RESUMEN DE LA PARTIDA____|");
        textTerminal.nextLine();
        textTerminal.showln("*******************************");
        textTerminal.nextLine();
        Collection<Player> playersList = playerList.values();
        for(Player player : playersList){
            textTerminal.showln("-> Jugador: " + player.getColor());
            textTerminal.showln("-> Nombre: " + player.getName());
            textTerminal.showln("-> Dinero: " + player.getBalance());
            textTerminal.showln("|-PROPIEDADES SIN HIPOTECAR-|");
            for(Property property : player.getProperties()){
                textTerminal.showln("---> Propiedad: [" + property.getId() + "] " + property.getName());
                if(property instanceof Street street){
                    textTerminal.showln("-> Casas: " + street.getBuiltHouses());
                }
                textTerminal.showln("-> Valor: " + property.getPrice());
                textTerminal.showln("-> Valor hipoteca: " + property.getMortgageValue());
            }
            textTerminal.showln("|-PROPIEDADES HIPOTECADAS-|");
            for(Property property : player.getPropertiesMortaged()){
                textTerminal.showln("Propiedad: [" + property.getId() + "] " + property.getName());
                if(property instanceof Street street){
                    textTerminal.showln("-> Casas: " + street.getBuiltHouses());
                }
                textTerminal.showln("-> Valor: " + property.getPrice());
                textTerminal.showln("-> Valor hipoteca: " + property.getMortgageValue());
            }
            textTerminal.nextLine();
            textTerminal.showln("*******************************");
            textTerminal.nextLine();
        }
    }
}