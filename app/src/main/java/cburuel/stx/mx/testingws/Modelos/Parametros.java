package cburuel.stx.mx.testingws.Modelos;

/**
 * @author Carlos Buruel
 * @since 10/03/2017
 */

public class Parametros
{
	private String e_HINT_1;
	private String e_HINT_2;
	private String e_TEXTO_1;
	private String e_TEXTO_2;

	public Parametros(String e_HINT_1, String e_HINT_2)
	{
		this.e_HINT_1 = e_HINT_1;
		this.e_HINT_2 = e_HINT_2;
	}

	public String getE_HINT_1()
	{
		return e_HINT_1;
	}

	public String getE_HINT_2()
	{
		return e_HINT_2;
	}

	public void setE_TEXTO_1(String e_TEXTO_1)
	{
		this.e_TEXTO_1 = e_TEXTO_1;
	}

	public void setE_TEXTO_2(String e_TEXTO_2)
	{
		this.e_TEXTO_2 = e_TEXTO_2;
	}

	public String getE_TEXTO_1()
	{
		return e_TEXTO_1;
	}

	public String getE_TEXTO_2()
	{
		return e_TEXTO_2;
	}
}