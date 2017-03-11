package cburuel.stx.mx.testingws.Utilidades;

import java.util.ArrayList;

/**
 * @author Carlos Buruel
 * @since 09/03/2017
 */

public class ACTIONS
{
//	public static final String e_EXT2 = "PYXTER_TOKENCASH_EXT2_EXT:C5A.EXT.";
	public static final ArrayList<String> a_WS_EXTS = new ArrayList<>();

	public static void iniciarEXT2()
	{
//		REGISTRO_SESION
		String e_REGISTRO_SESION = "REGISTRO_SESION.";
		a_WS_EXTS.add(e_REGISTRO_SESION + "CONSULTAR_PERFIL");
		a_WS_EXTS.add(e_REGISTRO_SESION + "GUARDAR_PERFIL");
		a_WS_EXTS.add(e_REGISTRO_SESION + "CAMBIAR_NIP");
		a_WS_EXTS.add(e_REGISTRO_SESION + "CAMBIAR_CONTRASENA");
//		FORMA_PAGO
		String e_FORMA_PAGO = "FORMA_PAGO.";
		a_WS_EXTS.add(e_FORMA_PAGO + "CREAR_TOKEN");
		a_WS_EXTS.add(e_FORMA_PAGO + "OBTENER_GIFTCARDS_VENTA");
		a_WS_EXTS.add(e_FORMA_PAGO + "ABONO_ESTABLECIMIENTO");
		a_WS_EXTS.add(e_FORMA_PAGO + "OBTENER_DETALLE_TOKEN");
		a_WS_EXTS.add(e_FORMA_PAGO + "OBTENER_ULTIMOS_TOKENS");
		a_WS_EXTS.add(e_FORMA_PAGO + "CERRAR_TOKEN");
		a_WS_EXTS.add(e_FORMA_PAGO + "CANCELAR_TOKEN");
		a_WS_EXTS.add(e_FORMA_PAGO + "CAMBIAR_TOKEN");
		a_WS_EXTS.add(e_FORMA_PAGO + "OBTENER_RECOMPENSAS_APLICABLES");
		a_WS_EXTS.add(e_FORMA_PAGO + "OBTENER_CONFIGURACION_REFERENCIA");
		a_WS_EXTS.add(e_FORMA_PAGO + "BUSCAR_TOKEN");
//		OTROS
		String e_OTROS = "OTROS.";
		a_WS_EXTS.add(e_OTROS + "OBTENER_VENDEDORES");
		a_WS_EXTS.add(e_OTROS + "GUARDAR_DEPOSITO_BANCARIO");
		a_WS_EXTS.add(e_OTROS +"ULTIMOS_DEPOSITOS_BANCARIOS");
		a_WS_EXTS.add(e_OTROS +"ACTUALIZAR_CONFIGURACION");
		a_WS_EXTS.add(e_OTROS +"CONSULTAR_BANCOS");
	}

	public static void iniciarEXT1()
	{

	}
}