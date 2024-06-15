
package monopolybank;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MonopolyBank {
    
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {
        GameManager gameManager = new GameManager();
        gameManager.start();
    }
}

/*

||--MODIFICACIONES--||

--> Modificación 1:

    Para cumplir esta modificación, en las clases que heredan de Property (Transpot, Service, Street) en la función 
    getPaymentForRent(), se ha añadido una comprobación para saber si la propiedad está hipotecada, si es el caso, 
    multiplicamos lo que el jugador tiene que pagar de alquiler por 1/3 y lo redondeamos para que no haya problemas 
    con el tipo de dato (ya que es un int).


--> Modificación 2:

    Para cumplir con la modificación, se ha creado una nueva clase FreeParking que heereda de MonopolyCode (ya que es una carta), 
    que  tiene una variable deposit dónde se almacena el dinero acumulado de los pagos. 

    Esta clase tiene tres funciones, getDeposit() que devuelve el dinero acumulado que hay, setDeposit() que añade dinero 
    al depósito y  doOperation() que se encarga de pagar al usuario el dinero acumulado cuando cae en la casilla.

    Para que podamos guardar el dinero en la clase FreeParking, se ha modificado el método doOperation() de la clase PaymentCharge,
    añadiendo a sus parámetros la clase FreeParking quedando así: doOperation(Player player, FreeParking freeParking). De esta forma,
    podemos pasarle la clase FreeParking al método showSummary() que se encarga de mostrar un resumen del pago y además añade al
    dinero que ha pagado el jugador a FreeParking.

    Además, en la clase Game, se ha añadido un método parking del tipo FreeParking que se almacena una vez que se crea la clase
    FreeParking, esto nos permite poder pasarle la clase FreeParking a la clase PaymentCharge (como se ha explicado anteriormente).

    --> Otra forma

        Otra forma de implementación de la modificación 2 podría ser creando una variable directamente en la clase MonopolyCard,
        ya que a esta clase tienen acceso el resto de clases que son cartas, por lo que no sería necesario tener una variable 
        parking en la clase Game. 

        Los métodos serían los mismos que los que hay en la clase FreeParking aunque en este caso deberían ser protected, 
        de esta  forma, sólo podrían acceder a ellos las clases que heredan y la propia clase.

        Comprobando el Id de la carta, se realizaría el pago del depósito al jugador.

        En principio, de esta forma deberían poder realizarse las mismas acciones que con la modificación que yo he implementado.
*/