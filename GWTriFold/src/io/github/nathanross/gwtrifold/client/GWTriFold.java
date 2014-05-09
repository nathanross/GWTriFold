/*
 * gwtrifold/client/GWTriFold.java
 * Developer-facing class to implement GWTriFold-based web app templates.
 * 
 * Copyright 2013 Nathan Ross
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package io.github.nathanross.gwtrifold.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
//import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Panel;



public class GWTriFold extends JavaScriptObject {
	static final public Integer LEFT = Integer.valueOf(0);
	static final public Integer CENT = Integer.valueOf(1);
	static final public Integer RIGHT = Integer.valueOf(2);

	static final protected int X = 0;
	static final protected int Y = 1;
	static final protected int NORTH = 0;
	static final protected int EAST = 1;
	static final protected int SOUTH = 2;
	static final protected int WEST = 3;
	
	protected GWTriFold() {}
	
	// root MUST be on the DOM tree when passed (that is, it must be on the 
	// page, even if its invisible via CSS), because if it's not, the only 
	// way to pass it from GWT's object model to JS is to "lose its place"
	// in the GWT object model by adding it to an HTMLpanel passed back 
	// from JS.

	public final void addTo(Panel panel) {
		panel.add(getPanelJS());
		displayPanelJS();
	};
	
	public static final GWTriFold create(Behavior behavior,
		Panel leftDiv, Panel centDiv, Panel rightDiv) {
		//String findElement = genClassName();
		//root.addStyleName(findElement);
		//GWTriFold result = createJSNI(findElement);
		GWTriFold result = createJSNI();;
		HTMLPanel resultPanel = HTMLPanel.wrap(getAsElement(result));
		for (int i=0;i<3;i++) {
			resultPanel.add((i ==0)? leftDiv:((i==1)? centDiv:rightDiv), 
							getColContainer(result,i));
		}
		storeHTMLpanel(result, resultPanel);
		setBehaviorJSNI(behavior, new ArrayCallbackWrapper(behavior), result);
			//HTMLPanel standIn = HTMLPanel.wrap(Document.get().getElementById(
			//		findElement + "GWTriFoldStandIn" + Integer.toString(i)));
		return result;
	};

	// apply visual changes and set visible.
	public final native void display() /*-{ this.display(); }-*/;
	
	// visual changes are not applied incrementally, that way you
	// can change them as a group. call redraw() to apply visual changes.
	// (otherwise changes are applied on resize)
	public final native void redraw() /*-{ this.redraw(); }-*/;

	// --- visual changes ---
	
	public final void setMinColDimensions(double in[]) 
	{ setMinColDimensions(in[X],in[Y]); }
	
	public final double[] getMinColDimensions() 
	{ return convertJsArray(getMinColDimensionsJSNI()); }

	public final void setPadding(double in[]) 
	{ setPadding(in[NORTH], in[EAST], in[SOUTH], in[WEST]); }
		
	public final double[] getPadding() 
	{ return convertJsArray(getPaddingJSNI()); }

	public final void setMargins(double in[]) 
	{ setMargins(in[NORTH], in[EAST], in[SOUTH], in[WEST]); }
	
	public final double[] getMargins() 
	{ return convertJsArray(getMarginsJSNI()); }
	
	public final native void setColSpacing(double x) 
	/*-{ this.setColSpacing(x); }-*/;
	
	public final native double getColSpacing() 
	/*-{ return this.getColSpacing(); }-*/;
	
	// --- animation changes ---
	
	public final native void setSlidespeed(int slidespeed) 
	/*-{ this.setSlidespeed(slidespeed); }-*/;
	
	public final native int getSlidespeed() 
	/*-{ return this.getSlidespeed(); }-*/;
	
	// --- behavior changes ---
	
	public final void setBehavior(Behavior p) {
		setBehaviorJSNI(p, new ArrayCallbackWrapper(p), this);
	}
	
	// --- implement your own behavior or buttons ---

	// advance tools, useful if you are writing a new implementation
	// of Behavior or LayoutManip, or extending DefaultBehavior.
	
	// manually has TriFold ask your behavior what the current layout
	// should be. Otherwise these checks are only triggered:
	// 1. on button press 2. when TriFold is provided with a new behavior
	// 3. when the number of columns that can be displayed on the screen
	//		changes.
	public final native void recheckBehavior() /*-{ this.recheckBehavior(); }-*/;
	
	// implementing your own layout ui controls? this function is callback 
	// syntactic sugar. Calling it is the equivalent of calling 
	// .onManipUse(src, keycode) and then calling reCheckBehavior()
	// on the current behavior. 
	public final native void simulateButtonPress(int src, int keycode) /*-{
		this.simulateButtonPress(src, keycode);
	}-*/;	

	public final native void setLayoutManip(int colnum, LayoutManip m) /*-{
		var jManip = new $wnd.TriFold.JavaManip();
		jManip.getStatusCallback = function(comp) {
			m.@io.github.nathanross.gwtrifold.client.LayoutManip::getStatus(I)(comp);
		}
		jManip.setStatusCallback = function(comp, status) {
			m.@io.github.nathanross.gwtrifold.client.LayoutManip::setStatus(II)(comp, status);
		}; 
		this.setLayoutManip(colnum, jManip);
	}-*/;

	// --- debugging ---
	
	public final native void debugJS(String message) /*-{ 
		if ("debugLog" in ($wnd.triFold[0]._proto_ || 
							$wnd.triFold[0].constructor.prototype)) {
			this.debugLog(message);
		}
	}-*/;
	
	// --- end public methods ---
	
	private final native void setMinColDimensions(double x, double y) 
	/*-{ this.setMinColDimensions([x, y]); }-*/;
	
	private final native JsArrayNumber getMinColDimensionsJSNI() 
	/*-{ return this.getMinColDimensions(); }-*/;
	
	private final native void setPadding(
			double n, double e, double s, double w) 
	/*-{ this.setPadding([n,e,s,w]); }-*/;
	
	private final native JsArrayNumber getPaddingJSNI() 
	/*-{ return this.getPadding(); }-*/;	
	
	private final native void setMargins(
			double n, double e, double s, double w) 
	/*-{ this.setMargins([n,e,s,w]); }-*/;
	
	private final native JsArrayNumber getMarginsJSNI() 
	/*-{ return this.getMargins(); }-*/;
	

	//Note: JSNI calls of Java methods CANNOT have line breaks
	//has a .call() type parameter because is called at startup.
	private static final native void setBehaviorJSNI(Behavior p, 
			ArrayCallbackWrapper acw, GWTriFold that) /*-{
		var jBehavior = new $wnd.TriFold.JavaBehavior();
		
		jBehavior.getLayoutCallback = function(availColumns) 
		{ return acw.@io.github.nathanross.gwtrifold.client.ArrayCallbackWrapper::getLayout(I)(availColumns); }
		jBehavior.getButtonStatusCallback = function(availColumns) 
		{ return acw.@io.github.nathanross.gwtrifold.client.ArrayCallbackWrapper::getButtonStatus(I)(availColumns); } 
		jBehavior.onManipUseCallback = function(src, keycode) 
		{ p.@io.github.nathanross.gwtrifold.client.Behavior::onManipUse(II)(src,keycode);}
		jBehavior.onAvailChange = function(newColAvail) 
		{ p.@io.github.nathanross.gwtrifold.client.Behavior::onAvailChange(I)(newColAvail); }
		
		that.setBehavior(jBehavior);
	
	}-*/;
	
	private final double [] convertJsArray(JsArrayNumber dArr) {
		int l = dArr.length();
		double [] results = new double[l];
		for (int i=0;i<l;i++) 
		{ results[i] = dArr.get(i); }
		return results;
	}
	
	private static final native GWTriFold createJSNI() /*-{
		//anchor it to the window so devs
		//can inspect it via browser console.
		
		if (!("triFold" in $wnd)) {
			$wnd.triFold = {};
		}
		//not sure why you'd need more than one of these on one
		//screen, but I'm not going to foreclose the possibility.
		var result;
		var standIns = [];
		var i=0;
		for (i=0;i<3;i++) { 
			standIns[i] = $doc.createElement("div"); 
			//standIns[i].className = "GWTriFoldWrapper";
		}
		for (i=0;true;i++) {
			if (!($wnd.triFold[i] &&
					typeOf($wnd.triFold[i]) !== 'undefined')) {
				$wnd.triFold[i] = new $wnd.TriFold(
				//new $wnd.TriFold.DefaultBehavior(),
				new $wnd.TriFold.DefaultBehavior(),
				standIns[0], standIns[1], standIns[2]);
				result = $wnd.triFold[i];
				break;
			}
		}
		result.colContainer = [];
		for (i=0;i<3;i++) {
			result.colContainer[i] = standIns[i].parentNode;
			result.colContainer[i].removeChild(standIns[i]);
		}
		//define wrapper classes
				
		$wnd.TriFold.JavaBehavior = function() {
			this.getLayoutCallback = function(availColumns){};
			this.getButtonStatusCallback = function(availColumns){};
			this.onManipUseCallback = function(src,keycode){};
			this.onAvailChangeCallback = function(newColAvail){};
		};
		$wnd.TriFold.JavaBehavior.prototype = Object.create($wnd.TriFold.Behavior);
		$wnd.TriFold.JavaBehavior.prototype.getLayout = function(availColumns) {
			return this.getLayoutCallback(availColumns);
		};
		$wnd.TriFold.JavaBehavior.prototype.getButtonStatus = 
			function(availColumns) {
			return this.getButtonStatusCallback(availColumns);
		};
		$wnd.TriFold.JavaBehavior.prototype.onManipUse = function(src,keycode) {
			this.onManipUseCallback(src,keycode);
		};
		$wnd.TriFold.JavaBehavior.prototype.onAvailChange = function(newColAvail) {
			this.onAvailChangeCallback(newColAvail);
		};
		
		$wnd.TriFold.JavaManip = function() {
			this.getStatusCallback = function(comp) {};
			this.setStatusCallback = function(comp,status) {};
		};
		$wnd.TriFold.JavaManip.prototype = Object.create($wnd.TriFold.LayoutManip);
		$wnd.TriFold.JavaManip.prototype.getStatus = function(comp) {
			return this.getStatusCallback(comp);
		}; 
		$wnd.TriFold.JavaManip.prototype.setStatus = function(comp, status) {
			this.setStatusCallback(comp,status);
		}; 
		
		var el = $doc.createElement("div");
		el.id = "trifold" + Math.random().toString(36).substring(2);
		$doc.body.appendChild(el);
		el.appendChild(result.getElement());
		return result;
		
	}-*/;
	
	
	
	
	private static final native Element getColContainer(
			GWTriFold that, int num) /*-{ return that.colContainer[num]; }-*/;
	private static final native Element getAsElement(GWTriFold that) /*-{
		return that.getElement();
	}-*/;
	

	// it's possible to store persistent references to
	// java objects through javascript's hesitant GC re. callback context.
	// this allows us to have the speed and connection to a JS object of 
	// a JSO while storing java objects.
	private static final native void storeHTMLpanel(
						GWTriFold that, HTMLPanel panel) /*-{
		that.getPanel = (function(panel) {
				var inner = function() { return panel; }
				return inner;
			})(panel);
	}-*/;
	
	private native final HTMLPanel getPanelJS() 
		/*-{ this.hide(true);
			return this.getPanel(); }-*/;
	private native final HTMLPanel displayPanelJS() /*-{
		this.redraw(); 
		this.hide(false); 
		var that = this;
		$wnd.TriFold.ResizeSensor(this.getElement(), 
									function() {that.redraw();} ); 
		//deal with old manip listeners getting erased by dom-reattach,
		//by re-adding them after adding to DOM. can't use addTo(parent)
		//because incompatible with HTMLpanel adding sequence.
		for (var i=0;i<3;i++) {
			this.getLayoutManip(i).isAddedToDom();
		}
	}-*/;
		
	
	private static final native String genClassName() /*-{
		//in the rare case an end dev wants to set up more than one widget
		//at a time, it can't hurt to reliably avoid collisions if they're
		//both being instantiated at near the same time.
		var name = "trifold" + Math.random().toString(36).substring(2);
		while (0 in $doc.getElementsByClassName(name)) {
			name = "trifold" + Math.random().toString(36).substring(2);
		}
		return name;
	}-*/;
	
	// fade is undergoing a re-factor.
	//
	// public native void setFade(boolean fade) 
	// /*-{ this.setFade(fade); }-*/;
	// public native boolean getFade() 
	// /*-{ return this.getFade(); }-*/;

	// needs reimplementation with the new tweening class.
	//
	// public native void setSwitchbarSlidespeed(int slidespeed) 
	// /*-{ this.setSwitchbarSlidespeed(slidespeed); }-*/;
	// public native int getSwitchbarSlidespeed() 
	// /*-{ return this.getSlidespeed(); }-*/;	
}