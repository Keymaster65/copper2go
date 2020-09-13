package de.wolfsvl.copper2go.workflowapi;

public interface Context {
    public String getRequest();
    public void reply(String message);
}
