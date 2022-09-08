package it.polito.tdp.poweroutages.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.poweroutages.db.PowerOutagesDAO;

public class Model {

	private PowerOutagesDAO dao;
	private Graph<Nerc, DefaultWeightedEdge> grafo;
	private Map<Integer, Nerc> idMap;
	
	// Output della simulazione
	private int numCatastrofi;
	private Map<Nerc, Long> bonus;
	
	public Model() {
		dao = new PowerOutagesDAO();
		idMap = new HashMap<>();
		dao.loadAllNercs(idMap);
	}
	
	public void creaGrafo() {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// Aggiunta dei vertici
		Graphs.addAllVertices(this.grafo, dao.loadAllNercs(idMap));
		
		// Aggiunta degli archi
		for (Adiacenza a : dao.getAllAdiacenze(idMap)) {
			int peso = dao.getCorrelation(a.getN1(), a.getN2());
			Graphs.addEdge(this.grafo, a.getN1(), a.getN2(), peso);
		}
	}
	
	public void simula(int mesi) {
		Simulatore sim = new Simulatore(this.grafo, this);
		sim.init(mesi);
		sim.run();
		this.numCatastrofi = sim.getNumCatastrofi();
		this.bonus = sim.getBonus();
	}
	
	public List<Adiacenza> getVicini(Nerc n) {
		List<Adiacenza> result = new LinkedList<>();
		
		for (Nerc vicino : Graphs.neighborListOf(this.grafo, n)) {
			int peso = (int)this.grafo.getEdgeWeight(this.grafo.getEdge(n, vicino));
			result.add(new Adiacenza(n, vicino, peso));
		}
		
		Collections.sort(result);
		return result;
	}
	
	public List<PowerOutage> getAllPowerOutages(){
		return this.dao.getAllPowerOutages(idMap);
	}
	
	public Set<Nerc> getAllNerc(){
		return this.grafo.vertexSet();
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}

	public int getNumCatastrofi() {
		return numCatastrofi;
	}

	public Map<Nerc, Long> getBonus() {
		return bonus;
	}
	
}
