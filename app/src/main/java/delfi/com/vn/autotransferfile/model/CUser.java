package delfi.com.vn.autotransferfile.model;
import java.io.Serializable;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by PC on 8/3/2017.
 */

public class CUser extends RealmObject implements Serializable{

    @PrimaryKey
    public String device_id;
    public String group_id;
    public String group_name;
    public String access_token;
    public String name;
    public String email;
    public String created_date;
    public String updated_date;

    public CUser(){

    }

    public CUser(String access_token,String device_id){
        this.access_token = access_token ;
        this.device_id = device_id ;
    }

    public CUser(String name, String email, String access_token, String created_date,String updated_date){
        this.name = name ;
        this.email = email ;
        this.access_token = access_token ;
        this.created_date = created_date;
        this.updated_date = updated_date;
    }

}
