
package monopolybank;

import java.util.Scanner;

public class TranslatorManager{
    
    //Atributos
    private Translator currentIdiom;
    
    //Constructor
    public TranslatorManager(){}
    
    //Métodos
    
    //Obtenemos el idioma actual
    public Translator getTranslator(){
        if(this.currentIdiom == null){
            changeIdiom();
            return this.currentIdiom;
        } else{
            return this.currentIdiom;
        }
    }
    
    //Cambiar el idioma
    public void changeIdiom(){
        System.out.println(" _____________________");
        System.out.println("|_Idiomas_Disponibles_|");
        System.out.println("| 1.Espanol           |");
        System.out.println("| 2.English           |");
        System.out.println("| 3.Catala            |");
        System.out.println("|_____________________|");
        System.out.print(">>Introduzca una opcion: ");
        Scanner scan = new Scanner(System.in);
        int option = scan.nextInt();
        switch(option){
            case 1 -> currentIdiom = new Translator("Español");
            case 2 -> currentIdiom = new Translator("English");
            case 3 -> currentIdiom = new Translator("Catala");
        }
    }
}
