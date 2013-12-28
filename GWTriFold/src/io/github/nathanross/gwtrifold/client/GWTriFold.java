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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;



public class GWTriFold {

	
	
	/* ------- Configuration hooks ------- */

	
	//appearance options.
	
	
	public void setMinColDimensions(double in[]) 
		{ xyMincolres = copyOf(in); }
	public double [] getMinColDimensions()
		{ return copyOf(xyMincolres); }
	
	public void setPadding(double in[]) { quadPadding = copyOf(in); }
	public final double [] getPadding() { return copyOf(quadPadding); }
	
	public void setColSpacing(double in) { colspacing = in; }
	public double getColSpacing() { return colspacing; }

	public void setMargins(double in[]) { quadMargin = copyOf(in); }
	public double [] getMargins() { return copyOf(quadMargin); }
	
	public void setFade(boolean fade) { this.fade = fade; }
	public boolean getFade() { return fade; }
	
	public void setSlidespeed(int in) { slidespeed = in; }
	public int getSlidespeed() { return slidespeed; }

	public void setSwitchbarSlidespeed(int in) { switchbarSlidespeed = in; }
	public int getSwitchbarSlidespeed() { return switchbarSlidespeed; }	
	

	
	// ----- enum-like constants -----
	
	static final public Integer LEFT = Integer.valueOf(0);
	static final public Integer CENT = Integer.valueOf(1);
	static final public Integer RIGHT = Integer.valueOf(2);

	static final protected int X = 0;
	static final protected int Y = 1;
	static final protected int NORTH = 0;
	static final protected int EAST = 1;
	static final protected int SOUTH = 2;
	static final protected int WEST = 3;
	

	// ----- tri-fold parameters ----
	
	// -- Shape values. --
	/* doubles are used to represent pixels because some browsers
	* support subpixel lengths, and almost all modern browsers degrade 
	* gracefully with them. */
	
	protected double [] xyMincolres = new double[]{240,400};
	protected double [] quadPadding = new double[]{15,10,15,10};
	protected double [] quadMargin = new double[]{20, 30,40,30};

	// -- animation values --
	protected double colspacing = 10;
	protected int slidespeed = 2500;
	//protected int fadespeed = 200;
	protected int switchbarSlidespeed = 800;
	protected boolean fade = false;
	
	// -- Policy values --
	Policy policy;
	
	//if you override the policy functions, these values will
	//only see use if you want to make use of them.

	// ----- status indicators -----
	
	//automated policy values updated by callbacks, rather
	//than options. Unlike the above policy options,
	//these don't need a call from the dev. to change.
	protected boolean recentResize = false;
	protected boolean recentSwitch = false; //recent switch, min., or max.
	
	// ---- internal variables -----
	
	//these variables' values are used in places other than policy.
	
	protected int availColumns = 0; //# cols avail. at last resize.
	
	//mem[number of columns available][leftmost displayed column...]
	//keep in mind the columns of a tier may be less than those available to use.

	
	//-- UI handles --
	
	//protected FadeAnimation fadeAnim = null 
	protected ComplexPanel columnPanels[];
	protected ComplexPanel outerParent;  //columns + padding + margin.
	protected ComplexPanel innerParent;	//columns + padding
	//prevent screensize detection until page is fully loaded.
	private boolean responseLocked = true;
	
	//You should note! not the same structure as mem!!
	//switchBarState[tier][LEFT, CENT, RIGHT]
	protected boolean [][] switchbarState = new boolean[4][];

	
	private double[] copyOf(double[] in) { 
		if (in == null) { return null;}
		double [] copy = new double[in.length];
		for (int i=0; i<in.length; i++) { copy[i] = in[i]; }
		return copy;
	}

	
	/*private native void setCssNPK(String keyword, String csskey, String value) /*-{		
		$wnd.checkElmap("npk"+keyword, keyword);
		$wnd.el["npk"+keyword].css(csskey, value);
	 }-*/ //;
	
	/* private native String getCssNPK(String keyword, String csskey) /*-{
		$wnd.checkElmap("npk"+keyword, keyword);
		return $wnd.el["npk"+keyword].css(csskey);
	}-*/ //;
	
	//currently the highest perf. way I've found to wire GWT objects
	//into javascript GWT Java-to-JS obj wrappers can actually
	//get pretty costly. I think in the future maybe implement everything
	//in between outerPanel and colContent in js. Because though
	//there are nice features of the setup where the developer
	//can interface with the inbetween components via GWT
	//practically most won't be doing that.
	
	private void addClass(Widget w, String keyword) {
		w.setStyleName("tri-fold_" + keyword, true);
		jsniCalls.queueRegistrationJSNI(jsniCalls.ID, keyword);
	}
	
	GWTriJSNI jsniCalls;
	ComplexPanel[] contentPanels;
	public GWTriFold(Policy policy,ComplexPanel outerParent,
			ComplexPanel leftp, ComplexPanel centp, ComplexPanel rightp) {
		this.policy = policy;

		String prefix = "tri-fold_";
		this.outerParent = outerParent;
		this.outerParent.setStyleName(prefix+"outerParent",true);
		jsniCalls = new GWTriJSNI(this);
		contentPanels = new ComplexPanel[]{leftp, centp, rightp};
		jsniCalls.createTriFoldAtRoot(prefix+"outerParent",
										jsniCalls);
	}
	//TODO 0.2 get this out W/O using an interface.
	private boolean jsSetOnce = false;
	public void jsSet() {
		if (jsSetOnce) { return; } //failsafe against dev calls.

		jsSetOnce = true;
		innerParent = new FlowPanel();
		addClass(innerParent, "innerParent");
		this.outerParent.add(innerParent);

		FlowPanel relWrapper = new FlowPanel();
		addClass(relWrapper, "innerParentRel");
		innerParent.add(relWrapper);
		jsniCalls.hideInnerParent(jsniCalls.ID);
		
		
		columnPanels = new ComplexPanel[]{
				new FlowPanel(), new FlowPanel(), new FlowPanel()};
		
		for (int i=0;i<3;i++) {
			String colname = "col" + Integer.toString(i);
			addClass(columnPanels[i], colname);
			addClass(columnPanels[i], "cols");
			relWrapper.add(columnPanels[i]);
			
			ComplexPanel colRel = new FlowPanel();
			columnPanels[i].add(colRel);
			addClass(colRel, "colRel");
			
			colRel.add(contentPanels[i]);
			addClass(contentPanels[i], "colContent");
			
			FlowPanel switchbar = new FlowPanel();
			colRel.add(switchbar);
			String sbname = "switchbar" + Integer.toString(i);
			addClass(switchbar, sbname);
			addClass(switchbar, "switchbar");
			
			FlowPanel sbRel = new FlowPanel();
			switchbar.add(sbRel);
			addClass(sbRel, "switchbarRel");
			
			int buttonNo=0;
			DefaultManip sbManip = new DefaultManip(i);
			int k=0;
			for (int j=0;j<3;j++) {
				if (i != j) {
					
					PushButton p = new PushButton("");
					p.addClickHandler(
							new ManipClickHandler(this, k, i));
					sbManip.addButton(p);
					sbRel.add(p);
					addClass(p, "switchbarBtn");
					addClass(p, "switchbarBtnSide" 
								+ Integer.toString(buttonNo));
					addClass(p, "buttonCol" + Integer.toString(j));
					buttonNo++; 
					k++;
				}
			}
			manips[i] = sbManip;
		}

		jsniCalls.registerElementsWithJS(jsniCalls.ID);

	    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        @Override
	        public void execute() {
	        	responseLocked = false;
	    		resizeCallback(new double[0]);
	        }
	    });
	}
	
	public void display() {
		jsniCalls.display(jsniCalls.ID);
	}
	
	//resets app as if started with current options.
	//changing some options, like Ordered, can mandate multiple
	//changes to layout, arrangement, visibility, causing lots of processor
	//intensive error checking. If you change multiple of these layout-changing
	//options at once, and these options auto-updated the UI, it'd waste
	//client processor time. Instead, once done changing layout options,
	//you're asked to signify that you want to apply changes.
	
	//note that this function changes the layout as if the user had just loaded
	//the default swlayout for their current number of avail
	public void setPolicy(Policy in) {
		int[] oldLayout = this.policy.getLayout(availColumns);
		int[] oldButtons = this.policy.getButtonStatus(availColumns);
		this.policy = in.copy();
		int[] newLayout = this.policy.getLayout(availColumns);
		int[] newButtons = this.policy.getButtonStatus(availColumns);
		animate(oldLayout, newLayout);
		for(int i=0;i<3;i++) {
			if (oldButtons[i] != newButtons[i]) {
				manips[i].setStatus(newButtons[i]);
			}
		}
	}

	//JSNI is awful at passing array and the GWT constructs to do so
	//are too unwieldy to justify using them once.
	public void animate(int[]oldlayout, int[]newlayout){
		int[] a = new int[]{-1,-1,-1};
		int[] b = new int[]{-1,-1,-1};
		for (int i=0;i<3;i++) {
			if (oldlayout.length > i) { a[i] = oldlayout[i]; }
			if (newlayout.length > i) { b[i] = newlayout[i]; }
		}
		jsniCalls.animate(jsniCalls.ID, a[0], a[1], a[2], b[0], b[1], b[2]);
	}

	abstract public class LayoutManip {
		static final int ENABLED = 2;
		static final int DISABLED =1;
		static final int HIDDEN=0;
		private int status = 0;
		public void setStatus(int status) { this.status = status; }
		public int getStatus() { return this.status; }
		abstract public CustomButton getButton(int num);
	}
	
	
	public class DefaultManip extends LayoutManip {
		private PushButton[] btns = new PushButton[]{null,null};
		private int colno;
		
		public DefaultManip(int colno) { this.colno = colno; }
		public void addButton(PushButton btn) {
			if (this.btns[0] == null) { this.btns[0] = btn;}
			else { this.btns[1] = btn; }
		}
		@Override
		public void setStatus(int status) {
			super.setStatus(status);
			consoleLog("defaultManip set status");
			jsniCalls.toggleDefaultManip(jsniCalls.ID,
							colno, status, switchbarSlidespeed);	
			if (status == 2) {
				btns[0].setEnabled(true);
				consoleLog("button0 passed");
				btns[1].setEnabled(true);
				consoleLog("button1 passed");
			} else {
				btns[0].setEnabled(false);
				consoleLog("button0 passed");
				btns[1].setEnabled(false);	
				consoleLog("button1 passed");			
			}
		}
		
		@Override
		public CustomButton getButton(int num) {
			return btns[num];
		}
	}
	
	private LayoutManip[] manips = new LayoutManip[3];
	
	public void setButtons(Integer colnum, LayoutManip replace) {
		replace.setStatus(manips[colnum].getStatus());
		for (int i=0;i<2;i++) {
			replace.getButton(0).addClickHandler(
					new ManipClickHandler(this, i, colnum));
		}
		manips[colnum] = replace;
	}
	
	
	public class ManipClickHandler implements ClickHandler {
		int keycode;
		int src;
		GWTriFold response;
		public ManipClickHandler(GWTriFold response, 
										int keycode, 
										int src) {
			consoleLog("new switchbar Click Handler instantialized");
			this.keycode = keycode;
			this.src = src;
			this.response = response;
		}
		
		public void onClick(ClickEvent e) {
			consoleLog("switchbar click handler detected click on column " + Integer.toString(src)
					+ " with target " + Integer.toString(keycode));
			if (manips[src].getStatus() == LayoutManip.ENABLED) 
			{ response.callbackManipButton(keycode, src); }
		}	
	}
	
	
	private void callbackManipButton(int keycode, int src) {
		consoleLog("callbackColumnSwitch called");
		int [] oldlayout = policy.getLayout(availColumns);
		int [] oldButtons = policy.getButtonStatus(availColumns);
		policy.onManipUse(keycode, src);
		int [] newlayout = policy.getLayout(availColumns);
		int [] newButtons = policy.getButtonStatus(availColumns);
		animate(oldlayout, newlayout);
		for (int i=0; i<3; i++) {
			if (newButtons[i] != oldButtons[i]) { 
				manips[i].setStatus(newButtons[i]); 
			} 
		}
	}


	private void consoleLog(String message) {
		jsniCalls.log(jsniCalls.ID, "triF:" + message);
	}
	
	
	private void callbackAvChange(int newcolavail) {
		//meant to be policy agnostic.

		if (newcolavail == availColumns) { return; }
		
		int [] tmplayout;
		int [] tmpButtons;
		if (availColumns == 0) {
			tmplayout = new int[0];
			tmpButtons = new int[]{LayoutManip.HIDDEN, LayoutManip.HIDDEN,
									LayoutManip.HIDDEN};
		} else {
			tmplayout = policy.getLayout(availColumns);
			tmpButtons = policy.getButtonStatus(availColumns);
		} 
		policy.onAvailChange(newcolavail);
		int [] newlayout = policy.getLayout(newcolavail);
		int [] newButtons = policy.getButtonStatus(newcolavail);

		consoleLog("AV change: columns avail before: " + 
							Integer.toString(tmplayout.length) 
							+ " columns avail after:" + 
							Integer.toString(newlayout.length));
		//consoleLog("AVchange: buttonsBefore:" + buttonDebug(tmpButtons)
		//		+ " buttonsAfter:" + buttonDebug(newButtons));
		for (int i=0; i<3; i++) {
			if (newButtons[i] != tmpButtons[i]) { 
				manips[i].setStatus(newButtons[i]); 
			} 
		}
		
		animate(tmplayout,newlayout);
		availColumns = newcolavail;
	}

	/*private TriFoldCallback echoCallback;
	public void setCallback(TriFoldCallback callback) { echoCallback = callback;}
	somethin
	public interface TriFoldCallback {
		public void onResize(double scale, double [] firstcolumnCoord,
							double [] secondColumnCoord, double [] thirdColumnCoord);
	} */

	 
	
	boolean scaleInitialized = false;
	public void resizeCallback(double [] freeCoords) {

		if (responseLocked) { return; }
		if (freeCoords.length == 0){
			jsniCalls.getParentDimensions(jsniCalls.ID, jsniCalls);
			return;
		}
		double [] now = freeCoords;

		consoleLog(" ---- BEGIN RESIZE CALLBACK ----");
		//mincolres:
		//the absolute minimum width and height necessary such that
		//an entire column's contents could be visible and readable
		//without a mash of pixels.
		
		//width by height
		double k_tmp = now[X] / now[Y];
		double k_cols[] = new double[4];
		double x_min[] = new double[4]; //used to ensure that when the scale is brought up to 1,
											//the number of columns is correct for scale 1.
		double [] xyPaddingSum = new double [] {
				quadPadding[WEST] + quadPadding[EAST],
				quadPadding[NORTH] + quadPadding[SOUTH]};
		double [] xyMarginSum = new double [] {
				quadMargin[WEST] + quadMargin[EAST],
				quadMargin[NORTH] + quadMargin[SOUTH]};
		for (int i=1; i<4; i++) {
			k_cols[i] = 
				(xyMincolres[X]*i + xyPaddingSum[X] + (i-1)*colspacing + xyMarginSum[X]) /
				(xyMincolres[Y] + xyPaddingSum[Y] + xyMarginSum[Y]);
			x_min[i] = (xyMincolres[X]*i + xyPaddingSum[X] + (i-1)*colspacing + xyMarginSum[X]);
		}
		//0.4 would mean I want 40% of the margin to be at the top.
		double reqNorth = quadMargin[NORTH] / xyMarginSum[Y];
		double reqWest = quadMargin[WEST] / xyMarginSum[X];
		
		
		//calculating it without margin is to support cases where hardMinMargin == false.
		//if we allow hardMinMargin to be true, we should startcalculating it with margin.
		//remember: hardMinMargin is whether the margin stays in some cases of
		//extremely low resolution.
		//hardMinMargin = false doesn't mean there's never a hard margin, just that it's
		//only partially implemented. So for example, at tier two, even with hardMinMargin=false,
		//as its currently set up the screen would rather have a scrollbar and push the right margin
		//off the edge of the screen than decrease both margins (opposite of hardMinMargin, I'd imagine)
		//or bump down to tier one (which would be hardMinMargin behavior, if we added a situation
		//below where k_cols and k_tmp were calculated with margin.
		
		//eta: scratch former notes here, we're now calculating with margin for k_cols.
		//resultant behavior: hardMinMargin = true: screen will always bump down to lower tier
		// or, if it can't do that, scroll, than decrease margins past ratio. hardMinMargin = false:
		// that same behavior will occur in most cases, however in select cases where reducing axis
		// length brings the axis Below scale 1 limit, (Y axis) and (X-axis at tier 1) margins will
		// be decreased before scrolling. 
		int colnum;
		if (k_tmp < k_cols[2] || now[X] < x_min[2]) {
			colnum = 1;
		} else if (k_tmp < k_cols[3] || now[X] < x_min[3])  {
			colnum = 2;
		} else {
			colnum = 3;
		}
		
		double [] inrvis = new double [] {
			xyMincolres[X]*colnum + xyPaddingSum[X] + (colnum-1)*colspacing,
			xyMincolres[Y] + xyPaddingSum[Y]};
		double [] scale1limit = new double [] {
			inrvis[X] + xyMarginSum[X], inrvis[Y] + xyMarginSum[Y]};
		
		double scale = 0;
		
		/* marginMin: at least this much length along this axis
		* is reserved such that a column or border never appears there as
		* long as the resolution is the same.
		* if you just resized horizontally inward, columns or borders
		* moving inward will be pushed ahead to not take up this margin's space.
		*
		* marginExtra: until a screen is resized, a border (and possibly 
		* column portion) might take up space in this area, but always on a 
		* temporary basis e.g. folding in. marginExtra is only ever greater
		* than 0 for one axis at a time, this is because marginExtra
		* is the result of the screen rarely being resized in
		* exact scaled proportion to the minimum resolution.
		*
		* when the user minimizes a column (if they can), activities might take
		* place in the new space that might normally occur within the margins
		* but for terminological purposes here, those would not be considered
		* part of marginMax because it's possible you could have stable padding
		* and column there. */
		
		double [] marginExtra = new double[]{0,0};
		double [] marginMin = new double[]{0,0};
		boolean hardMinMargin = false; //TODO: enable setting as param.
		
		if (now[X] <= scale1limit[X] || now[Y] <= scale1limit[Y]) {
			scale = 1;
		} else {
			scale = Math.min(
				now[X]/scale1limit[X], now[Y]/scale1limit[Y]);
		}
		
		double [] debugArgs1 = new double[]{now[X], now[Y],
				scale, scale1limit[X], scale1limit[Y], 
				k_tmp, k_cols[2], k_cols[3], x_min[2], x_min[3], colnum,
				scale1limit[X]*scale, scale1limit[Y]*scale};
		String [] debugDesc1 = new String[]{"now[X]", "now[Y]",
				"scale", "scale1limit[X]", "scale1limit[Y]", 
				"k_tmp", "k_cols[2]", "k_cols[3]", "x_min[2]",
				"x_min[3]", "colnum", "scale1limit[X]*scale", 
				"scale1limit[Y]*scale"};
		for(int i=0;i<debugArgs1.length; i++) { 
			consoleLog(debugDesc1[i] + ":\t" 
			+ Double.toString(debugArgs1[i]));
		}
		
		for (int i : new int[]{X,Y}) {
			if (now[i] <= scale1limit[i]) {
				if (i == X) { consoleLog("scale-limited by X or scale is 1"); }
				else { consoleLog("scale-limited by Y or scale is 1"); }
				marginExtra[i] = 0;
				if (!hardMinMargin && (i == Y || colnum == 1)) 
				{ marginMin[i] = Math.max(0,now[i] - scale*inrvis[i]); }
				else 
				{ marginMin[i] = scale*xyMarginSum[i]; }
			} else {
				marginMin[i] = scale*xyMarginSum[i];
				marginExtra[i] = now[i] - (scale*scale1limit[i]);
			}
		}

		int [] present = new int[]{0,0,0};
		double maxWidth = now[X] - (marginMin[X] + scale*xyPaddingSum[X]);
		double usedWidth = inrvis[X]; //currently an unused and misleadingly named value.
		if (scaleInitialized) {
			if (availColumns != colnum) {
				callbackAvChange(colnum);
			}
			int[] curLayout = policy.getLayout(availColumns);
			for (int i=0; i<curLayout.length; i++)
				{present[curLayout[i]] = 1; } //depends on LEFT being 0, CENT being 1... etc. fix that.
		}
		
		double [] debugArgs2 = new double[]{marginMin[X], marginExtra[X], marginMin[Y],
				marginExtra[Y], scale, slidespeed, reqWest, reqNorth, maxWidth, usedWidth,
				scale*quadPadding[NORTH], scale*quadPadding[EAST], 
				scale*quadPadding[SOUTH], scale*quadPadding[WEST], 
				xyMincolres[X], xyMincolres[Y], 
				scale*colspacing, present[LEFT], present[CENT], present[RIGHT]};
		String [] debugDesc2 = new String[]{"marginMin[X]", "marginExtra[X]", "marginMin[Y]",
				"marginExtra[Y]", "scale", "slidespeed", "reqWest", "reqNorth", "maxWidth", "usedWidth",
				"scale*quadPadding[NORTH]", "scale*quadPadding[EAST]", 
				"scale*quadPadding[SOUTH]", "scale*quadPadding[WEST]", 
				"xyMincolres[X]", "xyMincolres[Y]", 
				"scale*colspacing", "present[LEFT]", "present[CENT]", "present[RIGHT]"};
		for(int i=0;i<debugArgs2.length; i++) { 
			consoleLog(debugDesc2[i] + ":\t" + 
				Double.toString(debugArgs2[i]));
		}
		jsniCalls.scaleAndClipJSNI(jsniCalls.ID, marginMin[X], marginExtra[X], marginMin[Y],
				marginExtra[Y], scale, slidespeed, reqWest, reqNorth, maxWidth, usedWidth,
				scale*quadPadding[NORTH], scale*quadPadding[EAST], 
				scale*quadPadding[SOUTH], scale*quadPadding[WEST], 
				xyMincolres[X], xyMincolres[Y], 
				scale*colspacing, present[LEFT], present[CENT], 
							      present[RIGHT]); 
		if (!scaleInitialized) {
			callbackAvChange(colnum);
			scaleInitialized = true;
		}

		consoleLog(" ---- END RESIZE CALLBACK ----");
	}

}