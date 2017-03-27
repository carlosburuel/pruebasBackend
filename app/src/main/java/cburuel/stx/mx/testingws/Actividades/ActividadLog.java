package cburuel.stx.mx.testingws.Actividades;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cburuel.stx.mx.testingws.R;

/**
 * @author Carlos Buruel
 * @since 27/03/2017
 */

	public class ActividadLog
	extends AppCompatActivity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actividad_log);

		setTitle("Ultimo LOG creado");

		TextView o_TV = (TextView) findViewById(R.id.tvContenidoLog);

		Bundle o_PAQUETE = getIntent().getExtras();
		String e_CONTENIDO = "";
		if( o_PAQUETE != null )
		{
			e_CONTENIDO = o_PAQUETE.getString("BITACORA", "Error en la información");
		}

		o_TV.setText(e_CONTENIDO);
	}
}