package org.xianlv.stt.response;

import java.util.List;

/**
 * 
 * @author smc
 *
 */
public class TextResult {
	private String status;
	private List<String> texts;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getTexts() {
		return texts;
	}

	public void setTexts(List<String> texts) {
		this.texts = texts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((texts == null) ? 0 : texts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextResult other = (TextResult) obj;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (texts == null) {
			if (other.texts != null)
				return false;
		} else if (!texts.equals(other.texts))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TextResult [status=" + status + ", texts=" + texts + "]";
	}

}
