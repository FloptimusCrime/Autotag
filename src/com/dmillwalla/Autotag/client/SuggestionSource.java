package com.dmillwalla.Autotag.client;

import java.util.HashSet;

public interface SuggestionSource {
	
	public HashSet<String> getSuggestions();
}
