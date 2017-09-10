package delfi.com.vn.autotransferfile.model;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by PC on 9/1/2017.
 */

public class CDevice extends RealmObject{
    @PrimaryKey
    public String device_id ;
    public int flag_login;
    public String name_device ;
    public String created_date ;
    public String updated_date;
    public String group_id;

    public CDevice(){

    }

    public CDevice(String device_id,int flag_login,String name_device,String created_date,String updated_date,String group_id){
        this.device_id = device_id ;
        this.flag_login = flag_login;
        this.name_device = name_device;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.group_id = group_id;
    }
}
