/*
 * gwtrifold/client/GWTriJSNI.java
 * Groups and decouples JS behavior from GWTriFold layout decisions.
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


//kept seperate as to not needlessly expose the
//public variables/callbacks necessary to write back to java
//to devs using GWTriFold with an IDE and might get worried
//they have to dig into the source code to see what this or that
//function they're not aware of is.


public class GWTriJSNI {
	//access calls to java objects through javascript are more expensive
	//than one might think. For the small number of calls, as a stopgap
	//until transitioning the construction of the div to JS (which, tradeoff
	// regarding end- dev access, but overall is an improvement) 
	// syntax will just be a little more complex until then.
	
	
	public int ID = -1;
	public GWTriFold triRoot;
	public GWTriJSNI(GWTriFold triRoot) {
		this.triRoot = triRoot;			
	}
	public void recordPanelDimensions(double x, double y) {
		//consoleLog("recordPanelDimensions got a callback. x:");
		//consoleLog(Double.toString(x));	
		//consoleLog("y:");
		//consoleLog(Double.toString(y));
		double xyParentDimensions[] = new double[2];
		xyParentDimensions[0] = x; xyParentDimensions[1] = y;
		this.triRoot.resizeCallback(xyParentDimensions);
	}
	
	public native void log(int ID, String msg) /*-{
		$wnd.triFold[ID].debugCall(msg);
	}-*/;
	
	public void recordID(int ID) {
		this.ID = ID;
		this.triRoot.jsSet();
	}
	
	public native void getParentDimensions(
									int ID, GWTriJSNI inst) /*-{
		var xstr = getComputedStyle($wnd.triFold[ID].elOuterParent).width;
		var x = parseFloat(xstr.slice(0,-2));
		var ystr = getComputedStyle($wnd.triFold[ID].elOuterParent).height;
		var y = parseFloat(ystr.slice(0,-2));
		inst.@io.github.nathanross.gwtrifold.client.GWTriJSNI::recordPanelDimensions(DD)(x,y);
	}-*/;
	
	public native void hideInnerParent(int ID) /*-{
		$wnd.triFold[ID].elOuterParent
			.getElementsByClassName("tri-fold_innerParent")[0]
			.style.visibility = "hidden";
	}-*/;

	public native void createTriFoldAtRoot(
								String classname, GWTriJSNI inst) /*-{
		var id = $wnd.genTriFold();
		$wnd.triFold[id].setTriFoldRoot(classname);
		inst.@io.github.nathanross.gwtrifold.client.GWTriJSNI::recordID(I)(id);
	}-*/;
	
	public native void queueRegistrationJSNI(int ID, String s) /*-{
		$wnd.triFold[ID].el[s] = new Array();
	}-*/;

	public native void registerElementsWithJS(int ID) /*-{
		$wnd.triFold[ID].registerQueuedElements();
	}-*/;

	//JSNI is awful at passing array and the GWT constructs to do so
	//are too unwieldy to justify using them once.
	public native void animate(int ID, int old0, int old1,int old2,
								int new0, int new1,int new2) /*-{
		var oldlayout = [];
		var newlayout = [];
		if (old0 > -1) { oldlayout.push(old0); }
		if (old1 > -1) { oldlayout.push(old1); }
		if (old2 > -1) { oldlayout.push(old2); }
		if (new0 > -1) { newlayout.push(new0); }
		if (new1 > -1) { newlayout.push(new1); }
		if (new2 > -1) { newlayout.push(new2); }
		
		$wnd.triFold[ID].slideLogic.animate(oldlayout, newlayout);
	}-*/;
	
	public native void toggleDefaultManip(
						int ID, int colnum, int status, int speed) /*-{
		var sbname = "switchbar" + colnum.toString();
		if (!($wnd.triFold[ID].el[sbname] instanceof Array)) {
			console.log("something there, but it's not an array");
		}
		if (status == 0) {
			$wnd.triFold[ID].el[sbname][0].style.display = "none";
		//$wnd.el[sbname].stop().slideDown(speed); 
		} else {
			$wnd.triFold[ID].el[sbname][0].style.display = "block";
			//$wnd.el[sbname].slideUp(speed); 
		}
	}-*/; 
	
	public native void display(int ID) /*-{
		//fadeAnim = new FadeAnimation(this);
		if ($wnd.triFold[ID].initialScaleComplete) {
			$wnd.triFold[ID].el["innerParent"][0]
						.style.visibility = "visible";
		} else {
			$wnd.triFold[ID].reqDisplay = true;
		}
	}-*/;
	
	public native void scaleAndClipJSNI(int ID,
			double minMarginX, double extraMarginX, double MinMarginY, 
			double extraMarginY, double scale, double slidespeed,
			double reqWest, double reqNorth, 
			double maxWidth, double usedWidth, double padNorth, 
			double padEast, double padSouth, double padWest,
			double colX, double colY, double colspacing, int left, 
			int cent, int right) /*-{ 
		try {
			$wnd.triFold[ID].slideMotion.scaleAndClip(
			minMarginX, extraMarginX, MinMarginY, extraMarginY, scale, 
			slidespeed, reqWest, reqNorth, 
			maxWidth, usedWidth, padNorth, padEast, padSouth, 
			padWest, colX, colY, colspacing, left, cent, right);
			
			
		  }
		catch(err)
		  {
		  txt="There was an error on this page.";
		  txt+="Error name <" + err.name + "> Error description: <" + err.message + "> ";
		  txt+="Click OK to continue ";
		  console.log(txt);
		  } 
	}-*/;
}