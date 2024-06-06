
package monopolybank;

import java.util.Scanner;

public class Terminal {
 
    //Atributos
    
    //Métodos
    
    //Lee lo que el usuario introduce
    public int read(){ 
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        return n; 
    }
    
    //Muestra el texto sin salto de línea
    public void show(String text){ 
        System.out.print(text);
    }
}
