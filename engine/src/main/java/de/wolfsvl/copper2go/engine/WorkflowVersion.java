package de.wolfsvl.copper2go.engine;

public class WorkflowVersion {
    public final String name;
    public final long major;
    public final long minor;

    WorkflowVersion(final String name, final long major, final long minor) {
        this.name = name;
        this.major = major;
        this.minor = minor;
    }

    public static WorkflowVersion of(final String uri) throws EngineException {
        try {
            String[] parts = uri.split("/");
            String name = parts[parts.length - 1];
            String majorMinor = parts[parts.length - 2];
            String[] splittedVersion = majorMinor.split("\\.");
            return new WorkflowVersion(
                    name,
                    Long.parseLong(splittedVersion[0]),
                    Long.parseLong(splittedVersion[1])
            );
        } catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
            throw new EngineException("Unable to get worklow form uri '" + uri + "'.", e);
        }
    }
}