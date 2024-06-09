package monopolybank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameManager {
    
    //Attributes
    private transient TextTerminal textTerminal;
    private String fileDir = "..\\config\\oldGames\\";
    private Scanner scanner = new Scanner(System.in);
    private File file = new File(fileDir); //Carpeta
    private File[] fileList = file.listFiles(); //Lista de archivos de la carpeta
    private Game game;
    
    //Methods
    
    //Función que se encarga de iniciar el juego
    public void start() throws IOException, FileNotFoundException, ClassNotFoundException{
        //Local var
        int opt;
        
        //Code
        this.textTerminal =  TextTerminal.getInstance(); 
        do{
            opt = askForGame(); 
            switch(opt){ 
                case 1 -> askForResumeGame(); 
                case 2 -> askForNewGame(); 
                case 3 -> textTerminal.info("Saliendo...");
                default -> textTerminal.error("Opcion incorrecta"); 
            }
        } while(opt < 1 || opt > 3);
        this.game.play();
    }
    
    //Preguntamos el usuario si quiere crear o cargar una partida
    private int askForGame(){ 
        textTerminal.showln(" ___________________________");
        textTerminal.showln("|_Bienvenido_a_MonopolyBank_|");
        textTerminal.showln("| 1.Cargar partida guardada |");
        textTerminal.showln("| 2.Crear nueva partida     |");
        textTerminal.showln("| 3.Salir                   |");
        textTerminal.showln("|___________________________|");
        textTerminal.show(">>Introduzca una opcion: ");
        return textTerminal.read();
    }
        
    //Proceso para cargar una partida
    private void askForResumeGame() throws FileNotFoundException, IOException, ClassNotFoundException{   
        //Local var
        String fileName; 
        int counter = 1;
        int option = 0;
        
        //Code
        if(fileList.length != 0){
            textTerminal.info("Cargando lista de partidas");
            while(option < 1 || option > counter){
                //Recorremos la lista de partidas y las mostramos
                for(File actualFile:fileList){
                    fileName = actualFile.getName();
                    textTerminal.showln(counter + "." + fileName);
                    counter++;
                }
                textTerminal.showln(counter + "." + "|Cancelar|");
                textTerminal.show(">>Introduzca una opcion: ");
                option = textTerminal.read();
                if(option < 1 || option > counter){
                    textTerminal.error("Opcion incorrecta");
                    counter = 1;
                }
            }
            if(option == counter){
                start();
            } else{
                //Cargamos la partida 
                fileName = fileList[option - 1].getName();
                this.game = load(fileName);
            }
        } else{
            textTerminal.nextLine();
            textTerminal.error("La carpeta esta vacia");
            textTerminal.info("Se va a crear una partida nueva");
            askForNewGame();
        }
    }
    
    //Proceso para crear una partida
    private void askForNewGame() throws FileNotFoundException, IOException{
        //Local var
        boolean availableName = false;
        
        //Code
        while(availableName == false){
            textTerminal.show(">>Introduzca el nombre de la nueva partida: ");
            String fileName = scanner.nextLine();
            availableName = validateName(fileName);
            if(availableName == true){
                //Cargar datos y crear jugadores
                 this.game = new Game(this.textTerminal, fileName);
            } else{
                textTerminal.error("Ya existe una partida con ese nombre o incluye caracteres no validos");
            }
        }
        textTerminal.info("Creando la partida");
    }
    
    //Función para comprobar que el nombre de la partida es válido
    private boolean validateName(String gameName){ 
        //Local var
        String nameToCheck;
        String notAllowed = "[/\\:*?\"<>|']";
        
        //Code
        //Comprobar si tiene caracteres no válidos
        Pattern checkChars = Pattern.compile(notAllowed); //Contiene los caracteres no permitidos
        Matcher name = checkChars.matcher(gameName); //Contiene el nombre que vamos a comprobar 
        if(name.find()){ //Evaluamos si el nombre contiene caracteres ilegales
            return false;
        }
        //Comprobar si ya existe el nombre
        for(File fileName:fileList){
            nameToCheck = fileName.getName();
            if(gameName.equals(nameToCheck)){
                return false;
            }
        }
        return true;
    }
    
    //Cargamos el juego
    private Game load(String fileName) throws IOException{
        File file = new File(fileDir, fileName);
        try ( ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file))) {
           Object obj = objIn.readObject();
           if(obj instanceof Game){
               return (Game) obj;
           } else {
            throw new IOException("El archivo no contiene un objeto de la clase Game");
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Clase Game no encontrada al cargar el juego", e);
        }
    }
}