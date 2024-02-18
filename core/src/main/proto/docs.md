# MLSP protobuf specification

## Language Server Information

## Model Resource Interface
Editors can open, close, create and delete files
 - Project Opened - A new project has been opened and the server should clear all cache and load the new project
 - File Created
 - File Deleted
 - Handle Diff - A change has happened in a file

## Feedback Stream Interfaces
Language servers can provide feedback through a variety of methods.
 - Diagnostics
 - Notifications
 - Progress

