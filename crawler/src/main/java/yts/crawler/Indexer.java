package yts.crawler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Indexer {
	
	public ArrayList<String> index_movies(String url){
		
		ArrayList<String> movie_urls = new ArrayList<String>();
		
		System.out.println("Indexing pages...");
		
		try {
			
			boolean end = false;
			do {
				
				System.out.println("   Procesing: " + url);
				
				Document doc = Jsoup.connect(url).get();
				Elements navs = doc.select(".hidden-md.hidden-lg:last-child ul>li:last-of-type>a:last-of-type");
				
				for(Element el : navs) {
					
					url = el.attr("abs:href");
					
				}
				
				if(navs.isEmpty())
					end = true;
				
				Elements movs = doc.select(".browse-movie-link");
				
				for(Element el : movs) {
					
					movie_urls.add(el.attr("abs:href"));
					
				}
				
			} while(!end);
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		movie_urls = this.delete_duplicates(movie_urls);
		
		return movie_urls;
		
	}
	
	private ArrayList<String> delete_duplicates(ArrayList<String> al) {
		
		Set<String> aux = new LinkedHashSet<>();
		
		aux.addAll(al);
		al.clear();
		al.addAll(aux);
		
		return al;		
		
	}

}
