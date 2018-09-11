package nflpredict.com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class nflPredict {
    /*
     * This Pattern will match on either quoted text or text between commas, including
     * whitespace, and accounting for beginning and end of line.
     */
    private final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");	
    private ArrayList<String> allMatches = null;	
    private Matcher matcher = null;
    private String match = null;
    private int size;

    public nflPredict() {		
    	allMatches = new ArrayList<String>();
    	matcher = null;
    	match = null;
    }

    public String[] parse(String csvLine) {
    	matcher = csvPattern.matcher(csvLine);
    	allMatches.clear();
    	String match;
    	while (matcher.find()) {
    		match = matcher.group(1);
    		if (match!=null) {
    			allMatches.add(match);
    		}
    		else {
    			allMatches.add(matcher.group(2));
    		}
    	}

    	size = allMatches.size();		
    	if (size > 0) {
    		return allMatches.toArray(new String[size]);
    	}
    	else {
    		return new String[0];
    	}			
    }	
    
	public static void main(String[] args) {
		
		BufferedReader fantasyFile = null;
		BufferedReader statsFile1 = null;
		BufferedReader statsFile2 = null;
		BufferedReader statsFile3 = null;
		
		//String statsLine = "1,Drew Brees ,NO ,QB,422,670,63.0,41.9,\"5,177\",7.7,323.6,43,19,266,39.7,80T ,66,13,26,96.3";
		//String fantasyLine = "1. Peyton Manning,DEN,16,450,659,5,477,55,10,32,-31,1,496.8,31.0";
	    
	    String fantasyPattern = "(\\w+)(.)(\\s+)(\\S+)(\\s+)(\\S+)";
	    
    	nflPredict myCSV = new nflPredict();
    	

    	// Create a Pattern object
		Pattern fp = Pattern.compile(fantasyPattern);
    	
		try {
	    
			String sCurrentLine;

			fantasyFile = new BufferedReader(new FileReader("/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/NFL-player-fantasy-2013-1.csv"));
			String sCurrentLine2;

			PrintWriter writerGood = new PrintWriter("/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/nfl-predict-good-players.csv", "UTF-8");
			PrintWriter writerBad = new PrintWriter("/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/nfl-predict-bad-players.csv", "UTF-8");
			
			int goodCounter = 0;
			int badCounter = 0;
			
			String fantasyPlayer = new String();
			String statsPlayer = new String(); //get stats player
			
			while ((sCurrentLine = fantasyFile.readLine()) != null) {
				
				String statsPlayerRow = new String();
				
				String FFPTsPerGame = new String();
				
				//System.out.println("Testing CSVParser with: \n " + sCurrentLine);
				// Grab first players name
		    	String sqb[] = myCSV.parse(sCurrentLine);
		    	FFPTsPerGame = sqb[12];
		    	
				// Now create matcher object.
				Matcher m = fp.matcher(sqb[0]);
				if (m.find( )) {
					fantasyPlayer = m.group(4)+" "+m.group(6)+" ";
					System.out.println("Looking for Fantasy QB: "+fantasyPlayer +" FFPTsPerGame = "+FFPTsPerGame);				   	
				} else {
				     System.out.println("NO MATCH");
				}
								
				statsFile1 = new BufferedReader(new FileReader("/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/NFL-player-stats-2010-1.csv"));
				// read past the csv file header
				sCurrentLine2 = statsFile1.readLine(); 
				boolean found1 = false;
				while ((sCurrentLine2 = statsFile1.readLine()) != null) {
									
					// Grab first players name
			    	String fqb[] = myCSV.parse(sCurrentLine2);
			    	statsPlayer = fqb[1];
								
					if ((fantasyPlayer.compareTo(statsPlayer)) == 0) {					
						String YARDS = fqb[8];
						String TD = fqb[11];
						String INTS = fqb[12];
						YARDS = YARDS.replace(",", "");
						statsPlayerRow = statsPlayer + "," +YARDS+ "," + TD + "," + INTS + ",";
						//writerGood.println(sCurrentLine2);
						System.out.println("1  "+statsPlayerRow);
						
						found1 = true;
					} else {
						
					}
				}
				
				statsFile2 = new BufferedReader(new FileReader("/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/NFL-player-stats-2011-1.csv"));
				// read past the csv file header
				sCurrentLine2 = statsFile2.readLine(); 
				boolean found2 = false;
				while ((sCurrentLine2 = statsFile2.readLine()) != null) {
									
					// Grab first players name
			    	String fqb[] = myCSV.parse(sCurrentLine2);
			    	statsPlayer = fqb[1];
								
					if ((fantasyPlayer.compareTo(statsPlayer)) == 0) {
						String YARDS = fqb[8];
						String TD = fqb[11];
						String INTS = fqb[12];
						YARDS = YARDS.replace(",", "");
						statsPlayerRow = statsPlayerRow + YARDS+ "," + TD + "," + INTS + ",";
						//writerGood.println(sCurrentLine2);
						System.out.println("2  "+statsPlayerRow);
						
						found2 = true;
					} else {
						
					}
				}	
				
				statsFile3 = new BufferedReader(new FileReader("/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/NFL-player-stats-2012-1.csv"));
				// read past the csv file header
				sCurrentLine2 = statsFile3.readLine(); 
				boolean found3  = false;
				while ((sCurrentLine2 = statsFile3.readLine()) != null) {
									
					// Grab first players name
			    	String fqb[] = myCSV.parse(sCurrentLine2);
			    	statsPlayer = fqb[1];
								
					if ((fantasyPlayer.compareTo(statsPlayer)) == 0) {
						String YARDS = fqb[8];
						String TD = fqb[11];
						String INTS = fqb[12];
						YARDS = YARDS.replace(",", "");
						statsPlayerRow = statsPlayerRow + YARDS+ "," + TD + "," + INTS + ",";
						//writerGood.println(sCurrentLine2);
						System.out.println("3   "+statsPlayerRow);
												
						found3 = true;
					} else {
						
					}
				}				
				if( (!found1) || (!found2) || (!found3) ){
					System.out.println("################    Couldn't find QB ==> "+fantasyPlayer);					
					badCounter = badCounter +1;
				} else {
					statsPlayerRow = statsPlayerRow + FFPTsPerGame;
					System.out.println("***   FOUND: "+statsPlayerRow);
					writerGood.println(statsPlayerRow);
					goodCounter = goodCounter +1;
				}
			}
			writerGood.close();
			writerBad.close();
			System.out.println("Done!  Wrote out  "+goodCounter+" GOOD players");
			System.out.println("	   Wrote out  "+ badCounter+" BAD players");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fantasyFile != null)fantasyFile.close();
				if (statsFile1 != null)statsFile1.close();
				if (statsFile2 != null)statsFile2.close();
				if (statsFile3 != null)statsFile3.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}
