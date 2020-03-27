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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.ssehub.gcs.utilities.Logger;
import net.ssehub.gcs.utilities.Logger.MessageType;

/**
 * This class realizes a commit cache with a fixed capacity for <i>1000</i> commits. If this threshold is reached, those
 * commits are written to an output file given as a parameter to the constructor of this class, which clears the cache
 * for storing the subsequent commits.
 * 
 * @author Christian Kroeher
 *
 */
public class CommitCache {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "CommitCache";
    
    /**
     * The number of characters (Unicode code units) of a standard commit SHA. This number is used in combination with 
     * the {@link #CACHE_CAPACITY} to set the capacity of the {@link #commitCacheStringBuilder}.
     */
    private static final int STANDARD_COMMIT_UNICODE_CODE_UNITS = 40;
    
    /**
     * The number of commits defining the capacity of this {@link CommitCache} instance. This number is used in
     * combination with the {@link #STANDARD_COMMIT_UNICODE_CODE_UNITS} to set the capacity of the
     * {@link #commitCacheStringBuilder}.
     */
    private static final int CACHE_CAPACITY = 1000;
    
    /**
     * The index (running number) of the last commit defining the threshold of this {@link CommitCache} instance. 
     */
    private static final int CACHE_LAST_INDEX = CACHE_CAPACITY - 1;
    
    /**
     * The {@link Logger} for pretty-printing messages to the console.
     */
    private Logger logger = Logger.getInstance();
    
    /**
     * The {@link StringBuilder} representing the actual cache to which each commit will be append with an additional
     * line separator during {@link #add(String)}. Its length is reseted to <i>0</i> during {@link #clear()}, e.g., if
     * the threshold is reached.
     */
    private StringBuilder commitCacheStringBuilder;
    
    /**
     * The zero-based counter of commits currently stored in this {@link CommitCache} instance.
     */
    private int commitCounter;
    
    /**
     * The {@link RandomAccessFile} used to open the {@link #outputFileChannel} in {@link #openFileChannel()}.
     * 
     * @see #openFileChannel()
     * @see #toFileChannel(String)
     * @see #closeFileChannel()
     */
    private RandomAccessFile outputFileStream;
    
    /**
     * The {@link FileChannel} used to write the commits of this sequence to the {@link #outputFile}.
     * 
     * @see #openFileChannel()
     * @see #toFileChannel(String)
     * @see #closeFileChannel()
     */
    private FileChannel outputFileChannel;
    
    /**
     * Constructs a new {@link CommitCache} instance.
     * 
     * @param outputFile the {@link File} to which the content of this cache will be written
     * @throws CommitCacheCreationException if creating this instance fails
     */
    public CommitCache(File outputFile) throws CommitCacheCreationException {
        openFileChannel(outputFile);
        commitCacheStringBuilder = new StringBuilder(STANDARD_COMMIT_UNICODE_CODE_UNITS * CACHE_CAPACITY);
        commitCounter = 0;
    }
    
    /**
     * Adds the given commit to this cache. If the threshold is reached, the cache content is written to the output file
     * before the given commit is stored.
     * 
     * @param commit {@link String} representing a commit to be added to this cache
     * @return <code>true</code>, if adding the given commit was successful; <code>false</code> otherwise
     */
    public boolean add(String commit) {
        boolean commitAddedSuccessfully = false;
        if (commit != null && !commit.isBlank()) {
            if (commitCounter == CACHE_LAST_INDEX) {
                if (clear()) {
                    commitCacheStringBuilder.append(commit);
                    commitCacheStringBuilder.append(System.lineSeparator());
                    commitCounter++;
                    commitAddedSuccessfully = true;
                }
            } else {
                commitCacheStringBuilder.append(commit);
                commitCacheStringBuilder.append(System.lineSeparator());
                commitCounter++;
                commitAddedSuccessfully = true;
            }
        } else {
            logger.log(ID, "Addition of commit denied",
                    "The commit is \"null\", empty, or contains only white space codepoints", MessageType.WARNING);
        }
        return commitAddedSuccessfully;
    }
    
    /**
     * Destroys this cache in terms of writing the current commits to the output file, closing the file channel, and
     * deleting all references to internal objects.
     * 
     * @return <code>true</code>, if destroying this cache was successful; <code>false</code> otherwise
     */
    public boolean destroy() {
        boolean cacheDestroyedSuccessfully = clear() && closeFileChannel();
        logger = null;
        commitCacheStringBuilder = null;
        outputFileStream = null;
        outputFileChannel = null;
        return cacheDestroyedSuccessfully;
    }
    
    /**
     * Clears this cache by writing the content of the {@link #commitCacheStringBuilder} via the 
     * {@link #outputFileChannel} to the output file and setting the length of the {@link #commitCacheStringBuilder} as
     * well as the {@link #commitCounter} to <i>0</i>.
     * 
     * @return <code>true</code>, if clearing this cache was successful; <code>false</code> otherwise
     */
    private boolean clear() {
        boolean cacheClearedSuccessfully = false;
        // Write all commits in this cache via the given file channel
        String commitCacheString = commitCacheStringBuilder.toString();
        byte[] commitCacheStringBytes = commitCacheString.getBytes();
        ByteBuffer commitCacheStringByteBuffer = ByteBuffer.allocate(commitCacheStringBytes.length);
        commitCacheStringByteBuffer.put(commitCacheStringBytes);
        commitCacheStringByteBuffer.flip();
        try {
            if (outputFileChannel.write(commitCacheStringByteBuffer) == commitCacheStringBytes.length) {                
                // Clear the actual cache
                commitCacheStringBuilder.setLength(0);
                commitCounter = 0;
                cacheClearedSuccessfully = true;
            }
        } catch (IOException e) {
            logger.logException(ID, "Writing commit cache content to file failed", e);
        }
        return cacheClearedSuccessfully;
    }
    
    /**
     * Opens a file channel by creating the {@link #outputFileStream} and the {@link #outputFileChannel} based on the
     * given output file.
     * 
     * @return <code>true</code>, if creating (opening) the file channel was successful; <code>false</code> otherwise
     * @throws CommitSequenceCreationException if creating (opening) the file channel failed
     * @see #toFileChannel(String)
     * @see #closeFileChannel()
     */
    /**
     * Opens a file channel by creating the {@link #outputFileStream} and the {@link #outputFileChannel} based on the
     * given output file.
     * 
     * @param outputFile the {@link File} for which the file channel shall be opened
     * @return <code>true</code>, if opening the file channel was successful; <code>false</code> otherwise
     * @throws CommitCacheCreationException if the given output file does not exist
     */
    private boolean openFileChannel(File outputFile) throws CommitCacheCreationException {
        boolean fileChannelOpen = false;
        if (outputFile != null) {
            try {
                outputFileStream = new RandomAccessFile(outputFile.getAbsolutePath(), "rw");
                outputFileChannel = outputFileStream.getChannel();
                fileChannelOpen = outputFileStream != null && outputFileChannel != null;
            } catch (FileNotFoundException e) {
                throw new CommitCacheCreationException("Opening file channel for output file \""
                        + outputFile.getAbsolutePath() + "\" failed", e);
            }          
        } else {
            throw new CommitCacheCreationException("No output file for this cache available");
        }
        return fileChannelOpen;
    }
    
    /**
     * Closes the {@link #outputFileStream} and the {@link #outputFileChannel}.
     * 
     * @return <code>true</code>, if closing was successful; <code>false</code> otherwise
     * @see #openFileChannel()
     * @see #toFileChannel(String)
     */
    private boolean closeFileChannel() {
        boolean fileChannelClosed = false;
        try {
            outputFileStream.close();
            outputFileChannel.close();
            fileChannelClosed = true;
        } catch (IOException | NullPointerException e) {
            logger.logException(ID, "Closing the file channel failed", e);
        }
        return fileChannelClosed;
    }

}
