package yts.crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseManager {
	
	private static String path = "jdbc:sqlite:/home/psycho/Desktop/scripts/yts_crawler/yts_database.db";
		
	// Test connection with the database
	public void testConnect() {
	    	
		Connection conn = null;
		
		try {
		  
			conn = DriverManager.getConnection(path);
		    
		    System.out.println("Connection to the database has been stablished.");
		    
		} catch (SQLException e) {
			
		    System.out.println(e.getMessage());
		    
		} finally {
			
		    try {
		    	
		        if (conn != null)
		        	conn.close();
		        
		    } catch (SQLException ex) {
		    	
		        System.out.println(ex.getMessage());
		    
		    }
		    
		}
	    
	}
	
	// Creates the connection with the database
	private Connection connect(String path) {
        
		Connection conn = null;
		
		try {
		
			conn = DriverManager.getConnection(path);
		
		} catch (SQLException e) {
		
			System.out.println(e.getMessage());
		
		}
		
		return conn;
    
	}
	
	// Creates a temporary table to avoid downloading the same movie 2 times
	public void create_tmp_table() {
		
		try (Connection conn = DriverManager.getConnection(path)) {
			
			if(conn != null) {
				
				this.create_tmp_movies_table(conn);
				
			}
			
		} catch (SQLException e) {
				
			e.printStackTrace();
				
		}
		
	}
	private void create_tmp_movies_table(Connection conn) throws SQLException {
		
		String sql = "CREATE TABLE `tmp_movies` (\n" + 
				"	`movie_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" + 
				"	`movie_url`	TEXT NOT NULL,\n" + 
				"	`movie_title`	TEXT,\n" + 
				"	`movie_year`	INTEGER,\n" + 
				"	`movie_genre`	TEXT,\n" + 
				"	`movie_3D_torrent`	TEXT,\n" + 
				"	`movie_720p_torrent`	TEXT,\n" + 
				"	`movie_1080p_torrent`	TEXT,\n" + 
				"	`movie_thumbnail`	TEXT\n" + 
				");";
		
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		
		System.out.println("Table 'tmp_movies' created succesfully in /home/psycho/Desktop/scripts/yts_crawler/yts_database.db");
		
	}
	
	// Inserts the crawled movies into the temporary table
	public void insert_movie_info_tmp(String url, String title, String year, String genre, String link1, String link2, String link3, String thumb) {
    	
       	url = '"' + url + '"';
    	title = '"' + title + '"';
    	genre = '"' + genre + '"';
    	link1 = '"' + link1 + '"';
    	link2 = '"' + link2 + '"';
    	link3 = '"' + link3 + '"';
    	thumb = '"' + thumb + '"';
    	
    	String sql = "INSERT INTO tmp_movies(movie_url, movie_title, movie_year, movie_genre, movie_3D_torrent, movie_720p_torrent, movie_1080p_torrent, movie_thumbnail) VALUES(" + url + ", " + title + ", " + year + ", " + genre + ", " + link1 + ", " + link2 + ", " + link3 + ", " + thumb + ")";
    	
        try (Connection conn = this.connect(path);
        		
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            conn.close();
            
        } catch (SQLException e) {
            
        	System.out.println(e.getMessage());
        
        }
    
    }

	// Deletes the temporary table
	public void delete_tmp_table() {
		
		String sql = "DROP TABLE IF EXISTS tmp_movies";
		
		 try (Connection conn = this.connect(path);
	        		
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            conn.close();
	            
            System.out.println("Table 'tmp_movies' deleted succesfully in /home/psycho/Desktop/scripts/yts_crawler/yts_database.db");
	            
        } catch (SQLException e) {
	            
        	System.out.println(e.getMessage());
	        
        }
	
	}
	
	// Deletes the movies from tmp_movies that already exist in the table 'movies', the ones already downloaded
	public void delete_duplicates() {
		
		String sql = "DELETE FROM tmp_movies WHERE movie_title IN (SELECT movie_title FROM movies)";
		
		 try (Connection conn = this.connect(path);
	        		
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
           pstmt.executeUpdate();
           conn.close();
	            
           System.out.println("Duplicates deleted succesfully");
	            
       } catch (SQLException e) {
	            
    	   System.out.println(e.getMessage());
	        
       }
		
	}
	
	// Extracts the movies to be downloaded from the database
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> prepare_download_info(){
		
		ArrayList<ArrayList<String>> download_movies = new ArrayList<ArrayList<String>>();
		ArrayList<String> aux = new ArrayList<String>();
		
		String sql = "SELECT movie_title, movie_year, movie_3D_torrent, movie_720p_torrent, movie_1080p_torrent, movie_thumbnail FROM tmp_movies";
		
		try (Connection conn = this.connect(path);
	            Statement stmt  = conn.createStatement();
	            ResultSet rs    = stmt.executeQuery(sql)){
	        	
	        	while(rs.next()) {
	        		
	        		aux.add(rs.getString("movie_title"));
	        		aux.add(rs.getString("movie_year"));
	        		aux.add(rs.getString("movie_3D_torrent"));
	        		aux.add(rs.getString("movie_720p_torrent"));
	        		aux.add(rs.getString("movie_1080p_torrent"));
	        		aux.add(rs.getString("movie_thumbnail"));
	        		
	        		download_movies.add((ArrayList<String>) aux.clone());
	        		//System.out.println(aux);
	        		
	        		aux.clear();
	        		
	        	}
	        
	        } catch (SQLException e) {
	        	
	            System.out.println(e.getMessage());
	            
	        }
		
		return download_movies;
		
	}
	
	// Inserts the downloaded movies into the database
	public void insert_downloaded_movies() {
		
		String sql = "INSERT INTO movies(movie_url, movie_title, movie_year, movie_genre, movie_3D_torrent, movie_720p_torrent, movie_1080p_torrent, movie_thumbnail) SELECT movie_url, movie_title, movie_year, movie_genre, movie_3D_torrent, movie_720p_torrent, movie_1080p_torrent, movie_thumbnail FROM tmp_movies";
		
		try (Connection conn = this.connect(path);
        		
	            PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.executeUpdate();
	            conn.close();
	            
	        } catch (SQLException e) {
	            
	        	System.out.println(e.getMessage());
	        
	        }
		
	}
	
}
