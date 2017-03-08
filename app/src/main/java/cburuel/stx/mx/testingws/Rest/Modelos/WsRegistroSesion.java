package cburuel.stx.mx.testingws.Rest.Modelos;

import cburuel.stx.mx.testingws.Utilidades.Constant;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public interface WsRegistroSesion
{
	//region obtener llave
	interface WsObtenerLlaveEXT1
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT1 + Constant.e_OBTENER_LLAVE)
		Call<Responses> obtenerLlave(@Body Request request);
	}

	interface WsObtenerLlaveEXT2
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT2 + Constant.e_OBTENER_LLAVE)
		Call<Responses> obtenerLlave(@Body Request request);
	}
	//endregion
	//region inicio de sesion
	interface WsIniciarSesionEXT1
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT1 + Constant.e_INICIAR_SESION)
		Call<Responses> iniciarSesion(@Body Request request);
	}

	interface WsIniciarSesionEXT2
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT2 + Constant.e_INICIAR_SESION)
		Call<Responses> iniciarSesion(@Body Request request);
	}
	//endregion
	//region validar vinculacion
	interface WsValidarVinculacionEXT1
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT1 + Constant.e_VALIDAR_VINCULACION)
		Call<Responses> validarVinculacion(@Body Request request);
	}

	interface WsValidarVinculacionEXT2
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT2 + Constant.e_VALIDAR_VINCULACION)
		Call<Responses> validarVinculacion(@Body Request request);
	}
	//endregion
	//region vincular
	interface WsVincularEXT1
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT1 + Constant.e_VINCULAR)
		Call<Responses> vincular(@Body Request request);
	}

	interface WsVincularEXT2
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT2 + Constant.e_VINCULAR)
		Call<Responses> vincular(@Body Request request);
	}
	//endregion
}