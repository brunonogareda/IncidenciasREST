package es.brudi.incidencias.facturas;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.sun.jersey.core.header.FormDataContentDisposition;

import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.db.dao.ComentarioDAO;
import es.brudi.incidencias.db.dao.FacturaDAO;
import es.brudi.incidencias.db.dao.IncidenciaDAO;
import es.brudi.incidencias.documentos.XestionFicheiros;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.estados.Estado;
import es.brudi.incidencias.util.Util;

/**
 * 
 * Clase que xestiona as funcións relacionados coas facturas.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class XestionFacturas {
	
	private static Logger logger = Logger.getLogger(XestionFacturas.class);
	
	public JSONObject<String, Object> crear(Usuario user, int id_incidencia, String id_factura, String comentarios, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		String nome_ficheiro = null;
		String tipo_ficheiro = null;
		String dir_ficheiro = null;
		boolean errFile = false;
		
		if(!user.podeEngadirFactura()) {
			return Error.USER_NOPERMISOS.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id_incidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREARFACTURA_SENPERMISOS.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			nome_ficheiro = fileDetail.getFileName();
			tipo_ficheiro = FilenameUtils.getExtension(nome_ficheiro);
			dir_ficheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.RUTA_FACTURAS, inc.getData());
		}
		
		if(!inc.getEstado().equals(Estado.PENDENTE_F)) { //Comprobamos que o estado da incidencia é o correcto para engadir a factura.
			return Error.CREARFACTURA_ERRORESTADO.toJSONError();
		}
		if(inc.getFactura() != null) {//A incidencia xa ten factura.
			return Error.CREARFACTURA_EXISTE.toJSONError();
		}
		//comprobamos se xa existe algunha factura con ese Id.
		Factura fact = FacturaDAO.getById(id_factura);
		if(fact != null) {
			return Error.CREARFACTURA_DUPLICADA.toJSONError();
		}	
		
		boolean fret = FacturaDAO.crear(id_factura, dir_ficheiro+'/'+nome_ficheiro, tipo_ficheiro, comentarios); //Creamos a factura na táboa.
		if(!fret) {
			return Error.CREARFACTURA_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dir_ficheiro, nome_ficheiro);
			if(path == null ) {
				ret = Error.CREARFACTURA_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Factura factura = FacturaDAO.getById(id_factura); //Buscamos a factura que acabamos de insertar
		if(factura == null) {
			return Error.CREARFACTURA_ERRORDB.toJSONError();
		}
		if(!IncidenciaDAO.modifcarFactura(id_incidencia, id_factura)) { //Engadese o id de factura na incidencia correspondente.
			return Error.CREARFACTURA_ERRORDB.toJSONError();
		}
		if(!IncidenciaDAO.modificarEstado(id_incidencia, Estado.FACTURADO.getEstado())) { //Cambiamos o estado da incidencia.
			return Error.CREARFACTURA_ERRORDB.toJSONError();
		}
		
		logger.debug("Creouse a factura correctamente: "+id_factura);

		Timestamp data = Util.obterTimestampActual();
		//Engadimos o comentario de que se engadiu unha factura
		ComentarioDAO.crear(id_incidencia, user.getNome(), Comentario.ACCION_INSERTAR_FACTURA, Comentario.COMENTARIO_ADMINISTRACION, id_factura, data);
		
		if(!errFile)
			ret = Mensaxe.CREARFACTURA_OK.toJSONMensaxe();
		
		ret.put("Factura", factura);
		
		return ret;
	}

	public JSONObject<String, Object> modificar(Usuario user, String id_factura, String comentarios,
			InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		String nome_ficheiro = null;
		String tipo_ficheiro = null;
		String dir_ficheiro = null;
		boolean errFile = false;
		
		if(!user.podeEditarFactura()) {
			return Error.MODIFICARFACTURA_SENPERMISOS2.toJSONError();
		}
		
		ArrayList<Incidencia> incL = IncidenciaDAO.get(0, 0, 0, null, null, null, null, null, null, id_factura, null, null, null, 0, 0);
		if(incL.size()!=1) {
			return Error.OBTERFACTURA_NONEXISTE.toJSONError();
		}
		Incidencia inc = incL.get(0);
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.MODIFESTADOINCIDENCIA_SENPERMISOS1.toJSONError();
		}
				
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			nome_ficheiro = fileDetail.getFileName();
			tipo_ficheiro = FilenameUtils.getExtension(nome_ficheiro);
			dir_ficheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.RUTA_FACTURAS, inc.getData());
		}
		Factura fact = FacturaDAO.getById(id_factura);
		if(fact == null) {
			return Error.OBTERFACTURA_NONEXISTE.toJSONError();
		}
				
		boolean fret = FacturaDAO.modificar(id_factura, dir_ficheiro+'/'+nome_ficheiro, tipo_ficheiro, comentarios); //Creamos a factura na táboa.
		if(!fret) {
			return Error.MODIFICARFACTURA_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			if(fact.getRuta_ficheiro() != null && !fact.getRuta_ficheiro().equals("")) {//Se existe boramos o ficheiro antigo.
				if(!XestionFicheiros.borrar(fact.getRuta_ficheiro())) {
					logger.error("Erro o eliminar o ficheiro: "+fact.getRuta_ficheiro());
				}
			}
			
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dir_ficheiro, nome_ficheiro);
			if(path == null ) {
				ret = Error.CREARFACTURA_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Factura factura = FacturaDAO.getById(id_factura); //Buscamos a factura que acabamos de insertar
		if(factura == null) {
			return Error.MODIFICARFACTURA_ERRORDB.toJSONError();
		}
		
		logger.debug("Modificada a factura correctamente: "+id_factura);

		Timestamp data = Util.obterTimestampActual();
		//Engadimos o comentario de que se engadiu unha factura
		ComentarioDAO.crear(inc.getId(), user.getNome(), Comentario.ACCION_MODIFICAR_FACTURA, Comentario.COMENTARIO_ADMINISTRACION, id_factura, data);
		
		if(!errFile)
			ret = Mensaxe.MODIFICARFACTURA_OK.toJSONMensaxe();
		
		ret.put("Factura", factura);
		
		return ret;
	}
	

}
