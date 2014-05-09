package io.github.nathanross.gwtrifold.client;


abstract public class LayoutManip {
	public static final int BUTTON_TWO = 2;
	public static final int BUTTON_ONE = 1;
	public static final int CONTAINER = 0;
	
	public static final int NOCHANGE = 3;
	public static final int ENABLED = 2;
	public static final int DISABLED =1;
	public static final int HIDDEN=0;
	abstract public void setStatus(
							int component, int status);
	abstract public int getStatus(int component);
}