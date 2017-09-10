package delfi.com.vn.autotransferfile.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by PC on 9/1/2017.
 */

public class CGroups extends RealmObject{

    @PrimaryKey
    public int group_id;
    public String group_name;
    public String created_date;
    public String updated_date;
    public int status ;

    public CGroups(){

    }

    public CGroups(int group_id,String group_name,String created_date,String updated_date,int status){
        this.group_id = group_id ;
        this.group_name = group_name ;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.status = status;
    }

}
