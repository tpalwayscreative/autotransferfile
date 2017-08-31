package delfi.com.vn.autotransferfile.model;

/**
 * Created by PC on 8/30/2017.
 */

public class CAuToUpload {

    public String name ;
    public String full_path ;
    public boolean isEnable ;

    public CAuToUpload(String full_path,String name,boolean isEnable){
        this.name = name ;
        this.full_path = full_path;
        this.isEnable = isEnable;
    }

}
