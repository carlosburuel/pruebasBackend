package cburuel.stx.mx.testingws.Componentes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import cburuel.stx.mx.testingws.Rest.Modelos.Responses;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public class WebServiceResponse
	implements JsonDeserializer<Responses>
{
	@Override
	public Responses deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		//Casteo de json
		JsonObject jsonObject = (JsonObject) json;
		Responses responses = new Responses();
		//Verificar la existencia y el contenido de success
		if( jsonObject.has("success") && jsonObject.get("success") != null )
		{
			//Asignar el estatus al objeto responses
			responses.setStatus(jsonObject.get("success").getAsBoolean());
		}

		//Verificamos la existencia de RETURN
		if( jsonObject.has("RETURN") && jsonObject.get("RETURN") != null )
		{
			//Verificar que el JSON llamado sea de tipo primitivo
			if( jsonObject.get("RETURN").isJsonPrimitive() )
			{
				//Almacenamos el valor de la respuesta
				responses.setRespuesta( jsonObject.get("RETURN").getAsString() );
			}
			else
			{
				JsonObject jsonError = jsonObject.get("RETURN").getAsJsonObject();
				responses.setRespuesta( jsonError.get("ERROR_CODE").getAsString() );
			}
		}
		//Retornamos la respuesta validada y decodeada
		return responses;
	}
}