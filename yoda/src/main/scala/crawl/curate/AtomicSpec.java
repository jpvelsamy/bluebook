package crawl.curate;

import java.io.Serializable;

public class AtomicSpec implements Serializable {

	private static final long serialVersionUID = -5478864057756011425L;
	private String reference;
	private String text;
	private float rank;
	
	
	public AtomicSpec(String reference, String text, float rank) {
		super();
		this.reference = reference;
		this.text = text;
		this.rank = rank;
	}
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public float getRank() {
		return rank;
	}
	public void setRank(float rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "AtomicSpec [reference=" + reference + ", text=" + text
				+ ", rank=" + rank + "]";
	}
	
	

}
