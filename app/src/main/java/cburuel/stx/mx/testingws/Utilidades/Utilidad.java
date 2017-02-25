package cburuel.stx.mx.testingws.Utilidades;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class Utilidad
{
	/**
	 * Mostrar toast en pantalla partiendo de un recurso
	 */
	public static void mostrar_mensaje(Context o_CONTEXT, int e_RECURSO_CADENA)
	{
		Toast.makeText(o_CONTEXT, e_RECURSO_CADENA, Toast.LENGTH_LONG).show();
	}

	/**
	 * Mostrar toast en pantalla partiendo de un recurso
	 * @param o_CONTEXT Contexto App
	 * @param e_CADENA Cadena a mostrar
	 */
	public static void mostrar_mensaje(Context o_CONTEXT, String e_CADENA)
	{
		Toast.makeText(o_CONTEXT, e_CADENA, Toast.LENGTH_LONG).show();
	}

	/**
	 * Verificar si existe conexion a internet por algun proveedor
	 * @param o_CONTEXTO Contexto de donde se esta llamando el metodo
	 * @return Resultado de la existencia de conexion
	 */
	public static boolean verificaConexion(Context o_CONTEXTO)
	{
		ConnectivityManager o_CONNEC =
			(ConnectivityManager) o_CONTEXTO.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo[] redes = o_CONNEC.getAllNetworkInfo();

		for(int i = 0; i < 2; i++)
		{
			if(redes[i].getState() == NetworkInfo.State.CONNECTED)
			{
				return true;
			}
		}
		return false;
	}
}