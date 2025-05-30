package me.themoonis.ticketSystem.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.themoonis.ticketSystem.TicketSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.UUID;

public record Ticket(UUID reporter, UUID reported, Timestamp reportStamp, String[] reasons, String description) {

    /**
     * Checks to see if the reported time stamp is over 30 days ago.
     *
     * @param ticketSystem the main plugin class
     * @return whether the ticket was reported over 30 days ago.
     */
    @JsonIgnore
    public boolean isAvailableExpire(TicketSystem ticketSystem) {
        return reportStamp.before(ticketSystem.getExpiryTimeStamp());
    }
    @JsonIgnore
    public OfflinePlayer getReportedPlayer(){

        return Bukkit.getOfflinePlayer(reported);
    }
    @JsonIgnore
    public OfflinePlayer getReporterPlayer(){
        return Bukkit.getOfflinePlayer(reporter);
    }

    @Override
    @JsonIgnore
    public @NotNull String toString() {
        return reporter + " " + reported + " " + reportStamp + " " + Arrays.toString(reasons) + " " + description;
    }

}
