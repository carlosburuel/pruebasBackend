package cburuel.stx.mx.testingws.Comunicacion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;

import cburuel.stx.mx.testingws.R;
import cburuel.stx.mx.testingws.Utilidades.Constant;

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
	 * Guarda bandera de la existencia de una sesion valida dentro de la aplicacion
	 * @param o_CONTEXTO Contexto de donde se esta llamando el metodo
	 * @return Resultado de haber guardado el archivo de bandera
	 */
	public static boolean save_flag(Context o_CONTEXTO)
	{
		try
		{
			FileOutputStream o_FILE = o_CONTEXTO.getApplicationContext().openFileOutput("codigo.txt", Context.MODE_PRIVATE);
			o_FILE.write(Constant.e_EXT_ELEGIDO.getBytes());
			o_FILE.close();
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	/**
	 * Validar la existencia de la firma dentro de la aplicacion
	 * @param o_CONTEXTO Contexto de donde se esta llamando el metodo
	 * @return Representacion de la existencia de bandera
	 */
	public static boolean existe_flag(Context o_CONTEXTO)
	{
		File o_FILE = new File(o_CONTEXTO.getApplicationContext().getFilesDir() + "/codigo.txt");
		//Verificamos la existencia y que sea diferente de carpeta
		return o_FILE.exists() && !o_FILE.isDirectory();
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
	 * Recupera el JWT del contexto de la aplicacion
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return Contenido del JWT
	 */
	public static String obtenerJWT(Context o_CONTEXTO)
	{
		return obtenerContenidoArchivo(o_CONTEXTO, "jwt.txt");
	}

	/**
	 * Reponde un JSON con la informacion ya desencriptada
	 * @param e_RESPONSE_PAYLOAD JSON respondido por un webservice
	 * @param e_CONTEXTO Contexto de la aplicacion
	 * @return JSON con la informacion ya desencriptada
	 */
	public static JSONObject desencriptar_llave_publica(String e_RESPONSE_PAYLOAD, Context e_CONTEXTO)
	{
		RSAPublicKey o_LLAVE_PUBLICA = obtener_llave_publica(e_CONTEXTO);
		String[] m_PAYLOAD_RESPONSE = e_RESPONSE_PAYLOAD.split("\\.");

		//Acumulador de cadena para payload
		String e_PAYLOAD = "";

		try
		{
			Cipher o_CIPHER;

			for(String e_PAYLOAD_TMP : m_PAYLOAD_RESPONSE)
			{
				byte[] m_CHUNK_PAYLOAD = Base64.decode(e_PAYLOAD_TMP, Base64.DEFAULT);

				o_CIPHER = Cipher.getInstance(e_CONTEXTO.getResources().getString(R.string.instancia_cipher));

				o_CIPHER.init(Cipher.DECRYPT_MODE, o_LLAVE_PUBLICA);
				byte[] m_DESENCRIPTADO = o_CIPHER.doFinal(m_CHUNK_PAYLOAD);
				String e_DESENCRIPTADO = new String(m_DESENCRIPTADO, "UTF-8");
				e_PAYLOAD += e_DESENCRIPTADO;
			}
		}
		catch(Exception o_EX)
		{
			o_EX.printStackTrace();
			return null;
		}

		try
		{
			return new JSONObject(e_PAYLOAD);
		}
		catch(Exception o_EX)
		{
			return null;
		}
	}

	/**
	 * Encriptar JSON fuente para envio de informacion
	 * @param e_JSON_PAYLOAD JSON a encriptar
	 * @param e_CONTEXT Contexto de la aplicacion
	 * @return Valor de fuente encriptada
	 */
	public static String encriptarLlavePublica(JSONObject e_JSON_PAYLOAD, Context e_CONTEXT)
	{
		String e_DATOS = e_JSON_PAYLOAD.toString();
		RSAPublicKey o_RSA_LLAVE_PUBLICA = obtener_llave_publica(e_CONTEXT);
		byte[] e_BYTES = e_DATOS.getBytes();
		int e_LENGTH = e_BYTES.length;
		int e_CONTADOR = 0;
		//Proceso para generar String dividido cada 100 bytes
		ArrayList<byte[]> m_PAYLOAD = new ArrayList<>();
		ByteArrayOutputStream o_OUTPUT_STREAM = new ByteArrayOutputStream();

		for(int I = 0; I <= (e_LENGTH-1); I++)
		{
			byte e_BYTE = e_BYTES[I];
			o_OUTPUT_STREAM.write(e_BYTE);

			if(e_CONTADOR == 99)
			{
				byte[] m_BYTES = o_OUTPUT_STREAM.toByteArray();
				o_OUTPUT_STREAM.reset();
				m_PAYLOAD.add(m_BYTES);
				e_CONTADOR = 0;
			}
			else
			{
				e_CONTADOR++;
			}
		}

		byte[] m_BYTES = o_OUTPUT_STREAM.toByteArray();
		m_PAYLOAD.add(m_BYTES);
		if(m_PAYLOAD.size() == 0)
		{
			return "";
		}

		//Proceso para encriptar los bloques del String de 100 bytes
		String e_PAYLOAD = "";
		for(byte[] e_UN_PAYLOAD: m_PAYLOAD)
		{
			Cipher o_CIPHER;
			try
			{
				o_CIPHER = Cipher.getInstance(e_CONTEXT.getResources().getString(R.string.instancia_cipher));
				o_CIPHER.init(Cipher.ENCRYPT_MODE, o_RSA_LLAVE_PUBLICA);
				byte[] m_ENCRYPTED = o_CIPHER.doFinal(e_UN_PAYLOAD);
				byte[] m_BASE64_PAYLOAD = Base64.encode(m_ENCRYPTED, Base64.DEFAULT);
				String e_CHUNK_PAYLOAD = new String(m_BASE64_PAYLOAD, "UTF-8");
				e_PAYLOAD += e_CHUNK_PAYLOAD + ".";
			}
			catch(Exception o_EX)
			{
				o_EX.printStackTrace();
				return "";
			}
		}

		e_PAYLOAD = e_PAYLOAD.substring(0,e_PAYLOAD.length() -1);
		return e_PAYLOAD;
	}

	/**
	 *Recuperar la llave publica almacenada en la aplicacion
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return La llave publica
	 */
	private static RSAPublicKey obtener_llave_publica(Context o_CONTEXTO)
	{
		RSAPublicKey publicKey = null;
		try
		{
			String e_CONTENIDO = obtenerContenidoArchivo(o_CONTEXTO, "public.der");

			if(e_CONTENIDO == null)
			{
				return null;
			}

			byte[] b_LLAVE_PUBLICA = Base64.decode(e_CONTENIDO, Base64.DEFAULT);

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(b_LLAVE_PUBLICA);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return publicKey;
	}

	/**
	 * Metodo encargado de guardar el JWT en el contexto privado de la aplicacion
	 * @param e_JWT Representacion en cadena del JWT a almacenar
	 * @param o_CONTEXTO Contexto de donde se esta llamando el metodo
	 * @return Resultado sea exitoso o no del guardado del JWT
	 */
	public static boolean salvar_jwt(String e_JWT, Context o_CONTEXTO)
	{
		try
		{
			FileOutputStream o_FILE = o_CONTEXTO.getApplicationContext().openFileOutput("jwt.txt", Context.MODE_PRIVATE);
			o_FILE.write(e_JWT.getBytes());
			o_FILE.close();
		}
		catch(Exception o_EX)
		{
			return false;
		}
		return true;
	}

	public static String obtenerContenidoArchivo(Context o_CONTEXTO, String e_NOMBRE_ARCHIVO)
	{
		String e_JWT= "";
		try
		{
			//Abrimos el archivo
			FileInputStream f_FILE = o_CONTEXTO.openFileInput(e_NOMBRE_ARCHIVO);
			//Creamos el objeto de entrada
			DataInputStream o_STREAM = new DataInputStream(f_FILE);
			//Creamos el Buffer de Lectura
			BufferedReader o_BUFFER = new BufferedReader(new InputStreamReader(o_STREAM));
			//Leer el archivo linea por linea
			String e_LINEA;
			int e_NUM_LINEAS = 0;
			while( (e_LINEA = o_BUFFER.readLine()) != null )
			{
				e_JWT += e_LINEA + "\n\n";
				e_NUM_LINEAS++;
			}
			if( e_NUM_LINEAS < 2 )
			{
				e_JWT = e_JWT.substring(0, e_JWT.length()-2);
			}
		}
		catch(Exception e)
		{
			return "";
		}
		return e_JWT;
	}

	/**
	 * Obtener el fingerprint en base a la informacion del dispositivo
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return El valor del fingerprint
	 */
	public static String crearFingerprint(Context o_CONTEXTO)
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

	/**
	 * Cierre de sesion, son elimiados los archivos clave de la aplicacion
	 * @param o_CONTEXTO Contexto de la aplicacion
	 * @return Resultado de haber borrado los archivos
	 */
	public static boolean cerrarSesion(Context o_CONTEXTO)
	{
		try
		{
			//Borrar jwt
			o_CONTEXTO.getApplicationContext().deleteFile("public.der");
			//Borrar jwt
			o_CONTEXTO.getApplicationContext().deleteFile("jwt.txt");
			//Borrar bandera de vinculacion
			o_CONTEXTO.getApplicationContext().deleteFile("codigo.txt");
		}
		catch(Exception o_EX)
		{
			o_EX.printStackTrace();
		}
		return true;
	}
}