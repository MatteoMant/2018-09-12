package it.polito.tdp.poweroutages.model;

import java.time.LocalDateTime;

public class Event implements Comparable<Event>{
	
	public enum EventType {
		INIZIO_INTERRUZIONE,
		FINE_INTERRUZIONE,
		CANCELLA_PRESTITO
	}
	
	private EventType tipo;
	private Nerc nerc;
	private Nerc donatore;
	private LocalDateTime data;
	private LocalDateTime dataInizio;
	private LocalDateTime dataFine;
	
	public Event(EventType tipo, Nerc nerc, Nerc donatore, LocalDateTime data, LocalDateTime dataInizio,
			LocalDateTime dataFine) {
		super();
		this.tipo = tipo;
		this.nerc = nerc;
		this.donatore = donatore;
		this.data = data;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
	}

	public EventType getTipo() {
		return tipo;
	}

	public void setTipo(EventType tipo) {
		this.tipo = tipo;
	}

	public Nerc getNerc() {
		return nerc;
	}

	public void setNerc(Nerc nerc) {
		this.nerc = nerc;
	}

	public Nerc getDonatore() {
		return donatore;
	}

	public void setDonatore(Nerc donatore) {
		this.donatore = donatore;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public LocalDateTime getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(LocalDateTime dataInizio) {
		this.dataInizio = dataInizio;
	}

	public LocalDateTime getDataFine() {
		return dataFine;
	}

	public void setDataFine(LocalDateTime dataFine) {
		this.dataFine = dataFine;
	}

	@Override
	public int compareTo(Event other) {
		return this.getData().compareTo(other.getData());
	}
	
}
