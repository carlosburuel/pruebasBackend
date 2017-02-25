package cburuel.stx.mx.testingws.Servicios;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import cburuel.stx.mx.testingws.Componentes.WebServiceResponse;
import cburuel.stx.mx.testingws.Rest.Common.BaseRestEventItem;
import cburuel.stx.mx.testingws.Rest.Common.RestStatus;
import cburuel.stx.mx.testingws.Rest.Modelos.Request;
import cburuel.stx.mx.testingws.Rest.Modelos.Responses;
import cburuel.stx.mx.testingws.Rest.Modelos.WsRegistroSesion;
import cburuel.stx.mx.testingws.Utilidades.Constant;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class BackendService
	extends IntentService
{
	//region Campos clase
	public static final String EXTRA_REQUEST = "cburuel.stx.mx.testingws.EXTRA_REQUEST";
	private static final String EXTRA_REQUEST_CODE = "cburuel.stx.mx.testingws.EXTRA_REQUEST_CODE";
	private long e_CODIGO_RESPUESTA;
	//endregion

	public BackendService()
	{
		super("BackendService");
	}

	/**
	 * Accion responsable del llamado
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @param e_ACCION Accion identificadora
	 * @return Intent con una accion asignada
	 */
	private static Intent getBaseIntent(Context o_CONTEXTO, String e_ACCION)
	{
		//Se inicia un Intent
		Intent o_INTENT = new Intent(o_CONTEXTO, BackendService.class);
		//Se ingrese la accion al intent
		o_INTENT.setAction(e_ACCION);
		return o_INTENT;
	}

	/**
	 * Inicia el servicio
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @param o_INTENT Intent a ejecutar
	 */
	private static void init(Context o_CONTEXTO, Intent o_INTENT)
	{
		o_CONTEXTO.startService(o_INTENT);
	}

	/**
	 * Envio de informacion para hacer la peticion post
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @param o_REQUEST Objeto con la informacion a consultar
	 */
	public static void keyService(Context o_CONTEXTO, Request o_REQUEST, String e_WEBSERVICE)
	{
		//Contexto y el webservices como referencia a llamar
		Intent o_INTENT = getBaseIntent(o_CONTEXTO, e_WEBSERVICE);
		o_INTENT.putExtra(EXTRA_REQUEST, o_REQUEST);
		o_INTENT.putExtra(EXTRA_REQUEST_CODE, -1);
		init(o_CONTEXTO, o_INTENT);
	}

	@Override
	protected void onHandleIntent(Intent o_INTENT)
	{
		//Recuperar el codigo de respuesta
		e_CODIGO_RESPUESTA = o_INTENT.getLongExtra(EXTRA_REQUEST_CODE, -1);
		switch( o_INTENT.getAction() )
		{
			//Procesamos el llamado de WB de llave y enviamos el intent
			case Constant.e_OBTENER_LLAVE:
				canalLlaveServicio(o_INTENT);
				break;
		}
	}

	private void canalLlaveServicio(Intent o_INTENT)
	{
		RestEventKey restEventKey = null;
		boolean e_ES_CORRECTO = false;
		//Extraccion de request a realizar por clave EXTRA_REQUEST
		Request o_PETICION = (Request) o_INTENT.getSerializableExtra(EXTRA_REQUEST);
		//Verificar la existecia del request
		if( o_PETICION != null )
		{
			WsRegistroSesion.WsObtenerLlave o_ObtenerLlave
				= getRetrofit(false).create(WsRegistroSesion.WsObtenerLlave.class);
			Call<Responses> o_LLAMADA = o_ObtenerLlave.obtenerLlave(o_PETICION);
			try
			{
				//Representacion de respuesta
				Response<Responses> response = o_LLAMADA.execute();
				//Validar respuesta satisfactoria, si hay cuerpo de respuesta y el codigo de respuesta
				if( response.isSuccessful() && response.body() != null && validarCodigoRespuesta(response.code()) )
				{
					//Lectura de cuerpo de respuesta
					Responses respuestaLLave = response.body();
					restEventKey = new RestEventKey(RestStatus.Obtained, respuestaLLave);
					e_ES_CORRECTO = true;
				}
			}
			catch(Exception o_EX)
			{
				o_EX.printStackTrace();
			}
		}
		else
		{
			System.out.println("E_LLAVE: ERROR AL CONSULTAR LLAVE");
		}
		//Verificar que fue correcta la respuesta
		if( !e_ES_CORRECTO )
		{
			restEventKey = new RestEventKey(RestStatus.Failed, null);
		}
		//Enviar el codigo de respuesta
		restEventKey.setRequestCode(e_CODIGO_RESPUESTA);
		//Notificar envento a eventbus
		sendEvent(restEventKey);
	}

	//Notificaciones de tipo post a EventBus
	private void sendEvent(Object object)
	{
		EventBus.getDefault().post(object);
	}

	/**
	 * Clase para manejo de respuestas
	 */
	public class RestEventKey extends BaseRestEventItem<Responses>
	{
		RestEventKey(RestStatus status, Responses item)
		{
			super(status, item);
		}
	}

	private Retrofit getRetrofit(boolean excluirCamposSinExponerAnotaciones )
	{
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		//Para no reportar logs a monitor
		interceptor.setLevel( HttpLoggingInterceptor.Level.BODY );
		OkHttpClient o_CLIENTE = new OkHttpClient.Builder()
			.addInterceptor(interceptor)
			.readTimeout(15, TimeUnit.SECONDS)
			.connectTimeout(15, TimeUnit.SECONDS)
			.build();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Responses.class, new WebServiceResponse());
		Gson gson = gsonBuilder.create();

		return new Retrofit.Builder()
			.baseUrl(Constant.e_URL_BASE)
			.addConverterFactory(GsonConverterFactory.create(
				excluirCamposSinExponerAnotaciones ?
					new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create() : gson))
			.client(o_CLIENTE)
			.build();
	}

	//Validar el codigo de respuesta
	private boolean validarCodigoRespuesta(int e_CODIGO)
	{
		switch( e_CODIGO )
		{
			case Constant.OK:
			default:
				return true;
			case Constant.BAD_REQUEST:
			case Constant.NOT_FOUND:
			case Constant.UNAUTHORIZED:
			case Constant.INTERNAL_SERVER_ERROR:
			case Constant.SITE_DISABLE:
				return false;
		}
	}
}