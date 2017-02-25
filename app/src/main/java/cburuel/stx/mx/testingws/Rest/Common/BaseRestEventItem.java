package cburuel.stx.mx.testingws.Rest.Common;

/**
 * @author Carlos Buruel
 * @since 24/02/2017
 */

public abstract class BaseRestEventItem<T>
{
	private RestStatus restStatus;
	private T item;
	private long requestCode;

	public BaseRestEventItem(RestStatus status, T item)
	{
		this.restStatus = status;
		this.item = item;
	}

	public RestStatus getRestStatus() {
		return restStatus;
	}

	public T getItem() {
		return item;
	}

	public long getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(long requestCode)
	{
		this.requestCode = requestCode;
	}
}