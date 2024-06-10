
package monopolybank;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MonopolyBank {
    
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {
        GameManager gameManager = new GameManager();
        gameManager.start();
    }
}
