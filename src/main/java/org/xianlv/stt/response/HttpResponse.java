package org.xianlv.stt.response;

/**
 * Created by songsong.sss on 2017/1/18.
 */
public class HttpResponse {
	private int status;
	private String result;
	private String message;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
