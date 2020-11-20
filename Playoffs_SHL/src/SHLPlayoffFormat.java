import java.sql.*;

import java.sql.DriverManager;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
 
import java.io.FileWriter;

public class SHLPlayoffFormat {
   public static void main(String[] args) {
      String MySQLURL = "jdbc:mysql://localhost:3306/shl_dump?allowPublicKeyRetrieval=true&useSSL=false";
      String databseUserName = "root";
      String databasePassword = "luketd";
      Connection con = null;
      try {
    	  int league = 1;
    	  int season = 55;
         con = DriverManager.getConnection(MySQLURL, databseUserName, databasePassword);
         String query = "SELECT DISTINCT Home as Home_Team_Name, HomeScore, Away as Away_Team_Name, AwayScore From Schedules \r\n"
            		+ "INNER JOIN league_data USING(LeagueID)\r\n"
            		+ "INNER JOIN team_data D1 ON D1.TeamID=Schedules.Home\r\n"
            		+ "INNER JOIN team_data D2 ON D2.TeamID=Schedules.Away\r\n"
            		+ "WHERE type= 'Playoffs' and Schedules.LeagueId= " + league + " and D1.LeagueId= " + league + " and D2.LeagueId= " + league + " and Schedules.seasonID= " + season + " ORDER BY DATE_FORMAT(Schedules.Date, '%Y-%m-%d') asc;";
          
          
          
          Statement st = con.createStatement();
            
          ResultSet rs = st.executeQuery(query);
          ArrayList<Integer> Round = new ArrayList<Integer>();
          
          while (rs.next()) {
        	  int Home_Team = rs.getInt("Home_Team_Name");
        	  int HomeScore = rs.getInt("HomeScore");
        	  int Away_Team = rs.getInt("Away_Team_Name");
        	  int AwayScore = rs.getInt("AwayScore");
        	  
        	  //checks to see if the array is empty, if it is then start filling the array with the first series
        	  int happened = 0;
        	  if(Round.isEmpty()) {
	        	  if (HomeScore > AwayScore) {
	        		  Round.add(Home_Team);
					  Round.add(1);
					  Round.add(Away_Team);
					  Round.add(0);
					  happened = 1;
					  
				  } else {
					  Round.add(Home_Team);
					  Round.add(0);
					  Round.add(Away_Team);
					  Round.add(1);
					  happened = 1;
				  }
	        	  
	        	  
        	  } else {
        		//checks if the series is in use, if it is, then find the winner and update the series
        		  for (int x=0; x < Round.size() -2; x++) {
        			  
            		  if ((Round.get(x) == Home_Team && Round.get(x+2)  == Away_Team) ) {
            			  if (HomeScore > AwayScore) {
            				  Round.set(x+1, Round.get(x+1) +1) ;
            				  happened = 1;
            				  break;
            			  } else {
            				  Round.set(x+3, Round.get(x+3) +1);
            				  happened = 1;
            				  break;
            			}} else if(Round.get(x+2) == Home_Team && Round.get(x)  == Away_Team){
            			  if (HomeScore > AwayScore) {
            				  Round.set(x+3, Round.get(x+3) +1); 
            				  happened = 1;
            				  break;
            			  } else {
            				  Round.set(x+1, Round.get(x+1) +1);
            				  happened = 1;
            				  break;
            			  }
            			  
            		 }
            		 x = x+3; //Iterate to the next series
            		  
        		  }
        		  
        	  }
        	  
        	  //If the teams havent start yet, it will come here and create the series
        	  if (happened == 0) {
        		  if (HomeScore > AwayScore) {
	        		  Round.add(Home_Team);
					  Round.add(1);
					  Round.add(Away_Team);
					  Round.add(0);		  
				  } else {
					  Round.add(Home_Team);
					  Round.add(0);
					  Round.add(Away_Team);
					  Round.add(1);  
				  }
        	  }
        	  
        	  //System.out.format("%s,  %s, %s, %s, %s, %s, %s \n", LeagueID, SeasonID, Date, Home_Team, HomeScore, Away_Team, AwayScore);
          }
          st.close();
          
          
          ArrayList<Integer> Round1 = new ArrayList<Integer>();
          ArrayList<Integer> Round2 = new ArrayList<Integer>();
          ArrayList<Integer> Round3 = new ArrayList<Integer>();
          ArrayList<Integer> Round4 = new ArrayList<Integer>();
          //Check to see if in 8 Team Playoff Format
          if((league == 0 && season <= 55) || (league == 1 && season <= 54)  || league ==2 || league == 3) {
        	  for (int g = 0; g < Round.size() ; g++) {
        		  if (g< 13) {
        			  Round1.add(Round.get(g));
        			  Round1.add(Round.get(g+1));
        			  Round1.add(Round.get(g+2));
        			  Round1.add(Round.get(g+3));
        		  } else if (g >=13 && g<21) {
        			  Round2.add(Round.get(g));
        			  Round2.add(Round.get(g+1));
        			  Round2.add(Round.get(g+2));
        			  Round2.add(Round.get(g+3));
        		  } else {
        			  Round3.add(Round.get(g));
        			  Round3.add(Round.get(g+1));
        			  Round3.add(Round.get(g+2));
        			  Round3.add(Round.get(g+3));
        		  }
        		  g= g+3;
        	  }  
          }
        //Check to see if in 12 Team Playoff Format
          if((league == 0 && season > 55) || (league == 1 && season > 54)) { 
        	  for (int g = 0; g < Round.size() ; g++) {
        		  if (g< 13) {
        			  Round1.add(Round.get(g));
        			  Round1.add(Round.get(g+1));
        			  Round1.add(Round.get(g+2));
        			  Round1.add(Round.get(g+3));
        		  } else if (g >=13 && g<29) {
        			  Round2.add(Round.get(g));
        			  Round2.add(Round.get(g+1));
        			  Round2.add(Round.get(g+2));
        			  Round2.add(Round.get(g+3));
        		  } else if (g >=29 && g<37) {
        			  Round3.add(Round.get(g));
        			  Round3.add(Round.get(g+1));
        			  Round3.add(Round.get(g+2));
        			  Round3.add(Round.get(g+3));
        		  } else {
        			  Round4.add(Round.get(g));
        			  Round4.add(Round.get(g+1));
        			  Round4.add(Round.get(g+2));
        			  Round4.add(Round.get(g+3));
        		  }
        		  g= g+3;
        	  }
          }
          
          //Rounds and Series with 8 team playoff format
          JSONObject R1S1 = new JSONObject();
    	  JSONObject R1S2 = new JSONObject();
    	  JSONObject R1S3 = new JSONObject();
    	  JSONObject R1S4 = new JSONObject();
    	  
    	  JSONObject R2S1 = new JSONObject();
    	  JSONObject R2S2 = new JSONObject();
    	  
    	  JSONObject R3S1 = new JSONObject();
    	  
    	  //Start of extra series with 12 team playoff format
    	  JSONObject R2S3 = new JSONObject();
    	  JSONObject R2S4 = new JSONObject();
    	  
    	  JSONObject R3S2 = new JSONObject();
    	  
    	  JSONObject R4S1 = new JSONObject();
    	  
    	  //Arrays for json
    	  JSONArray RoundOne = new JSONArray();
    	  JSONArray RoundTwo = new JSONArray();
    	  JSONArray RoundThree = new JSONArray();
    	  JSONArray RoundFour = new JSONArray();
    	  JSONArray Rounds = new JSONArray();
    	  
         // Round 1, same for both
    	  try {
    		  R1S1.put("team1:", Round1.get(0));
        	  R1S1.put("score1:", Round1.get(1));
        	  R1S1.put("team2:", Round1.get(2));
        	  R1S1.put("score2:", Round1.get(3));
        	  
        	  
        	  R1S2.put("team1:", Round1.get(4));
        	  R1S2.put("score1:", Round1.get(5));
        	  R1S2.put("team2:", Round1.get(6));
        	  R1S2.put("score2:", Round1.get(7));
        	  
        	  R1S3.put("team1:", Round1.get(8));
        	  R1S3.put("score1:", Round1.get(9));
        	  R1S3.put("team2:", Round1.get(10));
        	  R1S3.put("score2:", Round1.get(11));
        	   
        	  R1S4.put("team1:", Round1.get(12));
        	  R1S4.put("score1:", Round1.get(13));
        	  R1S4.put("team2:", Round1.get(14));
        	  R1S4.put("score2:", Round1.get(15));
    		  
    	  } catch(Exception IndexOutOfBoundsException) { 
    	  }//End of Round 1
    	  
    	  
    	  // Start of Round 2
    	  try {
    		  R2S1.put("team1:", Round2.get(0));
        	  R2S1.put("score1:", Round2.get(1));
        	  R2S1.put("team2:", Round2.get(2));
        	  R2S1.put("score2:", Round2.get(3));
        	  
        	  R2S2.put("team1:", Round2.get(4));
        	  R2S2.put("score1:", Round2.get(5));
        	  R2S2.put("team2:", Round2.get(6));
        	  R2S2.put("score2:", Round2.get(7));
    	  } catch(Exception IndexOutOfBoundsException) { 
    	  } // End of Round 2 for 8 team playoffs
    	  
    	  // Start of Round 3
    	  try {
    		  R3S1.put("team1:", Round3.get(0));
        	  R3S1.put("score1:", Round3.get(1));
        	  R3S1.put("team2:", Round3.get(2));
        	  R3S1.put("score2:", Round3.get(3));
    	  } catch(Exception IndexOutOfBoundsException) { 
    	  }
    	  
    	  //Putting the rounds and series into json format
    	  RoundOne.add("Round 1:");
    	  RoundOne.add(R1S1);
    	  RoundOne.add(R1S2);
    	  RoundOne.add(R1S3);
    	  RoundOne.add(R1S4);
    	  
    	  RoundTwo.add("Round 2:");
    	  RoundTwo.add(R2S1);
    	  RoundTwo.add(R2S2);
    	  
    	  RoundThree.add("Round 3:");
    	  RoundThree.add(R3S1);
    	  
    	  //Check to see if 12 team playoff format
    	  if((league == 0 && season > 55) || (league == 1 && season > 54)) { 
    		  //Extension Round 2&3 for 12 team playoffs
    		  try {
    			  R2S3.put("team1:", Round2.get(8));
            	  R2S3.put("score1:", Round2.get(9));
            	  R2S3.put("team2:", Round2.get(10));
            	  R2S3.put("score2:", Round2.get(11));
            	  
            	  R2S4.put("team1:", Round2.get(12));
            	  R2S4.put("score1:", Round2.get(13));
            	  R2S4.put("team2:", Round2.get(14));
            	  R2S4.put("score2:", Round2.get(15));
    		  } catch(Exception IndexOutOfBoundsException) {
        	  }
    		  
        	  try {
        		//Extension of Round 3
            	  R3S2.put("team1:", Round3.get(4));
            	  R3S2.put("score1:", Round3.get(5));
            	  R3S2.put("team2:", Round3.get(6));
            	  R3S2.put("score2:", Round3.get(7));
            	  
        	  }catch(Exception IndexOutOfBoundsException) {
        	  }
        	  
        	  
        	  try {
        		//Round 4
        		  RoundFour.add("Round 4: ");
            	  R4S1.put("team1:", Round4.get(0));
            	  R4S1.put("score1:", Round4.get(1));
            	  R4S1.put("team2:", Round4.get(2));
            	  R4S1.put("score2:", Round4.get(3));
        		  
        	  }catch(Exception IndexOutOfBoundsException) {
        	  }
        	  
        	  
        	  RoundTwo.add(R2S3);
        	  RoundTwo.add(R2S4);
        	  RoundThree.add(R3S2);
        	  RoundFour.add(R4S1);
        	  
        	  //12 Team Format
        	  Rounds.add("Rounds");
        	  Rounds.add(RoundOne);
        	  Rounds.add(RoundTwo);
        	  Rounds.add(RoundThree);
        	  Rounds.add(RoundFour);
        	  
    	  } else {
    		  //8 Team Format
    		  Rounds.add("Rounds");
        	  Rounds.add(RoundOne);
        	  Rounds.add(RoundTwo);
        	  Rounds.add(RoundThree); 
    	  }
    	  
    	  
    	  FileWriter file = new FileWriter("D:\\Eclipse\\Playoffs_SHL\\output.json");
    	  System.out.println(Rounds);
    	  file.write(Rounds.toJSONString());
    	  file.close();
          
         
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}



