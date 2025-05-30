package me.themoonis.ticketSystem.files;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.themoonis.ticketSystem.TicketSystem;
import me.themoonis.ticketSystem.report.Ticket;
import me.themoonis.ticketSystem.report.TicketReference;
import me.themoonis.ticketSystem.utils.Colorful;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class TicketStorageFile {

    private final TicketSystem ticketSystem;

    private File jsonFile;
    private ObjectNode rootNode;

    @SneakyThrows
    public void load() {
        if (!ticketSystem.getDataFolder().exists())
            ticketSystem.getDataFolder().mkdirs();

        jsonFile = new File(ticketSystem.getDataFolder(), "tickets.json");

        if (!jsonFile.exists())
            jsonFile.createNewFile();

        ObjectMapper mapper = new ObjectMapper();

        try {
            rootNode = (ObjectNode) mapper.readTree(jsonFile);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Colorful.plugin("Couldn't load tickets due malformed json file!"));
            Bukkit.getConsoleSender().sendMessage(Colorful.plugin("<red>Defaulting to empty root node."));

            rootNode = JsonNodeFactory.instance.objectNode();
        }
    }

    public void read() {
        ObjectMapper mapper = new ObjectMapper();
        for (Iterator<String> it = rootNode.fieldNames(); it.hasNext(); ) {
            String ticketUUIDString = it.next();
            JsonNode ticketNode = rootNode.get(ticketUUIDString);

            try {
                Ticket parsedTicket = mapper.treeToValue(ticketNode, Ticket.class);
                ticketSystem.getTicketStorage().addTicket(UUID.fromString(ticketUUIDString), parsedTicket);
            } catch (JsonProcessingException e) {
                Bukkit.getConsoleSender().sendMessage(Colorful.plugin("Failed to parse ticket '%s'!".formatted(ticketUUIDString)));
            }
        }
    }

    public void save(TicketReference ticketReference) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode convertedJson = mapper.valueToTree(ticketReference.reference());
        delete(ticketReference.uuid());
        rootNode.put(ticketReference.uuid().toString(), convertedJson);
    }

    public void delete(UUID ticketUUID) {
        rootNode.remove(ticketUUID.toString());
    }
    public boolean has(UUID ticket){
        return rootNode.has(ticket.toString());
    }
    public void write() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, rootNode);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(Colorful.plugin("Failed to write tickets to json file!"));
        }
    }
    @SneakyThrows
    public String getJsonString(){
        return new ObjectMapper().writeValueAsString(rootNode);
    }
}
