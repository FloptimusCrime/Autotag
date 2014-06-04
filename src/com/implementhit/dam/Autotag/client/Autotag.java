package com.implementhit.dam.Autotag.client;

import java.util.HashSet;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class Autotag implements EntryPoint {

	AutoCompleteBox _autocompleteBox = null;
	HashSet<String> allSuggestions = new HashSet<String>();
	HorizontalPanel _suggestContainer = new HorizontalPanel();
	HorizontalPanel _labelConainer = new HorizontalPanel();
	VerticalPanel _mainPanel = new VerticalPanel();
	TextBox _suggestionBox = new TextBox();
	Label _values = new Label("Autotag Values: ");
	Button _submit = new Button("Add");
	Button _show = new Button("Show Values");
	
	SuggestionSource _suggestionsSource = new SuggestionSource() {
		
		@Override
		public HashSet<String> getSuggestions() {
			
			return allSuggestions;
			
		}
	};
	
	public void onModuleLoad() {
		
		allSuggestions.add("TestString");
		
		
		_autocompleteBox = new AutoCompleteBox(_suggestionsSource, true, false, false);
		
		_suggestContainer.add(_suggestionBox);
		_suggestContainer.add(_submit);
		
		_labelConainer.add(_values);
		_labelConainer.add(_show);
		
		_mainPanel.add(_suggestContainer);
		_mainPanel.add(_autocompleteBox);
		_mainPanel.add(_labelConainer);
		
		RootPanel.get("rootContainer").add(_mainPanel);
		
		_submit.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				

				if( _suggestionBox.getText() != null && !_suggestionBox.getText().trim().equals("") ) {
					
					allSuggestions.add( _suggestionBox.getText() );
					_mainPanel.remove(_autocompleteBox);
					_autocompleteBox = new AutoCompleteBox(_suggestionsSource, true, false, false);
					_mainPanel.insert(_autocompleteBox,1);
					_suggestionBox.setText(""); 
					
				}
				
			}
		} );
		
		_show.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

				_values.setText("Autotag values: "+_autocompleteBox.getTextBoxValue());
				
			}
		} );
		
	}
}
