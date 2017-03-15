package cburuel.stx.mx.testingws.Actividades;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Utilidades.Utilidad;

/**
 * @author Carlos Buruel
 * @since 14/03/2015
 */

public class AcvitidadImagen
	extends AppCompatActivity
	implements View.OnClickListener
{
	EditText o_ET_PATH;
	ImageView o_IV_IMAGEN;
	Button o_BTN_BUSCAR;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagen);

		o_IV_IMAGEN = (ImageView) findViewById(R.id.ivImagen);
		o_ET_PATH = (EditText) findViewById(R.id.etPath);
		o_BTN_BUSCAR = (Button) findViewById(R.id.btnBuscarImagen);

		o_BTN_BUSCAR.setOnClickListener(this);
	}

	@Override
	public void onClick(View o_VISTA)
	{
		switch(o_VISTA.getId())
		{
			case R.id.btnBuscarImagen:
				obtenerImagen();
				break;
		}
	}

	private void obtenerImagen()
	{
		String e_PATH = o_ET_PATH.getText().toString();
		if( "".equals(e_PATH) )
		{
			Utilidad.mostrar_mensaje(this, "Ingrese la ruta de la imagen");
			return;
		}
		Picasso.with(this)
			.load(e_PATH)
			.error(R.drawable.ic_camera)
			.placeholder(R.drawable.ic_camera)
			.into(o_IV_IMAGEN);
	}
}