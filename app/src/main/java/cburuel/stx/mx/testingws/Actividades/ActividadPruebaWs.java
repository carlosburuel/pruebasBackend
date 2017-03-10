package cburuel.stx.mx.testingws.Actividades;

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

import java.util.ArrayList;
import java.util.List;

import cburuel.stx.mx.testingws.Adaptador.AdaptadorParametros;
import cburuel.stx.mx.testingws.Comunicacion.Comunicacion;
import cburuel.stx.mx.testingws.Listener.OnItemClickListener;
import cburuel.stx.mx.testingws.Modelos.Parametros;
import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Utilidades.ACTIONS;
import cburuel.stx.mx.testingws.Utilidades.Constant;

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

		this.o_ADAPTADOR = new AdaptadorParametros(a_PARAMETROS);
		o_RV_PARAMETROS.setLayoutManager(new LinearLayoutManager(this));
		o_RV_PARAMETROS.setAdapter(o_ADAPTADOR);
		o_ADAPTADOR.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(Parametros o_PARAMETRO)
			{
				//Remover el objeto parametro de la lista actual
				a_PARAMETROS.remove(o_PARAMETRO);
				o_ADAPTADOR.notifyDataSetChanged();
			}
		});

		ArrayAdapter<String> o_ADAPTADOR =
			new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ACTIONS.a_WS_EXTS);
		o_SP_ACCIONES.setAdapter(o_ADAPTADOR);

		o_IV_AGREGAR.setOnClickListener(this);
		o_BTN_ENVIAR.setOnClickListener(this);
	}

	@Override
	public void onClick(View o_VISTA)
	{
		switch( o_VISTA.getId() )
		{
			case R.id.btnAgregar:
				a_PARAMETROS.add(
					new Parametros("Llave", "Valor")
				);
				o_ADAPTADOR.notifyDataSetChanged();
				break;
			case R.id.btnConsultar:

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