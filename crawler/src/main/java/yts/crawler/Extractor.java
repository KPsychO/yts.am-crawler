package yts.crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extractor {
	
	private ArrayList<String> extract_movie_info(String url, DatabaseManager dbManager) {
		
		ArrayList<String> movie_info = new ArrayList<String>();
		
		System.out.println("   Extracting data from: " + url);
		
		try {
			
			Document doc = Jsoup.connect(url).get();
			
			// Extract movie_title to movie_info.get(0)
			Elements title = doc.select("#movie-info .hidden-xs>h1");
			for(Element el : title) {
				
				movie_info.add(String.valueOf(el.childNode(0)));
				
			}
			
			// Extracts movie_year to movie_info.get(1)
			// Extracts movie_genre to movie_info.get(2)
			Elements year = doc.select("#movie-info .hidden-xs>h2");
			for(Element el : year) {
				
				movie_info.add(String.valueOf(el.childNode(0)));
				
			}
			
			// Extracts movie_3D_torrent to movie_info.get(3)
			// Extracts movie_720p_torrent to movie_info.get(4)
			// Extracts movie_1080p_torrent to movie_info.get(5)
			Elements links = doc.select(".download-torrent.button-green-download2-big");
			
			// Sets movie_3D_torrent to null if it does not exist (in order to preserve the order in the table)
			if(links.size() < 3)
				movie_info.add(null);

			for(Element el : links) {

				movie_info.add(String.valueOf(el.attr("abs:href")));
				
			}
			
			// Sets movie_1080p_torrent to null if it does not exist (in order to preserve the order in the table)
			if(links.size() == 1)
				movie_info.add(null);
			
			// Extracts movie_thumbnail to movie_info.get(6)
			Elements thumb = doc.select("#movie-poster img");
			for(Element el : thumb) {
					
				movie_info.add(String.valueOf(el.attr("abs:src")));
				
			}
			
			// Try to extract subtitles!
						
		}
		catch (IOException e){
			
			// Given when a certain movie page does not respond or does not contain the required info.
			// Usually happens when yts.am has deleted the movie
			System.out.println("Movie: " + url + " contains no info");
			
		}
				
		return movie_info;
		
	}

	public int extract_info(ArrayList<String> movie_urls, DatabaseManager dbManager) {
		
		ArrayList<String> movie_info = new ArrayList<String>();
		
		int total_crawled = 0;
		
		System.out.println("Gathering data...");
    	
    	for(String movie : movie_urls) {
    		
    		movie_info = this.extract_movie_info(movie, dbManager);
    		
    		if(movie_info.size() == 7) {
    			
    			// Store movie_info into the tmp_database
    			dbManager.insert_movie_info_tmp(movie, movie_info.get(0), movie_info.get(1), movie_info.get(2), movie_info.get(3), movie_info.get(4), movie_info.get(5), movie_info.get(6));
    			total_crawled++;
    			
    		}
    		
    		movie_info.clear();
    		
    	}
    	
		return total_crawled;
		
	}
	
}
