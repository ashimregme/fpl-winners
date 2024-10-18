package com.techlekh;

import java.util.Map;
import java.util.Objects;

public final class Manager {
    private int id;
    private String playerName;
    private int entry;
    private String entryName;
    private Map<Integer, Integer> gwPointsByGwNo;
    private int totalPoints;

    public Manager(
            int id,
            String playerName,
            int entry,
            String entryName,
            Map<Integer, Integer> gwPointsByGwNo,
            int totalPoints
    ) {
        this.id = id;
        this.playerName = playerName;
        this.entry = entry;
        this.entryName = entryName;
        this.gwPointsByGwNo = gwPointsByGwNo;
        this.totalPoints = totalPoints;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public Map<Integer, Integer> getGwPointsByGwNo() {
        return gwPointsByGwNo;
    }

    public void setGwPointsByGwNo(Map<Integer, Integer> gwPointsByGwNo) {
        this.gwPointsByGwNo = gwPointsByGwNo;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Manager) obj;
        return this.id == that.id &&
                Objects.equals(this.playerName, that.playerName) &&
                this.entry == that.entry &&
                Objects.equals(this.entryName, that.entryName) &&
                Objects.equals(this.gwPointsByGwNo, that.gwPointsByGwNo) &&
                this.totalPoints == that.totalPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playerName, entry, entryName, gwPointsByGwNo, totalPoints);
    }

    @Override
    public String toString() {
        return "Manager[" +
                "id=" + id + ", " +
                "playerName=" + playerName + ", " +
                "entry=" + entry + ", " +
                "entryName=" + entryName + ", " +
                "gwPointsByGwNo=" + gwPointsByGwNo + ", " +
                "totalPoints=" + totalPoints + ']';
    }

}
