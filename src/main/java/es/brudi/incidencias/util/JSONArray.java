package es.brudi.incidencias.util;

import java.util.ArrayList;
import java.util.List;

import es.brudi.incidencias.imaxes.Imaxe;

public class JSONArray<K> extends ArrayList<K> {

	private static final long serialVersionUID = -5380498550000808016L;

	public static Object getFromList(List<Imaxe> imaxes) {
		JSONArray<Object> jsonImaxes = new JSONArray<>();
		for(Imaxe i : imaxes)
			jsonImaxes.add(i.toJson());
		return jsonImaxes;
	}

}
