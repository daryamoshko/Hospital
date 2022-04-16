package mashko.hospital.entity;

import mashko.hospital.service.util.JsonConverter;

import java.io.Serializable;

public class Medicament implements Serializable {
    private int id;
    private String name;
    private boolean isEnabled;

    public Medicament() {
    }

    public Medicament(String name) {
        this.name = name;
    }

    public Medicament(String name, boolean isEnabled) {
        this.name = name;
        this.isEnabled = isEnabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Medicament that = (Medicament) o;

        if (id != that.id) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return JsonConverter.toJson(this);
    }
}
