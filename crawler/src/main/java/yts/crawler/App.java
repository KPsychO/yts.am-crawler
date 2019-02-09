package yts.crawler;

import java.util.ArrayList;

public class App {
	
	static DatabaseManager dbManager = new DatabaseManager();
	static Indexer indexer = new Indexer();
	static Extractor extractor = new Extractor();
	static Downloader downloader = new Downloader();
	
    public static void main( String[] args ) {
    	
    	System.out.println( "Welcome to the YTS.am crawler." );
    	System.out.println( "*******************************" );
    	
    	// Start URL
    	String url = "https://yts.am/browse-movies"; //?page=515";
    	// String path = "/home/psycho/Desktop/movies/";
    	String path = "/run/media/psycho/PENe/movies/";
    	    	
    	ArrayList<String> movie_urls = new ArrayList<String>();
    	
    	ArrayList<ArrayList<String>> download_movies = new ArrayList<ArrayList<String>>();
    	
    	// Test connection with the main database
    	dbManager.testConnect();
    	
    	// Create a temporal database
    	dbManager.create_tmp_table();

    	// Extract links in the main pages
    	movie_urls = indexer.index_movies(url);
    	
    	// Extract the info for each movie
    	int total_crawled = extractor.extract_info(movie_urls, dbManager);
    	    	
    	// Delete duplicated data from the tmp_database database
    	dbManager.delete_duplicates();
    	
    	// Extracts into an ArrayList<ArrayList<String>> all the movies to be downloaded
    	download_movies = dbManager.prepare_download_info();
    	
    	// Downloads the movies using java.nio
    	int total_downloaded = downloader.download_movies(download_movies, path);
    	
    	// Add new downloaded movies to the database
    	dbManager.insert_downloaded_movies();
    	
    	// Delete temporal database
    	dbManager.delete_tmp_table();
    	    	
    	System.out.println( "All done!" );
    	System.out.println("Crawled: " + total_crawled);
    	System.out.println("Downloaded: " + total_downloaded);
    	
    }
    
}
