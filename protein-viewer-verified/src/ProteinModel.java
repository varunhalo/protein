public class ProteinModel {

    private String name;
    private String pdbId;
    private String info;
    private String sequence;

    public ProteinModel(String name, String pdbId, String info, String sequence) {
        this.name = name;
        this.pdbId = pdbId;
        this.info = info;
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public String getPdbId() {
        return pdbId;
    }

    public String getInfo() {
        return info;
    }

    public String getSequence() {
        return sequence;
    }
}