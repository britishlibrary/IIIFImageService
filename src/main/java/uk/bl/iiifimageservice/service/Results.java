package uk.bl.iiifimageservice.service;

public class Results {

    public Results(String region, int reduce) {
        this.region = region;
        this.reduce = reduce;
    }

    public String region;

    public int reduce;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getReduce() {
        return reduce;
    }

    public void setReduce(int reduce) {
        this.reduce = reduce;
    }

}
