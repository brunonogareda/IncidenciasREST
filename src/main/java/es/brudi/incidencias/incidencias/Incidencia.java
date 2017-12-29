package es.brudi.incidencias.incidencias;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.json.simple.JSONObject;

import es.brudi.incidencias.db.dao.InstalacionDAO;
import es.brudi.incidencias.estados.Estado;
import es.brudi.incidencias.instalacions.Instalacion;

/**
 * 
 * Clase do obxecto incidencia
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Incidencia {

	private int id;
	private int cod_parte;
	private int ot;
	private Instalacion instalacion;
	private String zona_apartamento;
	private String descripcion_curta;
	private String observacions;
	private Estado estado;
	private boolean sol_presuposto;
	private String presuposto;
	private String factura;
	private Calendar data = Calendar.getInstance();
//	private int grupo_asig;
//	private String usuario_asig;
	private String autor;
	
	public Incidencia(int id, int cod_parte, int ot, Instalacion instalacion, String zona_apartamento,
			String descripcion_curta, String observacions, Estado estado, boolean sol_presuposto, String presuposto,
			String factura, Calendar data, int grupo_asig, String usuario_asig, String autor) {
		super();
		this.id = id;
		this.cod_parte = cod_parte;
		this.ot = ot;
		this.instalacion = instalacion;
		this.zona_apartamento = zona_apartamento;
		this.descripcion_curta = descripcion_curta;
		this.observacions = observacions;
		this.estado = estado;
		this.sol_presuposto = sol_presuposto;
		this.presuposto = presuposto;
		this.factura = factura;
		this.data = data;
//		this.grupo_asig = grupo_asig;
//		this.usuario_asig = usuario_asig;
		this.autor = autor;
	}
	
	public Incidencia(ResultSet res) throws SQLException {
		id = res.getInt("Id");
		cod_parte = res.getInt("Cod_parte");
		ot = res.getInt("Orden_traballo");
		instalacion = InstalacionDAO.getInstalacionById(res.getInt("Instalacion"));
		zona_apartamento = res.getString("Zona_apartamento");
		descripcion_curta = res.getString("descripcion_curta");
		observacions = res.getString("Observacions");
		//estado = new Estado(res.getString("Estado"));
		sol_presuposto = res.getBoolean("Solicitase_presuposto");
		presuposto = res.getString("Presuposto");
		factura = res.getString("Factura");
		data.setTime(res.getDate("Data"));
//		grupo_asig = res.getInt("Grupo_asignado");
//		usuario_asig = res.getString("Usuario_asignado");
		autor = res.getString("Autor");
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("id", id);
		ret.put("cod_parte", cod_parte);
		ret.put("ot", ot);
		ret.put("instalacion", instalacion);
		ret.put("zona_apartamento", zona_apartamento);
		ret.put("descripcion_curta", descripcion_curta);
		ret.put("observacions", observacions);
		ret.put("sol_presuposto", sol_presuposto);
		ret.put("Estado", estado);
		ret.put("presuposto", presuposto);
		ret.put("factura", factura);
		ret.put("data", data);
		ret.put(autor, autor);
		return ret;
	}

}
