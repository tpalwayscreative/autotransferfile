package delfi.com.vn.autotransferfile.model;

import com.google.gson.Gson;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by PC on 9/1/2017.
 */

public class CFileDocument extends RealmObject{

    @PrimaryKey
    public int file_document_id;
    public String file_name ;
    public String parent_folder_name ;
    public String created_date;
    public String updated_date ;
    public int status ;
    public int group_id ;
    public int folder_id;
    public String device_id;
    public String path_folder_name ;

    public CFileDocument(){

    }

    public CFileDocument(int file_document_id,String parent_folder_name,String file_name,String created_date,String updated_date,int status,int group_id,int folder_id,String device_id,String path_folder_name){
        this.file_document_id = file_document_id ;
        this.parent_folder_name = parent_folder_name;
        this.file_name = file_name;
        this.created_date = created_date;
        this.updated_date = updated_date;
        this.status = status;
        this.group_id = group_id;
        this.folder_id = folder_id;
        this.device_id = device_id;
        this.path_folder_name = path_folder_name;
    }

}
