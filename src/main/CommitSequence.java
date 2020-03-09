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
    
    private File repositoryDirectory;

    /**
     * Constructs a new {@link CommitSequence} instance.
     * 
     * @param startCommit the {@link String} representing the commit starting this sequence (the newest commit)
     * @param repositoryDirectory the {@link File} denoting the repository (directory) the given start commit belongs to
     */
    public CommitSequence(String startCommit, File repositoryDirectory) {
        logger.log(ID, "New commit sequence",
                "Start commit: \"" + startCommit + "\"\nRepository: \"" + repositoryDirectory.getAbsolutePath() + "\"",
                MessageType.INFO);
        this.repositoryDirectory = repositoryDirectory;
        this.add(startCommit);
        addParent(startCommit);
    }
    
    /**
     * TODO.
     * 
     * @param currentCommit TODO
     */
    private void addParent(String currentCommit) {
        String[] currentParentCommitCommand = ProcessUtilities.getInstance().extendCommand(GIT_PARENT_COMMIT_COMMAND,
                currentCommit);
        ExecutionResult executionResult = ProcessUtilities.getInstance().executeCommand(currentParentCommitCommand,
                repositoryDirectory);
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
            logger.log(ID, "Retrieving parent commit for \"" + currentCommit + "\" failed",
                    executionResult.getErrorOutputData(), MessageType.ERROR);
        }
    }
    
}
