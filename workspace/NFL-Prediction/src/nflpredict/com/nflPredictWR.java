package nflpredict.com;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class nflPredictWR {
    /*
     * This Pattern will match on either quoted text or text between commas, including
     * whitespace, and accounting for beginning and end of line.
     */
	
    private final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");	
    private ArrayList<String> allMatches = null;	
    private Matcher matcher = null;
    private String match = null;
    private int size;
    		
    public nflPredictWR() {		
    	allMatches = new ArrayList<String>();
    	matcher = null;
    	match = null;
    }
    
    public static boolean isNumeric(String str)  
    {  
      try  
      {  
        double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
    	System.out.println("************ Notice! found non-numeric string in data: >"+str+"<"); 
        return false;  
      }  
      return true;  
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
    
    private static double[] maxValues = {0,0, 0, 0, 0, 0, 0, 0, 0};
    private static double[] minValues = {100000,100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000};

    private static void buildMinAndMaxDoubleArrays(String[] line) {
    	
        for (int i = 0; i < line.length; i++) {
                //check the max value
        		double currentValue = 0;
        		System.out.println("====="+line[i]);
        		if (i>0) {
        			currentValue = Double.parseDouble(line[i]);
        		}
                if(currentValue > maxValues[i] ) {
                    maxValues[i] = currentValue;
                }

                //check the min value
                if(currentValue < minValues[i]) {
                    minValues[i] = currentValue;
                }
        }
        
    }
    private static String[] computeMinAndMax(String line) {
    	// line example:   Josh Cribbs,1,31,1,8,0,2,6,0
    		String cvsSplitBy = ",";
		    String[] columns = line.split(cvsSplitBy);
		    // [player1, 10, 26, 12, 115, 0, 46, 695, 5]
		    
		    String[] normalizedColumns = new String[9];
		    double normalizedValue = 0;
		    List<String> l1 = Arrays.asList(columns);
		    System.out.println("current Line:"+l1+" size = "+columns.length);
		    for (int i = 0; i < columns.length; i++) {
		        	
		        double currentValue = 0;
		        if (i>0) {
		        	currentValue = Double.parseDouble(columns[i]);
		        }
		        if ((maxValues[i]-minValues[i]) > 0) {
		        	normalizedValue = (currentValue - minValues[i])/(maxValues[i]-minValues[i]);
		        } else {
		        	normalizedValue = 0; 
		        }
		        normalizedColumns[i] = Double.toString(normalizedValue);
		    }
		    normalizedColumns[0] = columns[0].toString();
		    
		    List<String> l2 = Arrays.asList(normalizedColumns);
		    String nLine = l2.toString();
		    System.out.println("returning =========================>>>>"+nLine);
		    
		    return normalizedColumns;
    }
    
    public static void normalizeFile(String inputFile, String outputFile) {
    	
    	String line = "";
    	String cvsSplitBy = ",";
    	BufferedReader in  = null;
    	PrintWriter out = null;
    	
    	for (int i = 0; i < 8; i++) {
    		maxValues[i] = 0;
    		minValues[i] = 100000;
    	}
    	
    	try {
    		in = new BufferedReader(new FileReader(inputFile));
    		out = new PrintWriter(outputFile);
			while ((line = in.readLine()) != null) {
	            
	            String[] columns= line.split(cvsSplitBy);
	            
	            List<String> l1 = Arrays.asList(columns);
	            System.out.println("current Line:"+l1);	            
	            buildMinAndMaxDoubleArrays(columns);            
			}

			System.out.println("minValues:"+Arrays.toString(minValues));            
            System.out.println("maxValues:"+Arrays.toString(maxValues));            
            // Example output:
            //
            // minValues:[0.0, 22.0, 1.0, 6.0, 0.0, 1.0, 6.0, 0.0]
            // maxValues:[252.0, 36.0, 129.0, 1698.0, 16.0, 113.0, 1646.0, 13.0]
            
            in.close();
            in = new BufferedReader(new FileReader(inputFile));
            
			while ((line = in.readLine()) != null) {
				// line example:   Josh Cribbs,1,31,1,8,0,2,6,0
				
				String[] normalizedColumns = computeMinAndMax(line);
				
			    List<String> l2 = Arrays.asList(normalizedColumns);
			    String nLine = l2.toString();
			    System.out.println("normalized Line Array:"+nLine);
			    
			    nLine = nLine.replace("[", "");
			    nLine = nLine.replace("]", "");
			   // System.out.println("normalized Line String:"+nLine);
			    out.println(nLine);
	            
			}
			in.close();
			out.close();
			
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void printPlayerLine(String playerStats, String player, PrintWriter playersToPredict) {
		
			String[] normalizedPlayer1Stats = computeMinAndMax(playerStats);
		    List<String> l1 = Arrays.asList(normalizedPlayer1Stats);
		    String nLine = l1.toString();
		    System.out.println("Normalized Stats for ["+player+"] : "+ nLine);
		    
		    nLine = nLine.replace("[", "");
		    nLine = nLine.replace("]", "");
		   
		    playersToPredict.println(player + ","+ nLine);		    
    }
    
	public static void main(String[] args) {
		
		BufferedReader fantasyFile = null;
		BufferedReader statsFile1 = null;
		BufferedReader statsFile2 = null;
		BufferedReader statsFile3 = null;

	    String fantasyPattern = "(\\w+)(.)(\\s+)(\\S+)(\\s+)(\\S+)";
	    String fantasyFileName = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/years_2014_fantasy_WR_2.csv";
	    
	    String stats2013FileName = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/years_2013_fantasy_WR_2.csv";
	    String stats2012FileName = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/years_2012_fantasy_WR_2.csv";
	    
	    String finalMergedDataFile2012_2013 = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/nfl-finalMergedDataFile-WR-players-2012-2013.csv";
	    String listOfThrownOutPlayersFile = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/nfl-missingPlayers-WR-players-2012-2013.csv";
	    
	    String finalMergedDataFile2013_2014 = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/nfl-finalMergedDataFile-WR-players-2013-2014.csv";

	    String finalMergedDataFileNormalized2012_2013 = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/nfl-finalMergedDataFile-WR-players-2012-2013_Norm.csv";
	    String finalMergedDataFileNormalized2013_2014 = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/nfl-finalMergedDataFile-WR-players-2013-2014_Norm.csv";
	   
	    String playersToPredictFile = "/Users/jhwalczyk12/Documents/_CTO/Analytics/NFL/nfl-WR-players-2014.csv";
	    
    	nflPredictWR myCSV = new nflPredictWR();
    	    	
    	 // Create a Pattern object
		Pattern fp = Pattern.compile(fantasyPattern);
    	
		try {
	    
			String sCurrentLine;

			fantasyFile = new BufferedReader(new FileReader(fantasyFileName));
			

			PrintWriter writerGood2012_2013 = new PrintWriter(finalMergedDataFile2012_2013, "UTF-8");
			PrintWriter writerGood2013_2014 = new PrintWriter(finalMergedDataFile2013_2014, "UTF-8");
			PrintWriter writerBad = new PrintWriter(listOfThrownOutPlayersFile, "UTF-8");		
			PrintWriter playersToPredict = new PrintWriter(playersToPredictFile, "UTF-8");	
			
			int goodCounter = 0;
			int badCounter = 0;
			
			String fantasyPlayer = new String();
			String statsPlayer = new String(); //get stats player
			String FFPTs = new String();
			String FFPTsPerGame = new String();
			String statsPlayerRow = new String();

		    String player1 = new String("Julio Jones");  
		    String player2 = new String("T.Y. Hilton"); 
		    String player3 = new String("Calvin Johnson"); 
		    
		    String player1Stats = new String();
		    String player2Stats = new String();
		    String player3Stats = new String();
		    
		    String predictionTargetNameStatsFor2013_2014 = new String();
		    
			while ((sCurrentLine = fantasyFile.readLine()) != null) {
				
				statsPlayerRow = "";				
				FFPTs = "0";
				FFPTsPerGame = "0";
				
				// Grab first players name
				// Example Row
				// 9,Josh Gordon*+,CLE,22,14,14,0,0,0,0,0,5,88,17.6,0,159,87,1646,18.92,9,WR,227,16.21,108,2,9
				
		    	String sqb[] = myCSV.parse(sCurrentLine);
		    	FFPTs = sqb[21];
		    	FFPTsPerGame = sqb[22];

		    	String fPlayerAge = sqb[3];
				String fREC = sqb[16];
				String fYARDS = sqb[17];
				String fTD = sqb[19];
				fYARDS = fYARDS.replace(",", "");				

		    	fantasyPlayer = sqb[1];	
				fantasyPlayer = fantasyPlayer.replace("*", "");
				fantasyPlayer = fantasyPlayer.replace("+", "");
				
				
				if (isNumeric(FFPTs) == false) {
					FFPTs = "0";
				}
				if (isNumeric(fPlayerAge) == false) {
					fPlayerAge = "0";
				}
				if (isNumeric(fREC) == false) {
					fREC = "0";
				}
				if (isNumeric(fYARDS) == false) {
					fYARDS = "0";
				}
				if (isNumeric(fTD) == false) {
					fTD = "0";
				}				
				System.out.println("Looking for 2014 Fantasy QB: "+fantasyPlayer +" (Age:"+fPlayerAge+") Total 2014 Fantasy Points = "+FFPTs+" FFPTsPerGame = "+FFPTsPerGame);
				
				statsPlayerRow = FFPTs + "," +fPlayerAge;
		
				predictionTargetNameStatsFor2013_2014 = statsPlayerRow + "," +fREC+ "," + fYARDS + "," + fTD;
				
				// ***************************************************************
				// 2013 STATS
				statsFile1 = new BufferedReader(new FileReader(stats2013FileName));
				// read past the csv file header
				String sCurrentLine2013;
				sCurrentLine2013 = statsFile1.readLine(); 
				boolean foundIn2013 = false;

				while ((sCurrentLine2013 = statsFile1.readLine()) != null) {
									
					// Grab first players name
			    	String fqb[] = myCSV.parse(sCurrentLine2013);
			    	statsPlayer = fqb[1];
			    	
			    	statsPlayer = statsPlayer.replace("*", "");
			    	statsPlayer = statsPlayer.replace("+", "");
			    	
					if ((fantasyPlayer.compareTo(statsPlayer)) == 0) {		
						
						String REC = fqb[16];
						String YARDS = fqb[17];
						String TD = fqb[19];
						YARDS = YARDS.replace(",", "");

						if (isNumeric(REC) == false) {
							REC = "0";
						}
						if (isNumeric(YARDS) == false) {
							YARDS = "0";
						}
						if (isNumeric(TD) == false) {
							TD = "0";
						}
						statsPlayerRow = statsPlayerRow + "," +REC+ "," + YARDS + "," + TD;
						predictionTargetNameStatsFor2013_2014 = predictionTargetNameStatsFor2013_2014 + "," +REC+ "," + YARDS + "," + TD;
	
						System.out.println("1  "+statsPlayerRow);
						
						foundIn2013 = true;
						break;
					} else {
						
					}
				}
				// ***************************************************************
				//  2012 STATS
				statsFile2 = new BufferedReader(new FileReader(stats2012FileName));
				String sCurrentLine2012;
				sCurrentLine2012 = statsFile2.readLine(); 
				boolean foundIn2012 = false;

				while ((sCurrentLine2012 = statsFile2.readLine()) != null) {
									
					// Grab first players name
			    	String fqb[] = myCSV.parse(sCurrentLine2012);
			    	statsPlayer = fqb[1];
			    	String statsPlayerTeam = fqb[2];
			    	statsPlayer = statsPlayer.replace("*", "");
			    	statsPlayer = statsPlayer.replace("+", "");
					
			    	String statsNameNTeam = statsPlayer + statsPlayerTeam; 
			    	
					if ((fantasyPlayer.compareTo(statsPlayer)) == 0) {		
						
						String REC = fqb[16];
						String YARDS = fqb[17];
						String TD = fqb[19];
						YARDS = YARDS.replace(",", "");

						if (isNumeric(REC) == false) {
							REC = "0";
						}
						if (isNumeric(YARDS) == false) {
							YARDS = "0";
						}
						if (isNumeric(TD) == false) {
							TD = "0";
						}
						statsPlayerRow = statsPlayerRow + "," +REC+ "," + YARDS + "," + TD;
						//writerGood.println(sCurrentLine2);
						System.out.println("1  "+statsPlayerRow);
						
						foundIn2012 = true;
						break;
					} else {
						
					}
				}			
				
				if( (foundIn2013) && (foundIn2012) ) {
					System.out.println("***   FOUND 2012-2013 Player "+fantasyPlayer+" : "+ statsPlayerRow);
					System.out.println("***   FOUND 2013-2014 Player "+fantasyPlayer+" : "+ predictionTargetNameStatsFor2013_2014);
					writerGood2012_2013.println(fantasyPlayer + "," +statsPlayerRow);
					writerGood2013_2014.println(fantasyPlayer + "," + predictionTargetNameStatsFor2013_2014);
					goodCounter = goodCounter +1;
				} else {
					System.out.println("################    Couldn't find "+fantasyPlayer);			
					writerBad.println(fantasyPlayer + " foundIn2013 = "+foundIn2013+" foundIn2012 = "+foundIn2012);
					badCounter = badCounter +1;
				}
			
				if ((fantasyPlayer.compareTo(player1)) == 0) {
					player1Stats = fantasyPlayer + "," +predictionTargetNameStatsFor2013_2014;					
				} else
				if ((fantasyPlayer.compareTo(player2)) == 0) {
					player2Stats = fantasyPlayer + "," +predictionTargetNameStatsFor2013_2014;
				} else
				if ((fantasyPlayer.compareTo(player3)) == 0) {
					player3Stats = fantasyPlayer + "," +predictionTargetNameStatsFor2013_2014;
				}
				
			} // end of While() 
			
			writerGood2012_2013.close();
			writerGood2013_2014.close();
			writerBad.close();
			
			System.out.println("Done!  Wrote out  "+goodCounter+" GOOD players");
			System.out.println("	   Wrote out  "+ badCounter+" BAD players");
			
			System.out.println("NORMALIZE 2012-2013 DATA FILE");
			normalizeFile(finalMergedDataFile2012_2013,finalMergedDataFileNormalized2012_2013);
			
			System.out.println("NORMALIZE 2013-2014 DATA FILE");
			normalizeFile(finalMergedDataFile2013_2014,finalMergedDataFileNormalized2013_2014);
			
			System.out.println(":::::::::::::::::::::::::::::::");
			printPlayerLine(player1Stats,player1,playersToPredict);
			printPlayerLine(player2Stats,player2,playersToPredict);
			printPlayerLine(player3Stats,player3,playersToPredict);		    	
			
			playersToPredict.close();
			
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
