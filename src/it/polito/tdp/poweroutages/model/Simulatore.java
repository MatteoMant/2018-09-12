package it.polito.tdp.poweroutages.model;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.poweroutages.model.Event.EventType;

public class Simulatore {
	
	// Parametri della simulazione
	private int numeroMesi;
	
	// Coda degli eventi
	private PriorityQueue<Event> queue;
	
	// Output della simulazione
	private int numCatastrofi;
	private Map<Nerc, Long> bonus;
	
	// Stato del mondo
	private Graph<Nerc, DefaultWeightedEdge> grafo;
	private Map<Nerc, Set<Nerc>> prestiti; 
	private Model model;
	
	public Simulatore(Graph<Nerc, DefaultWeightedEdge> grafo, Model model) {
		this.grafo = grafo;
		this.model = model;
	}
	
	public void init(int mesi) {
		this.numeroMesi = mesi;
		
		this.bonus = new HashMap<>();
		this.prestiti = new HashMap<>();
		
		for (Nerc nerc : this.grafo.vertexSet()) {
			this.bonus.put(nerc, Long.valueOf(0));
			this.prestiti.put(nerc, new HashSet<>());
		}
		
		this.numCatastrofi = 0;
		
		this.queue = new PriorityQueue<>();
		
		for (PowerOutage po : this.model.getAllPowerOutages()) {
			Event e = new Event(EventType.INIZIO_INTERRUZIONE, po.getNerc(), null, po.getInizio(), po.getInizio(), po.getFine());
			this.queue.add(e);
		}
		
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}
	}

	private void processEvent(Event e) {

		switch(e.getTipo()) {
		case INIZIO_INTERRUZIONE:
			Nerc nerc = e.getNerc();
			System.out.println("INIZIO INTERRUZIONE NERC : " + nerc);
			
			// cerco se c'è un nerc donatore, altrimenti si verifica una catastrofe
			Nerc donatore = null;
			
			if (this.prestiti.get(nerc).size() > 0) { // allora significa che il nerc in questione ha prestato energia ad altri nerc
				// Quindi in questo caso scegliamo il donatore tra i suoi "debitori" ovvero coloro a cui ha prestato corrente
				double minimo = Long.MAX_VALUE;
				for (Nerc n : this.prestiti.get(nerc)) {
					double peso = this.grafo.getEdgeWeight(this.grafo.getEdge(nerc, n));
					if (peso < minimo) {
						if (!n.getStaPrestando()) {
							minimo = peso;
							donatore = n;
						}
					}
				}
			} else {
				// se invece il nerc non ha prestato energia a nessun altro nerc, allora scelgo il donatore sulla base del nerc
				// vicino con il peso dell'arco minimo
				double minimo = Long.MAX_VALUE;
				for (Nerc n : Graphs.neighborListOf(this.grafo, nerc)) {
					double peso = this.grafo.getEdgeWeight(this.grafo.getEdge(nerc, n));
					if (peso < minimo) {
						if (!n.getStaPrestando()) {
							minimo = peso;
							donatore = n;
						}
					}
				}
			}
			
			if (donatore != null) {
				// ho trovato un nerc donatore 
				System.out.println("\tTROVATO DONATORE: " + donatore);
				donatore.setStaPrestando(true);
				// se ho trovato un donatore, allora genero un evento di tipo 'FINE_INTERRUZIONE' perchè appunto
				// grazie a tale donatore l'interruzione di corrente terminerà
				this.queue.add(new Event(EventType.FINE_INTERRUZIONE, nerc, donatore, e.getDataFine(), e.getDataInizio(), e.getDataFine()));
		
				this.prestiti.get(donatore).add(nerc);
				
				this.queue.add(new Event(EventType.CANCELLA_PRESTITO, e.getNerc(), donatore, e.getData().plusMonths(numeroMesi), 
						e.getDataInizio(), e.getDataFine()));
			} else {
				// se non ho trovato un donatore allora si è verificata una catastrofe
				System.out.println("\tCATASTROFE!!");
				this.numCatastrofi++;
			}
			
			break;
			
		
		case FINE_INTERRUZIONE:
			System.out.println("FINE INTERRUZIONE NERC : " + e.getNerc());
			
			// dobbiamo assegnare un bonus al donatore
			if (e.getDonatore() != null) {
				this.bonus.put(e.getDonatore(), bonus.get(e.getDonatore()) + 
						Duration.between(e.getDataInizio(), e.getDataFine()).toDays());
			}
			// infine diciamo che il donatore non sta più prestando
			e.getDonatore().setStaPrestando(false);
			
			break;
			
			
		case CANCELLA_PRESTITO:
			System.out.println("CANCELLAZIONE PRESTITO: " + e.getDonatore() + "-" + e.getNerc());
			this.prestiti.get(e.getDonatore()).remove(e.getNerc());
			break;
		}
		
	}

	public int getNumCatastrofi() {
		return numCatastrofi;
	}

	public Map<Nerc, Long> getBonus() {
		return bonus;
	}
	
}
