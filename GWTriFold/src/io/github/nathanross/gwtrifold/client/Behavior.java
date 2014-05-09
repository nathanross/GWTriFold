/*
 * gwtrifold/client/Behavior.java
 * interface for any layout bevaior class plugged into GWTriFold.
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

public interface Behavior {

	public int[] getLayout(int availColumns);
	public int[][] getButtonStatus(int availColumns);
	public void onManipUse(int src, int keycode);
	public void onAvailChange(int newColAvail);	
}