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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

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
     * The {@link String} defining the constant prefix of each file, which is created for individual commit sequences.
     * <br><br>
     * Value: <code>CommitSequence_</code>
     */
    private static final String COMMIT_SEQUENCE_FILE_NAME_PREFIX = "CommitSequence_";
    
    /**
     * The {@link String} defining the constant postfix of each file, which is created for individual commit sequences.
     * <br><br>
     * Value: <code>.txt</code>
     */
    private static final String COMMIT_SEQUENCE_FILE_NAME_POSTFIX = ".txt";
    
    /**
     * The {@link Logger} for pretty-printing messages to the console.
     */
    private static Logger logger = Logger.getInstance();
    
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
     * The number of {@link CommitSequence}(s) created by this {@link GitCommitSequencer}.
     */
    private int numberOfCreatedCommitSequences;
    
    // TODO: also calculate shortest and longest sequence
    
    /**
     * Constructs a new {@link GitCommitSequencer} instance.
     * 
     * @param args the array of {@link String}s passed as arguments to this tool at start-up and received by 
     *        {@link #main(String[])}; should never be <code>null</code> nor <i>empty</i>
     * @throws ArgumentErrorException if user-defined arguments passed as input at start-up are not as expected
     * @see #parseArgs(String[])
     */
    private GitCommitSequencer(String[] args) throws ArgumentErrorException {
        parseArgs(args);
        numberOfCreatedCommitSequences = 0;
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
     * Starts the creation of commit sequences by this {@link GitCommitSequencer} instance.
     */
    private void run() {
        logger.log(ID, "Start", 
                "Repository directory: \"" + repositoryDirectory.getAbsolutePath() + "\"" + System.lineSeparator()
                + "Start commit: \"" + startCommit + "\"" + System.lineSeparator()
                + "Output directory: \"" + outputDirectory.getAbsolutePath() + "\"", MessageType.INFO);
        
        // Determine and save the current time in milliseconds for calculating the execution duration below 
        long startTimeMillis = System.currentTimeMillis();
        
        CommitSequence commitSequence = new CommitSequence(repositoryDirectory, this);
        commitSequence.run(startCommit);
        
        // Determine end date and time and display them along with the duration of the overall process execution
        long durationMillis = System.currentTimeMillis() - startTimeMillis;
        int durationSeconds = (int) ((durationMillis / 1000) % 60);
        int durationMinutes = (int) ((durationMillis / 1000) / 60);
        
        logger.log(ID, "Finished", "Commit sequences created: " + numberOfCreatedCommitSequences 
                + System.lineSeparator() + "Duration: " + durationMinutes + " min. and " + durationSeconds + " sec.",
                MessageType.INFO);
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
            logger.logException(ID, "Execution failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(CommitSequence commitSequence) {
        numberOfCreatedCommitSequences++;
        
        String commitSequenceFileName = COMMIT_SEQUENCE_FILE_NAME_PREFIX + commitSequence.getSequenceNumber() 
                + COMMIT_SEQUENCE_FILE_NAME_POSTFIX;
        writeFile(outputDirectory.getAbsolutePath(), commitSequenceFileName, commitSequence.toString(), false);        
        
        Date currentDate = new Date();
        logger.log(ID, "New commit sequence created", currentDate.toString() + System.lineSeparator() 
                + "Number of commits: " + commitSequence.size(), MessageType.DEBUG);
    }
    
    /**
     * Writes the given content to the file specified by the given path and file name.
     * 
     * @param path the string representation of the path denoting the location the file shall be saved
     * @param fileName the name of the file to be created or overridden; this name should contain a file extension
     * @param fileContent the content which shall be written to the file
     * @param override specifies whether to override an existing file (<code>true</code>) or not (<code>false</code>)
     * @return <code>true</code> if creating and writing the file was successful; <code>false</code> otherwise 
     */
    private boolean writeFile(String path, String fileName, String fileContent, boolean override) {
        boolean fileWrittenSuccessfully = false;
        File file = createFile(path, fileName);
        // Keep null-check separately to avoid false warning messages generated by inner check
        if (file != null) {           
            if (override || !file.exists()) {
                Path parentDirectory = file.toPath().getParent();
                try {
                    if (!Files.exists(parentDirectory)) {
                        Files.createDirectories(parentDirectory);
                    }
                    Files.write(file.toPath(), fileContent.getBytes());
                    fileWrittenSuccessfully = true;
                } catch (IOException e) {
                    logger.logException(ID, "Writing content to file \"" + file.getAbsolutePath() + "\" failed", e);
                }
            } else {
                logger.log(ID, "Writing file \"" + file.getAbsolutePath() + "\" denied",
                        "The file already exists and overriding is forbidden",
                        MessageType.ERROR);
            }
        } else {
            logger.log(ID, "Writing to file failed", "The path or file name is empty", MessageType.ERROR);
        }
        return fileWrittenSuccessfully;
    }
    
    /**
     * Creates a new {@link File} based on the given path and file name.
     * 
     * @param path the string representation of the path denoting the location of the file to be created
     * @param fileName the name of the file to be created
     * @return a new file or <code>null</code> if the path or file name is empty
     */
    private File createFile(String path, String fileName) {
        File file = null;
        if (path != null && !path.isEmpty()) {
            if (fileName != null && !fileName.isEmpty()) {
                file = new File(path, fileName);
            } else {
                logger.log(ID, "Cannot create file", "No file name specified", MessageType.ERROR);
            }
        } else {
            logger.log(ID, "Cannot create file", "No path for a file specified", MessageType.ERROR);
        }
        return file;
    }
}
