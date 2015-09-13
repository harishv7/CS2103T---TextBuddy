import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * TextBuddy is a Command Line Interface (CLI) program which assists the user to
 * write and save text onto a file saved to the disk. TextBuddy also helps to
 * delete specific lines of text from the file and clears the file's contents
 * when instructed to. The instructions are delivered in the form of commands,
 * namely, "display", "add", "delete", "sort", "search", "clear", "exit".
 * The command format is given by the interaction below:

 	Welcome to TextBuddy. mytextfile.txt is ready for use
 	command: add This is TextBuddy!
 	added to mytextfile.txt: "This is TextBuddy!"
 	command: display
 	1. This is TextBuddy!
 	command: add TextBuddy is Great!
 	added to mytextfile.txt: "TextBuddy is Great!"
 	command: display
 	1. This is TextBuddy!
 	2. TextBuddy is Great!
 	command: delete 1
 	deleted from mytextfile.txt: "This is TextBuddy!"
 	command: display
 	1. TextBuddy is Great!
	command: clear
	all content deleted from mytextfile.txt
	command: display
	mytextfile.txt is empty
	command: add C: A sample statement
	added to mytextfile.txt: "C: A sample statement"
	command: add B: A sample statement
	added to mytextfile.txt: "B: A sample statement"
	command: add A: A sample statement
	added to mytextfile.txt: "A: A sample statement"
	command: sort
	mytextfile.txt has been sorted alphabetically
	command: search A
	The following lines contain cool
	1. A: A sample statement
	command: exit

 * Notes to user:
 * 1. This program assumes that the user input is valid, especially
 * 	  for file handling related functions. When an invalid file or error during
 * 	  file writing or reading occurs, the program will throw an IOException.
 * 2. This program assumes that the line number corresponding to each line of
 * 	  text need not be written to the text file itself. The program will reflect
 * 	  the line number during the operation of TextBuddy only.

 * @author Venkatesan Harish, A0121828H
 * 
 */

public class TextBuddy {

	// List of Output Messages
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %s is ready for use";
	private static final String MESSAGE_ERROR_INVALID_FILE_NAME = "File name provided is invalid. TextBuddy will exit now.";
	private static final String MESSAGE_ADDED = "added to %1$s: \"%2$s\""; 
	private static final String MESSAGE_DELETED = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_CLEARED = "all content deleted from %s";
	private static final String MESSAGE_ERROR_INVALID_COMMAND = "You have provided an invalid command. Please try again.";
	private static final String MESSAGE_ERROR_INVALID_LINE_TO_DELETE = "You have provided an invalid line number.";
	private static final String MESSAGE_PROMPT_USER = "command: ";
	private static final String MESSAGE_FILE_EMPTY = "%s is empty";
	private static final String MESSAGE_SORT_SUCCESS = "%s has been sorted alphabetically";
	private static final String MESSAGE_SEARCH_KEYWORD_SUCCESS = "The following lines contain %s";
	private static final String MESSAGE_SEARCH_KEYWORD_FAILED = "There were no lines that contain %s";

	// List of Commands
	enum CommandType {
		ADD, DELETE, DISPLAY, CLEAR, SORT, SEARCH, INVALID, EXIT
	};

	// List of constants used to extract the correct parts of a string
	private static final int INDEX_OF_FILE_NAME = 0;
	private static final int INDEX_OF_USER_COMMAND = 0;
	private static final int INDEX_OF_LINE_NUMBER = 7;
	private static final int INDEX_OF_LINE_TO_ADD = 4;
	private static final int INDEX_OF_SEARCH_STRING = 7;
	
	// List of constants used to reflect error or successful exit
	private static final int SYSTEM_EXIT_SUCCESS = 0;
	private static final int SYSTEM_EXIT_WITH_ERROR = -1;
	private static final int NUMBER_FORMAT_EXCEPTION_ERROR = -1;
	
	// List of static class variables
	private static File textFile;
	private static String fileName;
	private static Scanner scanner;
	private static boolean shouldExitProgram = false;
	
	// This array stores the lines of text in a file
	private static ArrayList<String> storeText;

	public static void main(String[] args) throws IOException {
		validateArguments(args);
		printWelcomeMessage();
		getFileReady();
		getInputUntilUserExits();
	}

	/*
	 * This operation checks if the given argument if a valid file name. 
	 * If invalid, that is, no file name is provided, TextBuddy will exit.
	 * 
	 * @param args This is the argument as provided by the user when starting
	 * 			   TextBuddy.
	 */
	private static void validateArguments(String[] args) {
		if (args.length == 0) {
			displayMessage(MESSAGE_ERROR_INVALID_FILE_NAME);
			System.exit(SYSTEM_EXIT_WITH_ERROR);
		} else {
			fileName = args[INDEX_OF_FILE_NAME];
		}
	}

	private static void printWelcomeMessage() {
		System.out.println(String.format(MESSAGE_WELCOME, fileName));
	}

	/*
	 * This operation gets the system ready by initializing the text file,
	 * scanner object and the arraylist to store the text.
	 * If the text file does not already exist, it creates a new file.
	 */
	private static void getFileReady() throws IOException {
		textFile = new File(fileName);
		if (!textFile.exists()) {
			textFile.createNewFile();
		}
		scanner = new Scanner(System.in);
		storeText = new ArrayList<String>();
		loadTextFromFile();
	}

	/* 
	 * This operation loads the existing text from the file into storeText arraylist.
	 */
	private static void loadTextFromFile() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(textFile));
		String line;
		while ((line = reader.readLine()) != null) {
			storeText.add(line);
		}
		reader.close();
	}

	private static void displayMessage(String message) {
		System.out.println(message);
	}

	private static void getInputUntilUserExits() throws IOException {
		String userInput;
		while (!shouldExitProgram){
			promptUserForInput();
			userInput = getUserInput();
			CommandType userCommand = getUserCommand(userInput);
			runCommand(userCommand, userInput);
		}
	}

	private static void promptUserForInput() {
		System.out.print(MESSAGE_PROMPT_USER);
	}

	private static String getUserInput() {
		String userInput = scanner.nextLine();
		return userInput.trim();
	}

	private static CommandType getUserCommand(String userInput) {
		String[] userInputStringArray = userInput.split(" ");
		String command = userInputStringArray[INDEX_OF_USER_COMMAND];
		switch (command){
		  case "add" :
			  return CommandType.ADD;
		  case "delete" :
			  return CommandType.DELETE;
		  case "clear" :
			  return CommandType.CLEAR;
		  case "display" :
			  return CommandType.DISPLAY;
		  case "sort" :
			  return CommandType.SORT;
		  case "search" :
			  return CommandType.SEARCH;
		  case "exit" :
			  return CommandType.EXIT;
		  default :
			  return CommandType.INVALID;
		}
	}

	/*
	 * This operation determines what the user wishes to execute and calls the relevant
	 * methods to manipulate the text file.
	 * 
	 * @param userCommand  is the first word of the user's input command
	 * @param userInput	   is the whole string representing the user's entire command
	 * @throws IOException when there is a problem in manipulating the file 
	 */
	private static void runCommand(CommandType userCommand, String userInput) throws IOException {
		switch (userCommand) {
		  case ADD :
			  addToFile(userInput);
			  break;
		  case DELETE :
			  deleteFromFile(userInput);
			  break;
		  case CLEAR :
			  clearFile();
			  break;
		  case DISPLAY :
			  displayFileContents();
			  break;
		  case SORT :
			  sortFileContents();
			  break;
		  case SEARCH :
			  searchFileContents(userInput);
			  break;
		  case EXIT :
			  exitTextBuddy();
			  break;
		  case INVALID :
			  errorInCommand();
			  break;
		  default :
			  return;
		}
	}
	
	private static void sortFileContents() {
		java.util.Collections.sort(storeText);
		displaySuccessfulSortMessage();
	}
	
	private static void displaySuccessfulSortMessage() {
		System.out.println(String.format(MESSAGE_SORT_SUCCESS, fileName));
	}
	
	private static void searchFileContents(String userInput) {
		String searchString = userInput.substring(INDEX_OF_SEARCH_STRING);
		displaySearchMessage(searchString);
		boolean isSearchWordFound = false;
		
		for(int i = 0; i < storeText.size(); i++) {
			if(storeText.get(i).contains(searchString)) {
				System.out.println((i + 1) + ". " + storeText.get(i));
				isSearchWordFound = true;
			}
		}
		
		if(!isSearchWordFound) {
			displaySearchFailedMessage(searchString);
		}
	}

	private static void displaySearchFailedMessage(String searchString) {
		System.out.println(String.format(MESSAGE_SEARCH_KEYWORD_FAILED, searchString));
	}
	
	private static void displaySearchMessage(String searchString) {
		System.out.println(String.format(MESSAGE_SEARCH_KEYWORD_SUCCESS, searchString));
	}
	
	/*
	 * This operation deletes the user-specified line of text from the file.
	 * 
	 * @param userInput is the entire line of input as entered by the user
	 * @throws IOException when there is a problem in manipulating/saving the file.
	 */
	private static void deleteFromFile(String userInput) throws IOException {
		int lineToDelete = getLineNumberToDelete(userInput);
		if (isLineToDeleteValid(lineToDelete)) {
			String lineDeleted = storeText.remove(lineToDelete-1);
			saveFile();
			displayDeleteSuccessMessage(lineDeleted);
		} else {
			displayDeleteErrorMessage();
		}
	}
	
	/*
	 * This operation retrieves the line number as provided by the user.
	 * In the case of an exception where the input cannot be formatted, it
	 * returns NUMBER_FORMAT_EXCEPTION_ERROR which represents -1.
	 * 
	 * @param userInput is the entire line of input as entered by the user
	 * @throws NumberFormatException when the user's input cannot be parsed into an integer.
	 */
	private static int getLineNumberToDelete(String userInput) throws NumberFormatException {
		try {
			return Integer.parseInt(userInput.substring(INDEX_OF_LINE_NUMBER));
		} catch (NumberFormatException exception) {
			return NUMBER_FORMAT_EXCEPTION_ERROR;
		}
	}

	private static boolean isLineToDeleteValid(int lineToDelete) {
		if ((lineToDelete > 0) && (lineToDelete <= storeText.size())) {
			return true;
		}
		return false;
	}

	private static void displayDeleteErrorMessage() {
		if (storeText.isEmpty()) {
			displayFileEmptyMessage();
		} else {
			displayInvalidLineMessage();
		}
	}

	private static void displayInvalidLineMessage() {
		System.out.println(MESSAGE_ERROR_INVALID_LINE_TO_DELETE);
	}

	private static void displayDeleteSuccessMessage(String lineDeleted) {
		System.out.println(String.format(MESSAGE_DELETED, fileName, lineDeleted));
	}

	private static void clearFile() throws IOException {
		storeText.clear();
		saveFile();
		displayFileClearedMessage();
	}

	private static void displayFileClearedMessage() {
		System.out.println(String.format(MESSAGE_CLEARED, fileName));
	}

	private static void errorInCommand() {
		System.out.println(MESSAGE_ERROR_INVALID_COMMAND);
	}

	private static void exitTextBuddy() {
		shouldExitProgram = true;
		System.exit(SYSTEM_EXIT_SUCCESS);
	}

	private static void addToFile(String userInput) throws IOException {
		String lineToAdd = "";
		lineToAdd = userInput.substring(INDEX_OF_LINE_TO_ADD);
		storeText.add(lineToAdd);
		saveFile();
		displayAddSuccessMessage(lineToAdd);
	}

	private static void displayAddSuccessMessage(String lineAdded) {
		System.out.println(String.format(MESSAGE_ADDED, fileName, lineAdded));
	}

	private static void saveFile() throws IOException {
		FileWriter writer = new FileWriter(textFile);
		for (int i = 0; i < storeText.size(); i++) {
			writer.write(storeText.get(i));
			if (i != storeText.size() - 1) {
				writer.write("\n");
			}
			writer.flush();
		}		
		writer.close();
	}

	private static void displayFileContents() {
		if (storeText.size() == 0) {
			displayFileEmptyMessage();
		} else {
			for(int i = 0; i < storeText.size(); i++) {
				int lineNumber = i+1;
				System.out.println(lineNumber + ". " + storeText.get(i));
			}
		}
	}

	private static void displayFileEmptyMessage() {
		System.out.println(String.format(MESSAGE_FILE_EMPTY, fileName));
	}
}