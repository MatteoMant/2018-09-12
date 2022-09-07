package it.polito.tdp.poweroutages.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.poweroutages.db.PowerOutagesDAO;

public class Model {

	private PowerOutagesDAO dao;
	private Graph<Nerc, DefaultWeightedEdge> grafo;
	private Map<Integer, Nerc> idMap;
	
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
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
}
