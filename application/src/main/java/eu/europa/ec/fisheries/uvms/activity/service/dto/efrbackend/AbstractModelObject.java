package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module
 */
public abstract class AbstractModelObject {

    @NotNull(message = "Created at missing")
    private Instant clientCreatedAt;

    @NotNull(message = "Updated at missing")
    private Instant clientUpdatedAt;

    private Instant serverCreatedAt;
    private Instant serverUpdatedAt;

    // Suppress warnings about our NotNull instants not being set. This has to
    // be done by jsonb when deserializing the incoming DTOs, we don't want to
    // risk setting incorrect timestamps that give a false impression
    @SuppressWarnings("squid:S2637")
    public AbstractModelObject() {}

    public Instant getClientCreatedAt() {
        return clientCreatedAt;
    }

    public void setClientCreatedAt(Instant clientCreatedAt) {
        this.clientCreatedAt = clientCreatedAt;
    }

    public Instant getClientUpdatedAt() {
        return clientUpdatedAt;
    }

    public void setClientUpdatedAt(Instant clientUpdatedAt) {
        this.clientUpdatedAt = clientUpdatedAt;
    }

    public Instant getServerCreatedAt() {
        return serverCreatedAt;
    }

    public void setServerCreatedAt(Instant serverCreatedAt) {
        this.serverCreatedAt = serverCreatedAt;
    }

    public Instant getServerUpdatedAt() {
        return serverUpdatedAt;
    }

    public void setServerUpdatedAt(Instant serverUpdatedAt) {
        this.serverUpdatedAt = serverUpdatedAt;
    }

    @Override
    public String toString() {
        return "AbstractModelObject{" +
                "clientCreatedAt=" + clientCreatedAt +
                ", clientUpdatedAt=" + clientUpdatedAt +
                ", serverCreatedAt=" + serverCreatedAt +
                ", serverUpdatedAt=" + serverUpdatedAt +
                '}';
    }
}
