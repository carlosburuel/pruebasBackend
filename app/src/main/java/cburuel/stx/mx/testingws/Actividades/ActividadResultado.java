package cburuel.stx.mx.testingws.Actividades;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Utilidades.Constant;
import cburuel.stx.mx.testingws.Utilidades.Utilidad;

/**
 * @author Carlos Buruel
 * @since 13/03/2017
 */

public class ActividadResultado
	extends AppCompatActivity
{
	TextView o_TV_RESULTADO;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resultado);
		//Asignar el titulo
		setTitle(
			( Constant.e_EXT_ELEGIDO.equals(Constant.e_EXT1) ? "EXT1" : "EXT2" )
				+ " en prueba");
		o_TV_RESULTADO = (TextView) findViewById(R.id.tvResultado);
		//Obtencion de informacion
		Bundle o_PAQUETE = getIntent().getExtras();
		if( o_PAQUETE != null )
		{
			o_TV_RESULTADO.setText(o_PAQUETE.getString("RESULTADO","Error al cargar la información"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu o_MENU)
	{
		getMenuInflater().inflate(R.menu.envio_correo, o_MENU);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem o_ITEM)
	{
		switch(o_ITEM.getItemId())
		{
			case R.id.itemCerrar:
				Intent o_INTENT = new Intent(Intent.ACTION_SENDTO,
					Uri.fromParts("mailto", "", null));
				o_INTENT.putExtra(Intent.EXTRA_SUBJECT, "Respuesta WS");
				o_INTENT.putExtra(Intent.EXTRA_TEXT, o_TV_RESULTADO.getText().toString());

				try
				{
					startActivity(Intent.createChooser(o_INTENT, "Enviar correo..."));
				}
				catch(Exception o_EX)
				{
					o_EX.printStackTrace();
					Utilidad.mostrar_mensaje(this, "No existe un cliente de correos");
				}
				break;
		}
		return true;
	}
}