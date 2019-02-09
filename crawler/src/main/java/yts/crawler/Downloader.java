package yts.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Downloader {
	
	public int download_movies(ArrayList<ArrayList<String>> download_movies, String folder_path) {
		
		int total_downloaded = 0;
		
		for(ArrayList<String> al : download_movies) {
			
			String title = al.get(0);
			title = title.replace(' ', '_');
			String year = al.get(1);
			String link_3D = al.get(2);
			//link_3D = '"' + link_3D + '"';
			String link_720 = al.get(3);
			//link_720 = '"' + link_720 + '"';
			String link_1080 = al.get(4);
			//link_1080 = '"' + link_1080 + '"';
			String link_thumb = al.get(5);
			//link_thumb = '"' + link_thumb + '"';
			
			String path = title + '-' + year;
			
			System.out.println("	Downloading: " + path);
			
			Path check_path = Paths.get(folder_path + path);
			if(Files.notExists(check_path)) {
				
				new File(folder_path + path).mkdirs();
							
				if(link_3D.length() > 10)
					this.download_url_2_path(link_3D, folder_path + '/' + path + '/' + path + "-3D.torrent");
				
				if(link_720.length() > 10)
					this.download_url_2_path(link_720, folder_path + '/' + path + '/' + path + "-720p.torrent");
				
				if(link_1080.length() > 10)
					this.download_url_2_path(link_1080, folder_path + '/' + path + '/' + path + "-1080p.torrent");
				
				if(link_thumb.length() > 10)
					this.download_url_2_path(link_thumb, folder_path + '/' + path + '/' + path + "-thumb.jpg");
				
				total_downloaded++;
				
			}
			
		}
		
		return total_downloaded;
		
	}
	
	public void download_url_2_path(String url, String path) {
		
		try {
            URL website = new URL(url);
            ReadableByteChannel rbc;
            rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

}
