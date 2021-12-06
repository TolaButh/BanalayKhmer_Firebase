package tolabuth.example.banalaybook.model;

public class ModelCategory {
    String id, category, udi;
    long timestemp;

    public ModelCategory() {
    }

    public ModelCategory(String id, String category, String udi, long timestemp) {
        this.id = id;
        this.category = category;
        this.udi = udi;
        this.timestemp = timestemp;
    }

    @Override
    public String toString() {
        return this.category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUdi() {
        return udi;
    }

    public void setUdi(String udi) {
        this.udi = udi;
    }

    public long getTimestemp() {
        return timestemp;
    }

    public void setTimestemp(long timestemp) {
        this.timestemp = timestemp;
    }
}
