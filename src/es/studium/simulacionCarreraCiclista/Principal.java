package es.studium.simulacionCarreraCiclista;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Principal
{
	public static void main(String[] args)
	{
		// obtener un ArrayList de objetos Ciclista del fichero
		ArrayList<Ciclista> ciclistas = Ciclista.obtenerCiclistasDeFichero();
		// asignar la stamina según la posición en el equipo
		Ciclista.asignarStamina(ciclistas);
		// empezar todos los hilos
		for (Ciclista c : ciclistas) {
			c.start();
		}
		// hacer que el hilo main espere a la finalización de todos los hilos
		for (Ciclista c : ciclistas) {
	        try {
	            c.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
		// al terminar todos los hilos, se mostrará el ranking
		mostrarRanking(ciclistas);
		// actualizar el fichero ciclistas.txt
		Ciclista.actualizarFichero(ciclistas);
	}

	public static void mostrarRanking(ArrayList<Ciclista> ciclistas)
	{
		// crear un stream con todos los ciclistas
		List<Ciclista> ordenados = ciclistas.stream()
				// ordenar por kilómetros de mayor a menor
				.sorted(Comparator.comparing(Ciclista::getKilometros).reversed())
				// convertir el stream en una lista
				.collect(Collectors.toList());
		System.out.println("\nClasificación final:");
		for (int i = 0; i < ordenados.size(); i++) {
			// %2d para el número en el ranking, %3d para el dorsal, 
			// %-25s para los nombres (a la izquierda), %20.2f para kilómetros (a la derecha)
			System.out.printf("%2d: %3d - \t%-25s%20.2f kilómetros.%n",
			        i + 1, ordenados.get(i).getDorsal(), ordenados.get(i).getNombre(), ordenados.get(i).getKilometros());
		}
	}
}
