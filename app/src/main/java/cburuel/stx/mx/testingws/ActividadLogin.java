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
		setContentView(R.layout.activity_login);
		//Asignar el titulo
		setTitle(
			( Constant.e_EXT_ELEGIDO.equals(Constant.e_EXT1) ? "EXT1" : "EXT2" )
			+ " en prueba");

		//Registrar EventBus
		verificarPermisos();
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
				o_DIALOGO_PROGRESO.dismiss();
			}
		}
	}

	/**
	 * Revision de permisos para solicitar datos de telefono
	 */
	private void verificarPermisos()
	{
		o_DIALOGO_PROGRESO = ProgressDialog.show(this, "", "Leyendo información");
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
			//Consulta de llave publica
			Request o_PETICION = new Request(Comunicacion.obtenerFingerprint(this), "");
			//Envio de la informacion en la consulta del servicio, donde se inyecta la accion
			BackendService.keyService(this, o_PETICION, Constant.e_OBTENER_LLAVE_EXT);
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
							//Alerta mostrada al negar permisos
							new AlertDialog.Builder(this)
								.setTitle("Permisos denegados")
								.setMessage("No se cuenta con los permisos necesarios")
								//agregar boton de respuesta posisiva
								.setPositiveButton("Ok", new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										//Encargada del cierre de aplicacion
										finishAffinity();
									}
								}).show();
						}
					}
					//proceso de revision
					procesoRevision();
				}
				break;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onStop()
	{
		EventBus.getDefault().unregister(this);
		super.onStop();
	}
}