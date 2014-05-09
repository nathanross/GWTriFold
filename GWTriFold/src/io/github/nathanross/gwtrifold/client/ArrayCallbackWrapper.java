package io.github.nathanross.gwtrifold.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;

public class ArrayCallbackWrapper {
	private Behavior policy;
	ArrayCallbackWrapper(Behavior policy) {
		this.policy = policy;
	}

	private native JsArrayNumber jsArrayNumFactory() /*-{
		return [];
	}-*/;
	
	
	private native JsArray<JsArrayNumber> jsTwoDimFactory() /*-{
		return [];
	}-*/;
	
	
	public JsArrayNumber getLayout(int availColumns) {
		int[] resultsTmp = policy.getLayout(availColumns);
		JsArrayNumber results = jsArrayNumFactory();
		for (int i=0;i<resultsTmp.length;i++) {
			results.push(resultsTmp[i]);
		}
		return results;
	}
	
	public JsArray<JsArrayNumber> getButtonStatus(int availColumns) {
		int[][] resultsTmp = policy.getButtonStatus(availColumns);
		JsArray<JsArrayNumber> results = jsTwoDimFactory();
		for (int i=0;i<resultsTmp.length;i++) {
			JsArrayNumber subList = jsArrayNumFactory();
			for (int j=0; j<resultsTmp[i].length; j++) {
				subList.push(resultsTmp[i][j]);
			}
			results.push(subList);
		}
		return results;
	}
}
