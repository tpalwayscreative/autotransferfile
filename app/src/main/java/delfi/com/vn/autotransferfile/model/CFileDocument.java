package delfi.com.vn.autotransferfile.model;

import io.realm.RealmObject;

/**
 * Created by PC on 9/1/2017.
 */

public class CFileDocument extends RealmObject {

    public int filedocument_id;
    public String file_name ;
    public String created_date;
    public String updated_date ;
    public int status ;
    public int group_id ;
    public int folder_id;
    public String device_id;

    public CFileDocument(){

    }

    public CFileDocument(int filedocument_id,String file_name,String created_date,String updated_date,int status,int group_id,int folder_id,String device_id){
        this.filedocument_id = filedocument_id ;
        this.file_name = file_name;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.status = status;
        this.group_id = group_id;
        this.folder_id = folder_id;
        this.device_id = device_id;
    }

}
