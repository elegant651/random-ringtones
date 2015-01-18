package com.u2likeus.android.rrt;

import android.app.Dialog;
import android.content.Context;

public class CopyrightDialog extends Dialog{
	public CopyrightDialog(Context context){
		super(context);
		setContentView(R.layout.copyright_dialog);
		
		setTitle("Copyright");
	}
}
