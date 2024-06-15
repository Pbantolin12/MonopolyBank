
package monopolybank;

public class FreeParking extends MonopolyCode{
    
    //Atributos
    private transient TextTerminal textTerminal; //Terminal para la interacción con el usuario
    private int deposit; //Dinero acumulado en el estacionamiento gratuito

    //Constructor
    public FreeParking(int id, String desc, String configInfo){
        super(id, desc, configInfo); //Llama al constructor de la superclase MonopolyCode
        this.textTerminal = TextTerminal.getInstance(); //Obtiene la instancia del terminal
        this.deposit = 0; //Inicializa el depósito a 0
    }
    
    // Métodos
    
    //Obtiene el dinero acumulado
    public int getDeposit(){
        return this.deposit;
    }
    
    //Añade dinero al depósito
    public void setDeposit(int amount){
        this.deposit += amount;
    }
    
    //Da al jugador todo el dinero acumulado
    @Override
    public void doOperation(Player player){
        this.textTerminal = TextTerminal.getInstance(); //Asegura que el terminal está inicializado
        player.setBalance(this.getDeposit()); //Da al jugador el dinero acumulado
        textTerminal.showln("El jugador " + player.getColor() + " cobrara " + this.getDeposit() + " euros del parking gratuito");
        this.setDeposit(0); //Reinicia el depósito a 0
    }
}