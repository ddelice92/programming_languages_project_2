//package programming_languages_project_2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.IOException;

public class Main
{
	static Tokenizer tokenizer;
	static TokenObject currentToken;
	static boolean unexpected = false;
	static String file;
	//static Node currentParent = null;
	
	public static void main(String[] args)
	{
		//scanner class used for input at David's specific request
		//original alterantive method without scanner is saved in case using scanner breaks rules of assignment
		Scanner input = new Scanner(System.in);
		System.out.println("Enter file name: ");
		//BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		String retry = "n";
		
		do
		{
			System.out.println("Enter file to parse:");
			file = input.next();
			
			/*
			try
			{
				System.out.println("Enter file to parse:");
				file = input.readLine();
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
			*/
			
			//the tokenizer opens the file and passes tokens to the parser one at a time
			tokenizer = new Tokenizer(file);
			next();
			program();
			
			if(unexpected)
				System.out.println("Error was found at line:" + currentToken.getRow() + " column: " + currentToken.getColumn());
			else
				System.out.println("program file " + file + " was parsed without errors");
			
			System.out.println("read another file? Enter y for yes, anything else for no");
			
			retry = input.next();
			
			/*
			try
			{
				retry = input.readLine();
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
			*/
			
			unexpected = false;
			//currentParent = null;
		}
		while(retry.equals("y"));
		
		input.close();
	}
	
	/*
	static void createIdNode(TokenObject currentToken, Node parentNode)
	{
		Node node = new Node(currentToken.kind, parentNode);
		parentNode.addChild(node);
		Node node2 = new Node(currentToken.stringValue, node);
		node.addChild(node2);
	}
	
	static void createSymbolNode(TokenObject currentToken, Node parentNode)
	{
		Node node = new Node(currentToken.kind, parentNode);
		parentNode.addChild(node);
	}
	*/
	
	// next() gets the next token from the input file, outputting what kind of token is about to be evaluated
	// if the kind of the token is UNEXPECTED when next is called, next() will output that the token is 
	// unknown
	static void next()
	{
		tokenizer.next();
		currentToken = tokenizer.getToken();
	}
	
	// program() looks for the "program" keyword, an identifier, the ":" symbol, then calls the body()
	// method, and then looks for the "end" keyword, note that all or most function calls named as a 
	// keyword in the grammar, test whether an unexpected token has been called, before executing any
	// major method
	static void program()
	{
		//Node node = new Node("program", currentParent);
		//currentParent = node;
		
		if(!unexpected)
		{
			match("program", "program");
			if(!unexpected)
			{
				createIdNode(currentToken, currentParent);
				match("ID", "program");
				if(!unexpected)
				{
					match(":", "program");
					if(!unexpected)
					{
						body();
						if(!unexpected)
							match("end", "program");
					}
				}
			}
		}
	}
	
	// expected() outputs true if the kind of the global variable, currentToken, is the same as the
	// string argument passed to the method, returns false otherwise
	static boolean expected(String compare)
	{
		 if(currentToken.kind.equals(compare))
			 return true;
		 
		 return false;
	}
	
	// match() calls expected(), if expected() returns true, then calls next(), if expected() returns false,
	// it sets the global variable unexpected to be true, meaning that an unexpected token has been found, 
	// then puts out an error message stating what the current token is and what it is supposed to be
	static void match(String compare, String function)
	{		
		if(currentToken.kind.equals("UNEXPECTED"))
		{
			unexpected = true;
			System.out.println("ERROR: unknown token \'" + currentToken.kind + "\'");
		}
		
		if(expected(compare))
			next();
		else
		{
			unexpected = true;
			
			if(currentToken.kind.equals("ID"))
				System.out.println("ERROR FOUND IN FUNCTION CALL " + function + "(): \'" + currentToken.stringValue + "\' is supposed to be \'" + compare + "\'");
			else if(currentToken.kind.equals("NUM"))
				System.out.println("ERROR FOUND IN FUNCTION CALL " + function + "(): \'" + currentToken.intValue + "\' is supposed to be \'" + compare + "\'");
			else
				System.out.println("ERROR FOUND IN FUNCTION CALL " + function + "(): \'" + currentToken.kind + "\' is supposed to be \'" + compare + "\'");
		}
	}
	
	// body() checks for the "bool" or "int" keyword and, if found, calls declarations(), otherwise it
	// calls statements()
	static void body()
	{
		//Node node = new Node("body", currentParent);
		//currentParent = node;
		
		if(expected("bool") || expected("int"))
			declarations();
			
		
		if(!unexpected)
			statements();
	}
	
	// calls declaration(), and continues to do so as long as another instance of "bool" or "int" is
	// found
	static void declarations()
	{
		//Node node = new Node("declarations", currentParent);
		//currentParent = node;
		
		declaration();
		while(!unexpected && (expected("bool") || expected("int")))
		{
			declaration();
		}
	}
	
	// calls statement(), if a ";" is found after statement() returns, then it is noted, and then statement()
	// is called again
	static void statements()
	{
		//Node node = new Node("statements", currentParent);
		//currentParent = node;
		
		statement();
		while(!unexpected && expected(";"))
		{
			next();
			if(!unexpected)
				statement();
		}
	}
	
	// declaration() first calls next() to move past the "bool" or "int" keyword that allowed declaration()
	// to be called, then match() an identifier and match() ";"
	static void declaration()
	{
		//Node node = new Node("declaration", currentParent);
		//currentParent = node;
		
		next();
		if(!unexpected)
		{
			match("ID", "declaration");
			if(!unexpected)
				match(";", "declaration");
		}
	}
	
	// statement() has 4 test cases, if currentToken is an identifier, call assignmentStatement(), if currentToken is
	// "if", call conditionalStatement(), if currentToken is "while", call iterativestatement(), if currentToken is 
	// "print", call printStatement(), if none of these ecpectations are met the token is unexpected, which outputs an error
	static void statement()
	{
		//Node node = new Node("statement", currentParent);
		//currentParent = node;
		
		if(expected("ID"))
			assignmentStatement();
		else if(expected("if"))
			conditionalStatement();
		else if(expected("while"))
			iterativeStatement();
		else if(expected("print"))
			printStatement();
		else
		{
			unexpected = true;
			System.out.println("ERROR: unexpected token in statement(): \'" + currentToken.kind + "\'");
			System.out.println("ERROR: expected tokens are (IDENTIFIER, if, while, print)");
		}
	}
	
	// note: the first match() call does not check for unexpected because it was checked before statement was called
	// assignmentStatement() checks for an identifier, the ":=" symbol, and then calls expression()
	static void assignmentStatement()
	{
		//Node node = new Node("assignmentStatement", currentParent);
		//currentParent = node;
		
		match("ID", "assignmentStatement");
		if(!unexpected)
		{
			match(":=", "assignmentStatement");
			if(!unexpected)
				expression();
		}
	}
	
	// note: the first match() call does not check for unexpected because it was checked before statement was called
	// conditionalStatement() checks for keyword "if", calls expression(), checks for "then", calls body(), if "else"
	// satisfies expected(), then "else" is matched and body() is called, otherwise "fi" is matched
	static void conditionalStatement()
	{
		//Node node = new Node("conditionalStatement", currentParent);
		//currentParent = node;
		
		match("if", "conditionalStatement");
		if(!unexpected)
		{
			expression();
			match("then", "conditionalStatement");
			if(!unexpected)
			{
				body();
				if(expected("else"))
				{
					match("else", "conditionalStatement");
					if(!unexpected)
						body();
				}
				
				if(!unexpected)
					match("fi", "conditionalStatement");
			}
		}	
	}
	
	// note: the first match() call does not check for unexpected because it was checked before statement was called
	// checks for keyword "while", calls expression(), checks for "do", calls body(), checks for "od"
	static void iterativeStatement()
	{
		//Node node = new Node("iterativeStatement", currentParent);
		//currentParent = node;
		
		match("while", "iterativeStatement");
		if(!unexpected)
		{
			expression();
			if(!unexpected)
			{
				match("do", "iterativeStatement");
				if(!unexpected)
				{
					body();
					if(!unexpected)
						match("od", "iterativeStatement");
				}
			}
		}
	}
	
	// note: the first match() call does not check for unexpected because it was checked before statement was called
	// checks for "print", calls expression()
	static void printStatement()
	{
		//Node node = new Node("printStatement", currentParent);
		//currentParent = node;
		
		match("print", "printStatement");
		if(!unexpected)
			expression();
	}
	
	// note: unexpected is checked before expression() is called so does not need to be called at first
	// expression() calls simpleExpression(), if relationalOperator() returns true, then some operator is matched,
	// then simpleExpression() is called again
	static void expression()
	{
		//Node node = new Node("expression", currentParent);
		//currentParent = node;
		
		simpleExpression();
		if(!unexpected && relationalOperator())
		{
			match(getOperator(), "expression");
			if(!unexpected)
				simpleExpression();
		}
	}
	
	// note: unexpected is checked before expression() is called so does not need to be called at first
	// calls term(), checks for an additive operator which is matched if found, then calls term and continues
	// this cycle as long as more additive operators are found
	static void simpleExpression()
	{
		//Node node = new Node("simpleExpression", currentParent);
		//currentParent = node;
		
		term();
		while(!unexpected && additiveOperator())
		{
			match(getOperator(), "simpleExpression");
			if(!unexpected)
				term();
		}
			
	}
	
	// returns true if the kind of the currentToken object is a relational operator
	static boolean relationalOperator()
	{
		//Node node = new Node("relationalOperator", currentParent);
		//currentParent = node;
		
		if(currentToken.kind.equals("<") || currentToken.kind.equals("=<") || currentToken.kind.equals("=") || 
				currentToken.kind.equals("!=") || currentToken.kind.equals(">=") || currentToken.kind.equals(">"))
			return true;
		
		return false;
	}
	
	// used in expression(), simpleExpression(), and term() to match() an expected operator
	// saves me the trouble of writing a long list of conditionals
	static String getOperator()
	{
		return currentToken.kind;
	}
	
	// note: unexpected is checked before expression() is called so does not need to be called at first
	// calls factor() once, and then continues to do so as long as a multiplicative operator is found
	static void term()
	{
		//Node node = new Node("term", currentParent);
		//currentParent = node;
		
		factor();
		while(!unexpected && multiplicativeOperator())
		{
			match(getOperator(), "term");
			if(!unexpected)
				factor();
		}
	}
	
	// returns true if the kind of the currentToken object is one of the additive operators
	static boolean additiveOperator()
	{
		//Node node = new Node("additiveOperator", currentParent);
		//currentParent = node;
		
		if(currentToken.kind.equals("+") || currentToken.kind.equals("-") || currentToken.kind.equals("or"))
			return true;
		
		return false;
	}
	
	// first tests for a unary operator, then calls literal(), matches an identifier, or calls an expression
	// depending on what expected returns, also sets unexpected to true if none of the expected tests return true
	static void factor()
	{
		//Node node = new Node("factor", currentParent);
		//currentParent = node;
		
		if(expected("-") || expected("not"))
			unaryOperator();
		
		if(!unexpected)
		{
			if(expected("false") || expected("true") || expected("NUM"))
				literal();
			else if(expected("ID"))
				match("ID", "factor");
			else if(expected("("))
			{
				match("(", "factor");
				if(!unexpected)
				{
					expression();
					if(!unexpected)
						match(")", "factor");
				}
			}
			else
			{
				unexpected = true;
				System.out.println("ERROR: unexpected token in factor(): \'" + currentToken.kind + "\'");
				System.out.println("ERROR: expected tokens are (false, true, integer literal, IDENTIFIER, \'(\' )");
			}
		}
	}
	
	// if this is being called, it has already been verified that one of the unary operators is in use,
	// this just checks which one to match against
	static void unaryOperator()
	{
		if(expected("-"))
			match("-", "unaryOperator");
		else
			match("not", "unaryOperator");
	}
	
	// returns true if a multiplicative operator is found
	static boolean multiplicativeOperator()
	{
		//Node node = new Node("multiplicativeOperator", currentParent);
		//currentParent = node;
		
		if(expected("*") || expected("/") || expected("and"))
			return true;
		
		return false;
	}
	
	// factor() verifies that expected() is either a number, 'true', or 'false', this just tests which one to match
	static void literal()
	{
		//Node node = new Node("literal", currentParent);
		//currentParent = node;
		
		if(expected("NUM"))
			match("NUM", "literal");
		else if (expected("false"))
			match("false", "literal");
		else
			match("true", "literal");
	}
	
	// this class encompasses all functionality for reading each character from the file until either a valid token is
	// found or until it can be deemed that the current character or string of character is not a valid token
	private static class Tokenizer
	{
		int currentChar;
		boolean isWord = false;
		boolean isNumber = false;
		boolean isSymbol = false;
		boolean isComment = false;
		boolean isWhite = false;
		String keywordTable[] = {"program", "end", "bool", "int", "if", "then", "else", "fi",
				"while", "do", "od", "print", "or", "and", "not", "false", "true"};
		int row = 1;
		int column = 1;
		String currentToken;
		FileReader file = null;
		TokenObject recentToken;

		public Tokenizer(String file)
		{
			//open file
			try
			{
				this.file = new FileReader(file); //change the file reader object to the name of the file
			}
			catch(FileNotFoundException e)
			{
				System.out.println(e);
			}
			System.out.println("file was found");
			
			//initializes currentChar to first character in file
			try
			{
				currentChar = this.file.read();
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
		}
		
		public int getCurrentChar()
		{
			return this.currentChar;
		}
		
		public TokenObject getToken()
		{
			return recentToken;
		}
		
		public String getKind()
		{
			return recentToken.kind;
		}
		
		public String getPosition()
		{
			return position();
		}

		
		public void next()
		{
			
			TokenObject token = new TokenObject(0, 0, null, null);
			String stringTemp = null;
			
			//read a token and if that token was a comment or white space, read another token
			do
			{
				this.readToken();
			}
			while(isComment || isWhite);

			//if the token is a word
			if(isWord)
			{
				//make keyword token
				if(isKeyWord())
					token = new TokenObject(row, column - currentToken.length(), currentToken, stringTemp);
				else //make identifier token
					token = new TokenObject(row, column - currentToken.length(), "ID", currentToken);
			}
			else if(isNumber) //if the token is a number (collection of only digits)
				token = new TokenObject(row, column - currentToken.length(), "NUM", Integer.parseInt(currentToken));
			else if((isSymbol && !isComment) || currentToken == "end-of-text") //if the token is a symbol or the end of text
			{
				if(currentToken == "end-of-text")
					token = new TokenObject(row, column, "end-of-text", null);
				else //if current token is a symbol
					token = new TokenObject(row, column - currentToken.length(), currentToken, null);
			}
			else if(isComment)
				comment();
			else if(isWhite) //for testing to find whitespace
				token = new TokenObject(0, 0, "WHITE", null);
			else
				token = new TokenObject(row, column - currentToken.length(), "UNEXPECTED", currentToken);
			
			recentToken = token;
		}
		
		
		//called by next() to read a collection of 1 or more characters making up a token
		private void readToken()
		{
			isComment = false;
			isWhite = false;
			isWord = false;
			isNumber = false;
			isSymbol = false;
			currentToken = "";
			
			//if first character of a token is a letter, the token must be an identifier or a keyword
			if(charIsLetter())
			{
				//as long as the character being read is a letter, digit, or the underscore symbol, it is valid to be added to current token
				while(charIsLetter() || charIsNum() || currentChar == 95) //95 = '_'
				{
					currentToken += (char)currentChar;
					readChar();
				}
				isWord = true;
			} //if the first character read is a digit, the token must be a number
			else if(charIsNum())
			{
				while(charIsNum()) //as long as digits are being read, continue to create a number token
				{
					currentToken += (char)currentChar;
					readChar();
				}
				isNumber = true;
			}
			else if(charIsSymbol()) //if the first character read is in the list of valid symbols, the token will be a symbol
			{
				if(currentChar == 58) //58 = ':'
				{
					currentToken += (char)currentChar;
					readChar();
					if(currentChar == 61) //61 = '='; this tests for multicharacter assignment operator
					{
						currentToken += (char)currentChar;
						readChar();
					}
				}
				else if(currentChar == 61) //61 = '='
				{
					currentToken += (char)currentChar;
					readChar();
					if(currentChar == 60) //60 = '<'; this tests for multicharacter equal to or less than operator
					{
						currentToken += (char)currentChar;
						readChar();
					}
				}
				else if(currentChar == 33) //33 = '!'
				{
					currentToken += (char)currentChar;
					readChar();
					if(currentChar == 61) //61 = '='; this tests for multicharacter not equal to operator
					{
						currentToken += (char)currentChar;
						readChar();
					}
					else
					{
						System.out.println(currentToken + " is unexpected");
					}
				}
				else if(currentChar == 62) //62 = '>'
				{
					currentToken += (char)currentChar;
					readChar();
					if(currentChar == 61) //61 = '='; this tests for multicharacter greater than or equal to operator
					{
						currentToken += (char)currentChar;
						readChar();
					}
				}
				else if(currentChar == 47) //47 = '/'
				{
					currentToken += (char)currentChar;
					readChar();
					if(currentChar == 47) //this tests for multicharacter comment operator
					{
						currentToken += (char)currentChar;
						isComment = true;
						comment();
					}
				}
				else //this checks for any symbol that is not also the beginning of a multicharacter operator
				{
					currentToken += (char)currentChar;
					readChar();
				}
				
				isSymbol = true;
			}
			else if(currentChar == 10 || currentChar ==32) //checking for whitespace; 10 is newline and 32 is traditional space
			{
				if(currentChar == 10) //newline
				{
					row++;
					column = 1;
					readChar();
				}
				else //space
					readChar();
				
				isWhite = true;
			}
			else if(currentChar == -1) //java returns -1 when end of text is read as a character
				currentToken = "end-of-text";
			else //unexpected token; program flow should only get here if a character is read that is not recognized by the grammar
			{
				System.out.println((char)currentChar + " is unexpected");
				currentToken += (char)currentChar;
				readChar();
			}
		}
		
		//called by readToken() to evaluate individual characters for fit-ness in the grammar
		private void readChar()
		{	
			if(currentChar > -1) //if current character is not end of text
			{
				try
				{
					//if character is new line symbol, read another character without incrementing index in line count
					if(currentChar == 10)
						currentChar = file.read();
					else //otherwise, read next character and increment index in line count
					{
						currentChar = file.read();
						column++;	
					}
				}
				catch(IOException e)
				{
					System.out.println(e);
				}
			}
		}
		
		//check if identifier is a keyword
		private boolean isKeyWord()
		{
			for(String key : keywordTable)
			{
				//if currentToken is in the keyword table listed at the top
				if(currentToken.equals(key))
					return true;
			}
			
			return false;
		}
		
		private boolean charIsLetter()
		{
			//65 - 90 are capital letters, 97 - 122 are lowercase letters
			if((currentChar > 64 && currentChar < 91) || (currentChar > 96 && currentChar < 123))
				return true;
			
			return false;
		}
		
		private boolean charIsNum()
		{
			//48 - 57 are digits 0 - 9
			if(currentChar > 47 && currentChar < 58)
				return true;
			
			return false;
		}
		
		private boolean charIsSymbol()
		{
			if(currentChar == 58/*:*/ || currentChar == 59/*;*/ || currentChar == 61/*=*/ || currentChar == 60/*<*/ ||
				currentChar == 33/*!*/ || currentChar == 62/*>*/ || currentChar == 43/*+*/ || currentChar == 45/*-*/ ||
				currentChar == 42/***/ || currentChar == 47/*/*/ || currentChar == 40/*(*/ || currentChar == 41/*)*/ ||
				currentChar == 95/*_*/)
				return true;
			
			return false;
		}
		
		//called by readToken() if the '//' symbol is found
		private void comment()
		{
			//continue to read characters until either a newline or end of text is found
			while(currentChar != 10 && currentChar != -1)
			{
				readChar();
			}
		}
		
		//called directly after next() to print the position of the most recent token found
		protected String position()
		{
			String output = "line: " + recentToken.row + ", index: " + recentToken.column;
			return output;
		}
		
		//called directly after next() to print the kind of the most recent token found
		protected String kind()
		{
			String output = "kind: " + recentToken.kind;
			return output;
		}
		
		//called directly after next() to print the value of the most recent token found
		protected String value()
		{
			String output;
			if(recentToken.isNum)
				output = "value: " + recentToken.intValue;
			else
				output = "value: " + recentToken.stringValue;
			return output;
		}
	}
	
	// this class encompasses all the attributes of a potential token object
	private static class TokenObject
	{
		int row;
		int column;
		String kind;
		String stringValue;
		int intValue;
		boolean isNum = false;
		
		TokenObject(int row, int column, String kind, String stringValue)
		{
			this.row = row;
			this.column = column;
			this.kind = kind;
			this.stringValue = stringValue;
		}
		
		TokenObject(int row, int column, String kind, int intValue)
		{
			this.row = row;
			this.column = column;
			this.kind = kind;
			this.intValue = intValue;
			this.isNum = true;
		}
		
		int getRow()
		{
			return row;
		}
		
		int getColumn()
		{
			return column;
		}
	}
}
