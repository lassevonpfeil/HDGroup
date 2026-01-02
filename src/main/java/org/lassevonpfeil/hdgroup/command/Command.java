package org.lassevonpfeil.hdgroup.command;

public interface Command {
    void execute();
    void undo();
}
