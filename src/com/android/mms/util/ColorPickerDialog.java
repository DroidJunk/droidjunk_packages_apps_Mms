/*
 * Copyright (C) 2010 Daniel Nilsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.mms.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;

import com.android.mms.R;
import com.android.mms.util.ColorPickerPreference;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ColorPickerDialog 
	extends 
		Dialog 
	implements
		ColorPickerView.OnColorChangedListener,
		View.OnClickListener {

	private ColorPickerView mColorPicker;
	private ColorPickerPanelView mOldColor;
	private ColorPickerPanelView mNewColor;
	private OnColorChangedListener mListener;
    private EditText mHex;
    private Button mSetButton;


	
    TextWatcher inputTextWatcher = new TextWatcher() {
    	public void onTextChanged(CharSequence s, int start, int before, int count) {}
		@Override
		public void afterTextChanged(Editable s) {
	        try {
	        	int c = Color.parseColor(mHex.getText().toString()); 
	        	mNewColor.setColor(c);
	        } catch (Exception e) {
	        }
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    }; 
    
    
    
    
	public interface OnColorChangedListener {
		public void onColorChanged(int color);
	}
	
	public ColorPickerDialog(Context context, int initialColor) {
		super(context);
		init(initialColor);
	}

	private void init(int color) {
		// To fight color banding.
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setUp(color);
	}

	private void setUp(int color) {
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_color_picker, null);
		setContentView(layout);
		setTitle(R.string.dialog_color_picker);
				
		mColorPicker = (ColorPickerView) layout.findViewById(R.id.color_picker_view);
		mOldColor = (ColorPickerPanelView) layout.findViewById(R.id.old_color_panel);
		mNewColor = (ColorPickerPanelView) layout.findViewById(R.id.new_color_panel);
        mHex = (EditText) layout.findViewById(R.id.hex);
        mSetButton = (Button) layout.findViewById(R.id.enter);

		
		((LinearLayout) mOldColor.getParent()).setPadding(
			Math.round(mColorPicker.getDrawingOffset()), 
			0, 
			Math.round(mColorPicker.getDrawingOffset()), 
			0
		);	
		
		mOldColor.setOnClickListener(this);
		mNewColor.setOnClickListener(this);
		mColorPicker.setOnColorChangedListener(this);
		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);
        mHex.setText(ColorPickerPreference.convertToARGB(color));
        mHex.addTextChangedListener(inputTextWatcher);
			

        mSetButton.setOnClickListener(new View.OnClickListener() {

        	@Override
            public void onClick(View v) {
            	mHex.setText(ColorPickerPreference.convertToARGB(mNewColor.getColor()));
            	try {
                    if (mListener != null) {
                        mListener.onColorChanged(mNewColor.getColor());
                    }
                } catch (Exception e) {
                }
                dismiss(); 
            }
        });
        


	}

	@Override
	public void onColorChanged(int color) {

		mNewColor.setColor(color);
		mHex.setText(ColorPickerPreference.convertToARGB(color));
	}


	public void setAlphaSliderVisible(boolean visible) {
		mColorPicker.setAlphaSliderVisible(visible);
	}
	
	/**
	 * Set a OnColorChangedListener to get notified when the color
	 * selected by the user has changed.
	 * @param listener
	 */
	public void setOnColorChangedListener(OnColorChangedListener listener){
		mListener = listener;
	}

	public int getColor() {
		return mColorPicker.getColor();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.new_color_panel) {
			if (mListener != null) {
				mListener.onColorChanged(mNewColor.getColor());
			}
		}
		dismiss();
	}
	
	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt("old_color", mOldColor.getColor());
		state.putInt("new_color", mNewColor.getColor());
		return state;
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mOldColor.setColor(savedInstanceState.getInt("old_color"));
		mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
	}
}
