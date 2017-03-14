package cburuel.stx.mx.testingws.Actividades;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cburuel.stx.mx.testingws.Adaptador.AdaptadorParametros;
import cburuel.stx.mx.testingws.Comunicacion.Comunicacion;
import cburuel.stx.mx.testingws.Modelos.Parametros;
import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Utilidades.ACTIONS;
import cburuel.stx.mx.testingws.Utilidades.Constant;
import cz.msebera.android.httpclient.Header;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class ActividadPruebaWs
	extends AppCompatActivity
	implements View.OnClickListener
{
	ImageView o_IV_AGREGAR;
	Button o_BTN_ENVIAR;
	Spinner o_SP_ACCIONES;
	RecyclerView o_RV_PARAMETROS;
	AdaptadorParametros o_ADAPTADOR;
	ProgressDialog o_PROGRESO;
	List<Parametros> a_PARAMETROS = new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//Ocultar teclado
		getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
		);
		setContentView(R.layout.activity_prueba_ws);

		String e_TITULO = "";
		if( Constant.e_EXT_ELEGIDO.equals(Constant.e_EXT1) )
		{
			ACTIONS.iniciarEXT1();
			e_TITULO = "EXT1";
		}
		else if( Constant.e_EXT_ELEGIDO.equals(Constant.e_EXT2) )
		{
			ACTIONS.iniciarEXT2();
			e_TITULO = "EXT2";
		}
		//Asignar el titulo
		setTitle( e_TITULO + " en prueba");

		o_SP_ACCIONES = (Spinner) findViewById(R.id.spAcciones);
		o_IV_AGREGAR = (ImageView) findViewById(R.id.btnAgregar);
		o_BTN_ENVIAR = (Button) findViewById(R.id.btnConsultar);
		o_RV_PARAMETROS = (RecyclerView) findViewById(R.id.rvParametros);

		o_ADAPTADOR = new AdaptadorParametros(a_PARAMETROS);
		o_RV_PARAMETROS.setLayoutManager(new LinearLayoutManager(this));
		o_RV_PARAMETROS.setAdapter(o_ADAPTADOR);

		ArrayAdapter<String> o_ADAPTADOR =
			new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ACTIONS.a_WS_EXTS);
		o_SP_ACCIONES.setAdapter(o_ADAPTADOR);

		o_IV_AGREGAR.setOnClickListener(this);
		o_BTN_ENVIAR.setOnClickListener(this);
	}

	private void hacerLLamado()
	{
		o_PROGRESO = ProgressDialog.show(this,"", "Probando WS", false);
		//Encriptacion de datos
		Map<String, String> m_DATOS_PAYLOAD = new HashMap<>();
		m_DATOS_PAYLOAD.put("JWT", Comunicacion.obtenerJWT(this));
		//Recorrido
		for(int e_INDEX = 0; e_INDEX < a_PARAMETROS.size(); e_INDEX++)
		{
			Parametros o_PARAMETRO = a_PARAMETROS.get(e_INDEX);
			m_DATOS_PAYLOAD.put(o_PARAMETRO.getE_TEXTO_1(), o_PARAMETRO.getE_TEXTO_2());
		}

		JSONObject o_JSON_OBJECT = new JSONObject(m_DATOS_PAYLOAD);
		String o_JSON_PARSE = Comunicacion.encriptarLlavePublica(o_JSON_OBJECT, this);
		//Elementos de envio
		RequestParams o_PARAMETROS = new RequestParams();
		o_PARAMETROS.put("FINGERPRINT", Comunicacion.crearFingerprint(this));
		o_PARAMETROS.put("PAYLOAD", o_JSON_PARSE);

		String e_RUTA_MODULO = o_SP_ACCIONES.getSelectedItem().toString();
		AsyncHttpClient o_CLIENTE = new AsyncHttpClient();
		o_CLIENTE.post(
			Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT_ELEGIDO + e_RUTA_MODULO,
			o_PARAMETROS, new JsonHttpResponseHandler()
			{
				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject o_RESPUESTA)
				{
					o_PROGRESO.dismiss();
					Intent o_INTENT = new Intent(ActividadPruebaWs.this, ActividadResultado.class);
					o_INTENT.putExtra("RESULTADO", o_RESPUESTA.toString());
					startActivity(o_INTENT);
				}

				@Override
				public void onSuccess(int statusCode, Header[] a_CABECERA, JSONObject o_RESPUESTA)
				{
					o_PROGRESO.dismiss();
					String e_INFORMACION = "";
					try
					{
						String e_RESPUESTA = o_RESPUESTA.getString("RETURN");
						if( o_RESPUESTA.getBoolean("success") )
						{
							JSONObject o_DESCRY = Comunicacion.desencriptar_llave_publica(e_RESPUESTA, ActividadPruebaWs.this);

							if( o_DESCRY != null )
							{
								e_INFORMACION = o_DESCRY.toString();
							}
						}
						else
						{
							JSONObject o_ERROR = new JSONObject(e_RESPUESTA);
							e_INFORMACION = o_ERROR.getString("ERROR_CODE");
						}
						//Envio de información de nueva ventana
						Intent o_INTENT = new Intent(ActividadPruebaWs.this, ActividadResultado.class);
						o_INTENT.putExtra("RESULTADO", e_INFORMACION);
						startActivity(o_INTENT);
					}
					catch(Exception o_EX)
					{
						o_EX.printStackTrace();
					}
				}
			}
		);
	}

	@Override
	public void onClick(View o_VISTA)
	{
		switch( o_VISTA.getId() )
		{
			case R.id.btnAgregar:
				a_PARAMETROS.add( new Parametros("Llave", "Valor") );
				o_ADAPTADOR.notifyDataSetChanged();
				break;
			case R.id.btnConsultar:
				hacerLLamado();
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu o_MENU)
	{
		getMenuInflater().inflate(R.menu.cerrar_sesion, o_MENU);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem o_ITEM)
	{
		switch(o_ITEM.getItemId())
		{
			case R.id.itemCerrar:
				new AlertDialog.Builder(this)
					.setTitle("Cerrar Sesión")
					.setMessage("¿Está seguro que desea cerrar sesión?")
					.setPositiveButton("Si", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							Comunicacion.cerrarSesion(ActividadPruebaWs.this);
							startActivity(new Intent(ActividadPruebaWs.this, ActividadElegirEXT.class));
							finish();
						}
					})
					.setNegativeButton("No", null)
					.show();
				break;
		}
		return true;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ACTIONS.a_WS_EXTS.clear();
	}
}