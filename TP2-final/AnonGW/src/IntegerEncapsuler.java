public class IntegerEncapsuler {

    private Integer i;

    public IntegerEncapsuler(int i){

        this.i=i;
    }

    public Integer getI(){

        return this.i;
    }

    public void setI(Integer i) {

        this.i = i;
    }

    public boolean equals(Object o){

        if(o == null) return false;

        if(this.getClass() != o.getClass()) return false;

        IntegerEncapsuler ie = (IntegerEncapsuler)o;

        return this.i == ie.i;
    }
}
