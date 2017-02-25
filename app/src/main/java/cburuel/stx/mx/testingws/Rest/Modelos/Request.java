package cburuel.stx.mx.testingws.Rest.Modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Carlos Buruel
 * @since 24/20/2017
 * Serializar las respuestas a consultas de webservices
 */

public class Request
	implements Serializable
{
	@SerializedName("FINGERPRINT")
	private String e_FINGERPRINT;
	@SerializedName("PAYLOAD")
	private String e_PAYLOAD;

	public Request(String e_FINGERPRINT, String e_PAYLOAD)
	{
		this.e_FINGERPRINT = e_FINGERPRINT;
		this.e_PAYLOAD = e_PAYLOAD;
	}
}