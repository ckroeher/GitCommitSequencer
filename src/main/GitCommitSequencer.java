package main;

import java.io.File;

import utilities.Logger;
import utilities.ProcessUtilities;
import utilities.Logger.MessageType;
import utilities.ProcessUtilities.ExecutionResult;

public class GitCommitSequencer {
	
	private static final String ID = "GitCommitSequencer";
	
	private static final String[] GIT_HEAD_COMMIT_COMMAND = {"git", "rev-parse", "HEAD"};
	
	private Logger logger = Logger.getInstance();
	
	private File repositoryDirectory;
	
	private GitCommitSequencer(String[] args) {
		String repositoryDirectoryString = "C:\\Users\\kroeher\\Data\\Repositories\\DevOpt@TUC";
		repositoryDirectory = new File(repositoryDirectoryString);
	}

	private void run() {
		String startCommit = getStartCommit();
		CommitSequence commitSequence = new CommitSequence(startCommit, repositoryDirectory);
		logger.log(ID, "Sequencing finished", "Number of commits in sequence: " + commitSequence.size(), MessageType.INFO);
	}
	
	private String getStartCommit() {
		String startCommit = null;
		ExecutionResult executionResult = ProcessUtilities.getInstance().executeCommand(GIT_HEAD_COMMIT_COMMAND, repositoryDirectory);
		if (executionResult.executionSuccessful()) {
			startCommit = executionResult.getStandardOutputData().trim();
		} else {
			logger.log(ID, "Retrieving HEAD commit failed", executionResult.getErrorOutputData(), MessageType.ERROR);
		}
		return startCommit;
	}
	
	public static void main(String[] args) {
		GitCommitSequencer sequencer = new GitCommitSequencer(args);
		sequencer.run();
	}
}
