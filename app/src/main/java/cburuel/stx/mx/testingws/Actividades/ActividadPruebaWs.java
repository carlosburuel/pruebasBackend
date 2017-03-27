package cburuel.stx.mx.testingws.Actividades;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cburuel.stx.mx.testingws.Comunicacion.Comunicacion;
import cburuel.stx.mx.testingws.Modelos.Parametros;
import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Utilidades.ACTIONS;
import cburuel.stx.mx.testingws.Utilidades.Constant;
import cburuel.stx.mx.testingws.Utilidades.Utilidad;
import cz.msebera.android.httpclient.Header;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class ActividadPruebaWs
	extends AppCompatActivity
	implements View.OnClickListener
{
	ImageView o_IV_AGREGAR, o_IV_JSON;
	Button o_BTN_ENVIAR;
	Spinner o_SP_ACCIONES;
	LinearLayout o_LAYOUT;
	ProgressDialog o_PROGRESO;
	JSONArray a_DATOS_PRUEBA;

	final int REQUEST_EXTERNAL_STORAGE = 66;
	String[] PERMISSIONS_STORAGE =
		{
			Manifest.permission.READ_EXTERNAL_STORAGE,
		};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//Ocultar teclado
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_prueba_ws);

		String e_TITULO = "";
		switch(Constant.e_EXT_ELEGIDO)
		{
			case Constant.e_EXT1:
				ACTIONS.iniciarEXT1(this);
				e_TITULO = "EXT1";
				break;
			case Constant.e_EXT2:
				ACTIONS.iniciarEXT2(this);
				e_TITULO = "EXT2";
				break;
		}
		//Asignar el titulo
		setTitle( e_TITULO + " en prueba");

		o_SP_ACCIONES = (Spinner) findViewById(R.id.spAcciones);
		o_IV_AGREGAR = (ImageView) findViewById(R.id.btnAgregar);
		o_IV_JSON = (ImageView) findViewById(R.id.btnJSON);
		o_BTN_ENVIAR = (Button) findViewById(R.id.btnConsultar);

		o_LAYOUT = (LinearLayout) findViewById(R.id.llParametros);

		ArrayAdapter<String> o_ADAPTADOR =
			new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ACTIONS.a_WS_EXTS);
		o_SP_ACCIONES.setAdapter(o_ADAPTADOR);

		o_IV_AGREGAR.setOnClickListener(this);
		o_BTN_ENVIAR.setOnClickListener(this);
		o_IV_JSON.setOnClickListener(this);
	}

	/**
	 * Llamado de WS con parametros precargados para pruebas
	 */
	private void hacerLLamado()
	{
		//Reiniciar archivo bitacora
		Utilidad.escribirBitacora(this, "Inicio de log: \n", false);
		o_PROGRESO = ProgressDialog.show(this,"", "Probando WS", false);
		//Encriptacion de datos
		final Map<String, String> m_DATOS_PAYLOAD = new HashMap<>();
		String e_JWT = Comunicacion.obtenerJWT(this);
		//Se agrega si existe el JWT
		if(!"".equals(e_JWT))
		{
			m_DATOS_PAYLOAD.put("JWT", e_JWT);
		}

		//Iteracion y cardado de contenido de archivo
		if( a_DATOS_PRUEBA == null )
		{
			Utilidad.mostrar_mensaje(this, "No se encontro información");
			o_PROGRESO.dismiss();
			return;
		}
		for( int e_INDEX_JSON = 0; e_INDEX_JSON < a_DATOS_PRUEBA.length(); e_INDEX_JSON++ )
		{
			try
			{
				//Creacion de JSON y decodeo del mismo
				JSONObject o_JSON = a_DATOS_PRUEBA.getJSONObject(e_INDEX_JSON);
				if( o_JSON.has("ACTION") && o_JSON.has("PARAMETROS") && o_JSON.has("RESULTADO") )
				{
					//Extracccion de valores escalares
					String e_ACCION = o_JSON.getString("ACTION");
					//Resultado esperado por cada llamada
					final String e_RESULTADO_ESPERADO = o_JSON.getString("RESULTADO");
					//Extraccion de arreglo de parametros
					JSONArray a_PARAMETROS = new JSONArray(o_JSON.getString("PARAMETROS"));
					if( a_PARAMETROS.length() == 0 )
					{
						Utilidad.mostrar_mensaje(this, "JSON no válido");
						return;
					}
					o_LAYOUT.removeAllViews();
					for(int e_INDEX = 0; e_INDEX < a_PARAMETROS.length(); e_INDEX++)
					{
						JSONObject o_JSON_ITEM = a_PARAMETROS.getJSONObject(e_INDEX);
						//Extraccion
						Iterator o_ITERADOR = o_JSON_ITEM.keys();
						while( o_ITERADOR.hasNext() )
						{
							String e_LLAVE = (String) o_ITERADOR.next();
							String e_VALOR = o_JSON_ITEM.getString(e_LLAVE);

							Parametros o_PARAMETRO = new Parametros("Llave", "Valor");
							o_PARAMETRO.setE_TEXTO_1(e_LLAVE);
							o_PARAMETRO.setE_TEXTO_2(e_VALOR);
							//Agregar fila
							agregarFila(e_LLAVE, e_VALOR);
						}
					}
					//elegir ACTION en listado
					int e_INDEX = ACTIONS.a_WS_EXTS.indexOf(e_ACCION);
					if( e_INDEX >= 0 )
					{
						o_SP_ACCIONES.setSelection(e_INDEX);

						//region Recorrido de contenido en hijos de layout
						for(int e_INDEX2 = 0; e_INDEX2 < o_LAYOUT.getChildCount(); e_INDEX2++)
						{
							ViewGroup a_GRUPO_HIJOS = (ViewGroup) o_LAYOUT.getChildAt(e_INDEX2);
							EditText o_ET_LLAVE = (EditText) a_GRUPO_HIJOS.getChildAt(0);
							EditText o_ET_VALOR = (EditText) a_GRUPO_HIJOS.getChildAt(1);
							String e_LLAVE = o_ET_LLAVE.getText().toString();
							String e_VALOR = o_ET_VALOR.getText().toString();

							m_DATOS_PAYLOAD.put(e_LLAVE, e_VALOR);
						}
						//endregion

						JSONObject o_JSON_OBJECT = new JSONObject(m_DATOS_PAYLOAD);
						String o_JSON_PARSE = Comunicacion.encriptarLlavePublica(o_JSON_OBJECT, this);
						//Elementos de envio
						RequestParams o_PARAMETROS = new RequestParams();
						o_PARAMETROS.put("FINGERPRINT", Comunicacion.crearFingerprint(this));
						o_PARAMETROS.put("PAYLOAD", o_JSON_PARSE);

						final String e_RUTA_MODULO = o_SP_ACCIONES.getSelectedItem().toString();
						AsyncHttpClient o_CLIENTE = new AsyncHttpClient();
						o_CLIENTE.post(
							Constant.e_URL_BASE + Constant.e_URL_PATH + Constant.e_EXT_ELEGIDO + e_RUTA_MODULO,
							o_PARAMETROS, new JsonHttpResponseHandler()
							{
								@Override
								public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject o_RESPUESTA)
								{
									o_PROGRESO.dismiss();
									//region Reporte de datos enviados y recibidos
									Utilidad.escribirBitacora(getBaseContext(), "Ruta modulo: " + e_RUTA_MODULO + "\n\n", true);
									Utilidad.escribirBitacora(getBaseContext(), "Datos enviados: " + m_DATOS_PAYLOAD.toString() + "\n", true);
									Utilidad.escribirBitacora(getBaseContext(), "En espera: " + e_RESULTADO_ESPERADO + "\n", true);
									//endregion
									Utilidad.escribirBitacora(getBaseContext(), "Respuesta no procesada \n", true);
								}

								@Override
								public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
								{
									o_PROGRESO.dismiss();
									//region Reporte de datos enviados y recibidos
									Utilidad.escribirBitacora(getBaseContext(), "Ruta modulo: " + e_RUTA_MODULO + "\n\n", true);
									Utilidad.escribirBitacora(getBaseContext(), "Datos enviados: " + m_DATOS_PAYLOAD.toString() + "\n", true);
									Utilidad.escribirBitacora(getBaseContext(), "En espera: " + e_RESULTADO_ESPERADO + "\n", true);
									//endregion
									Utilidad.escribirBitacora(getBaseContext(), "Respuesta no procesada \n", true);
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
										//region Reporte de datos enviados y recibidos
										Utilidad.escribirBitacora(getBaseContext(), "Ruta modulo: " + e_RUTA_MODULO + "\n\n", true);
										Utilidad.escribirBitacora(getBaseContext(), "Datos enviados: " + m_DATOS_PAYLOAD.toString() + "\n", true);
										Utilidad.escribirBitacora(getBaseContext(), "En espera: " + e_RESULTADO_ESPERADO + "\n", true);
										//endregion

										//Envio de información de nueva ventana
										if(e_RESULTADO_ESPERADO.equals(e_INFORMACION))
										{
											Utilidad.escribirBitacora(getBaseContext(), "Repuesta: " + e_INFORMACION + "\n", true);
										}
										else
										{
											Utilidad.escribirBitacora(getBaseContext(), "Repuesta: " + e_INFORMACION + "\n", true);
										}
									}
									catch(Exception o_EX)
									{
										o_EX.printStackTrace();
									}
								}
							}
						);
					}
					else
					{
						Utilidad.mostrar_mensaje(this, "JSON no válido");
					}
				}
				else
				{
					Utilidad.mostrar_mensaje(getBaseContext(), "Sin datos");
					o_PROGRESO.dismiss();
				}
			}
			catch(Exception o_EX)
			{
				Log.e("Error", o_EX.getMessage());
				o_PROGRESO.dismiss();
			}
		}


	}

	@Override
	public void onClick(View o_VISTA)
	{
		switch( o_VISTA.getId() )
		{
			case R.id.btnAgregar:
				agregarFila("", "");
				break;
			case R.id.btnConsultar:
				hacerLLamado();
				break;
			case R.id.btnJSON:
				verSelectorArchivo();
				break;
		}
	}

	/**
	 * agrega la fila para el ingreso de parametros en el envio de post
	 */
	private void agregarFila(String e_LLAVE, String e_VALOR)
	{
		//region Creacion de LinearLayout horizontal
		final LinearLayout o_LAYOUT_HIJO = new LinearLayout(this);
		o_LAYOUT_HIJO.setOrientation(LinearLayout.HORIZONTAL);
		o_LAYOUT_HIJO.setWeightSum(5f);
		LinearLayout.LayoutParams o_PARAMETROS = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		);
		o_LAYOUT_HIJO.setLayoutParams(o_PARAMETROS);

		//endregion
		Resources o_RECURSO = getResources();
		int e_PIXELES = (int)TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			5,
			o_RECURSO.getDisplayMetrics()
		);
		//region primer caja
		EditText o_VISTA = new EditText(this);
		LinearLayout.LayoutParams o_PARAMETRO = new LinearLayout.LayoutParams
			(
				0,LinearLayout.LayoutParams.MATCH_PARENT, 2f
			);
		o_PARAMETRO.rightMargin = e_PIXELES;
		o_VISTA.setLayoutParams(o_PARAMETRO);
		o_VISTA.setBackground(ContextCompat.getDrawable(this, R.drawable.borde_verde));
		o_VISTA.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		o_VISTA.setPadding(0, 0, 0, 0);
		o_VISTA.setHint("Llave");
		o_VISTA.setInputType(
			InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
		);
		if(!"".equals(e_LLAVE))
		{
			o_VISTA.setText(e_LLAVE);
		}
		o_LAYOUT_HIJO.addView(o_VISTA);
		//endregion
		//region segunda caja
		o_VISTA = new EditText(this);
		o_PARAMETRO = new LinearLayout.LayoutParams
			(
				0,LinearLayout.LayoutParams.MATCH_PARENT, 2f
			);
		o_PARAMETRO.leftMargin = e_PIXELES;
		o_VISTA.setLayoutParams(o_PARAMETRO);
		o_VISTA.setBackground(ContextCompat.getDrawable(this, R.drawable.borde_verde));
		o_VISTA.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		o_VISTA.setPadding(0, 0, 0, 0);
		o_VISTA.setHint("Valor");
		o_VISTA.setInputType(
			InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
		);
		if(!"".equals(e_VALOR))
		{
			o_VISTA.setText(e_VALOR);
		}
		o_LAYOUT_HIJO.addView(o_VISTA);
		//endregion
		ImageView o_IV = new ImageView(this);
		o_IV.setLayoutParams(new LinearLayout.LayoutParams(
			0,
			LinearLayout.LayoutParams.MATCH_PARENT, 1f)
		);
		o_IV.setScaleType(ImageView.ScaleType.CENTER);
		o_IV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_remove));
		//Evento click para remover padre
		o_IV.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View o_VISTA)
			{
				//Remover vista hijo del contenedor
				o_LAYOUT.removeView(o_LAYOUT_HIJO);
			}
		});
		o_LAYOUT_HIJO.addView(o_IV);

		o_LAYOUT.addView(o_LAYOUT_HIJO);
	}

	/**
	 * Procesar json partiendo de un archivo
	 */
	private void procesarJSON(String e_PATH)
	{
		StringBuilder stringBuilder = new StringBuilder();
		File o_FILE = new File(e_PATH);
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(o_FILE));
			String e_LINEA;
			//Verificamos que no sea vacio y si es posible guardamos la linea leida
			while( (e_LINEA = bufferedReader.readLine() ) != null)
			{
				stringBuilder.append(e_LINEA);
				stringBuilder.append('\n');
			}
			bufferedReader.close();
			//Almacenamiento
			a_DATOS_PRUEBA = new JSONArray( stringBuilder.toString() );
			if( a_DATOS_PRUEBA.length() == 0 )
			{
				Utilidad.mostrar_mensaje(this, "Archivo sin contenido");
			}
		}
		catch(Exception o_EX)
		{
			Log.e("ERROR", o_EX.getMessage());
		}
	}

	private void verSelectorArchivo()
	{
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != 0)
		{
			ActivityCompat.requestPermissions(
				this,
				PERMISSIONS_STORAGE,
				REQUEST_EXTERNAL_STORAGE
			);
			return;
		}

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("text/plain");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try
		{
			startActivityForResult(
				Intent.createChooser(intent, "Selecciona un archivo txt"), 77
			);
		}
		catch(Exception o_EX)
		{
			Utilidad.mostrar_mensaje(this, "Por favor instale un admin de archivos");
		}
	}

	@Override
	public void onRequestPermissionsResult(int e_RESPUESTA, @NonNull String[] a_PERMISOS, @NonNull int[] a_RESULTADO)
	{
		switch( e_RESPUESTA )
		{
			case REQUEST_EXTERNAL_STORAGE:
				//existencia de permisos aceptados
				if( a_PERMISOS.length > 0 )
				{
					for ( int e_permiso : a_RESULTADO )
					{
						//es diferente a permisos aceptado
						if( e_permiso != PackageManager.PERMISSION_GRANTED)
						{
							Utilidad.mostrar_mensaje(this, "No es posible la lectura de archivos");
							return;
						}
					}
					verSelectorArchivo();
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int e_CODIGO_PETICION, int e_CODIGO_RESULTADO, Intent o_INFORMACION)
	{
		switch(e_CODIGO_PETICION)
		{
			case 77:
				if(e_CODIGO_RESULTADO == RESULT_OK)
				{
					Uri o_URI = o_INFORMACION.getData();
					String e_PATH = getPath(this, o_URI);
					if(e_PATH != null && !"".equals(e_PATH))
					{
						procesarJSON(e_PATH);
					}
					else
					{
						Utilidad.mostrar_mensaje(this, "Error al encontrar el archivo");
					}
				}
				break;
		}
	}

	public static String getPath(Context context, Uri uri)
	{
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor o_CURSOR;

			try
			{
				o_CURSOR = context.getContentResolver().query(uri, projection, null, null, null);
				if( o_CURSOR != null )
				{
					int column_index = o_CURSOR.getColumnIndexOrThrow("_data");
					if (o_CURSOR.moveToFirst())
					{
						return o_CURSOR.getString(column_index);
					}
					o_CURSOR.close();
				}
			}
			catch(Exception o_EX)
			{
				o_EX.printStackTrace();
			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
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
			case R.id.itemLog:
				String e_CONTENIDO = Comunicacion.obtenerContenidoArchivo(this, "bitacora.txt");
				Intent o_INTENCION = new Intent(this, ActividadLog.class);
				o_INTENCION.putExtra("BITACORA", e_CONTENIDO);
				startActivity(o_INTENCION);
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