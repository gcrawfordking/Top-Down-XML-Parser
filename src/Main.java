import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//CS575 Project 1

public class Main {

	public static void main (String args[]) throws IOException
	{
		// read in the input file, strip whitespace, and put in String Buffer
		final String fileName = "input0.xml";
		BufferedReader buffReader = new BufferedReader(new FileReader(fileName));
		
		String inputLine = null;
		StringBuilder strBldr = new StringBuilder();
		
		while((inputLine = buffReader.readLine()) != null) {
			strBldr.append(" " + inputLine.trim()); }
		
		buffReader.close();
		// System.out.println(strBldr);
		
		ArrayList<Token> tokenList = XML_Scanner.scan(strBldr.toString());

//		System.out.println("---- TOKEN LIST ----");
//		for (int i = 0; i < tokenList.size(); i++) {
//			System.out.println(tokenList.get(i).getTypeStr() + "\t" + tokenList.get(i).toString());
//		}
		
		// basic error checking
		if (XML_Scanner.checkList(tokenList) == true)
		{
			XML_Parser.parseTokens(tokenList); // run parser
		}
		
	}
}
