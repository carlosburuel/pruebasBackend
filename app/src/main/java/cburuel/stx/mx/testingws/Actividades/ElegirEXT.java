package cburuel.stx.mx.testingws.Actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import cburuel.stx.mx.testingws.Comunicacion.Comunicacion;
import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Utilidades.Constant;

/**
 * @author Carlos Buruel
 * @since 08/03/2017
 */

public class ElegirEXT
	extends AppCompatActivity
	implements View.OnClickListener
{
	RadioButton o_RBTN_EXT1;
	RadioButton o_RBTN_EXT2;
	Button o_BTN_CONTINUAR;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_elegir_ext);

		//Revision de si existe una sesión
		elegirApertura();

		o_RBTN_EXT1 = (RadioButton) findViewById(R.id.rbtnEXT1);
		o_RBTN_EXT2 = (RadioButton) findViewById(R.id.rbtnEXT2);
		o_BTN_CONTINUAR = (Button) findViewById(R.id.btnContinuar);

		o_RBTN_EXT1.setOnClickListener(this);
		o_RBTN_EXT2.setOnClickListener(this);
		o_BTN_CONTINUAR.setOnClickListener(this);

		o_RBTN_EXT1.setChecked(true);
		o_RBTN_EXT2.setChecked(false);
		Constant.e_EXT_ELEGIDO = Constant.e_EXT1;
	}

	private void elegirApertura()
	{
		Class o_CLASE = null;
		if( Comunicacion.existe_flag(this) )
		{
			o_CLASE = PruebaWs.class;
		}
		else if( !"".equals(Comunicacion.obtenerJWT(this)) )
		{
			o_CLASE = VerificarCuenta.class;
		}
		if( o_CLASE != null )
		{
			startActivity(new Intent(this, o_CLASE));
			finish();
		}
	}

	@Override
	public void onClick(View o_VISTA)
	{
		switch( o_VISTA.getId() )
		{
			case R.id.rbtnEXT1:
			case R.id.rbtnEXT2:
				elegirEXT(o_VISTA.getId());
				break;
			case R.id.btnContinuar:
				//Enviar a pantalla de login
				Intent o_INTENT = new Intent(this, ActividadLogin.class);
				o_INTENT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(o_INTENT);
				break;
		}
	}

	/**
	 * Eleccion al seleccionar ya se un RadioButton u otro
	 */
	private void elegirEXT(int e_ID)
	{
		if( e_ID ==  R.id.rbtnEXT1)
		{
			o_RBTN_EXT1.setChecked(true);
			o_RBTN_EXT2.setChecked(false);
			Constant.e_EXT_ELEGIDO = Constant.e_EXT1;
		}
		else
		{
			o_RBTN_EXT1.setChecked(false);
			o_RBTN_EXT2.setChecked(true);
			Constant.e_EXT_ELEGIDO = Constant.e_EXT2;
		}
	}
}