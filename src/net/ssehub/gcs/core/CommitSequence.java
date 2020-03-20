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
public class CommitSequence extends ArrayList<String> {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "CommitSequence";

    /**
     * The serial version UID of this class required by the extended {@link ArrayList}.
     */
    private static final long serialVersionUID = -1350039576783309464L;
    
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
     * <i>1</i> every time {@link CommitSequence#CommitSequence(File, ISequenceStorage)} is called. Hence, the first
     * instance has a sequence number of <i>1</i>.
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
     * The {@link ISequenceStorage} to which this commit sequence will be stored.
     */
    private ISequenceStorage sequenceStorage;
    
    /**
     * The {@link String} representing the commit (SHA) starting this sequence (the newest commit).
     */
    private String startCommit;
    
    /**
     * The {@link List} of {@link CommitSequence}s to create after this sequence is created successfully.
     */
    private List<CommitSequence> postponedCommitSequenceList;

    /**
     * Constructs a new {@link CommitSequence} instance.
     * 
     * @param repositoryDirectory the {@link File} denoting the repository (directory) from which this commit sequence
     *        shall be created; should never be <code>null</code> and always needs to be an <i>existing directory</i>
     * @param sequenceStorage the {@link ISequenceStorage} to add this and all other (sub-)sequences to; should never be
     *        <code>null</code>
     * @param startCommit the {@link String} representing the commit (SHA) starting this sequence (the newest commit);
     *        passing <code>null</code> or a <i>blank</i> string terminates the creation immediately resulting in a
     *        potentially empty commit sequence
     */
    public CommitSequence(File repositoryDirectory, ISequenceStorage sequenceStorage, String startCommit) {
        instanceCounter++;
        sequenceNumber = instanceCounter;
        processUtilities = ProcessUtilities.getInstance();
        this.repositoryDirectory = repositoryDirectory;
        this.sequenceStorage = sequenceStorage;
        this.startCommit = startCommit;
        postponedCommitSequenceList = new ArrayList<CommitSequence>();
        
        logger.log(ID, "Commit sequence " + sequenceNumber,
                "Repository: \"" + repositoryDirectory.getAbsolutePath() + "\"", MessageType.DEBUG);
    }
    
    /**
     * Starts the creation of this commit sequence as well as all sub-sequences and adds them to the
     * {@link ISequenceStorage} passed to the constructor of this class.
     */
    public void run() {
        if (commitAvailable(startCommit)) {            
            logger.log(ID, "Start sequence creation", "Start commit: \"" + startCommit + "\"", MessageType.DEBUG);
            createSequence(startCommit);
            sequenceStorage.add(this);
            for (CommitSequence subCommitSequence : postponedCommitSequenceList) {
                subCommitSequence.run();
            }
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
     * method creates respective sub-sequences and adds them to the {@link #postponedCommitSequenceList} for creating
     * them after this sequences is created successfully.
     * 
     * @param startCommit the {@link String} representing the commit (SHA) to add to this sequence and for which the
     *        parents will be determined; should never be <code>null</code> nor <i>blank</i>
     */
    private void createSequence(String startCommit) {
        // Add the start commit as the first commit in this sequence
        this.add(startCommit);
        // Start adding parent commit(s)
        String currentCommit = startCommit;
        String[] currentCommitParents;
        CommitSequence subCommitSequence;
        while ((currentCommitParents = getParents(currentCommit)) != null) {
            // Make the first parent the current commit
            currentCommit = currentCommitParents[0];
            /*
             * Add all other parent commits to work list (postpone the creation of additional sequences until the
             * current sequence is done)
             */
            for (int i = 1; i < currentCommitParents.length; i++) {
                subCommitSequence = cloneThis(currentCommitParents[i]);
                postponedCommitSequenceList.add(subCommitSequence);
            }
            // After cloning, add the current commit (first parent) to this sequence
            this.add(currentCommit);
        }
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
     * Creates a clone of this commit sequence by creating a new {@link CommitSequence} instance with the given
     * {@link String} as start commit. As part of this cloning, all commits currently available in this sequence will
     * also be added to the new sequence in the same order.
     * 
     * @param startCommit the {@link String} representing the commit (SHA) starting the cloned sequence after all
     *        commits of this sequence; should never be <code>null</code> nor <i>blank</i>
     * @return the {@link CommitSequence} representing a clone of this one
     */
    private CommitSequence cloneThis(String startCommit) {
        CommitSequence clonedSequence = new CommitSequence(repositoryDirectory, sequenceStorage, startCommit);
        for (String commit : this) {
            clonedSequence.add(commit);
        }
        return clonedSequence;
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String commitSequenceString = "";
        if (!this.isEmpty()) {
            StringBuilder commitSequenceStringBuilder = new StringBuilder();
            for (String commit : this) {
                commitSequenceStringBuilder.append(commit);
                commitSequenceStringBuilder.append(System.lineSeparator());
            }
            commitSequenceString = commitSequenceStringBuilder.toString();
        }
        return commitSequenceString;
    }
}
