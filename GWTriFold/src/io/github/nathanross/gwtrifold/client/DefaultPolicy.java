/*
 * gwtrifold/client/DefaultPolicy.java
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


public class DefaultPolicy implements Policy {

	static final public int ONE_AVAILABLE = 1;
	static final public int TWO_AVAILABLE = 2;
	static final public int THREE_AVAILABLE = 3;
	static final public int LEFT = 0;
	static final public int CENT = 1;
	static final public int RIGHT = 2;

	static final protected int X = 0;
	static final protected int Y = 1;
	static final protected int NORTH = 0;
	static final protected int EAST = 1;
	static final protected int SOUTH = 2;
	static final protected int WEST = 3;

	  private native void consoleLog( String message) /*-{
	  	//doesn't support multiple. disable for release then? seems the sort of
	  	//thing that stays pretty stable.
	  	$wnd.triFold[0].debugCall("defaultPol:" + message);
	  }-*/;
	

	boolean recentResize = true;
	boolean recentSwitch = false;

	protected int []defaultOneCol = new int[]{CENT};
	protected int []defaultTwoCol = new int[]{RIGHT, CENT};

	protected int[][] mem = new int[4][];	
	protected boolean[][] switchbarState = new boolean[4][];
	
	int availColumns;
	
	protected int fixedColumn = CENT;
	protected boolean fixedAtTwo = true;
	protected boolean orderedAtTwo = true;
	protected boolean fixedAtThree = true;
	protected boolean orderedAtThree = true;
	protected boolean canMinimize = true;

	public DefaultPolicy() {
		mem[1] = copyOf(defaultOneCol);
		mem[2] = copyOf(defaultTwoCol);
		mem[3] = new int[]{LEFT, CENT, RIGHT};
		switchbarChanged();
	}

	public void setCanMinimize(boolean canMinimize) 
		{ this.canMinimize = canMinimize; switchbarChanged(); }
	public boolean getCanMinimize() { return canMinimize; }
	
	public void setDefaultOneCol(int[] in) { defaultOneCol = copyOf(in); }
	public int[] getDefaultOneCol() { return copyOf(defaultOneCol); }
	

	public void setDefaultTwoCol(int[] in) { defaultTwoCol = copyOf(in); }
	public int[] getDefaultTwoCol() { return copyOf(defaultTwoCol); }

	public void setFixedColumn(int in) 
		{ fixedColumn = in;  switchbarChanged(); }
	public int getFixedColumn() { return fixedColumn; }

	public void setIsFixedAt(int tier, boolean fixed) { 
		if (tier == TWO_AVAILABLE) {fixedAtTwo = fixed; } 
		else if (tier == THREE_AVAILABLE) { fixedAtThree = fixed; }
		 switchbarChanged();
	}
	public boolean getIsFixedAt(int tier, boolean fixed) {
		if (tier == TWO_AVAILABLE) { return fixedAtTwo;} 
		else { return fixedAtThree; }
	}
	
	public void setAlwaysOrdered(int tier, boolean ordered) { 
		if (tier == TWO_AVAILABLE) {orderedAtTwo = ordered;} 
		else if (tier == THREE_AVAILABLE) { orderedAtThree = ordered; }
	}
	
	public boolean getAlwaysOrdered(int tier) {
		if (tier == TWO_AVAILABLE) { return orderedAtTwo;} 
		else { return orderedAtThree; }
	}
	
	//GWT's array.copyOf support has problems.
	protected int[] copyOf(int[] in) { 
		if (in == null) { return null;}
		int [] copy = new int[in.length];
		for (int i=0; i<in.length; i++) { copy[i] = in[i]; }
		return copy;
	}
	
	protected String debugMem() {
		StringBuilder results = new StringBuilder();
		for (int i=1;i<4;i++) {
			results.append("[");
			for (int j=0; j<mem[i].length; j++) {
				results.append(Integer.toString(mem[i][j]) + ",");
			}
			results.append("],");
		}
		return results.toString();
	}
	
	protected String debugArr(int[] in) {
		StringBuilder sb= new StringBuilder();
		sb.append("{");
		for (int i=0;i<in.length;i++) {
			if (i > 0) { sb.append(", "); }
			consoleLog(Integer.toString(in[i]));
			sb.append(Integer.toString(in[i]));
		}
		sb.append("}");
		return sb.toString();
	}

	protected void switchbarChanged() {
		switchbarState[1] = new boolean[]{false,false,false};
		switchbarState[2] = new boolean[]{true, true, true};
		switchbarState[3] = new boolean[]{false,false,false};
		if (canMinimize) {
			if (fixedAtThree) { switchbarState[3][fixedColumn] = true; }
			else { switchbarState[3] =  new boolean[]{true,true,true}; }
		} 
		if (fixedAtTwo) { 
			switchbarState[2] = new boolean[]{false,false,false};
			switchbarState[2][fixedColumn] = true;
		}
	}
	
	@Override
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
	
	@Override
	public int[] getButtonStatus(int availColumns) {
		int results[] = new int[3];
		for (int i=0;i<3;i++) {
			results[i] = (switchbarState[availColumns][i])?
					GWTriFold.LayoutManip.ENABLED:
					GWTriFold.LayoutManip.HIDDEN;
		}
		return results;
	}
	
	@Override
	public Policy copy() {
		DefaultPolicy echo = new DefaultPolicy();
		echo.setDefaultOneCol(defaultOneCol);
		echo.setDefaultTwoCol(defaultTwoCol);
		echo.setCanMinimize(canMinimize);
		echo.setFixedColumn(fixedColumn);
		echo.setIsFixedAt(TWO_AVAILABLE, orderedAtTwo);
		echo.setIsFixedAt(THREE_AVAILABLE, orderedAtThree);
		echo.setAlwaysOrdered(TWO_AVAILABLE, orderedAtTwo);
		echo.setAlwaysOrdered(THREE_AVAILABLE, orderedAtThree);
		return echo;
	}

	protected Integer intgr(int i) { return Integer.valueOf(i); }
	
	@Override
	public void onManipUse(int keycode, int src) {
		/* this is called after a column switch button is
		 *  pressed or you call the column switch callback 
		 *  through a widget.*/

		consoleLog("polColSwitch: running Policy column switch."
				+ "At beginning of policy call,"
				+ "mem looked like this : " + debugMem());
		recentSwitch = true;
		
		List<Integer> results = new Vector<Integer>();
		for (int i=0;i<mem[availColumns].length;i++) {
			results.add(mem[availColumns][i]);
		}	
		if (canMinimize) {
			int column = keycode;
			if (src == 0) { column = keycode + 1; }
			if (src == 1 && keycode == 1) { column = 2; }
			recentSwitch = true;
			int pos=0;
			if (results.contains(column)) {
				if (canMinimize) { results.remove(Integer.valueOf(column)); }
				else { recentSwitch = false; }
			} else if (availColumns == 3) {
				if (orderedAtThree) {
					if (column == CENT && results.get(0) == LEFT) { pos=1; }
					else if (column == RIGHT) { pos=results.size(); }
				} else {
					if (!fixedAtThree || fixedColumn == LEFT) 
						{ pos=results.size(); }
					else if (fixedColumn == CENT && results.size() == 1) { pos=1; }
				}
				results.add(pos, column);
			} else if (availColumns == 2) {
				boolean rollOver = false; //one displayed column "rolls over"
											//another, hiding the other, to make room for 
											//new column.
				if (orderedAtTwo) {
					if (src == CENT) {
						pos = (column==LEFT)? 0:1 ;
						rollOver = true;
					} else { pos = (src==LEFT)? 1:0 ; }
				} else if ((!fixedAtTwo && src == results.get(0)) ||
							(fixedAtTwo && 
							 fixedColumn == defaultTwoCol[0]))		
					{ pos = 1; 
					}
				
				if (results.size() == 2) { 
					if (rollOver) { results.remove(1-pos);
					} else { results.remove(pos); } 
				}
				results.add(pos, column);
			} else {
				results.set(0, column);
			}
		} else {
			
		}

		mem[availColumns] = new int[results.size()];
		for (int i=0;i<results.size();i++) {
			//gwt has trouble with .toArray
			mem[availColumns][i] = results.get(i);
		}
		consoleLog("polColSwitch: finishing Policy column switch. At end of policy call,"
				+ "mem looked like this : " + debugMem());
		
		
	}
	
	@Override
	public void onAvailChange(int newcolavail) {
		/* changes in column memories that occur as soon as a
		 * larger or smaller number of columns are available
		 * due to resizing. changes to memory here can affect the columns
		 * loaded at the new tier. */
		recentResize = true;
		recentSwitch = false;
		consoleLog("beginning Policy AV change. At beginning of policy call,"
				+ "mem looked like this : " + debugMem());
		
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
		consoleLog("finishing Policy AV change. At end of policy call,"
				+ "mem looked like this : " + debugMem());
	}
	
}