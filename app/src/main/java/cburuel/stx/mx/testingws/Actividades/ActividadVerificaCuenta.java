package cburuel.stx.mx.testingws.Actividades;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cburuel.stx.mx.testingws.Comunicacion.Comunicacion;
import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Rest.Modelos.Request;
import cburuel.stx.mx.testingws.Rest.Modelos.Responses;
import cburuel.stx.mx.testingws.Servicios.BackendService;
import cburuel.stx.mx.testingws.Utilidades.Constant;
import cburuel.stx.mx.testingws.Utilidades.Utilidad;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class ActividadVerificaCuenta
	extends AppCompatActivity
	implements View.OnClickListener
{
	private ProgressDialog o_DIALOGO_PROGRESO;

	EditText o_ET_CODIGO;
	Button o_BTN_ENVIAR;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verifica_cuenta);
		//Asignar el titulo
		setTitle(
			( Constant.e_EXT_ELEGIDO.equals(Constant.e_EXT1) ? "EXT1" : "EXT2" )
				+ " en prueba");

		o_ET_CODIGO = (EditText) findViewById(R.id.editTextVerifica);
		o_BTN_ENVIAR = (Button) findViewById(R.id.btnEnviar);

		o_BTN_ENVIAR.setOnClickListener(this);
		o_ET_CODIGO.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView o_VISTA, int e_ACCION, KeyEvent o_EVENTO)
			{
				vincular();
				return false;
			}
		});

		validarVinculacion();
	}

	private void validarVinculacion()
	{
		o_DIALOGO_PROGRESO = ProgressDialog.show(this, "", "Validando cuenta");
		//Encriptacion de datos
		Map<String, String> m_DATOS_PAYLOAD = new HashMap<>();
		m_DATOS_PAYLOAD.put("JWT", Comunicacion.obtenerJWT(this));

		JSONObject o_JSON_OBJECT = new JSONObject(m_DATOS_PAYLOAD);
		String o_JSON_PARSE = Comunicacion.encriptarLlavePublica(o_JSON_OBJECT, this);
		//Consulta de llave publica
		Request o_PETICION = new Request(Comunicacion.crearFingerprint(this), o_JSON_PARSE);
		//Envio de la informacion en la consulta del servicio, donde se inyecta la accion
		BackendService.keyService(this, o_PETICION,
			Constant.e_ACCION_ELEGIDA = Constant.e_VALIDAR_VINCULACION);
	}

	private void vincular()
	{
		String e_CODIGO_VINCULACION = o_ET_CODIGO.getText().toString();
		if( "".equals(e_CODIGO_VINCULACION) )
		{
			Utilidad.mostrar_mensaje(this, "Ingrese el código de verificación");
			return;
		}

		if( e_CODIGO_VINCULACION.length() != 6 )
		{
			Utilidad.mostrar_mensaje(this, "El código de verificación debe ser de 6 dígitos");
			return;
		}

		o_DIALOGO_PROGRESO = ProgressDialog.show(this, "", "Vinculando cuenta");
		//Encriptacion de datos
		Map<String, String> m_DATOS_PAYLOAD = new HashMap<>();
		m_DATOS_PAYLOAD.put("JWT", Comunicacion.obtenerJWT(this));
		m_DATOS_PAYLOAD.put("CODIGO_VINCULACION", e_CODIGO_VINCULACION);

		JSONObject o_JSON_OBJECT = new JSONObject(m_DATOS_PAYLOAD);
		String o_JSON_PARSE = Comunicacion.encriptarLlavePublica(o_JSON_OBJECT, this);
		//Consulta de llave publica
		Request o_PETICION = new Request(Comunicacion.crearFingerprint(this), o_JSON_PARSE);
		//Envio de la informacion en la consulta del servicio, donde se inyecta la accion
		BackendService.keyService(this, o_PETICION,
			Constant.e_ACCION_ELEGIDA = Constant.e_VINCULAR);
	}

	//Respuesta del WB es leida aqui
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(BackendService.RestEventKey o_EVENT_KEY)
	{
		//Recuperamos el resultado de respuesta
		Responses restEventItem = o_EVENT_KEY.getItem();
		//Revisamos si es existente y fue correcta la respuesta
		if (restEventItem != null)
		{
			if( restEventItem.isStatus() )
			{
				//Decodificar respuesta
				JSONObject o_DESCRY = Comunicacion.desencriptar_llave_publica(restEventItem.getRespuesta(), this);
				try
				{
					switch(Constant.e_ACCION_ELEGIDA)
					{
						case Constant.e_VALIDAR_VINCULACION:
							if( o_DESCRY != null && o_DESCRY.getString("RESPUESTA").equals("OK") )
							{
								Utilidad.mostrar_mensaje(getApplicationContext(), "Se ha enviado tu código de verificación");
							}
							break;
						case Constant.e_VINCULAR:
							//Verificar la existencia del JSON y respuesta
							if( o_DESCRY != null && o_DESCRY.has("RESPUESTA") )
							{
								if( o_DESCRY.getString("RESPUESTA").equals("OK") )
								{
									Comunicacion.save_flag(this);
									Intent o_INTENT = new Intent(this, ActividadPruebaWs.class);
									o_INTENT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
									startActivity(o_INTENT);
								}
							}
							break;
					}
				}
				catch(Exception o_EX)
				{
					o_EX.printStackTrace();
				}
			}
			//Al ser negativa la respuesta
			else
			{
				Utilidad.mostrar_mensaje(this, restEventItem.getRespuesta());
			}

		}
		o_DIALOGO_PROGRESO.dismiss();
	}


	@Override
	public void onClick(View o_VISTA)
	{
		switch(o_VISTA.getId())
		{
			case R.id.btnEnviar:
				vincular();
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