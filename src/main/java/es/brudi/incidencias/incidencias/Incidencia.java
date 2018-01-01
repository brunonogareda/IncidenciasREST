package es.brudi.incidencias.incidencias;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import es.brudi.incidencias.util.JSONObject;
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
		data.setTime(res.getTimestamp("Data"));
//		grupo_asig = res.getInt("Grupo_asignado");
//		usuario_asig = res.getString("Usuario_asignado");
		autor = res.getString("Autor");
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the cod_parte
	 */
	public int getCod_parte() {
		return cod_parte;
	}

	/**
	 * @param cod_parte the cod_parte to set
	 */
	public void setCod_parte(int cod_parte) {
		this.cod_parte = cod_parte;
	}

	/**
	 * @return the ot
	 */
	public int getOt() {
		return ot;
	}

	/**
	 * @param ot the ot to set
	 */
	public void setOt(int ot) {
		this.ot = ot;
	}

	/**
	 * @return the instalacion
	 */
	public Instalacion getInstalacion() {
		return instalacion;
	}

	/**
	 * @param instalacion the instalacion to set
	 */
	public void setInstalacion(Instalacion instalacion) {
		this.instalacion = instalacion;
	}

	/**
	 * @return the zona_apartamento
	 */
	public String getZona_apartamento() {
		return zona_apartamento;
	}

	/**
	 * @param zona_apartamento the zona_apartamento to set
	 */
	public void setZona_apartamento(String zona_apartamento) {
		this.zona_apartamento = zona_apartamento;
	}

	/**
	 * @return the descripcion_curta
	 */
	public String getDescripcion_curta() {
		return descripcion_curta;
	}

	/**
	 * @param descripcion_curta the descripcion_curta to set
	 */
	public void setDescripcion_curta(String descripcion_curta) {
		this.descripcion_curta = descripcion_curta;
	}

	/**
	 * @return the observacions
	 */
	public String getObservacions() {
		return observacions;
	}

	/**
	 * @param observacions the observacions to set
	 */
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

	/**
	 * @return the estado
	 */
	public Estado getEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	/**
	 * @return the sol_presuposto
	 */
	public boolean isSol_presuposto() {
		return sol_presuposto;
	}

	/**
	 * @param sol_presuposto the sol_presuposto to set
	 */
	public void setSol_presuposto(boolean sol_presuposto) {
		this.sol_presuposto = sol_presuposto;
	}

	/**
	 * @return the presuposto
	 */
	public String getPresuposto() {
		return presuposto;
	}

	/**
	 * @param presuposto the presuposto to set
	 */
	public void setPresuposto(String presuposto) {
		this.presuposto = presuposto;
	}

	/**
	 * @return the factura
	 */
	public String getFactura() {
		return factura;
	}

	/**
	 * @param factura the factura to set
	 */
	public void setFactura(String factura) {
		this.factura = factura;
	}

	/**
	 * @return the data
	 */
	public Calendar getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Calendar data) {
		this.data = data;
	}

	/**
	 * @return the autor
	 */
	public String getAutor() {
		return autor;
	}

	/**
	 * @param autor the autor to set
	 */
	public void setAutor(String autor) {
		this.autor = autor;
	}

	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		ret.put("id", id);
		ret.put("cod_parte", cod_parte);
		ret.put("ot", ot);
		ret.put("instalacion", instalacion);
		ret.put("zona_apartamento", zona_apartamento);
		ret.put("descripcion_curta", descripcion_curta);
		ret.put("observacions", observacions);
		ret.put("sol_presuposto", sol_presuposto);
		ret.put("estado", estado);
		ret.put("presuposto", presuposto);
		ret.put("factura", factura);
		ret.put("data", data);
		ret.put(autor, autor);
		return ret;
	}

}
