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
import java.util.List;

import utilities.Logger;
import utilities.Logger.MessageType;
import utilities.ProcessUtilities;
import utilities.ProcessUtilities.ExecutionResult;

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
     * The {@link Logger} for pretty-printing messages to the console.
     */
    private Logger logger = Logger.getInstance();
    
    /**
     * The {@link File} denoting the root directory of the Git repository from which commit sequences shall be created. 
     */
    private File repositoryDirectory;
    
    /**
     * The {@link List} of all {@link CommitSequence}s created from a Git repository.
     */
    private List<CommitSequence> commitSequenceList;
    
    /**
     * Constructs a new {@link GitCommitSequencer} instance.
     * 
     * @param args the array of {@link String}s passed as arguments to this tool at start-up and received by 
     *        {@link #main(String[])}; should never be <code>null</code>, but may be <i>empty</i>
     */
    private GitCommitSequencer(String[] args) {
        String repositoryDirectoryString = "C:\\Users\\kroeher\\Data\\Repositories\\DevOpt@TUC";
        repositoryDirectory = new File(repositoryDirectoryString);
        commitSequenceList = new ArrayList<CommitSequence>();
    }

    /**
     * Starts the creation of commit sequences by this {@link GitCommitSequencer} instance.
     */
    private void run() {
        String startCommit = getStartCommit();
        CommitSequence commitSequence = new CommitSequence(repositoryDirectory, this);
        commitSequence.run(startCommit);
        
        System.out.println("\n\nNumber of sequences: " + commitSequenceList.size());
        for (int i = 0; i < commitSequenceList.size(); i++) {
            System.out.println("Sequence " + i);
            CommitSequence cs = commitSequenceList.get(i);
            for (String commit : cs) {
                System.out.println(commit);
            }
            System.out.println("");
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
     * Starts this tool.
     * 
     * @param args the user-defined arguments passed as input at start-up; never <code>null</code>, but may be
     *        <i>empty</i>
     */
    public static void main(String[] args) {
        GitCommitSequencer sequencer = new GitCommitSequencer(args);
        sequencer.run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(CommitSequence commitSequence) {
        commitSequenceList.add(commitSequence);
    }
    
}
