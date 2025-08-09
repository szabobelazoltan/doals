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

import java.util.Objects;

@Entity
@Table(name = "ACCESS")
public class Access {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "PERMISSION")
    private int permissionCode;

    @Column(name = "ROLE")
    @Enumerated(EnumType.ORDINAL)
    private AccessRole role;

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

    public AccessRole getRole() {
        return role;
    }

    public void setRole(AccessRole role) {
        this.role = role;
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
        return id == access.id && permissionCode == access.permissionCode && role == access.role && Objects.equals(actor, access.actor) && Objects.equals(entry, access.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permissionCode, role, actor, entry);
    }

    @Override
    public String toString() {
        return "Access{" +
                "id=" + id +
                ", permissionCode=" + permissionCode +
                ", role=" + role +
                ", actor=" + actor +
                ", entry=" + entry +
                '}';
    }
}
