package cburuel.stx.mx.testingws;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cburuel.stx.mx.testingws.Comunicacion.Comunicacion;
import cburuel.stx.mx.testingws.Rest.Modelos.Request;
import cburuel.stx.mx.testingws.Rest.Modelos.Responses;
import cburuel.stx.mx.testingws.Servicios.BackendService;
import cburuel.stx.mx.testingws.Utilidades.Constant;
import cburuel.stx.mx.testingws.Utilidades.Utilidad;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class ActividadLogin
	extends AppCompatActivity
	implements View.OnClickListener
{
	private ProgressDialog o_DIALOGO_PROGRESO;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_login);

		//Registrar EventBus
		EventBus.getDefault().register(this);
		verificar_permisos();
	}

	/**
	 * Clic sobre una vista
	 * @param o_VISTA Vista que lanzo el evento
	 */
	@Override
	public void onClick(View o_VISTA)
	{

	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(BackendService.RestEventKey o_EVENT_KEY)
	{
		//Recuperamos el resultado de respuesta
		Responses restEventItem = o_EVENT_KEY.getItem();
		//revisar que exista respuesta
		if( restEventItem != null )
		{
			if( restEventItem.isStatus() )
			{
				//Recuperar llave
				String e_LLAVE = restEventItem.getRespuesta();
				//Almacenar llave
				if( !Comunicacion.guardarLlavePublica(e_LLAVE, this) )
				{
					Utilidad.mostrar_mensaje(this, R.string.no_permisos_escritura);
					return;
				}
				EventBus.getDefault().unregister(this);
				o_DIALOGO_PROGRESO.dismiss();
			}
		}
	}

	/**
	 * Revision de permisos para solicitar datos de telefono
	 */
	private void verificar_permisos()
	{
		o_DIALOGO_PROGRESO = ProgressDialog.show(this, "", "Leyendo informacion");
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != 0)
		{
			ActivityCompat.requestPermissions(this,
				new String[]
					{
						Manifest.permission.ACCESS_WIFI_STATE,
						Manifest.permission.ACCESS_NETWORK_STATE,
						Manifest.permission.INTERNET,
						Manifest.permission.READ_PHONE_STATE
					},
				1);
		}
		else
		{
			procesoRevision();
		}
	}

	private void procesoRevision()
	{
		if( Utilidad.verificaConexion(this) )
		{
			if( !Comunicacion.existeLlavePublica(this) )
			{
				//Consulta de llave publica
				Request o_PETICION = new Request(Comunicacion.obtenerFingerprint(this), "");
				//Envio de la informacion en la consulta del servicio, donde se inyecta la accion
				BackendService.keyService(this, o_PETICION, Constant.e_OBTENER_LLAVE);
				return;
			}
			//Sacar de registro EventBus
			EventBus.getDefault().unregister(this);
			o_DIALOGO_PROGRESO.dismiss();
		}
		else
		{
			o_DIALOGO_PROGRESO.dismiss();
			new AlertDialog.Builder(this)
				.setTitle("Error de comunicación")
				.setCancelable(false)
				.setMessage("Comprueba la conexión a internet")
				.setPositiveButton("REINTENTAR", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int i)
					{
						procesoRevision();
					}
				})
				.setNegativeButton("CERRAR", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int i)
					{
						//Dar por finalizada la app
						finishAffinity();
					}
				})
				.show();
		}
	}

	@Override
	public void onRequestPermissionsResult(int e_RESPUESTA, @NonNull String[] a_PERMISOS, @NonNull int[] a_RESULTADO)
	{
		switch( e_RESPUESTA )
		{
			case 1:
				//existencia de permisos aceptados
				if( a_PERMISOS.length > 0 )
				{
					for ( int e_permiso : a_RESULTADO )
					{
						//es diferente a permisos aceptado
						if( e_permiso != PackageManager.PERMISSION_GRANTED)
						{
							//llamar cierra de sesion
//							llamada_cierre_session();
						}
					}
					//proceso de revision
					procesoRevision();
				}
				break;
		}
	}
}