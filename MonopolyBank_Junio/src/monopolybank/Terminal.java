
package monopolybank;

import java.util.Scanner;

public class Terminal {
    
    //Atributos
    
    //Métodos
    
    public int read(){ //Lee lo que el usuario introduce
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        return n; 
    }
    
    public void show(String text){ //Muestra el texto sin salto de línea
        System.out.print(text);
    }
}
