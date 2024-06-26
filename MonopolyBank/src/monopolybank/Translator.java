
package monopolybank;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Translator {
    
    //Atributos
    private Map<String, String> dictionary; //Mapa dónde se guardarán las traducciones
    private String language; //Idioma actual
    private String fileDir = "..\\config\\languages\\";
    
    //Métodos
    
    //Constructor
    public Translator(String fileName){ 
        this.dictionary = new HashMap<>(); //Inicializamos el mapa
        this.language = fileName; 
        try {
            readFile(fileName); //Leemos el archivo de traducción
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Traducimos el texto
    public String translate(String text){
        String[] splitText = text.split("&"); //Separa la línea según los "&"
        String returnText = new String(); //String donde almacenaremos toda la cadena
        for(String word : splitText){ //Recorremos el array de strings separados por "&"
           returnText += this.dictionary.getOrDefault(word, word); //Añadimos la palabra traducida a la cadena
        }
        return returnText; //Devolvemos la cadena traducida
    }
    
    //Lee el archivo y proporciona líneas a loadMonopolyCodes()
    private void readFile(String fileName) throws FileNotFoundException{
        //Local var
        File fileToRead = new File(fileDir + fileName + ".txt"); //Archivo del que leemos la información
        Scanner scan = new Scanner(fileToRead);
        String line;
        
        //Code
        while(scan.hasNextLine()){
            line = scan.nextLine();
            loadTranslations(line);
        }
    }
    
    //Cargamos el mapa de traducciones
    private void loadTranslations(String line){
        String[] splitInfo = line.split(";"); //Separa la línea según los ";"
        if(splitInfo.length == 2){
            this.dictionary.put(splitInfo[0], splitInfo[1]);
        }
    }
}
