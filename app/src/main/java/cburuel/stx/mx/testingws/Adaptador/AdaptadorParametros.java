package cburuel.stx.mx.testingws.Adaptador;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import cburuel.stx.mx.testingws.Listener.OnItemClickListener;
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
	private OnItemClickListener o_ITEM_CLICK_LISTENER;

	public AdaptadorParametros(List<Parametros> a_PARAMETROS)
	{
		this.a_PARAMETROS = a_PARAMETROS;
	}

	@Override
	public CustomViewHolder onCreateViewHolder(ViewGroup o_PADRE, int e_INT)
	{
		View o_VISTA = LayoutInflater.from(o_PADRE.getContext())
			.inflate(R.layout.elemento_parametro, null);
		return new CustomViewHolder(o_VISTA);
	}

	@Override
	public void onBindViewHolder(CustomViewHolder o_HOLDER, int e_POSICION)
	{
		final Parametros o_PARAMETRO = a_PARAMETROS.get(e_POSICION);
		o_HOLDER.o_ET_LLAVE.setHint( o_PARAMETRO.getE_TEXTO_1() );
		o_HOLDER.o_ET_VALOR.setHint(o_PARAMETRO.getE_TEXTO_2() );

		View.OnClickListener o_LISTENER = new View.OnClickListener()
		{
			@Override
			public void onClick(View o_VISTA)
			{
				o_ITEM_CLICK_LISTENER.onItemClick(o_PARAMETRO);
			}
		};

		o_HOLDER.o_IV_CERRAR.setOnClickListener(o_LISTENER);
	}

	@Override
	public int getItemCount()
	{
		return (null != a_PARAMETROS ? a_PARAMETROS.size() : 0);
	}

	public void setOnItemClickListener(OnItemClickListener o_ITEM_CLICK_LISTENER)
	{
		this.o_ITEM_CLICK_LISTENER = o_ITEM_CLICK_LISTENER;
	}

	class CustomViewHolder
	extends RecyclerView.ViewHolder
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
		}
	}
}