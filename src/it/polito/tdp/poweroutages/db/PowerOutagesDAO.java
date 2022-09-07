package it.polito.tdp.poweroutages.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.poweroutages.model.Adiacenza;
import it.polito.tdp.poweroutages.model.Nerc;

public class PowerOutagesDAO {
	
	public List<Nerc> loadAllNercs(Map<Integer, Nerc> idMap) {

		String sql = "SELECT id, value FROM nerc";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = new Nerc(res.getInt("id"), res.getString("value"));
				nercList.add(n);
				if (!idMap.containsKey(n.getId())) {
					idMap.put(n.getId(), n);
				}
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
	
	public List<Adiacenza> getAllAdiacenze(Map<Integer, Nerc> idMap) {

		String sql = "SELECT nerc_one, nerc_two FROM NercRelations";
		List<Adiacenza> result = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n1 = idMap.get(res.getInt("nerc_one"));
				Nerc n2 = idMap.get(res.getInt("nerc_two"));
				result.add(new Adiacenza(n1, n2, 0));
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}
	
	public int getCorrelation(Nerc nerc, Nerc neighbor) {
		String sql = "select distinct year(p1.date_event_began), month(p1.date_event_began) "
				+ "from poweroutages p1, poweroutages p2 "
				+ "where p1.nerc_id = ? and p2.nerc_id = ? and month(p1.date_event_began)=month(p2.date_event_began) "
				+ "and year(p1.date_event_began)=year(p2.date_event_began)";
		int count = 0;

		try{
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, nerc.getId());
			st.setInt(2, neighbor.getId());
			
			ResultSet rs = st.executeQuery();
			while (rs.next()) {				
				count++;
			}

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		return count;
	}
	
}
