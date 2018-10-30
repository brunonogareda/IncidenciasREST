package es.brudi.incidencias.incidencias;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.incidencias.estados.Estado;
import es.brudi.incidencias.instalacions.Instalacion;
import es.brudi.incidencias.instalacions.db.InstalacionAccessor;

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
	private int codParte;
	private int ot;
	private Instalacion instalacion;
	private String zonaApartamento;
	private String descripcionCurta;
	private String observacions;
	private Estado estado;
	private boolean solPresuposto;
	private String presuposto;
	private String factura;
	private Calendar data = Calendar.getInstance();
	private int autor;
	
	public Incidencia(int id, int codParte, int ot, Instalacion instalacion, String zonaApartamento,
			String descripcionCurta, String observacions, Estado estado, boolean solPresuposto, String presuposto,
			String factura, Calendar data, int autor) {
		super();
		this.id = id;
		this.codParte = codParte;
		this.ot = ot;
		this.instalacion = instalacion;
		this.zonaApartamento = zonaApartamento;
		this.descripcionCurta = descripcionCurta;
		this.observacions = observacions;
		this.estado = estado;
		this.solPresuposto = solPresuposto;
		this.presuposto = presuposto;
		this.factura = factura;
		this.data = data;
		this.autor = autor;
	}
	
	public Incidencia(ResultSet res) throws SQLException {
		id = res.getInt("Id");
		codParte = res.getInt("Cod_parte");
		ot = res.getInt("Orden_traballo");
		instalacion = InstalacionAccessor.getInstalacionById(res.getInt("Instalacion"));
		zonaApartamento = res.getString("Zona_apartamento");
		descripcionCurta = res.getString("descripcion_curta");
		observacions = res.getString("Observacions");
		estado = Estado.getByString(res.getString("Estado"));
		solPresuposto = res.getBoolean("Solicitase_presuposto");
		presuposto = res.getString("Presuposto");
		factura = res.getString("Factura");
		data.setTime(res.getTimestamp("Data"));
		autor = res.getInt("Autor");
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
	 * @return the codParte
	 */
	public int getCodParte() {
		return codParte;
	}

	/**
	 * @param codParte the codParte to set
	 */
	public void setCodParte(int codParte) {
		this.codParte = codParte;
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
	 * @return the zonaApartamento
	 */
	public String getZonaApartamento() {
		return zonaApartamento;
	}

	/**
	 * @param zonaApartamento the zona_apartamento to set
	 */
	public void setZonaApartamento(String zonaApartamento) {
		this.zonaApartamento = zonaApartamento;
	}

	/**
	 * @return the descripcionCurta
	 */
	public String getDescripcionCurta() {
		return descripcionCurta;
	}

	/**
	 * @param descripcionCurta the descripcionCurta to set
	 */
	public void setDescripcionCurta(String descripcionCurta) {
		this.descripcionCurta = descripcionCurta;
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
	public boolean isSolPresuposto() {
		return solPresuposto;
	}

	/**
	 * @param solPresuposto the solPresuposto to set
	 */
	public void setSolPresuposto(boolean solPresuposto) {
		this.solPresuposto = solPresuposto;
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
	public int getAutor() {
		return autor;
	}

	/**
	 * @param autor the autor to set
	 */
	public void setAutor(int autor) {
		this.autor = autor;
	}

	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> ret = new JSONObject<>();
		ret.put("id", id);
		ret.put("codParte", codParte);
		ret.put("ot", ot);
		ret.put("instalacion", instalacion);
		ret.put("zonaApartamento", zonaApartamento);
		ret.put("descripcionCurta", descripcionCurta);
		ret.put("observacions", observacions);
		ret.put("solPresuposto", solPresuposto);
		ret.put("estado", estado.getEstado());
		ret.put("presuposto", presuposto);
		ret.put("factura", factura);
		ret.put("data", data);
		ret.put("autor", autor);
		return ret;
	}

}
