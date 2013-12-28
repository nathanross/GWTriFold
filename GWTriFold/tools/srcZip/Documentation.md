# GWTriFold v.0.1
#### December 2013

###Contents

1. What is GWTriFold?
2. Quickstart
3. Styling GWTriFold
4. configuring behavior
5. default behavior policy

## 1. What is GWTriFold?  
 GWTriFold is a GWT wrapper for Tri-Fold.
 It enables you to use Tri-Fold in GWT without any javascript.
 Tri-Fold is a web app templating tool  
 that creates and manages a responsive,  
 scaling and folding, tri-panel design.

 GWTriFold and Tri-Fold are copyright 2013 Nathan Ross, and licensed under the Apache 2 license for public use. Both are unaffiliated with Google or The GWT Project. Please see the LICENSE file for full details.   

#### Web app responsive design
 Tri-Fold aims to fill a dearth of responsive-designs templates for web applications. For applications, consistency and an immediately recognizable UI across platforms is the cardinal virtue of a responsive design.
 
 This is a divergent goal from the website responsive design strategy of requiring that nearly the entire page's content operate as a minimally-structured fluid layout.
 
#### Multi-paned design
 
 Multi-paned designs lend themselves well towards web app responsive design, because the appearance and behavior of each panel on a phone, tablet, or computer is nearly identical, giving the user a frictionless and frustration-free experience in using your web application on their phone and tablet.

 Tri-Fold is the implementation of that idea, and the abstraction of that responsive design away from any work that you have to do. 

#### Multi-featured with built in customization
 
 You create the content for the columns, style the application, and customize any behavior. GWTriFold provides comprehensive scaling, panel switching, smoother-than-jQuery sliding animation, minimization, and resize-based panel pop-in and pop-out.

#### See GWTriFold in action:
 The best way to get a sense of the variety of features and configuration GWTriFold offers is to use the Configuration Demo at. The source for the configuration Demo is included in this distribution package.
 
## 2. Quickstart

Include a GWTriFold panel in your app in five minutes
 
1. copy GWTriFold.jar to your project's /war/WEB-INF/lib/ dir
2. Include GWTriFold.jar in your build path
3. Inherit the GWTriFold package: In your GWT project's XML file, add:  
   ```<inherits name='io.github.nathanross.gwtrifold.GWTriFold.gwt.xml'/>```
4. In any java file which you're using GWTriFold, add:  
   ```import io.github.nathanross.gwtrifold.client.GWTriFold; ````
6. Decide which div or panel will contain the GWTriFold panel (it's good for the GWTriFold panel to be the only child panel of this "outer Parent" panel)
7. Select three flowpanels to become the contents of the three columns, and the order they should be in.
8. create a Policy object, and change any options you don't want to be default. e.g.
	```Policy policy = new DefaultPolicy();```
9. create a GWTriFold object (it will add itself to the parent object) e.g.  	
   ```GWTriFold triFold = new GWTriFold(policy, outerParent,
		leftColumn, centColumn, rightColumn);```
10. call display() when you want the Tri-Fold to appear in the "outer Parent"
	panel. Tri-Fold's scaling will be determined by the height and width of
	the outerParent.
		
Congratulations! GWTriFold is all set up. Now it's time to configure
it for the behavior you'd like, and learn about how to style it and 
child widgets.
	
## 3. Styling GWT

By default 


You can demo various configuration options quickly online at
~ ~ ~ ~, or compile that page yourself using the included
com.googlecode.GWTriFold.Demo class.

9.



## 4. Configuring Behavior 

 GWTriFold has default values set for all of its configuration options. The default values (easily visible in the configuration demo), are also used in the example configuration calls below. 

### A. Layout

##### A.i. Column Layout

    public void setMinColDimensions(double[]) 
    public double [] getMinColDimensions()
	
e.g. `triFold.setMinColDimensions(new double[]{300, 800});`  
  set minimum dimensions of 300px width by 800px height;
	
Provides minimum resolution for each column panel, and is also used to define proportion between column size and border and margin size.
   
>###### more info:	
>GWTriFold usually scales columns based on browser window (or more precisely, any and only the factors affecting parentPanel size), but below a minimum window size, browser columns stop scaling down, and you get a scrollbar instead.

>This is because below a certain resolution (size aside) trying to include the entire column in that resolution will render the text illegible or unreadable. Although all widgets may correctly render at subpixel resolution, the behavior is variable. The minimum column dimensions also are used to define the height to width proportion of columns and the proportion of padding and margin to columns. 
	
#### A.ii. Padding dimensions
	
    public void setPaddingDimensions(double[]) 
	public double [] getPaddingDimensions() 
	
e.g. `triFold.setPaddingDimensions(new double[]{15, 10,15,10});`  
   set padding dimensions of 15px width (on each side) by 10px height (applied to bottom and top) relative to the column dimensions.
							
All columns are contained within a single flowpanel (the 'innerParent div') that resizes as panels pop in and pop out (and slides doing so). This sets the padding for the div; if you don't set a background for that div, it's function is nearly indistinguishable from an addition to the margin. 

#### A.iii. Margin dimensions

	public void setMarginDimensions(double[]) 
	public double [] getMarginDimensions()
	
e.g. `triFold.setMarginDimensions(new double[]{20,50,20,50});`  
   set margin dimensions of 20px on the top margin, 50px on the right margin, 20px on the bottom margin, and 50px on the left margin, with the lengths relative to the column dimensions.
							
If the outerParent (the parent panel provided to a GWTriFold object on initializiation) is nearly the size of the full browser window (e.g. as in the Configuration Demo), most designers would want to provide a bit of spacing to properly frame the columns.

>There are two reasons margins might become more or less than the proportion (relative to column dimensions) specified through this option: Columns are of a particular height to width ratio, and often there will be extra space along one axis. If the columns are meant to be much taller than they are thin and the browser window very wide, the margins will extend to fill the gap, rather than columns distorting out of shape. Also, at or near minimum column resolution, vertical margins and horizontal margins can be collapsible.
	
#### A.iv. Column Spacing

    public void setColumnSpacing(double)
    public double getCOlumnSpacing()
    
e.g. `triFold.setColumnSpacing(15);`  
   Sets a spacing of 15px, in proportion to column width, between any two displayed adjacent columns. 

### B. Animation 

#### B.i. slide/fade animations
    public void setFade(bool)
    public bool getFade()
    
e.g. `triFold.setFade(false);`  
   Use the default sliding transitions. Setting this to true would make columns fade in or out instead.

#### B.i. slide speed
	
	public void setSlidespeed(int)
	public int getSlidespeed()
	
e.g. `triFold.setSlideSpeed(800);`  
   when columns are sliding to a different position, or in or out of appearance, they will take 800ms to travel the width of one column. 
 
Animation is done with an extremely light tweening timeout, and not with any library, but if you've used jQuery you can use jQuery animation durations as a point of reference for how fast it will be.

#### B.ii. button slide speed

	public void setSwitchbarSlidespeed(int)
	public int getSwitchbarSlidespeed()
	
e.g. `triFold.setButtonSlideSpeed(300);`  
   GWTriFold can place small bar panels (switchBars) at the bottom of each column allowing one to choose to display, hide, or switch to the other columns.

You are capable of making some columns fixed. that is, when there are more than one column, that column always appears. If you do this, it will be only the column with a switchBar.
		
This is default behavior. Switchbars normally appear and disappear by sliding. Use this option to increase or decrease the speed or make the appearance instantaneous.
	

### C. Column placement policy 

These functions parameterize the column placement policy.
If you override column placement policy, these parameters 
will have no effect unless you make use of them.
	
#### C.i. Default two column layout.

	vi. public void setDefaultTwoCol(Integer[] in) 
		public Integer[] getDefaultTwoCol()
	
e.g. `triFold.setDefaultTwoCol(new Integer[]{GWTriFold.RIGHT, GWTriFold.CENT});`  
   When the user first resizes the window so that it can only fit two columns, the two columns will be the right and center column, in that order.
	
  If you set a column to fixed (see below), you'd use the order here to determine which side the fixed column is on.

#### C.ii. Default one column layout.
	
	public void setDefaultOneCol(Integer[] in) 
	public Integer[] getDefaultOneCol()

e.g. `triFold.setDefaultOneCol(new Integer[]{GWTriFold.CENT});`

#### C.iii. fixed column

	public void setFixedColumn(Integer in) {}
	public Integer getFixedColumn() {}
	
e.g. `triFold.setFixedColumn(GWTriFold.CENT);`
	
When there's only enough space for one column, fixed column has no effect. When there's enough space for two or three, the fixed column (if enabled in those areas) is a column that is ALWAYS visible, and cannot be switched out or minimized.
	
Usually you'll want to select the column that's most crucial to operating the app.
	
Besides visual consistency and a more intuitive UI, a benefit of a fixed column is that you only ever have a  switchbar (column minimize/switch out buttons) on one column at a time. 
	
#### C.iv. fixed column is fixed when there are two columns
	
	public void setFixedAtTwo(Boolean in)
	public Boolean getFixedAtTwo()
	
e.g. `triFold.setFixedAtTwo(true);`
	this sets it so that if the fixed column
	is center, then when there are two columns available,
	you can only switch out right or left;
	
#### C.v. fixed column is fixed when there are three columns available.	
	
	public void setFixedAtThree(Boolean in)
	public Boolean getFixedAtThree()

e.g. `triFold.setFixedAtThree(true);`
	for example, if the fixed column was right,
	you'd only be able to minimize the left and center
	column. 	


## 5. COLUMN PLACEMENT POLICY 

Resizing behavior and corner cases:

 How the default policy works: 
 When a user begins resizing a window, the window goes into 
 "resize mode." 

 One of the design decisions of the default policy is that
 the two-column area is the only area stored by memory long-term.
 If you change the layout of one of the other areas, that layout
 lasts so long as you remain in that area and/or resize the window.
 But as soon as you press a button in another layout that layout is lost.
 This also applies to the two column area in a corner case.

 This "short term memory" is a design decision for two separate reason:
	*aid the user in efficiently resizing the window. Opening
		an extra column is only possible once one has reached
		the appropriate width, Demonstrating that live prevents the
		user from having to overshoot, move the mouse to press the
		button to show the extra column, then move the mouse back
		to the window corner and resize again. 
	The button press trigger greatly increases the likelihood that
		one is tired of playing in a lower column number and wants
		to increase the column count.
	*aid the user in quickly switching to a smaller window
	again the button press indicates they've likely already set up
		the other columns. In an app where there is no central
		important monitoring or usage column, this feature would
		be less desirable and can be changed out.

 CORNER CASE: for consistency of columns "popping in", two-column
 layouts are also replaced with default Only when a button is pressed
 and their current layout is of size 1.

 Finally, an extra function of default policy:
 when layout for the 3-column area is set to any two column layout 
   (even if temporarily) that layout propagates down to the 2-column 
   area's memorized layout on any button press.
 (this is to prevent changes in 2-column area layout just from
	accidentally, briefly pressing the minimize button on a column)


 