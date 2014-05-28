Working code at: <a href = 'http://autotagbox.appspot.com' target = '_blank'> autotagbox.appspot.com </a>

Code walkthrough:

The SearchTerms class:

Private Inner Class, within AutoCompleteBox to hold searchterms. The class is an extension of the grid class, which hold two cells. teh first holds the name of the search term and second acts as an 'X'(close) button to delete the search term.

	Class Definition: 

			private class SearchTerms extends Grid implements ClickHandler {

		ClickHandler is to receive click handlers for the 'X' button.

	Constructor:

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


		super(1,2) tells the grid class to construct a grid widget with 1 row and 2 columns. isDisabled is a boolean we use inside AutoCompleteBox if we want it to behave as a read-only class. Argument txt is the name of the search term. multiSuggest is another boolean from the AutoCompleteBox that allows the box to take one or multiple search terms. If its not supposed to take more than one value, make the autosuggestbox invisible after the constructor is called.

	Click Handler:

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

		The click handler should only respond to click events on column index 1, hence 'if (this.getCellForEvent(event).getCellIndex() == 1)'. Since this grid will be inside the composite AutoCompleteBox which will have its own focus handlers, the click even needs to be sunk on the grid itself. inputbox is cleared just for the sake of it, if you click the remove button on a search term, the suggest box will lose focus anyway and make the already entered term a tag. Remove the word from the local hashset 'multiword' inside AutoCompleteBox that keeps track of the search terms existent in the box. If it wasn't a Multiple Suggestion Box, the autosuggestbox should be made visible again. After that, detach the widget from its parent. Additionally set visible as false, just for safety measure.

The SuggestionSource interface:

			public interface SuggestionSource {
				
				public HashSet<String> getSuggestions();
			}

		Override the getSuggestions() method to return the list of string. This is in place so you can have your custom logic to process strings in one place.

The AutoCompleteBox class:

	Class Definition:

			public class AutoCompleteBox extends FocusPanel implements KeyDownHandler { 

		The entire box is an extension of a FocusPanel, to receive focus and blur events. KeyDownHandler for handling Enter and Backspace key events.

	Internal Variables:

			FlowPanel rootPanel;
			ScrollPanel container;
			SuggestBox inputBox;
			SuggestionSource _suggestionSource;
			MultiWordSuggestOracle _oracle = new MultiWordSuggestOracle();
			StringBuilder text;
			HashSet<String> multiWord;
			HashSet<String> suggestions;

			boolean multiSuggest = false;
			boolean restricted = false;
			boolean isDisabled = false;

		rootPanel holds the SearchTerms objects and the autosuggestbox together. The container holds the rootPanel, so that it puts a scroll as and when it expands. inputBox is out GWT-given autosuggestbox, that on its own, is only as useful as displaying a dropdown. _oracle is the object that represents this dropdown. _suggestionSource is an interface we use to supply the list of suggestions. text is a StringBuilder object to compute the value of the box, ie, a comma-separated string of all the search terms entered. multiWord is the local hashset that holds all the suggestions, while the suggestions hashset holds the search terms currently tagged inside the box. multiSuggest determines whether the box can hold more than one search term, restricted allows you to only select values within the list of suggestions you provide, isDisable to turn it into a read-only box.

	Constructor:

			public AutoCompleteBox(SuggestionSource suggestionSource, boolean multi, boolean restricted ,boolean disabled){

				this.restricted = restricted;
				init(suggestionSource, multi);

				isDisabled = disabled;

			}

		Constructor simply assigns values to the booleans and then calls the init() method to initialise all internal widgets, and add event handlers.

	init() method:

			private void init( SuggestionSource suggestionSource, boolean multi){

				text=new StringBuilder();
				multiSuggest = multi;
				multiWord = new HashSet<String>();
				suggestions = new HashSet<String>();
				_suggestionSource = suggestionSource;
				_oracle = getOracle( );
				inputBox = new SuggestBox(_oracle);

		Remember the getSuggestions method of the SuggestionSource interface? Inside the getOracle method that returns the MultiWordSuggestOracle object, we use that method to get string values, add them to the MultiSuggestOracle oracle object initialised inside, and return it. This _oracle object is then used to initialise the suggestbox:: inputBox.

				rootPanel = new FlowPanel();
				container = new ScrollPanel();
				inputBox.setStyleName("invisibleBorder");

				this.addStyleName("combinedBorder");

		Style it so it has no borders, so we can imitate the entire widget as one. CSS Styles will be described at the end.

				inputBox.setAutoSelectEnabled(false);

		Don't make it autoSelect or the first suggestion in the dropdown will automatically be selected. Otherwise, you may wanna add a custom suggestion by pressing enter key, but because something in the dropdown will be always selected, it would always respond by tagging the dropdown term.

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

		onSelection method is called when you click on a suggestion or press enter when selecting a ssuggestion. Check if multiWord hashset is empty, or if it doesn't already have that sggestion inside it. If its empty, or doesnt have that suggestion already inside, it means you've evntered a string not already tagged in the box. Create a new SearchTerm object and add it to the FlowPanel rootPanel. shoft the focus back to the inputBox.


				inputBox.getValueBox().addKeyDownHandler(this);

				rootPanel.add(inputBox);

				DOM.setStyleAttribute(container.getElement(),"overflowX","hidden");

		This is so that it only grows vertically. A horizontal scroll would be too big and wouldn't exactly look too good, but a vertical scroll be that big a deal. If it allows multiple search terms, its important it grows only vertically to fit contents, not wide. It can grow infinitely, and the ScrollPanel will take care of it.

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

		On Focus or Click on the widget, shift the focu to the inputBox.

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


		This was one of the most troublesome part to code. Partly because of GWT's fault of not treating the dropdown and suggestionbox as one widget. If you click on the suggestion dropdown, it would trigger a blur even on the input box. If you'd select a suggestion by a mouse click, it would fire a blur event and a selection even simultaneously. Even if you clear the inputBox on the selection even handler, it was of no sue as both events would run in parallel. So there needed to be a way to know when it was a proper blur event, or just the user clicking the dropdown suggestion. Thankfully, GWT had a deprecated method within suggestBox that returns a boolean that denotes if the dropdown is showing or not. Hence in the 'if' loop at the top, there's a '!inputBox.isSuggestionListShowing()' so that it only goes further if the dropdown isn't showing. Next, get the text entered inside the suggestbox, split them with the comma character, which we're using as a delimiter, and create SearchTerms object for each. Too easy? N-O-T We also want it to behave differently in the event we don't want any suggestion other than the ones in the dropdown. that doesn't mean we just ignore the auto tagging feature when it loses focus. So we just check if its 'NOT RESTRICTED' OR 'ITS RESTRICTED BUT THE SUGGESTION LIST HAS THIS WORD SO WE COOL YO' and then we make a SearchTerm object if its not just an empty string. Else we say, 'NO CAN DO MATE'. Also check if its not a multi suggest box so we break on the first word from the list. But wait, why do we split by comma and treat is as multiple search terms, can't we just consider it one single term? N-to-the-O coz it looks cool copy+paste all your comma seperated search terms to see them become tags on their own and also because its important to have delimiters. A comma is naturally a way to 'list' terms, so it really would seem as if a comma seperated list is treated as a composite list of Strings.

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

		This pretty much does the same, except that it tags a searh term every time you press enter or the comma key. Cool isn't it? Type, press comma, see it get tagged, type again. Isn't it?

	Other methods:


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

		I think I might be able to doi a better job with getTextBox value. It only takes the values inside multiword, makes it a comma seperated string of search terms, takes whatever that might be left in the inputbox, appends to the comma seperated list, and returns the string back.

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

		I might need to work on this one here so it complies with the restricted policy. Oh well, nothing's perfect, isn't it? 


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

		clear() method = 'FUCK THIS SHIT WIPE EVERYTHING OFF'


				public void onKeyDown(KeyDownEvent event) {


					if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE
							&& inputBox.getValue().equals("")
							&& rootPanel.getWidgetCount() > 1 && !isDisabled) {


						String delString = ((Grid)rootPanel.getWidget(rootPanel.getWidgetCount()-2)).getText(0,0);
						multiWord.remove(delString);


						rootPanel.remove(rootPanel.getWidgetCount() - 2);


					}
				}

		This is to delete the last SearchTerm on every backspace event. It takes care if you just wanna edit test you're typing on the inputBox or you wanna clear a search term.

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

		And finally this is what we use to initialise the _oracle object.

					public void newWidth(String width){
						this.setWidth(width);
					}

		This is because IE wanted to act like the needy asshole it is and fuck shit up with dimensions.

CSS:

				.taggedName {
					outline: none;
					font-size: 10px;
				}

				.tagged {
					border: 1px;
					border-style: solid;
					border-color: #999999;
					border-radius: 5px;
					float: left;
				}

				.tagged:hover {
					background-color: #a6b3cf;
				}

				.taggedX {
					font-size: 10px;
				}

				.taggedX:hover {
					cursor: pointer;
				}

				.taggedX {
					font-size: 10px;
					
				}	
					
				.taggedX:hover{
					cursor:pointer;
					}

		All classes that have 'tagged' in their names are for SearchTerms. tagged CSS class places a combined border over them, while taggedName and taggedX remove their respective border/outlines, place pointer cursor on 'X', etc.
					
				.invisibleBorder{
					outline:none;
					border:1px;
					border-color:#ffffff;
					border-style:solid;
				}

				.invisibleBorder:focus{
					outline:none;
					border:1px;
					border-color:#ffffff;
					border-style:solid;
				}

				.invisibleBorder:active{
					outline:none;
					border:1px;
					border-color:#ffffff;
					border-style:solid;
				}

				input.invisibleBorder:focus{
					outline:none;
					border:1px;
					border-color:#ffffff;
					border-style:solid;

				}

				.invisibleBorder:active {
					outline: none;
					border: 1px;
					border-color: #ffffff;
					border-style: solid;
					width: 100%;
				}

				input.invisibleBorder:focus {
					outline: none;
					border: 1px;
					border-color: #ffffff;
					border-style: solid;
					width: 100%;
				}

				.invisibleBorder:focus {
					outline: none;
					border: 1px;
					border-color: #ffffff;
					border-style: solid;
					float: left;
				}

				.combinedBorder {
					border: inset 2px #EBE9ED;
					border-right: solid 1px #CCCCCC;
					border-bottom: solid 1px #CCCCCC;
					background-color: #ffffff;
				}

				input.invisibleBorder:focus {
					outline: none;
					border: 1px;
					border-color: #ffffff;
					border-style: solid;
				}

				.noScroll {
					overflow: hidden;
					outline: none;
				}

				.combinedBorder {
					border: inset 2px #EBE9ED;
					border-right: solid 1px #CCCCCC;
					border-bottom: solid 1px #CCCCCC;
					width: 200px;
					background-color: #ffffff;
				}

		invisibleBorder is to remove borders from all elements inside the AutoCompleteBox, to remove border/outlines in focus/active/default. combinedBorder places a border on AutoCompleteBox that makes it look like a simgle component. noScroll isn used to restric the box as only a single line box if its not a multiple value box.


Liked it? Buy me a cookie and a coffee. :)

Daanish Millwalla.
ImplementHIT.
@FloptimusCrime



