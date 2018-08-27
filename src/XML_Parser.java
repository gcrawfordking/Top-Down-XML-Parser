import java.util.ArrayList;
import java.util.Stack;

public class XML_Parser {
	
	// *******************	Token and Variable Integer Codes ******************* //	
	
	// Terminals:
			static final int BOTTOM = 0;	// bottom of stack symbol
			static final int GT = 1;
			static final int LT = 2;
			static final int GTFS = 3;
			static final int LTFS = 4;
			static final int EQ = 5;
			static final int NAME = 6; // push name tags on this one
			static final int PNAME = 7; // for popping tag names
			static final int ANAME = 8;
			static final int STRING = 9;
			static final int DATA = 10;
			
	// Variables:
			static final int VARIABLES = 11;
			static final int DOC = 12;
			static final int E = 13;
			static final int E0 = 14;
			static final int E1 = 15;
			static final int ED = 16;
			static final int A = 17;

	public static int parseTokens( ArrayList<Token> tokenList )
	{
		
// method takes ArrayList of Token objects as parameter. Prints parsing results to console. Returns 0 if no error, 1 otherwise.		
		
		
		// *******************	Grammar Rules ******************* //
		
//		(1)			DOC     ==>  E
// 		(2)			E       ==>  (<) E0
// 		(3)			E0      ==>  (name) A E1	// this needs to push name val... using int nameStackMode == 0
		
// 		(4)			E1      ==>  (>) ED (</)(name)(>)	//this needs to pop name val... using int nameStackMode == 1
// 		(5)			E1      ==>  (/>)
		
// 		(6)			ED   	==>  (<)E0 ED
// 		(7)			ED   	==>  (data) ED
// 		(8)			ED   	==>  (empty)
		
// 		(9)			A     	==>  (name)(=)("string") A		// need to ensure unique attribute names? or implement in scanner...
// 		(10)		A     	==>  (empty)					// this uses 
		
// 		Attribute names must be unique!
// 		Start Tag and End Tag names must agree!
		
// Stacks
		Stack<Integer> pdaStack = new Stack<Integer>();
		Stack<Token> nameStack = new Stack<Token>();
		int poppedVal = 0;
		Token poppedName = null;
		
// indexes for consumed input
		int currentTokenIndx = 0;
		//int inputLen = tokenList.size();
		
		
// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>	 //		
// ***************************** Parse procedure below ***************************** //
// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>	 //			
	
		
		pdaStack.push(BOTTOM);
		pdaStack.push(DOC);
		
		// main loop runs until all of input is comsumed or error is encountered
		while ((pdaStack.size() > 0) && (currentTokenIndx < tokenList.size()))
		{
			poppedVal = pdaStack.pop();
			
			//if terminal...
			if (poppedVal < VARIABLES)
				{
					 // current token matches top of stack -- consume input and move on
				if (poppedVal == tokenList.get(currentTokenIndx).getTypeVal()
						|| (tokenList.get(currentTokenIndx).getTypeVal() == NAME && poppedVal == PNAME))
					{
					
					// check for name tag match
					if (poppedVal == NAME)
					{
						nameStack.push(tokenList.get(currentTokenIndx));
					}
					
					if (poppedVal == PNAME) // check to make sure Names in tags match
					{
						poppedName = nameStack.pop();
						if (!(tokenList.get(currentTokenIndx).toString().equals(poppedName.toString())))
						{
							System.out.println("Error: Start Tag and End Tag Name Mismatch:");
							System.out.println("Expected: " + poppedName.toString());
							System.out.println("Got: " + tokenList.get(currentTokenIndx).toString());
							return 1;
						}
					}
						
						// bottom of stack reached -- is it also end of input? if so, parse was successful
						if (poppedVal == BOTTOM)
						{	
							
							if (tokenList.get(currentTokenIndx).getTypeVal() == Token.END)
							{
								System.out.println("Parse Successful!");
								return 0;
							}
							
							else
							{
								System.out.println("Error: Bottom of stack reached prematurely!");
								return 1;
							}
						}
						
						else { currentTokenIndx++; }
					}
					
					
					else // error
					{
						System.out.println("Generic Error!");
						return 1;
					}
					
					// Print status of stack
					System.out.println("Popped terminal: " + getTypeStr(poppedVal));
					//System.out.println("New Input Consumed: " + tokenList.subList(currentTokenIndx - 1, currentTokenIndx).toString());
					System.out.println("Cumulative Input Consumed: " + tokenList.subList(0, currentTokenIndx).toString());
					System.out.println("Current Token: " + tokenList.get(currentTokenIndx).toString());
					System.out.println("--Current Stack--");
					for (int k = pdaStack.size() - 1; k >= 0; k--)
					{
						System.out.println(getTypeStr(pdaStack.get(k)));
					}
					
//					System.out.println("--Current Name Stack--");
//					for (int k = nameStack.size() - 1; k >= 0; k--)
//					{
//						System.out.println(nameStack.get(k).toString());
//					}
					
					System.out.println();
					
			}
			
			//if variable...
				else
				{
					System.out.println("Popped variable: " + getTypeStr(poppedVal));
					
					switch(poppedVal){
					
					case (DOC):
					{
	//					(1)			DOC     ==>  E
						pdaStack.push(E);
						System.out.println("Applied Rule: (1)  DOC  ==>  E");
						break;
					}
						
					case (E):
					{
	//			 		(2)			E       ==>  (<) E0
						pdaStack.push(E0);
						pdaStack.push(LT);
						System.out.println("Applied Rule: (2)  E  ==>  < E0");
						break;
					}
						
					case (E0): // this rule needs to push N
					{
	//			 		(3)			E0      ==>  (name) A E1
						pdaStack.push(E1);
						pdaStack.push(A);
						pdaStack.push(NAME);
						System.out.println("Applied Rule: (3)  E0  ==>  name A E1");
						break;
					}
						
					case (E1): // this rule needs to pop N
					{
	//			 		(4)			E1      ==>  (>) ED (</)(name)(>)
	//			 		(5)			E1      ==>  (/>)
						
						if (tokenList.get(currentTokenIndx).getTypeVal() == GT)
						{
							pdaStack.push(GT);
							pdaStack.push(PNAME);
							pdaStack.push(LTFS);
							pdaStack.push(ED);
							pdaStack.push(GT);
							System.out.println("Applied Rule: (4)  E1  ==>  > ED </ name >");
						}
						
						else if (tokenList.get(currentTokenIndx).getTypeVal() == GTFS)
						{
							pdaStack.push(GTFS);
							nameStack.pop(); // tag was empty tag, no need to verify names
							System.out.println("Applied Rule: (5)  E1  ==>  />"); 
						}
						
						break;
					}
						
					case (ED):
					{
	//			 		(6)			ED   	==>  (<)(E0) ED
	//			 		(7)			ED   	==>  (data) ED
	//			 		(8)			ED   	==>  (empty)
						
						if (tokenList.get(currentTokenIndx).getTypeVal() == LT)
						{
							pdaStack.push(ED);
							pdaStack.push(E0);
							pdaStack.push(LT);
							System.out.println("Applied Rule: (6)  ED  ==>  < E0 ED");
							break;
						}
						
						else if (tokenList.get(currentTokenIndx).getTypeVal() == DATA)
						{
							pdaStack.push(ED);
							pdaStack.push(DATA);
							System.out.println("Applied Rule: (7)  ED  ==>  data ED");
							break;
						}
						
						System.out.println("Applied Rule: (8)  ED  ==>  empty");
						break;
					}
						
					case (A):
					{
	//			 		(9)			A     	==>  (name)(=)("string") A
	//			 		(10)		A     	==>  (empty)
						
						if (tokenList.get(currentTokenIndx).getTypeVal() == ANAME)
						{
							pdaStack.push(A);
							pdaStack.push(STRING);
							pdaStack.push(EQ);
							pdaStack.push(ANAME);
							System.out.println("Applied Rule: (9) A  ==>  name = string A");
							break;
						}
						
						System.out.println("Applied Rule: (10)  A  ==>  empty");
						break;
					} 
				
				} // end switch
					
					// Print status of stack
					//System.out.println("Popped variable: " + getTypeStr(poppedVal));
					System.out.println("--Current Stack--");
					for (int k = pdaStack.size() - 1; k >= 0; k--)
					{
						System.out.println(getTypeStr(pdaStack.get(k)));
					}
					
					System.out.println();
			}
		}
		
		
		return 0; // no error
		
	}
	
	public static String getTypeStr(int val)
	{
		switch(val)
		{
			case (BOTTOM): { return "BOTTOM"; }
//			case (GT): { return "GT"; }
//			case (LT): { return "LT"; }
//			case (GTFS): { return "GTFS"; }
//			case (LTFS): { return "LTFS"; }
//			case (EQ): { return "EQ"; }
			case (GT): { return ">"; }
			case (LT): { return "<"; }
			case (GTFS): { return "/>"; }
			case (LTFS): { return "</"; }
			case (EQ): { return "="; }
			case (NAME): { return "name"; }
			case (PNAME): { return "name"; }
			case (ANAME): { return "name"; }
			case (STRING): { return "string"; }
			case (DATA): { return "data"; }
			
			case (DOC): { return "DOC"; }
			case (E): { return "E"; }
			case (E0): { return "E0"; }
			case (E1): { return "E1"; }
			case (ED): { return "ED"; }
			case (A): { return "A"; }
			
			default: return "err";
		}
	}
	
}
