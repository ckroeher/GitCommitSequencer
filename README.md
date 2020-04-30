# GitCommitSequencer
This tool creates commit sequences from a given Git repository.
In order to illustrate its workflow and to give examples for its output, consider the following simplified Git commit graph:
```
C (latest commit; HEAD)
| \
|  \
B1 B2
|  /
| /
A (initial commit)
```
The execution of this tool on a repository with the illustrated four commits (*A*, *B1*, *B2*, and *C*) will result in two commit sequences:
  1. *C*, *B1*, *A*
  2. *C*, *B2*, *A*
  
Hence, each commit sequence represents an unique path from the latest (HEAD) commit to the initial commit of a repository.

It is also possible to start with a user-defined commit, like *B2*, without moving the HEAD.
This will result in a single commit sequence: *B2*, *A*.
For that purpose, the tool offers an optional parameter to provide the desired commit (SHA) as the start commit.

The actual result of this tool is a set of text files and a comma-separated-values file.
Each of the text files represents exactly one commit sequence and contains the respective commits (a single SHA per file line).
The comma-separated-values file acts as a summary and contains in each line a particular sequence number as well as the total number of commits in that sequence.


## Installation
In order to use this tool, you need to have Java and Git installed. On Ubuntu 20.04 LTS, you may install these requirements as follows:

### OpenJDK (version >= 13)
```
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt update
sudo apt install openjdk-13-jdk
```

### Git (version >= 2.25.1)
```
sudo apt-get install git
```

The actual installation is simply to download the latest release of this tool and save it at your favorite location.


## Execution
```
java -jar GitCommitSequencer.jar [PATH1] [PATH2] [SHA?]
    [PATH1] the mandatory absolute (file) path to the Git repository (root directory)
    [PATH2] the mandatory absolute (file) path to the (existing) output directory
    [SHA?]  the optional start commit (SHA) 
```