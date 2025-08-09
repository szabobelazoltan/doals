package hu.szbz.hbc.doals.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "DIRECTORY_ENTRY")
public class DirectoryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "EXT_ID")
    private String externalId;

    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DirectoryEntryType type;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private DirectoryEntryStatus status;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CREATION_TS")
    private OffsetDateTime creationTimeStamp;

    @Column(name = "MODIFICATION_TS")
    private OffsetDateTime modificationTimeStamp;

    @Column(name = "DELETION_TS")
    private OffsetDateTime deletionTimeStamp;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")
    private DirectoryEntry parent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public DirectoryEntryType getType() {
        return type;
    }

    public void setType(DirectoryEntryType type) {
        this.type = type;
    }

    public DirectoryEntryStatus getStatus() {
        return status;
    }

    public void setStatus(DirectoryEntryStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(OffsetDateTime creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public OffsetDateTime getModificationTimeStamp() {
        return modificationTimeStamp;
    }

    public void setModificationTimeStamp(OffsetDateTime modificationTimeStamp) {
        this.modificationTimeStamp = modificationTimeStamp;
    }

    public OffsetDateTime getDeletionTimeStamp() {
        return deletionTimeStamp;
    }

    public void setDeletionTimeStamp(OffsetDateTime deletionTimeStamp) {
        this.deletionTimeStamp = deletionTimeStamp;
    }

    public DirectoryEntry getParent() {
        return parent;
    }

    public void setParent(DirectoryEntry parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DirectoryEntry that = (DirectoryEntry) o;
        return id == that.id && Objects.equals(externalId, that.externalId) && type == that.type && status == that.status && Objects.equals(creationTimeStamp, that.creationTimeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, type, status, creationTimeStamp);
    }

    @Override
    public String toString() {
        return "DirectoryEntry{" +
                "id=" + id +
                ", externalId='" + externalId + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", creationTimeStamp=" + creationTimeStamp +
                ", modificationTimeStamp=" + modificationTimeStamp +
                ", deletionTimeStamp=" + deletionTimeStamp +
                '}';
    }
}
