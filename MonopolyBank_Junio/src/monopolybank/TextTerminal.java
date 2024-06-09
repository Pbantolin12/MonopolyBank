package monopolybank;

public class TextTerminal extends Terminal {
    
    /*Como textTerminal no se puede guardar con la partida, puede haber errores al cargarla, 
    ya que textTerminal es nulo, por lo que vamos a aplicar el patroón Singleton para no 
    tener este tipo de errores*/
    
    //Atributos
    private static TextTerminal instance = new TextTerminal(); //Instancia única
    private Translator translator;
    
    //Constructor
    private TextTerminal(){ 
        TranslatorManager translatorM = new TranslatorManager();
        this.translator = translatorM.getTranslator();
    }
    
    //Métodos
    
    //Obtenemos la instancia
    public static TextTerminal getInstance(){
        if(instance == null){
            instance = new TextTerminal();
        }
        return instance; 
    }
    
    //Muestra el texto sin salto de línea
    @Override
    public void show(String text){ 
        System.out.print(this.translator.translate(text));
    }
    
    //Muestra el texto con un salto de línea
    public void showln(String text){ 
        System.out.println(this.translator.translate(text));
    }
    
    //Introduce un salto de línea
    public void nextLine(){ 
        System.out.println(" ");
    }
    
    //Muestra un mensaje de error
    public void error(String desc){ 
        this.nextLine();
        System.out.println("//-ERROR-\\\\ --> " + this.translator.translate(desc));
        this.nextLine();
    }
    
    //Muestra un mensaje informando de algo 
    public void info(String info){ 
        this.nextLine();
        System.out.println("**" + this.translator.translate(info) + "**");
        this.nextLine();
    }
}
