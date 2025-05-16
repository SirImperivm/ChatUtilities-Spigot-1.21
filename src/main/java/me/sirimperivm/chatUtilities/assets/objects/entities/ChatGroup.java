package me.sirimperivm.chatUtilities.assets.objects.entities;

@SuppressWarnings("all")
public class ChatGroup {

    private int weight;
    private String name;
    private boolean def;

    public ChatGroup(int weight, String name, boolean def) {
        this.weight = weight;
        this.name = name;
        this.def = def;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }
}