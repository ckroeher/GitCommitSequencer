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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import net.ssehub.gcs.utilities.Logger;
import net.ssehub.gcs.utilities.Logger.MessageType;
import net.ssehub.gcs.utilities.ProcessUtilities;
import net.ssehub.gcs.utilities.ProcessUtilities.ExecutionResult;

/**
 * This class represents a particular sequence of commits in the order from newest to oldest.
 * 
 * @author Christian Kroeher
 *
 */
public class CommitSequence {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "CommitSequence";
    
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
     * The constant part of the command for printing the parent commit(s) to console. The commit (SHA), for which
     * the parent commit(s) shall be printed, must be appended.<br>
     * <br>
     * Command: <code>git log --pretty=%P -1</code> 
     */
    private static final String[] GIT_PARENT_COMMIT_COMMAND = {"git", "log", "--pretty=%P", "-1"};
    
    /**
     * The constant part of the command for printing the commit information to console. The commit (SHA), for which
     * the information shall be printed, must be appended.<br>
     * <br>
     * Command: <code>git show</code> 
     */
    private static final String[] GIT_SHOW_COMMAND = {"git", "show"};
    
    /**
     * The {@link CommitSequence} instance counter. The value is initialized with <i>0</i> and will be increased by
     * <i>1</i> every time a new instance is created. Hence, the first instance has a sequence number of <i>1</i>.
     */
    private static int instanceCounter = 0;
    
    /**
     * The running number of this {@link CommitSequence} instance.
     */
    private int sequenceNumber;
    
    /**
     * The {@link Logger} for pretty-printing messages to the console.
     */
    private Logger logger = Logger.getInstance();
    
    /**
     * The {@link ProcessUtilities} for retrieving Git information, like the available commits and their data, via the
     * execution of external processes.
     */
    private ProcessUtilities processUtilities;
    
    /**
     * The {@link File} denoting the root directory of the Git repository from which this commit sequence shall be
     * created. 
     */
    private File repositoryDirectory;
    
    /**
     * The {@link ISequenceStorage} to which commit sub-sequence will be stored for later creation.
     */
    private ISequenceStorage sequenceStorage;
    
    /**
     * The {@link String} representing the commit (SHA) starting this sequence (the newest commit).
     */
    private String startCommit;
    
    /**
     * The {@link File} denoting the output file to which the commits of this {@link CommitSequence} will be written
     * iteratively.
     */
    private File outputFile;
    
    /**
     * The {@link File} denoting the child {@link CommitSequence} of this sequence. The content of that file will be
     * prepended to this sequence until the {@link #childCommit} (inclusive). May be <code>null</code>, if no child
     * commit sequence exists.
     */
    private File childCommitSequence;
    
    /**
     * The {@link String} representing the child commit of this sequence. This commit marks the last commit in the
     * {@link #childCommitSequence} which has to be prepended to this sequence to result in a complete commit sequence.
     * May be <code>null</code>, if no child commit (sequence) exists.
     */
    private String childCommit;

    /**
     * Constructs a new {@link CommitSequence} instance.
     * 
     * @param sequenceStorage the {@link ISequenceStorage} to add commit sub-sequences to for their creation after this
     *        sequences is created completely; should never be <code>null</code>
     * @param repositoryDirectory the {@link File} denoting the repository (directory) from which this commit sequence
     *        shall be created; should never be <code>null</code> and always needs to be an <i>existing directory</i>
     * @param startCommit the {@link String} representing the commit (SHA) starting this sequence (the newest commit);
     *        passing <code>null</code> or a <i>blank</i> string terminates the creation immediately resulting in a
     *        potentially empty commit sequence
     * @param outputDirectory the {@link File} denoting the output directory to which the file representing this commit
     *        sequence shall be stored; should never be <code>null</code> and always needs to be an
     *        <i>existing directory</i> 
     */
    public CommitSequence(ISequenceStorage sequenceStorage, File repositoryDirectory, String startCommit,
            File outputDirectory) {
        setup(sequenceStorage, repositoryDirectory, startCommit, outputDirectory);
        
        logger.log(ID, "Commit sequence " + sequenceNumber,
                "Repository: \"" + repositoryDirectory.getAbsolutePath() + "\"" + System.lineSeparator() 
                + "Output file: \"" + outputFile.getAbsolutePath() + "\"", MessageType.DEBUG);
    }
    
    /**
     * Constructs a new {@link CommitSequence} instance.
     * 
     * @param sequenceStorage the {@link ISequenceStorage} to add commit sub-sequences to for their creation after this
     *        sequences is created completely; should never be <code>null</code>
     * @param repositoryDirectory the {@link File} denoting the repository (directory) from which this commit sequence
     *        shall be created; should never be <code>null</code> and always needs to be an <i>existing directory</i>
     * @param startCommit the {@link String} representing the commit (SHA) starting this sequence (the newest commit);
     *        passing <code>null</code> or a <i>blank</i> string terminates the creation immediately resulting in a
     *        potentially empty commit sequence
     * @param outputDirectory the {@link File} denoting the output directory to which the file representing this commit
     *        sequence shall be stored; should never be <code>null</code> and always needs to be an
     *        <i>existing directory</i>
     * @param childCommitSequence The {@link File} denoting the {@link #childCommitSequence} of this sequence; can be
     *        <code>null</code>, if no child commit sequence exists
     * @param childCommit The {@link String} representing the {@link #childCommit} of this sequence; can be
     *        <code>null</code>, if no child commit (sequence) exists
     */
    //CHECKSTYLE:OFF - Avoid errors due to too many arguments
    private CommitSequence(ISequenceStorage sequenceStorage, File repositoryDirectory, String startCommit,
            File outputDirectory, File childCommitSequence, String childCommit) {
        setup(sequenceStorage, repositoryDirectory, startCommit, outputDirectory);
        
        this.childCommitSequence = childCommitSequence;
        this.childCommit = childCommit;
        
        logger.log(ID, "Commit sequence " + sequenceNumber,
                "Repository: \"" + repositoryDirectory.getAbsolutePath() + "\"" + System.lineSeparator() 
                + "Output file: \"" + outputFile.getAbsolutePath() + "\"" + System.lineSeparator()
                + "Child commit sequence: \"" + childCommitSequence.getAbsolutePath() + "\"" + System.lineSeparator()
                + "Child commit: \"" + childCommit + "\"", MessageType.DEBUG);
    }
    //CHECKSTYLE:ON - Resume after avoiding errors due to too many arguments
    
    /**
     * Initializes this {@link CommitSequence} instance.
     * 
     * @param sequenceStorage the {@link ISequenceStorage} to add commit sub-sequences to for their creation after this
     *        sequences is created completely; should never be <code>null</code>
     * @param repositoryDirectory the {@link File} denoting the repository (directory) from which this commit sequence
     *        shall be created; should never be <code>null</code> and always needs to be an <i>existing directory</i>
     * @param startCommit the {@link String} representing the commit (SHA) starting this sequence (the newest commit);
     *        passing <code>null</code> or a <i>blank</i> string terminates the creation immediately resulting in a
     *        potentially empty commit sequence
     * @param outputDirectory the {@link File} denoting the output directory to which the file representing this commit
     *        sequence shall be stored; should never be <code>null</code> and always needs to be an
     *        <i>existing directory</i>
     */
    private void setup(ISequenceStorage sequenceStorage, File repositoryDirectory, String startCommit,
            File outputDirectory) {
        instanceCounter++;
        sequenceNumber = instanceCounter;
        
        processUtilities = ProcessUtilities.getInstance();
        
        this.sequenceStorage = sequenceStorage;
        this.repositoryDirectory = repositoryDirectory;
        this.startCommit = startCommit;
        this.childCommitSequence = null;
        this.childCommit = null;
        
        String outputFileName = COMMIT_SEQUENCE_FILE_NAME_PREFIX + sequenceNumber + COMMIT_SEQUENCE_FILE_NAME_POSTFIX;
        outputFile = new File(outputDirectory, outputFileName);
    }
    
    /**
     * Starts the creation of this commit sequence and adds all detected sub-sequences to the {@link #sequenceStorage}
     * for their creation after this sequences is created completely.
     */
    public void run() {
        // TODO this only needs to be checked of the public constructor is called
        // If the private constructor is called, the start string is actually an available (parent) commit
        if (commitAvailable(startCommit)) {            
            logger.log(ID, "Start sequence creation", "Start commit: \"" + startCommit + "\"", MessageType.DEBUG);
            createSequence(startCommit);
        } else {
            logger.log(ID, "The commit \"" + startCommit + "\" is not available in \"" 
                    + repositoryDirectory.getAbsolutePath() + "\"", null, MessageType.ERROR);
            instanceCounter--;
        }
    }
    
    /**
     * Checks whether the given commit (SHA) is available in the repository denoted by the current
     * {@link #repositoryDirectory} using the {@link #GIT_SHOW_COMMAND}.
     * 
     * @param commit the {@link String} representing the commit (SHA), which shall be checked for availability
     * @return <code>true</code>, if the given commit is available; <code>false</code> otherwise
     */
    private boolean commitAvailable(String commit) {
        boolean commitAvailable = false;
        if (commit != null && !commit.isBlank()) {
            String[] showCommitCommand = processUtilities.extendCommand(GIT_SHOW_COMMAND, commit);
            ExecutionResult result = processUtilities.executeCommand(showCommitCommand, repositoryDirectory);
            commitAvailable = result.executionSuccessful();
        }
        return commitAvailable;
    }
    
    /**
     * Creates this commit sequence by iterating all parent commits. If multiple parent commits are available, the
     * method creates respective sub-sequences and adds them to {@link #sequenceStorage} for creating them after this
     * sequences is created completely.
     * 
     * @param startCommit the {@link String} representing the commit (SHA) to add to this sequence and for which the
     *        parents will be determined; should never be <code>null</code> nor <i>blank</i>
     */
    private void createSequence(String startCommit) {
        // First, prepend potential child commits to this sequence
        if (prependChildren()) {            
            // Add the start commit as the first commit in this sequence
            toOutputFile(startCommit);
            // Start adding parent commit(s)
            String currentCommit = startCommit;
            String[] currentCommitParents;
            CommitSequence subCommitSequence;
            while ((currentCommitParents = getParents(currentCommit)) != null) {
                /*
                 * Add all other parent commits to work list (postpone the creation of additional sequences until the
                 * current sequence is done)
                 */
                for (int i = 1; i < currentCommitParents.length; i++) {
                    subCommitSequence = new CommitSequence(sequenceStorage, repositoryDirectory,
                            currentCommitParents[i], outputFile.getParentFile(), outputFile, currentCommit);
                    sequenceStorage.add(subCommitSequence);
                }
                // Make the first parent the current commit
                currentCommit = currentCommitParents[0];
                toOutputFile(currentCommit);
            }
        }
    }
    
    /**
     * Reads each line in the {@link #childCommitSequence} until the current line equals the {@link #childCommit} of
     * this sequence and adds them as initial content to the {@link #outputFile}.
     * 
     * @return <code>true</code>, if prepending the (part of the) {@link #childCommitSequence} until the
     *         {@link #childCommit} (inclusive) to the {@link #outputFile} was successful; <code>false</code> otherwise
     */
    private boolean prependChildren() {
        boolean prependingChildrenSuccessful = false;
        if (childCommitSequence == null && childCommit == null) {
            // There is no child commit sequence for this sequence; nothing to prepend
            prependingChildrenSuccessful = true;
        } else {            
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;       
            try {
                fileReader = new FileReader(childCommitSequence);
                bufferedReader = new BufferedReader(fileReader);
                String fileLine;
                while ((fileLine = bufferedReader.readLine()) != null) {
                    toOutputFile(fileLine);
                    if (fileLine.equals(childCommit)) {
                        break;
                    }
                }
                prependingChildrenSuccessful = true;
            } catch (IOException | OutOfMemoryError e) {
                Logger.getInstance().logException(ID, "Reading content from file \"" 
                        + childCommitSequence.getAbsolutePath() + "\" failed", e);
            } finally {
                // Close the readers in any case
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException e) {
                        Logger.getInstance().logException(ID, "Closing file reader for \""
                                + childCommitSequence.getAbsolutePath() + "\" failed", e);
                    }
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Logger.getInstance().logException(ID, "Closing buffered reader for \"" 
                                + childCommitSequence.getAbsolutePath() + "\" failed", e);
                    }
                }
            }
        }
        return prependingChildrenSuccessful;
    }
    
    /**
     * Writes the given {@link String} denoting a commit of this sequences to the {@link #outputFile}. If that file does
     * not exist, it will be created, while an existing file will be extended by appending the given commit to the end
     * of that file.
     * 
     * @param commit the {@link String} denoting a commit of this sequence to be written
     * @return <code>true</code> if writing the given commit to the {@link #outputFile} was successful;
     *         <code>false</code> otherwise
     */
    private boolean toOutputFile(String commit) {
        boolean fileWrittenSuccessfully = false;
        String outputLine = commit + System.lineSeparator();
        // Keep null-check separately to avoid false warning messages generated by inner check
        if (outputFile != null) {
            try {
                if (!outputFile.exists()) {                    
                    Files.write(outputFile.toPath(), outputLine.getBytes());
                } else {
                    Files.write(outputFile.toPath(), outputLine.getBytes(), StandardOpenOption.APPEND);
                }
                fileWrittenSuccessfully = true;
            } catch (IOException e) {
                logger.logException(ID, "Writing commit \"" + commit + "\" to file \"" + outputFile.getAbsolutePath()
                        + "\" failed", e);
            }
        } else {
            logger.log(ID, "Writing to file failed", "The output file is \"null\"", MessageType.ERROR);
        }
        return fileWrittenSuccessfully;
    }
    
    /**
     * Determines the parent commit(s) of the given commit by calling the {@link #GIT_PARENT_COMMIT_COMMAND}.
     * 
     * @param childCommit the {@link String} representing the commit (SHA) for which the parent commit(s) shall be
     *        returned; should never <code>null</code> nor <i>blank</i> 
     * @return the {@link String} array containing all parent commit(s) (SHAs) of the given commit; may be
     *         <code>null</code>, if retrieving the parent commit(s) fails or there are no parent commit(s)
     */
    private String[] getParents(String childCommit) {
        String[] parentCommits = null;
        String[] parentCommitsCommand = processUtilities.extendCommand(GIT_PARENT_COMMIT_COMMAND, childCommit);
        ExecutionResult parentCommitsCommandResult = processUtilities.executeCommand(parentCommitsCommand,
                repositoryDirectory);
        if (parentCommitsCommandResult.executionSuccessful()) {
            String parentCommitsString = parentCommitsCommandResult.getStandardOutputData().trim();
            if (!parentCommitsString.isBlank()) {
                parentCommits = parentCommitsString.split(" ");
            }
        } else {
            logger.log(ID, "Retrieving parent commits for \"" + childCommit + "\" failed",
                    parentCommitsCommandResult.getErrorOutputData(), MessageType.ERROR);
        }
        return parentCommits;
    }
    
    /**
     * Returns the {@link #sequenceNumber} of this instance.
     * 
     * @return the {@link #sequenceNumber} of this instance; equal to or greater than <i>1</i>
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }
    
    /**
     * Resets the {@link #instanceCounter} to <i>0</i>.
     */
    public static void resetInstanceCounter() {
        instanceCounter = 0;
    }

}
