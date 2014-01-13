
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A class for determining the URL's of a MM2.0 list to download based on the
 * kittystore/scripts.py file on the hyperkitty github account. this class can
 * output to console the month-years of a lists archive in order to retrieve the
 * text-archives for each month where there was correspondence on a list.
 * presumably, next we'd retreive the .txt file and parse it for a search key to
 * help the user determine whether the key occurred.
 * 
 * @Author Chris Cargile
 **/
public class Computater {

	static enum MONTHS {
		January, February, March, April, May, June, July, August, September, October, November, December
	};

	private ArrayList<String> answer;

	/**
	 * get a url-specified list and return a list of all the URL's for the
	 * Monthly files associated with it.
	 * 
	 * @param args
	 *            either a single arg or optionally 3 args, where the starting
	 *            year and ending year are provided if desired
	 **/
	public Computater(String[] args) {
		answer = getArchiverMonths();
	}

	public ArrayList<String> getArchiverMonths() {
		String url = "http://lists.csclug.org/pipermail/csclug";
		String htmlSource = stringifyHTMLSource(url);
		int startYear = 1988;
		final Calendar thisYear = Calendar.getInstance();
		int endYear = thisYear.get(Calendar.YEAR);
		int year = startYear;
		ArrayList<String> result = new ArrayList<String>();
		for (; year >= startYear && year <= endYear; year++) {
			for (Object Month : MONTHS.values()) {
				String month = (String) Month.toString();
				String target = year + "-" + month;
				result.add(target);
			}
		}
		ArrayList<String> result2 = new ArrayList<String>();
		for (String a : result)
			result2.add(new String(a));

		for (int i = 0; i < result.size(); i++) {
			String word = result.get(i);
			if (!htmlSource.contains(word)) {
				result2.remove(word);
			}
		}
		result2.clear();
		result2.add("2014-January");
		return result2;
	}

	public String stringifyHTMLSource(String url) {
		URL url2;
		HttpURLConnection http;
		InputStreamReader is;
		String htmlSource = "";
		try {
			url2 = new URL(url);
			http = (HttpURLConnection) url2.openConnection();
			http.setRequestMethod("GET");
			is = new InputStreamReader(http.getInputStream());
			BufferedReader rd = new BufferedReader(is);
			String line = rd.readLine();
			while (line != null) {
				htmlSource += line;
				line = rd.readLine();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlSource;
	}

	public void getMonths(){//ArrayList<String> getMonths() {
		File directory = new File("archives/");
		String currentFileContents = "";
		String archiveMonthEmailsURL = "";
		FileWriter writer = null;
		File currentFile = null;
		for(String s:getArchiverMonths()){
			archiveMonthEmailsURL = "http://lists.csclug.org/pipermail/csclug/" + s + "/thread.html";
			currentFileContents = stringifyHTMLSource(archiveMonthEmailsURL);
			//System.out.println(currentFile.createNewFile());
			
			currentFile = new File(directory+"/"+s);
			try {
				directory.mkdir();
				currentFile.createNewFile();
				writer = new FileWriter(currentFile);
				writer.write(currentFileContents);
				writer.flush();
				writer.close();
				getEmailsFromMonthHTMLSource(currentFile);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ;//months;
	}
	public void getEmailsFromMonthHTMLSource(File htmlSource){
		try{
			Document doc = Jsoup.parse(htmlSource, "UTF-8");
			Elements links = doc.select("a[href]"); // a with href
			links.remove(0);
			links.remove(0);
			links.remove(0);
			links.remove(0);
			links.remove(links.size()-1);
			links.remove(links.size()-1);
			links.remove(links.size()-1);
			links.remove(links.size()-1);
			for(Element e:links){
				System.out.println(e.toString()+"-"+e.attr("href")+"<BR>");
		}
//			System.out.println(links.toString());
			/*String out="";
			String html = "";
			Scanner scan = new Scanner(htmlSource);
			while(scan.hasNext()){
				html+=scan.nextLine();
			}
			scan.close();
			while(html.contains("--><LI><A HREF=\"")){
				html=html.substring(html.indexOf("<LI><A HREF=\""),
						html.indexOf("</A>"));
				System.out.println(html+"<BR><BR>");
			}*/
		}
			
		catch(Exception e){
			System.out.println(e);
		}
	}
	public static void main(String[] args) {
		Computater c = new Computater(null);
		c.getMonths();
	}
}
