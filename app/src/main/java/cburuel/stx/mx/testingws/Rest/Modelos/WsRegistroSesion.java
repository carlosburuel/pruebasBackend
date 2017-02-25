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
	interface WsObtenerLlave
	{
		@POST(Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_OBTENER_LLAVE)
		Call<Responses> obtenerLlave(@Body Request request);
	}
}