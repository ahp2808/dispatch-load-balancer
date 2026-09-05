package com.freightfox.dispatch.model;

public enum Priority {
    HIGH(1),
    MEDIUM(2),
    LOW(3);

    private final int rank;

    Priority(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
