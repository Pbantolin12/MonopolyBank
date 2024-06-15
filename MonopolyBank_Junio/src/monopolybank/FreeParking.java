
package monopolybank;

public class FreeParking extends MonopolyCode{
    //Atributos
    private transient TextTerminal textTerminal;
    private int deposit;

    //Constructor
    public FreeParking(int id, String desc, String configInfo) {
        super(id, desc, configInfo);
        this.textTerminal = TextTerminal.getInstance();
        this.deposit = 0;
    }
    
    //Métodos
    
    //Obtenemos el dinero acumulado
    public int getDeposit(){
        return this.deposit;
    }
    
    //Añadimos dinero al depósito
    public void setDeposit(int amount){
        this.deposit += amount;
    }
    
    //Damos al jugador todo el dinero acumulado
    @Override
    public void doOperation(Player player){
        this.textTerminal = TextTerminal.getInstance();
        player.setBalance(this.getDeposit());
        textTerminal.showln("El jugador " + player.getColor() + " cobrara " + this.getDeposit() + " euros del parking gratuito");
        this.setDeposit(0);
    }
    
}
