package delfi.com.vn.autotransferfile.model;
import io.realm.RealmObject;

/**
 * Created by PC on 9/1/2017.
 */

public class CFolder extends RealmObject{

    public int folder_id ;
    public int group_id;
    public String folder_name ;
    public String created_date ;
    public String updated_date ;
    public int status ;


    public CFolder(){

    }

    public CFolder(int folder_id,int group_id,String folder_name,String created_date,String updated_date,int status){
        this.folder_id = folder_id;
        this.group_id = group_id;
        this.folder_name = folder_name;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.status = status;
    }


}
