
package monopolybank;

public class TextTerminal extends Terminal {
    
    //Atributos
    
    //Constructor
    public TextTerminal(){ 
        TranslatorManager translatorM = new TranslatorManager();
    }
    
    //Métodos
    
    //Muestra el texto con un salto de línea
    public void showln(String text){ 
        System.out.println(text);
    }
    
    //Introduce un salto de línea
    public void nextLine(){ 
        System.out.println(" ");
    }
    
    //Muestra un mensaje de error
    public void error(String desc){ 
        this.nextLine();
        System.out.println("//-ERROR-\\\\ --> " + desc);
        this.nextLine();
    }
    
    //Muestra un mensaje informando de algo 
    public void info(String info){ 
        this.nextLine();
        System.out.println("**" + info + "**");
        this.nextLine();
    }
}
