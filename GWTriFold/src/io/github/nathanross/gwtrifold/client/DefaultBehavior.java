/*
 * gwtrifold/client/DefaultBehavior.java
 * Provides parameterized default layout behavior for columns.
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

import java.util.List;
import java.util.Vector;


public class DefaultBehavior implements Behavior {

	static final public int ONE_AVAILABLE = 0;
	static final public int TWO_AVAILABLE = 1;
	static final public int THREE_AVAILABLE = 2;
	static final public int LEFT = 0;
	static final public int CENT = 1;
	static final public int RIGHT = 2;
	static final public int FIXED = 0;
	static final public int SLIDE = 1;
	static final public int CAN_MINIMIZE = 2;
	static final public int NO_ROLLOVER = 0;
	static final public int ROLLOVER = 1;
	static final public int ROLLOVER_OPT = 2;
	
	static final protected int X = 0;
	static final protected int Y = 1;
	static final protected int NORTH = 0;
	static final protected int EAST = 1;
	static final protected int SOUTH = 2;
	static final protected int WEST = 3;
	
	static final protected int HIDDEN = 0;
	static final protected int DISABLED = 1;
	static final protected int ENABLED = 2;
	static final protected int CONTAINER = 0;
	static final protected int BUTTON_ONE = 1;
	static final protected int BUTTON_TWO = 2;

	  private native void consoleLog( String message) /*-{
	  	//doesn't support multiple. disable for release then? 
	  	//behavior stays pretty stable.
		//if ("debugLog" in ($wnd.triFold[0]._proto_ || 
		//					$wnd.triFold[0].constructor.prototype)) {
	 	//	$wnd.triFold[0].debugLog("defaultPol:" + message);
	  	//}
	  	console.log(message);
	 }-*/;
	

	boolean recentResize = true;
	boolean recentSwitch = false;

	protected int []defaultOneCol = new int[]{CENT};
	protected int []defaultTwoCol = new int[]{LEFT, CENT};

	protected int[] orderTier2 = new int[]{LEFT, CENT, RIGHT};
	protected int[][] mem = new int[4][];	
	
	protected int availColumns = 0;
	protected int[] modes = new int[]{CAN_MINIMIZE, CAN_MINIMIZE,
										CAN_MINIMIZE};
	protected int[] aVisTwoCol = new int[]{CENT};
	protected int[] aVisThreeCol = new int[]{CENT};
	protected int rolloverAtTwo = NO_ROLLOVER;
	protected boolean orderedAtThree = true;
	
	public DefaultBehavior() {
		resetToDefaults();
	}
	
	public void resetToDefaults() {
		mem[0] = new int[0];
		mem[1] = copyOf(defaultOneCol);
		mem[2] = copyOf(defaultTwoCol);
		mem[3] = new int[]{LEFT, CENT, RIGHT};		
	}

	public void setDefaultOneCol(int in) { defaultOneCol = new int[]{in}; }
	public int getDefaultOneCol() { return defaultOneCol[0]; }
	
	public void setDefaultTwoCol(int[] in) { defaultTwoCol = copyOf(in); }
	public int[] getDefaultTwoCol() { return copyOf(defaultTwoCol); }
	
	public void setVisTwoCol(int[] in) { aVisTwoCol = copyOf(in); }
	public int[] getVisTwoCol() { return copyOf(aVisTwoCol); }
	
	public void setVisThreeCol(int[] in) { aVisThreeCol = copyOf(in); }
	public int[] getVisThreeCol() { return copyOf(aVisThreeCol); }
	
	public void setOrderedThreeCol(boolean y) { orderedAtThree = y; }
	public boolean getOrderedThreeCol() { return orderedAtThree; }
	
	public void setRolloverTwoCol(int roll) { rolloverAtTwo = roll; }
	public int getRolloverTwoCol() { return rolloverAtTwo; }
	
	public void setMode(int tier, int mode) { modes[tier] = mode; }
	public int getMode(int tier) { return modes[tier]; }
	
	
	//GWT's array.copyOf support has problems.
	protected int[] copyOf(int[] in) { 
		if (in == null) { return null;}
		//can't use generic because can't use new on generic.
		int [] copy = new int[in.length]; 
		for (int i=0; i<in.length; i++) { copy[i] = in[i]; }
		return copy;
	}
	
	protected int indexOf(int[] defaultTwoCol2, int src) {
		for (int i=0;i<defaultTwoCol2.length;i++) 
		{ if (defaultTwoCol2[i] == src) { return i; } }
		return -1;
	}

	protected String debugThreeDimArray(int [][][] in) {
		StringBuilder results = new StringBuilder();
		results.append("[");
		for (int i=0;i<in.length;i++) {
			results.append(debugTwoDimArray(in[i]) + ",");
		}
		results.append("]");
		return results.toString();
	}
	
	protected String debugTwoDimArray(int [][] in) {
		StringBuilder results = new StringBuilder();
		results.append("[");
		for (int i=0;i<in.length;i++) {
			results.append(debugArr(in[i]) + ",");
		}
		results.append("]");
		return results.toString();
	}
	
	protected String debugArr(int[] in) {
		StringBuilder sb= new StringBuilder();
		sb.append("{");
		for (int i=0;i<in.length;i++) {
			if (i > 0) { sb.append(", "); }
			sb.append(Integer.toString(in[i]));
		}
		sb.append("}");
		return sb.toString();
	}
	
	
	public void onWidgetUse() {
		/* callbacks triggered when a user begins using the app
		 * with blocks for:
		 * 	the first time they do after resizing the window
		 *  the first time they do after minimizing, maximizing, 
		 *  	or switching out a column.
		 * 'using' could mean using any widget but those to minimize, max., 
		 * 	or switch a columns.
		 */
		
		if (recentResize) {
			recentResize = false;
			if (availColumns != 1) {
				if (!(mem[1][0] == CENT)) { mem[1] = copyOf(defaultOneCol);  }
			} else if (availColumns != 2 && mem[2].length < 2) {
				mem[2] = copyOf(defaultTwoCol);
			} else if (availColumns != 3 && mem[3].length < 3) {
				mem[3] = new int[]{LEFT, CENT, RIGHT};
			}
		}
		if (recentSwitch) { //recentSwitch is cancelled out by a resize.
			recentSwitch = false;
			if (availColumns == 3 && mem[3].length == 2) {
				mem[2] = copyOf(mem[3]);
			}
			
		}
	}
	
	@Override
	public int[] getLayout(int availColumns) {
		return copyOf(mem[availColumns]);
	}
	
	protected int[] allHidden() 
	{ return new int[]{HIDDEN, HIDDEN, HIDDEN}; }
	protected int[] disabledContainer() 
	{ return new int[]{HIDDEN, HIDDEN, HIDDEN}; }
	protected int[] enabledAll() 
	{ return new int[]{ENABLED, ENABLED, ENABLED}; }
	protected int[] enabledLeft() 
	{ int[] en = enabledAll(); en[BUTTON_TWO] = HIDDEN; return en; }
	protected int[] enabledRight() 
	{ int[] en = enabledAll(); en[BUTTON_ONE] = HIDDEN; return en; }


	@Override
	public int[][] getButtonStatus(int availColumns) {

		int [] curLayout = mem[availColumns];
		int[][] manipState = new int[][]{
							allHidden(), allHidden(), allHidden()};

		if (modes[availColumns-1] == FIXED) { return manipState; }
		if (availColumns == 1){
			manipState = new int[][]{enabledAll(), enabledAll(), enabledAll()};
		} else if (availColumns == 2) {
			if (modes[TWO_AVAILABLE] == SLIDE) {
				if (aVisTwoCol.length == 1) {
					int fk = aVisTwoCol[0];
					if (rolloverAtTwo == ROLLOVER_OPT) {
						manipState[fk] = enabledAll();
					} else {
						manipState[fk] = (curLayout[0] == fk)?
											enabledRight(): enabledLeft();
					}
				} else {
					manipState[curLayout[0]] = enabledRight();
					manipState[curLayout[1]] = enabledLeft();
				}				
			} else {
				if (aVisTwoCol.length == 1) {
					manipState[aVisTwoCol[0]] = enabledAll();
				} else {
					manipState = new int[][]{enabledAll(), enabledAll(),
											enabledAll()};
				}
			}
		} else if (availColumns == 3) {
			if (modes[THREE_AVAILABLE] == SLIDE) {
				manipState[curLayout[0]] = enabledRight();
				manipState[curLayout[1]] = enabledAll();
				manipState[curLayout[2]] = enabledLeft();
			} else {
				if (aVisThreeCol.length == 0) {
					manipState = new int[][]{enabledAll(), enabledAll(), 
											enabledAll()};
				} else if (aVisThreeCol.length == 1) {
					manipState[aVisThreeCol[0]] = enabledAll();
				} else {
					int hidable=3;
					hidable -= (aVisThreeCol[0] + aVisThreeCol[1]);
					for (int i=0;i<2;i++) {
						manipState[aVisThreeCol[i]] =
							((hidable == 2 ||
							(hidable == 1 && aVisThreeCol[i] == 2))?
							enabledRight() : enabledLeft());
					}
				}
			}
		}
		consoleLog("got manip state in modes " + debugArr(modes) +
				" for mem " + debugTwoDimArray(mem) + 
				" it was:" + debugTwoDimArray(manipState));
		return manipState;
	}
	
	public DefaultBehavior copy() {
		//does NOT copy current mem[][] values.
		//not nec. though - only called during setBehavior()
		DefaultBehavior echo = new DefaultBehavior();
		echo.setDefaultOneCol(defaultOneCol[0]);
		echo.setDefaultTwoCol(defaultTwoCol);
		echo.setMode(ONE_AVAILABLE, modes[ONE_AVAILABLE]);
		echo.setMode(TWO_AVAILABLE, modes[TWO_AVAILABLE]);
		echo.setMode(THREE_AVAILABLE, modes[THREE_AVAILABLE]);
		echo.setVisTwoCol(aVisTwoCol);
		echo.setVisThreeCol(aVisThreeCol);
		echo.setRolloverTwoCol(rolloverAtTwo);
		echo.setOrderedThreeCol(orderedAtThree);
		return echo;
	}

	protected Integer intgr(int i) { return Integer.valueOf(i); }
	
	@Override
	public void onManipUse(int src, int keycode) {
		/* this is called after a column switch button is
		 *  pressed or you call the column switch callback 
		 *  through a widget.*/

		int srcpos, toadd, tgtpos;
		consoleLog("polColSwitch: running Behavior column switch."
				+ "At beginning of behavior call,"
				+ "mem looked like this : " + debugTwoDimArray(mem));
		recentSwitch = true;
		
		List<Integer> results = new Vector<Integer>();
		for (int i=0;i<mem[availColumns].length;i++)
			{ results.add(mem[availColumns][i]); }	
		int column = keycode;
		if (src == 0 || src == 1 && keycode == 1) { column = keycode + 1; }
		
		consoleLog("manip -> src:" + Integer.toString(src) + 
				"keycode" + Integer.toString(keycode) +
				"column" + Integer.toString(column));
		srcpos = 0;
		if (results.size() > 1 && results.get(1) == src) { srcpos = 1; }
		if (availColumns == 1) {
			results.set(0, column);
		} else if (availColumns == 2) {
			if (modes[TWO_AVAILABLE] == SLIDE) {
				if (aVisTwoCol.length == 2) {
					results.add(results.get(0));
					results.remove(0);
				} else {
					toadd = 3 - (src + results.get(1-srcpos));
					if (rolloverAtTwo == ROLLOVER) {
						results.set(1-srcpos, src);
						results.set(srcpos, toadd);
					} else {
						results.set(1-srcpos, toadd);
					}
				}
			} else if (results.contains(intgr(column))) {
				results.remove(intgr(column));
			} else {
				if (aVisTwoCol.length == 1) {
					tgtpos = 1-indexOf(defaultTwoCol, src);
					if (rolloverAtTwo == ROLLOVER &&
							indexOf(defaultTwoCol, column) == -1) {
						tgtpos = 1-tgtpos;
					}
				} else {
					tgtpos = 1-srcpos;
					if (rolloverAtTwo == ROLLOVER && results.size() == 2)
					{ tgtpos = srcpos; }
				}
				results.clear();
				results.add(intgr(src));
				results.add(intgr(src));
				results.set(tgtpos, intgr(column));
			}
		} else if (availColumns == 3) {
			consoleLog("manip -> three columns available");
			if (results.size() > 2 && results.get(2) == src) { srcpos = 2; }
			if (modes[THREE_AVAILABLE] == SLIDE) {
				consoleLog("manip -> mode == slide");
				results.remove(src);
				results.add(srcpos + ((keycode == 1)?1:-1), src);
			} else if (results.contains(intgr(column))) {
				results.remove(intgr(column));
			} else {
				if ((!orderedAtThree && (aVisThreeCol.length == 0 ||
						(src==0||(src==1 && results.size()==2))))
					|| (orderedAtThree && column == 2)) {
					results.add(intgr(column));
				} else if (!orderedAtThree
						|| (column == 0 || results.get(0) == 2)) {
					results.add(0,intgr(column));
				} else {
					results.add(1,intgr(1));
				}
			}
		}

		mem[availColumns] = new int[results.size()];
		for (int i=0;i<results.size();i++) {
			//gwt has trouble with .toArray
			mem[availColumns][i] = results.get(i);
		}
		consoleLog("polColSwitch: finishing Behavior column switch."
				+ "At end of behavior call,"
				+ "mem looked like this : " + debugTwoDimArray(mem));
	}
	
	@Override
	public void onAvailChange(int newcolavail) {
		/* changes in column memories that occur as soon as a
		 * larger or smaller number of columns are available
		 * due to resizing. changes to memory here can affect the columns
		 * loaded at the new tier. */
		recentResize = true;
		recentSwitch = false;
		consoleLog("beginning Behavior AV change. At beginning of behavior"
				+ "call, mem looked like this : " + debugTwoDimArray(mem));
		
		if (newcolavail == availColumns) { return; } 
		
		//if we ever allow default avail3 to be l.t. 3 length we might
		// have to ensure that layout 3 isn't automatic at start up...
		// otherwise this will autocopy layout 3 to 2.
		if (availColumns == 3 && newcolavail == 2 && mem[3].length <= 2) {
			mem[2] = copyOf(mem[3]);
		//} else	if 
		//   (availColumns == 1 && newcolavail == 2 && mem[1][0] == CARDCOL) {
		//	mem[2] = ;
		}		
		availColumns = newcolavail;
		consoleLog("finishing Behavior AV change. At end of behavior call,"
				+ "mem looked like this : " + debugTwoDimArray(mem));
	}
	
}