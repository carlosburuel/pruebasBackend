package cburuel.stx.mx.testingws.Adaptador;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import cburuel.stx.mx.testingws.Modelos.Parametros;
import cburuel.stx.mx.testingws.R;

/**
 * @author Carlos Buruel
 * @since 10/03/2017
 */

public class AdaptadorParametros
	extends RecyclerView.Adapter<AdaptadorParametros.CustomViewHolder>
{
	private List<Parametros> a_PARAMETROS;

	public AdaptadorParametros(List<Parametros> a_PARAMETROS)
	{
		this.a_PARAMETROS = a_PARAMETROS;
	}

	@Override
	public CustomViewHolder onCreateViewHolder(ViewGroup o_PADRE, int e_INT)
	{
		View o_VISTA = LayoutInflater.from(o_PADRE.getContext())
//			.inflate(R.layout.elemento_parametro, o_PADRE, false);
			.inflate(R.layout.elemento_parametro, null);
		return new CustomViewHolder(o_VISTA);
	}

	@Override
	public void onBindViewHolder(CustomViewHolder o_HOLDER, int e_POSICION)
	{
		final Parametros o_PARAMETRO = a_PARAMETROS.get(e_POSICION);
		o_HOLDER.o_ET_LLAVE.setHint( o_PARAMETRO.getE_HINT_1() );
		o_HOLDER.o_ET_VALOR.setHint(o_PARAMETRO.getE_HINT_2() );

		//Eventos de lectura al escribir
		o_HOLDER.o_ET_LLAVE.addTextChangedListener(new TextWatcher()
		{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after){}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count){}

		//Despues al terminar de ingresar información
		@Override
		public void afterTextChanged(Editable o_TEXTO)
		{
			o_PARAMETRO.setE_TEXTO_1(o_TEXTO.toString());
		}
	});
		o_HOLDER.o_ET_VALOR.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}

			//Despues al terminar de ingresar información
			@Override
			public void afterTextChanged(Editable o_TEXTO)
			{
				o_PARAMETRO.setE_TEXTO_2(o_TEXTO.toString());
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return (null != a_PARAMETROS ? a_PARAMETROS.size() : 0);
	}

	public void removeAt(int e_POSICION)
	{
		if( e_POSICION > -1 )
		{
			a_PARAMETROS.remove(e_POSICION);
			notifyItemRemoved(e_POSICION);
			notifyItemRangeChanged(e_POSICION, a_PARAMETROS.size());
		}
	}

	class CustomViewHolder
	extends RecyclerView.ViewHolder
		implements View.OnClickListener
	{
		EditText o_ET_LLAVE;
		EditText o_ET_VALOR;
		ImageView o_IV_CERRAR;

		public CustomViewHolder(View o_VISTA)
		{
			super(o_VISTA);
			this.o_ET_LLAVE = (EditText) o_VISTA.findViewById(R.id.etLlave);
			this.o_ET_VALOR = (EditText) o_VISTA.findViewById(R.id.etValor);
			this.o_IV_CERRAR = (ImageView) o_VISTA.findViewById(R.id.ivCerrar);

			this.o_IV_CERRAR.setOnClickListener(this);
		}

		//Clic para borrado de elementos en RecyclerView
		@Override
		public void onClick(View o_VISTA)
		{
			//Obtener la posicion del elemento en la posicion
			removeAt(getAdapterPosition());

			ViewGroup o_PADRE = (ViewGroup) o_VISTA.getParent();
			for (int e_INDEX = 0; e_INDEX < o_PADRE.getChildCount(); e_INDEX++)
			{
				View o_HIJO = o_PADRE.getChildAt(e_INDEX);
				if( o_HIJO instanceof EditText )
				{
					((EditText) o_HIJO).setText("");
				}
			}
		}

	}
}