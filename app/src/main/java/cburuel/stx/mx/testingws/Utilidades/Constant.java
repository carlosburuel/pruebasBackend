package cburuel.stx.mx.testingws.Utilidades;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class Constant
{
	//Status Wed Services
	public static final int OK = 200;
	public static final int BAD_REQUEST = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int SITE_DISABLE = 403;
	public static final int NOT_FOUND = 404;
	public static final int INTERNAL_SERVER_ERROR = 500;

	//Rutas principales
	public static final String e_URL_BASE = "http://narvi.klayware.com/";
//	public static final String e_URL_BASE = "http://192.168.1.34:90/";
	public static final String e_URL_PATH = "index.php?ACTION=";
	//Variantes de EXT's
	public static final String e_EXT1 = "PYXTER_TOKENCASH_EXT1_EXT:C5A.EXT.";
	public static final String e_EXT2 = "PYXTER_TOKENCASH_EXT2_EXT:C5A.EXT.";
	public static String e_EXT_ELEGIDO = "";
	public static String e_ACCION_ELEGIDA = "";

	//Webservices dependiendo el EXT elegido
	public static final String e_OBTENER_LLAVE = "REGISTRO_SESION.OBTENER_LLAVE";
	public static final String e_INICIAR_SESION = "REGISTRO_SESION.INICIAR_SESION";
	public static final String e_VALIDAR_VINCULACION = "REGISTRO_SESION.VALIDAR_VINCULACION";
	public static final String e_VINCULAR = "REGISTRO_SESION.VINCULAR";
}