/*
 * Copyright 2020 University of Hildesheim, Software Systems Engineering
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.ssehub.gcs.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.gcs.utilities.FileUtilities;
import net.ssehub.gcs.utilities.Logger;
import net.ssehub.gcs.utilities.Logger.MessageType;
import net.ssehub.gcs.utilities.ProcessUtilities;
import net.ssehub.gcs.utilities.ProcessUtilities.ExecutionResult;

/**
 * The main class of this project starting the core processes to create commit sequences from a Git repository.
 * 
 * @author Christian Kroeher
 *
 */
public class GitCommitSequencer implements ISequenceStorage {

    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "GitCommitSequencer";
    
    /**
     * The command for printing the current HEAD commit (SHA) to console.<br>
     * <br>
     * Command: <code>git rev-parse HEAD</code>
     */
    private static final String[] GIT_HEAD_COMMIT_COMMAND = {"git", "rev-parse", "HEAD"};
    
    /**
     * The {@link String} defining the constant summary file name.
     * <br><br>
     * Value: <code>GitCommitSequencer_Summary.txt</code>
     */
    private static final String SUMMARY_FILE_NAME = "GitCommitSequencer_Summary.csv";
    
    /**
     * The {@link Logger} for pretty-printing messages to the console.
     */
    private static Logger logger = Logger.getInstance();
    
    /**
     * The {@link FileUtilities} for opening and closing the {@link #outputFileChannel} for writing the commits to an
     * output file.
     */
    private FileUtilities fileUtilities;
    
    /**
     * The {@link File} denoting the root directory of the Git repository from which commit sequences shall be created. 
     */
    private File repositoryDirectory;
    
    /**
     * The {@link String} defining the commit (SHA) to start the creation of commit sequences from.
     */
    private String startCommit;
    
    /**
     * The {@link File} denoting the directory to save the results of the {@link GitCommitSequencer} to. 
     */
    private File outputDirectory;
    
    /**
     * The {@link File} denoting the file containing the summary of the results of the {@link GitCommitSequencer}. 
     */
    private File summaryFile;
    
    /**
     * The {@link List} of {@link CommitSequence}s that have to be created by calling {@link CommitSequence#run()}. This
     * list is filled during the creation of commit sequences that detect sub-sequences. Those sub-sequences are
     * initialized but their actual creation is postponed until the current sequence is created completely. Hence, the
     * postponed sequences are added to this list via {@link #add(CommitSequence)}.
     */
    private List<CommitSequence> worklist;
    
    /**
     * Constructs a new {@link GitCommitSequencer} instance.
     * 
     * @param args the array of {@link String}s passed as arguments to this tool at start-up and received by 
     *        {@link #main(String[])}; should never be <code>null</code> nor <i>empty</i>
     * @throws ArgumentErrorException if user-defined arguments passed as input at start-up are not as expected
     * @see #parseArgs(String[])
     */
    public GitCommitSequencer(String[] args) throws ArgumentErrorException {
        parseArgs(args);
        summaryFile = new File(outputDirectory, SUMMARY_FILE_NAME);
        fileUtilities = new FileUtilities();
    }
    
    /**
     * Parses the given arguments received by {@link #main(String[])} and passed through 
     * {@link #GitCommitSequencer(String[])}. Further, it sets the following attributes based on the given arguments:
     * <ul>
     * <li>The first argument as {@link #repositoryDirectory} (<b>mandatory</b>)</li>
     * <li>The second argument as {@link #outputDirectory} (<b>mandatory</b>)</li>
     * <li>The third argument as {@link #startCommit} (<b>optional</b>; if not defined, current HEAD commit (SHA) is
     *     used by default</li>
     * </ul>
     * 
     * @param args the array of {@link String}s passed as arguments to this tool at start-up; should never be 
     *        <code>null</code>  nor <i>empty</i>
     * @throws ArgumentErrorException if user-defined arguments passed as input at start-up are not as expected
     */
    private void parseArgs(String[] args) throws ArgumentErrorException {
        if (args != null) {
            if (args.length == 2 || args.length == 3) {
                // The first argument as repository directory (mandatory)
                try {
                    repositoryDirectory = new File(args[0]);
                    if (!repositoryDirectory.exists()) {
                        throw new ArgumentErrorException("The repository directory \"" 
                                + repositoryDirectory.getAbsolutePath() + "\" does not exist");
                    }
                    if (!repositoryDirectory.isDirectory()) {
                        throw new ArgumentErrorException("The repository directory \"" 
                                + repositoryDirectory.getAbsolutePath() + "\" is not a directory");
                    }
                    // TODO check for git installation and whether the directory is a git repository?
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new ArgumentErrorException("The repository directory is not defined", e);
                }
                // The second argument as output directory (mandatory)
                try {
                    outputDirectory = new File(args[1]);
                    if (!outputDirectory.exists()) {
                        throw new ArgumentErrorException("The output directory \"" + outputDirectory.getAbsolutePath() 
                                + "\" does not exist");
                    }
                    if (!outputDirectory.isDirectory()) {
                        throw new ArgumentErrorException("The output directory \"" + outputDirectory.getAbsolutePath() 
                                + "\" is not a directory");
                    }
                    if (outputDirectory.list().length != 0) {
                        throw new ArgumentErrorException("The output directory \"" + outputDirectory.getAbsolutePath() 
                                + "\" is not empty");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new ArgumentErrorException("The output directory is not defined", e);
                }
                // The third argument as start commit (optional)
                if (args.length == 3) {
                    startCommit = args[2];
                } else {
                    startCommit = getStartCommit();
                }
            } else {
                throw new ArgumentErrorException("Wrong number of arguments");
            }
        } else {
            throw new ArgumentErrorException("No input arguments defined");
        }
    }
    
    /**
     * Determines the commit, which represents the start of all commit sequences.
     * 
     * @return the {@link String} representing the start commit (SHA) of all sequences; maybe <code>null</code>, if no
     *         start commit can be determined
     */
    private String getStartCommit() {
        String startCommit = null;
        ExecutionResult executionResult = ProcessUtilities.getInstance().executeCommand(GIT_HEAD_COMMIT_COMMAND,
                repositoryDirectory);
        if (executionResult.executionSuccessful()) {
            startCommit = executionResult.getStandardOutputData().trim();
        } else {
            logger.log(ID, "Retrieving HEAD commit failed", executionResult.getErrorOutputData(), MessageType.ERROR);
        }
        return startCommit;
    }

    /**
     * Starts the creation of commit sequences by this {@link GitCommitSequencer} instance.
     * 
     * @throws CommitSequenceCreationException if creating a commit sequence fails 
     */
    public void run() throws CommitSequenceCreationException {
        logger.log(ID, "Start", 
                "Repository directory: \"" + repositoryDirectory.getAbsolutePath() + "\"" + System.lineSeparator()
                + "Start commit: \"" + startCommit + "\"" + System.lineSeparator()
                + "Output directory: \"" + outputDirectory.getAbsolutePath() + "\"", MessageType.INFO);
        
        // Determine and save the current time in milliseconds for calculating the execution duration below 
        long startTimeMillis = System.currentTimeMillis();
        
        // Open the file channel for writing the summary file
        if (fileUtilities.openFileChannel(summaryFile)) {            
            // Create commit sequences
            try {                
                worklist = new ArrayList<CommitSequence>();
                CommitSequence commitSequence = new CommitSequence(this, repositoryDirectory, startCommit,
                        outputDirectory);
                logger.log(ID, "Creating initial commit sequence", null, MessageType.INFO);
                commitSequence.run();
                toSummary(commitSequence.getOutputFileName(), commitSequence.getNumberOfCommits());
                synchronized (this) {
                    if (!worklist.isEmpty()) {
                        logger.log(ID, "Creating commit sub-sequences", "Current number of sub-sequences to go: " 
                                + worklist.size(), MessageType.INFO);
                        while (!worklist.isEmpty()) {
                            if ((worklist.size() % 100) == 0) {
                                logger.log(ID, "Creating commit sub-sequences",
                                        "Current number of sub-sequences to go: " + worklist.size(), MessageType.INFO);
                            }
                            commitSequence = worklist.remove(0);
                            commitSequence.run();
                            toSummary(commitSequence.getOutputFileName(), commitSequence.getNumberOfCommits());
                        }
                    }
                }
            } catch (CommitSequenceCreationException e) {
                /*
                 * This is a hack to ensure that the summary file channel will be closed in any situation although it is
                 * intended that the exception will be propagated.
                 */
                throw e;
            } finally {                
                // Close the file channel for writing the summary file
                if (!fileUtilities.closeFileChannel()) {
                    logger.log(ID, "Closing the file channel for summar file \"" + summaryFile.getAbsolutePath() 
                            + "\" failed", null, MessageType.ERROR);
                }
            }
        } else {
            logger.log(ID, "Terminating execution", "Opening the file channel for writing summary file \""
                    + summaryFile.getAbsolutePath() + "\" failed", MessageType.ERROR);
        }
        
        // Determine end date and time and display them along with the duration of the overall process execution
        long durationMillis = System.currentTimeMillis() - startTimeMillis;
        int durationSeconds = (int) ((durationMillis / 1000) % 60);
        int durationMinutes = (int) ((durationMillis / 1000) / 60);
        
        logger.log(ID, "Finished", "Commit sequences created: " + CommitSequence.getNumberOfInstances() 
                + System.lineSeparator() + "Duration: " + durationMinutes + " min. and " + durationSeconds + " sec.",
                MessageType.INFO);
    }
    
    /**
     * Writes the given commit sequence file name and the number of commits separated by a comma and extended by a line
     * separator via the {@link #fileUtilities} to the {@link #summaryFile}.
     * 
     * @param commitSequenceFileName the name of the commit sequence file to add to the summary
     * @param numberOfCommits the number of commits in the commit sequence of the given commit sequence file name
     */
    public void toSummary(String commitSequenceFileName, int numberOfCommits) {
        String simpleCommitSequenceFileName = commitSequenceFileName.split("\\.")[0]; // Remove file extension 
        String summaryFileContent = simpleCommitSequenceFileName + "," + numberOfCommits + System.lineSeparator();
        fileUtilities.write(summaryFileContent);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(CommitSequence commitSequence) {
        synchronized (this) {
            worklist.add(commitSequence);
        }
    }
    
    /**
     * Starts this tool.
     * 
     * @param args the user-defined arguments passed as input at start-up; never <code>null</code>, but may be
     *        <i>empty</i>
     */
    public static void main(String[] args) {
        try {            
            GitCommitSequencer sequencer = new GitCommitSequencer(args);
            sequencer.run();
        } catch (ArgumentErrorException e) {
            logger.logException(ID, "Parsing the given arguments failed", e);
        } catch (CommitSequenceCreationException e) {
            logger.logException(ID, "Creating a commit sequence failed", e);
        }
    }
}
