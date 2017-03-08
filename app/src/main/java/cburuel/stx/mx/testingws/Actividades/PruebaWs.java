package cburuel.stx.mx.testingws.Actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import cburuel.stx.mx.testingws.Comunicacion.Comunicacion;
import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Utilidades.Constant;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class PruebaWs
	extends AppCompatActivity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prueba_ws);

		//Asignar el titulo
		setTitle(
			( Constant.e_EXT_ELEGIDO.equals(Constant.e_EXT1) ? "EXT1" : "EXT2" )
				+ " en prueba");
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
							Comunicacion.cerrarSesion(PruebaWs.this);
							startActivity(new Intent(PruebaWs.this, ElegirEXT.class));
							finish();
						}
					})
					.setNegativeButton("No", null)
					.show();
				break;
		}
		return true;
	}
}