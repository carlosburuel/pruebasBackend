package cburuel.stx.mx.testingws.Comunicacion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class Comunicacion
{
	/**
	 * Existencia de llave publica dentro del App
	 */
	public static boolean existeLlavePublica(Context o_CONTEXT)
	{
		File o_FILE = new File(o_CONTEXT.getApplicationContext().getFilesDir() + "/public.der");
		//Verificamos la existencia y que sea diferente de carpeta
		return o_FILE.exists() && !o_FILE.isDirectory() ;
	}

	/**
	 * Almacenar la llave publica dentro de la aplicacion
	 * @param e_PUBLIC_KEY Contenido de la llave publica
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return Resultante de si fue guardada
	 */
	public static boolean guardarLlavePublica(String e_PUBLIC_KEY, Context o_CONTEXTO)
	{
		try
		{
			FileOutputStream r_ARCHIVO = o_CONTEXTO.openFileOutput("public.der", Context.MODE_PRIVATE);
			byte[] bytes = e_PUBLIC_KEY.getBytes();
			r_ARCHIVO.write(bytes);
			r_ARCHIVO.close();
		}
		catch (Exception o_EX)
		{
			o_EX.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Obtener el fingerprint en base a la informacion del dispositivo
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return El valor del fingerprint
	 */
	private static String crear_fingerprint(Context o_CONTEXTO)
	{
		String e_FINGERPRINT = "";
		String e_IMEI = getImei(o_CONTEXTO);
		String e_MACADDRESS = getMacAddress(o_CONTEXTO);
		try
		{
			e_FINGERPRINT =
				Base64.encodeToString(
					(e_IMEI + e_MACADDRESS + getDeviceId(o_CONTEXTO) + "B").getBytes(),
					Base64.DEFAULT);
		}
		catch (Exception o_EX)
		{
			o_EX.printStackTrace();
		}
		return e_FINGERPRINT;
	}

	public static String obtenerFingerprint(Context o_CONTEXTO)
	{
		return crear_fingerprint(o_CONTEXTO);
	}

	/**
	 * Obtener imei del dispositivo
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return Retorna el IMEI del dispositivo
	 */
	@SuppressLint("HardwareIds")
	private static String getImei(Context o_CONTEXTO)
	{
		return ((TelephonyManager) o_CONTEXTO.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	/**
	 * Obtener direccion MAC
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return Contenido de la direccion MAC
	 */
	@SuppressLint("HardwareIds")
	private static String getMacAddress(Context o_CONTEXTO)
	{
		return ((WifiManager) o_CONTEXTO.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
	}

	/**
	 * Obtener id del dispositivo
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return ID del dispositivo
	 */
	@SuppressLint("HardwareIds")
	private static String getDeviceId(Context o_CONTEXTO)
	{
		String deviceId = ((TelephonyManager) o_CONTEXTO.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		return deviceId != null ? deviceId : Build.SERIAL;
	}
}