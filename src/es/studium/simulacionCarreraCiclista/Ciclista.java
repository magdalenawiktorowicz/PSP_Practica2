package es.studium.simulacionCarreraCiclista;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Ciclista extends Thread
{
	private int dorsal;
	private String nombre;
	private String equipo;
	double kilometros = 0;
	int stamina;
	double avance;
	Random random = new Random();

	public Ciclista(int dorsal, String nombre, String equipo)
	{
		this.dorsal = dorsal;
		this.nombre = nombre;
		this.equipo = equipo;
	}

	@Override
	public void run()
	{
		// para mostrar solo dos puntos decimales
		DecimalFormat df = new DecimalFormat("0.00");
		// 20 avances
		for (int i = 0; i < 20; i++)
		{
			// calcular el avance
			avance = (random.nextInt(5) + 1) * ((double) this.getStamina() / 12.0) * 10;
			// sumar el avance al total de kilómetros
			this.setKilometros(this.getKilometros() + avance);
			try
			{
				// dormir el hilo 3 segundos
				Thread.sleep(3000);
			} catch (InterruptedException e)
			{
				e.getMessage();
			}
			System.out.println("Corredor " + this.getDorsal() + " ha avanzado " + df.format(avance)
					+ " y en total lleva " + df.format(this.getKilometros()));
		}

	}

	// getters & setters
	int getDorsal()
	{
		return dorsal;
	}

	void setDorsal(int dorsal)
	{
		this.dorsal = dorsal;
	}

	String getNombre()
	{
		return nombre;
	}

	void setNombre(String nombre)
	{
		this.nombre = nombre;
	}

	String getEquipo()
	{
		return equipo;
	}

	void setEquipo(String equipo)
	{
		this.equipo = equipo;
	}

	double getKilometros()
	{
		return kilometros;
	}

	void setKilometros(double kilometros)
	{
		this.kilometros = kilometros;
	}

	int getStamina()
	{
		return stamina;
	}

	void setStamina(int stamina)
	{
		this.stamina = stamina;
	}

	@Override
	public String toString()
	{
		return "Ciclista [dorsal=" + dorsal + ", nombre=" + nombre + ", equipo=" + equipo + ", kilometros=" + kilometros
				+ ", stamina=" + stamina + "]";
	}

	// método para obtener los ciclistas del fichero
	static ArrayList<Ciclista> obtenerCiclistasDeFichero()
	{
		// crear un ArrayList vacio
		ArrayList<Ciclista> ciclistas = new ArrayList<>();
		File fichero = new File("ciclistas.txt");
		/*
		 * try-with-resources para que los objetos FileReader y BufferedReader se
		 * cierren automáticamente al final del bloque try; en los paréntesis declaramos
		 * e iniciamos los objetos FileReader y BufferedReader
		 */
		try (BufferedReader bf = new BufferedReader(new FileReader(fichero)))
		{
			// leer todas las líneas del fichero, guardarlas en una List
			List<String> listaCiclistas = Files.readAllLines(fichero.toPath());
			if (listaCiclistas != null)
			{
				// para cada ciclista de la lista
				for (String ciclista : listaCiclistas)
				{
					// obtener todos sus atributos
					String[] ciclistaAttr = ciclista.split(", ");
					int dorsalCiclista = Integer.parseInt(ciclistaAttr[0]);
					String nombreCiclista = ciclistaAttr[1];
					String equipoCiclista = ciclistaAttr[2];
					// crear objetos Ciclista y añadirlos al ArrayList 'ciclistas'
					ciclistas.add(new Ciclista(dorsalCiclista, nombreCiclista, equipoCiclista));
				}
			}
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		return ciclistas;
	}

	// método para asignar la stamina
	static void asignarStamina(ArrayList<Ciclista> ciclistas)
	{
		// crear un objeto Map cuyos claves son Objetos (equipos) y valores - Lists de ciclistas
		Map<Object, List<Ciclista>> porEquipos = ciclistas.stream() // abrir un stream
				// agrupar los ciclistas por sus equipos
				.collect(Collectors.groupingBy(Ciclista::getEquipo,
						// crear un objeto TreeMap para poder tener los elementos ordenados
						TreeMap::new, Collectors.toList()));
		// en cada equipo, ordenar los ciclistas según su número de dorsal ascendiente
		porEquipos.forEach((equipo, listaCiclistas) ->
		{
			listaCiclistas.sort(Comparator.comparingInt(Ciclista::getDorsal));
			for (int i = 0; i < listaCiclistas.size(); i++)
			{
				Ciclista c = listaCiclistas.get(i);
				// para el lider del equipo, asignarle el valor 10 de stamina
				if (i == 0)
				{
					c.setStamina(10);
					// para el último del equipo, asignarle el valor 1 de stamina
				} else if (i == (listaCiclistas.size() - 1))
				{
					c.setStamina(1);
				} else
				{
					// para los demás, de 8 hasta 2 respectivamente
					c.setStamina(8 - (i - 1));
				}
			}
		});
	}

	// método para actualizar el fichero
	static void actualizarFichero(ArrayList<Ciclista> ciclistas)
	{
		// actualizar los números de dorsales
		actualizarDorsal(ciclistas);
		// ordenar los ciclistas por el dorsal
		ciclistas.sort(Comparator.comparing(Ciclista::getDorsal));
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("ciclistas.txt"))) {
			for (Ciclista ciclista : ciclistas) {
				writer.write(ciclista.getDorsal() + ", " + ciclista.getNombre() + ", " + ciclista.getEquipo() + "\n");
			}
		} catch (IOException e) {
			System.out.println("Se ha producido un error.");
		}
		System.out.println("La lista se ha guardado correctamente en el fichero.");
	}

	// método para actualizar dorsales de los ciclistas
	private static void actualizarDorsal(ArrayList<Ciclista> ciclistas)
	{
		Map<Object, List<Ciclista>> porEquipos = ciclistas.stream() // abrir un stream
				// agrupar los ciclistas por sus equipos
				.collect(Collectors.groupingBy(Ciclista::getEquipo,
						// crear un objeto TreeMap para poder tener los elementos ordenados
						TreeMap::new, Collectors.toList()));
		// en cada equipo, ordenar los ciclistas según su número de dorsal ascendiente
		porEquipos.forEach((equipo, listaCiclistas) ->
		{
			for (int i = 0; i < listaCiclistas.size()-1; i++)
			{
				Ciclista c = listaCiclistas.get(i);
				Ciclista c2 = listaCiclistas.get(i+1);
				// comparar los kilómetros de ciclistas
				// si el siguiente ciclista tiene más kilómetros
				if (c2.getKilometros() > c.getKilometros())
				{
					int aux = c.getDorsal(); // guardar el valor del dorsal de Ciclista c
					// intercambiar los dorsales
					c.setDorsal(c2.getDorsal());
					c2.setDorsal(aux);
				}
			}
		});
	}

}
