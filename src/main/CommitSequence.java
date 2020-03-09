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
package main;

import java.io.File;
import java.util.ArrayList;

import utilities.Logger;
import utilities.Logger.MessageType;
import utilities.ProcessUtilities;
import utilities.ProcessUtilities.ExecutionResult;

/**
 * This class represents a particular sequence of commits in the order from newest to oldest.
 * 
 * @author Christian Kroeher
 *
 */
public class CommitSequence extends ArrayList<String> {
    
    private static final String ID = "CommitSequence";

    /**
     * The serial version UID of this class.
     */
    private static final long serialVersionUID = -1350039576783309464L;
    
    private static final String[] GIT_PARENT_COMMIT_COMMAND = {"git", "log", "--pretty=%P", "-1"};
    
    private Logger logger = Logger.getInstance();
    
    private ProcessUtilities processUtilities = ProcessUtilities.getInstance();
    
    private File repositoryDirectory;
    
    private ISequenceStorage sequenceStorage;

    /**
     * Constructs a new {@link CommitSequence} instance.
     * 
     * @param repositoryDirectory the {@link File} denoting the repository (directory) the given start commit belongs to
     * @param sequenceStorage the {@link ISequenceStorage} to add this and all other (sub-)sequences to
     */
    public CommitSequence(File repositoryDirectory, ISequenceStorage sequenceStorage) {
        logger.log(ID, "New commit sequence", "Repository: \"" + repositoryDirectory.getAbsolutePath() + "\"",
                MessageType.INFO);
        this.repositoryDirectory = repositoryDirectory;
        this.sequenceStorage = sequenceStorage;
    }
    
    /**
     * Starts the actual creation of Git commit sequences and adds itself to the {@link ISequenceStorage} passed to the
     * constructor of this class after that creation is finished.
     * 
     * @param startCommit the {@link String} representing the commit starting this sequence (the newest commit)
     */
    public void run(String startCommit) {
        logger.log(ID, "Start sequence creation", "Start commit: \"" + startCommit + "\"",
                MessageType.INFO);
//        this.add(startCommit);
//        addParent(startCommit);
        createSequence(startCommit);
        sequenceStorage.add(this);
    }
    
    private void createSequence(String currentCommit) {
        if (currentCommit != null && !currentCommit.isBlank()) {            
            // Add the current commit to this sequence
            this.add(currentCommit);
            // Get all possible parents of the current commit
            String[] currentCommitParents = getParents(currentCommit);
            // Proceed with parent commits, if available; otherwise we are finished with this sequence
            if (currentCommitParents != null) {
                // Proceed with parent commits depending on their number
                if (currentCommitParents.length == 1) {
                    // There is only a single parent commit; call this method again with that commit
                    createSequence(currentCommitParents[0]);
                } else {
                    /*
                     * There are multiple parent commits; the first parent commit is the parent commit within this
                     * sequence, while the other parent commits represent another branch and, hence, another sequence.
                     * 
                     * Hence, for each of the other parent commits, create a new commit sequence and add the current
                     * commits of this sequence to them before starting their actual process.
                     */
                    CommitSequence newCommitSequence;
                    for (int i = 1; i < currentCommitParents.length; i++) {
                        newCommitSequence = cloneThis();
                        newCommitSequence.run(currentCommitParents[i]);
                    }
                    // Proceed with this sequence
                    createSequence(currentCommitParents[0]);
                }
            }
        }
    }
    
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
    
    private CommitSequence cloneThis() {
        CommitSequence clonedSequence = new CommitSequence(repositoryDirectory, sequenceStorage);
        for (String commit : this) {
            clonedSequence.add(commit);
        }
        return clonedSequence;
    }
    
}
