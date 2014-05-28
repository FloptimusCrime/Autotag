package com.dmillwalla.Autotag.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;



public class AutoCompleteBox extends FocusPanel implements KeyDownHandler { 
	FlowPanel rootPanel;
	ScrollPanel container;
	public SuggestBox inputBox;
	SuggestionSource _suggestionSource;
	MultiWordSuggestOracle _oracle = new MultiWordSuggestOracle();
	StringBuilder text;
	HashSet<String> multiWord;
	HashSet<String> suggestions;

	boolean multiSuggest;
	boolean restricted = false;
	boolean isDisabled = false;

	public AutoCompleteBox(SuggestionSource suggestionSource, boolean multi, boolean restricted ,boolean disabled){


		this.restricted = restricted;
		init(suggestionSource, multi);

		isDisabled = disabled;

	}
	
	
	public void setDisable(boolean disable){
		isDisabled = disable;
	}

	private void init( SuggestionSource suggestionSource, boolean multi){

		text=new StringBuilder();
		multiSuggest = multi;
		multiWord = new HashSet<String>();
		suggestions = new HashSet<String>();
		_suggestionSource = suggestionSource;
		_oracle = getOracle( );
		inputBox = new SuggestBox(_oracle);
		rootPanel = new FlowPanel();
		container = new ScrollPanel();
		inputBox.setStyleName("invisibleBorder");

		this.addStyleName("combinedBorder");

		inputBox.setAutoSelectEnabled(false);

		inputBox.addSelectionHandler( new SelectionHandler<SuggestOracle.Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {

				inputBox.getValueBox().setText("");

				MultiWordSuggestion select = (MultiWordSuggestion) event.getSelectedItem();



				if(multiWord.isEmpty() || !multiWord.contains(select.getReplacementString()))
				{


					rootPanel.insert((IsWidget) new SearchTerms(select.getReplacementString()), rootPanel.getWidgetCount() - 1);

				}


				inputBox.setFocus(true);

			
			}
			
			
		} );

		inputBox.getValueBox().addKeyDownHandler(this);

		rootPanel.add(inputBox);

		DOM.setStyleAttribute(container.getElement(),"overflowX","hidden");

		if(multiSuggest)

		{
			container.setHeight("50px");
		}
		else
		{
			container.setHeight("25px");
			container.setStyleName("noScroll");
			this.addStyleName("noScroll");
		}


		container.add(rootPanel);
		this.add(container);
		this.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				inputBox.getValueBox().setFocus(true);
			}
		});

		this.addFocusHandler(new FocusHandler(){

			@Override
			public void onFocus(FocusEvent event) {
				inputBox.getValueBox().setFocus(true);
			}
		});

		this.addBlurHandler(new BlurHandler(){

			@Override
			public void onBlur(BlurEvent event) {

				if(inputBox.getValueBox().getText()!=null && !inputBox.getValueBox().getText().trim().equals("")
						&& !inputBox.isSuggestionListShowing() && !multiWord.contains(inputBox.getValueBox().getText().trim()))
				{

					String text="";
					if(inputBox.getValueBox().getText().charAt(0) == ','){
						text = inputBox.getValueBox().getText().substring(1);
					}
					else{
						text = inputBox.getValueBox().getText();
					}
					inputBox.getValueBox().setText("");
					if(!text.trim().equals("")){
						List<String> arr = Arrays.asList(text.split(","));
						for(String eachWord : arr)
						{

							if(!restricted || (restricted && suggestions.contains(eachWord)))
							{
								if(!eachWord.trim().equals("")){
									rootPanel.insert((IsWidget) new SearchTerms(eachWord.trim()), rootPanel.getWidgetCount() - 1);	
								}
							}else{
								Window.alert("No suggestions found");
								inputBox.getValueBox().setText("");
								break;
							}
							if(!multiSuggest)
							{
								break;
							}

						}

					}
				}

			}

		});



		inputBox.getValueBox().addBlurHandler(new BlurHandler(){

			@Override
			public void onBlur(BlurEvent event) {

				if(inputBox.getValueBox().getText()!=null && !inputBox.getValueBox().getText().trim().equals("")
						&& !inputBox.isSuggestionListShowing() && !multiWord.contains(inputBox.getValueBox().getText().trim()))
				{

					String text="";
					if(inputBox.getValueBox().getText().charAt(0) == ','){
						text = inputBox.getValueBox().getText().substring(1);
					}
					else{
						text = inputBox.getValueBox().getText();
					}
					inputBox.getValueBox().setText("");
					if(!text.trim().equals("")){
						List<String> arr = Arrays.asList(text.split(","));
						for(String eachWord : arr)
						{
							if(!restricted || (restricted && suggestions.contains(eachWord)))
							{
								if(!eachWord.trim().equals("")){
									rootPanel.insert((IsWidget) new SearchTerms(eachWord.trim()), rootPanel.getWidgetCount() - 1);	
								}
							}else{
								Window.alert("No suggestions found");
								inputBox.getValueBox().setText("");
								break;
							}
							if(!multiSuggest)
							{
								break;
							}

						}

					}
				}

			}

		});

		inputBox.getValueBox().addKeyDownHandler(new KeyDownHandler(){

			@Override
			public void onKeyDown(KeyDownEvent event) {

				if(event.getNativeKeyCode() == 188 || event.getNativeKeyCode() == 13){

					inputBox.getValueBox().cancelKey();

					if(inputBox.getValueBox().getText()!=null && !inputBox.getValueBox().getText().trim().equals("")
							&& !multiWord.contains(inputBox.getValueBox().getText().trim()))
					{

						String text="";
						if(inputBox.getValueBox().getText().charAt(0) == ','){
							text = inputBox.getValueBox().getText().substring(1);
						}
						else{
							text = inputBox.getValueBox().getText();
						}
						inputBox.getValueBox().setText("");
						if(!text.trim().equals("")){
							List<String> arr = Arrays.asList(text.split(","));
							for(String eachWord : arr)
							{

								if(!restricted || (restricted && suggestions.contains(eachWord)))
								{
									if(!eachWord.trim().equals("")){
										rootPanel.insert((IsWidget) new SearchTerms(eachWord.trim()), rootPanel.getWidgetCount() - 1);	
									}
								}else{
									Window.alert("No suggestions found");
									inputBox.getValueBox().setText("");
									break;
								}
								if(!multiSuggest)
								{
									break;
								}

							}

						}
					}

				}

			}


		});

	}


	public void newWidth(String width){
		this.setWidth(width);
	}

	public String getTextBoxValue(){

		String returnText ="";
		text = new StringBuilder();

		if(multiWord.isEmpty())
		{ 
			if(inputBox.getValueBox().getText()!=null && !inputBox.getValueBox().getText().trim().equals(""))
			{
				List<String> arr = Arrays.asList(inputBox.getValueBox().getText().split(","));
				for(String eachWord : arr)
				{
					text.append(eachWord.trim() +",");

				}
			}
			else
			{
				returnText ="";
			}

		}
		else
		{   
			for(String eachWord : multiWord)
			{
				text.append(eachWord.trim() +",");

			}

			if(multiSuggest)
			{
				if(inputBox.getValueBox().getText()!=null && !inputBox.getValueBox().getText().trim().equals(""))
				{
					List<String> arr = Arrays.asList(inputBox.getValueBox().getText().split(","));
					for(String eachWord : arr)
					{
						text.append(eachWord.trim() +",");

					}
				}
			}

		}

		if(text.length() > 0 && text.charAt(text.length()-1) == ',')
			returnText = text.substring(0,text.length()-1);
		else
			returnText = "";
		System.out.println(returnText);
		return returnText;
	}

	public void setTextBoxValue(ArrayList<String> strSet)
	{

		if(strSet != null){
			for(String eachStr : strSet){

				if(!eachStr.trim().equals("")){
					rootPanel.insert((IsWidget) new SearchTerms(eachStr), rootPanel.getWidgetCount() - 1);
				}

			}	

		}
		inputBox.setValue("");
	}


	public void clear()
	{

		multiWord.clear();

		inputBox.getValueBox().setText("");

		int i=0;
		while(rootPanel.getWidgetCount()>1)
		{
			rootPanel.remove(i);
		}

		if(!multiSuggest)
		{
			inputBox.setVisible(true);
		}

	}


	public void onKeyDown(KeyDownEvent event) {


		if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE
				&& inputBox.getValue().equals("")
				&& rootPanel.getWidgetCount() > 1 && !isDisabled) {


			String delString = ((Grid)rootPanel.getWidget(rootPanel.getWidgetCount()-2)).getText(0,0);
			multiWord.remove(delString);


			rootPanel.remove(rootPanel.getWidgetCount() - 2);


		}
	}

	public MultiWordSuggestOracle getOracle() {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();


		if(_suggestionSource.getSuggestions() != null)
		{
			suggestions.addAll(_suggestionSource.getSuggestions());
		}

		for( String eachString : suggestions){
			oracle.add(eachString);
			System.out.println(eachString);
		}

		return oracle;
	}

	private class SearchTerms extends Grid implements ClickHandler {

		public SearchTerms(String txt) {
			super(1, 2);
			System.out.println(txt);

			if(!isDisabled){
				this.addClickHandler(this);
			}
			this.setText(0, 0, txt);
			this.getCellFormatter().addStyleName(0,0,"taggedName");
			this.setText(0, 1, "X");
			this.getCellFormatter().addStyleName(0,1,"taggedX");
			this.addStyleName("tagged");
			DOM.setStyleAttribute(this.getElement(),"float","left");
			multiWord.add(txt);
			if(!multiSuggest)
			{
				inputBox.setVisible(false);
			}



		}


		public void onClick(ClickEvent event) {


			if (this.getCellForEvent(event).getCellIndex() == 1)
			{
				event.stopPropagation();
				inputBox.getValueBox().setText("");
				multiWord.remove(this.getText(0,0));

				if(!multiSuggest)
				{
					inputBox.setVisible(true);
				}
				this.setVisible(false);
				this.removeFromParent();
			}
		}
	}


}

