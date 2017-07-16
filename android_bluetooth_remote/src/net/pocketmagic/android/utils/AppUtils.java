package net.pocketmagic.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;

/* @project 
 * 
 * License to access, copy or distribute this file.
 * This file or any portions of it, is Copyright (C) 2013, Pocketmagic.net
 * @author Radu Motisan, radu.motisan@gmail.com
 * 
 * This file is protected by copyright law and international treaties. Unauthorized access, reproduction 
 * or distribution of this file or any portions of it may result in severe civil and criminal penalties.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * @purpose 
 */

public class AppUtils {
	
	
	public static void StartActivity(Context context,Class class1) {
		Intent iActivity = new Intent(context, class1);
		context.startActivity(iActivity);
	}
	

	public static void OpenWeb(Context context, String m_szWeb) {
		Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(m_szWeb));  
		context.startActivity(viewIntent); 
	}
	
	public static void Sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// interface elements
	public static RelativeLayout.LayoutParams GetRLP(int vw, int vh, int styles[][], int left, int top){
		RelativeLayout.LayoutParams lpv = new RelativeLayout.LayoutParams(vw,vh);
		if (styles!= null)
			for (int i=0;i<styles.length;i++) {
				if (styles[i] != null) {
					if (styles[i].length == 1) lpv.addRule(styles[i][0]);
					if (styles[i].length == 2) lpv.addRule(styles[i][0], styles[i][1]);
				}
			}
	    if (top!=-1) lpv.topMargin = top;
	    if (left!=-1) lpv.leftMargin = left;
	    return lpv;
	}
	
	public static void AddView(RelativeLayout panel, View v, int vw, int vh, int styles[][], int left, int top ) {
		panel.addView(v, GetRLP(vw, vh, styles, left, top));
	}
	public static void UpdateView(RelativeLayout panel, View v, int vw, int vh, int styles[][], int left, int top ) {
		panel.updateViewLayout(v, GetRLP(vw, vh, styles, left, top));
	}
	
	

}
