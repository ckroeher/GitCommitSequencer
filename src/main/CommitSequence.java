package main;

import java.io.File;
import java.util.ArrayList;

import utilities.Logger;
import utilities.ProcessUtilities;
import utilities.Logger.MessageType;
import utilities.ProcessUtilities.ExecutionResult;

public class CommitSequence extends ArrayList<String> {
	
	private static final String ID = "CommitSequence";

	/**
	 * 
	 */
	private static final long serialVersionUID = -1350039576783309464L;
	
	private static final String[] GIT_PARENT_COMMIT_COMMAND = {"git", "log", "--pretty=%P", "-1"};
	
	private Logger logger = Logger.getInstance();
	
	private File repositoryDirectory;

	public CommitSequence(String startCommit, File repositoryDirectory) {
		logger.log(ID, "New commit sequence",
				"Start commit: \"" + startCommit + "\"\nRepository: \"" + repositoryDirectory.getAbsolutePath() + "\"", MessageType.INFO);
		this.repositoryDirectory = repositoryDirectory;
		this.add(startCommit);
		addParent(startCommit);
	}
	
	private void addParent(String currentCommit) {
		String[] currentParentCommitCommand = ProcessUtilities.getInstance().extendCommand(GIT_PARENT_COMMIT_COMMAND, currentCommit);
		ExecutionResult executionResult = ProcessUtilities.getInstance().executeCommand(currentParentCommitCommand, repositoryDirectory);
		if (executionResult.executionSuccessful()) {
			String fullParentCommitString = executionResult.getStandardOutputData().trim();
			if (!fullParentCommitString.isBlank()) {				
				if (fullParentCommitString.contains(" ")) {
					// Multiple parents
					String[] parentCommits = fullParentCommitString.split(" ");
					this.add(parentCommits[0]);
					System.out.println(parentCommits[0]);
					addParent(parentCommits[0]);
					// TODO use parentCommits[1]ff as new start commits for new sequences
					// TODO how to relate these new sequences with parentCommits[0] as anchor?
				} else {
					// Single parent
					this.add(fullParentCommitString);
					System.out.println(fullParentCommitString);
					addParent(fullParentCommitString);
				}
			}
		} else {
			logger.log(ID, "Retrieving parent commit for \"" + currentCommit + "\" failed", executionResult.getErrorOutputData(), MessageType.ERROR);
		}
	}
	
}
