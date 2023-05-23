package model;

public class Sensor {
    private String name;

    public Sensor(String name) {
        this.name = name;
    }

    public Sensor() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "name='" + name + '\'' +
                '}';
    }
}
