package hu.szbz.hbc.doals.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "ACCESS")
public class Access {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "PERMISSION")
    private int permissionCode;

    @Column(name = "OWNERSHIP")
    private boolean ownership;

    @ManyToOne
    @JoinColumn(name = "ACTOR_ID", referencedColumnName = "ID")
    private Actor actor;

    @ManyToOne
    @JoinColumn(name = "ENTRY_ID", referencedColumnName = "ID")
    private DirectoryEntry entry;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(int permissionCode) {
        this.permissionCode = permissionCode;
    }

    public boolean isOwnership() {
        return ownership;
    }

    public void setOwnership(boolean ownership) {
        this.ownership = ownership;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public DirectoryEntry getEntry() {
        return entry;
    }

    public void setEntry(DirectoryEntry entry) {
        this.entry = entry;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Access access = (Access) o;
        return id == access.id && permissionCode == access.permissionCode && ownership == access.ownership && Objects.equals(actor, access.actor) && Objects.equals(entry, access.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permissionCode, ownership, actor, entry);
    }

    @Override
    public String toString() {
        return "Access{" +
                "id=" + id +
                ", permissionCode=" + permissionCode +
                ", ownership=" + ownership +
                ", actor=" + actor +
                ", entry=" + entry +
                '}';
    }

    public static Access createNew(Actor actor, DirectoryEntry entry, boolean ownership, int permissionCode) {
        final Access entity = new Access();
        entity.setActor(actor);
        entity.setEntry(entry);
        entity.setOwnership(ownership);
        entity.setPermissionCode(permissionCode);
        return entity;
    }
}
