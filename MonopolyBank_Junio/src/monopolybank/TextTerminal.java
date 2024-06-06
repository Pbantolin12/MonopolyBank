
package monopolybank;

public class TextTerminal extends Terminal {
    
    //Atributos
    
    //Métodos
    
    public TextTerminal(){ 
        TranslatorManager translatorM = new TranslatorManager();
    }
    
    public void showln(String text){ //Muestra el texto con un salto de línea
        System.out.println(text);
    }
    
    public void nextLine(){ //Introduce un salto de línea
        System.out.println(" ");
    }
    
    public void error(String desc){ //Muestra un mensaje de error
        this.nextLine();
        System.out.println("//-ERROR-\\\\ --> " + desc);
        this.nextLine();
    }
    
    public void info(String info){ //Muestra un mensaje informando de algo 
        this.nextLine();
        System.out.println("**" + info + "**");
        this.nextLine();
    }
}
